package ng.autotopup.glo_etls.cruncher;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.time.StopWatch;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import ng.autotopup.glo_etls.cache.InfinispanBucketCache;
import ng.autotopup.glo_etls.enums.AppProperties;
import ng.autotopup.glo_etls.jaxrs.TopUpClient;
import ng.autotopup.glo_etls.tools.JmsManager;
import ng.autotopup.glo_etls.tools.PropertiesManager;
import ng.autotopup.glo_etls.tools.Utils;

@Stateless
public class CdrTransactions {
	
	private Logger log = Logger.getLogger(getClass());

	@Inject
	private InfinispanBucketCache cache ;

	@Inject
	private Utils utils ;

	@Inject
	private JmsManager jmsManager ;
	
	@Inject
	private PropertiesManager props ;
	
	@Inject
	private TopUpClient topupClient;
	
	private DateTimeFormatter dateTimeFormatter ;
	
	@PostConstruct
	public void init(){
		dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	}

	/**
	 * Filter line extract against watch-list.
	 * Confirm age of transaction hasn't surpassed configured limit
	 * Filter line extract against threshold monitor character.
	 * Confirm MSISDN hasn't been triggered in 'X' configured time and file transaction hasn't already been treated previously.
	 * Forward MSISDN and triggered threshold to VTU service for further processing.
	 * Handle post trigger operations.
	 * 
	 * @param msisdn subscriber unique MSISDN
	 * @param transaction raw extracted transaction line
	 * @param filename CDR file name
	 * @param creationDate time stamp CDR file was received on server
	 * @param timestamp CDR event transaction time stamp 
	 * @param balance subscriber account balance
	 */
	@Asynchronous
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 10)
	public void processCDRTransaction(String msisdn, String transaction, String filename, String creationDate, 
			Timestamp timestamp, BigDecimal balance){

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		msisdn = utils.formatMsisdn(msisdn);

		if (!cache.isSuscriberInBucketList(msisdn)){
			if (props.getBool(AppProperties.JMS.getProperty(), Boolean.parseBoolean(AppProperties.JMS.getValue())))
				jmsManager.sendMessage(transaction);
			stopWatch.stop();
			log.info("Time taken to complete CDR transaction:" + msisdn + " crunching:" + stopWatch.getTime() + "ms");
			return;
		}
		
		BigDecimal threshold = cache.getToupConfigurationFromCache(msisdn);
		
		if (balance.compareTo(threshold) > 0){
			stopWatch.stop();
			log.info("Time taken to complete CDR transaction:" + msisdn + " crunching:" + stopWatch.getTime() + "ms");
			return;
		}
		
		/**
		 * confirm NSISDN hasn't already been triggered from file with same name or within time boundary
		 */
		if (cache.getMSISDNFromTriggeredFileCache(filename, msisdn) != null 
				|| cache.getMSISDNFromTimeboundCache(msisdn) != null){
			stopWatch.stop();
			log.info("Time taken to complete CDR transaction:" + msisdn + " crunching:" + stopWatch.getTime() + "ms");
			return;
		}
		
		LocalDateTime filetime = timestamp.toLocalDateTime();
		if (LocalDateTime.now().minusMinutes(props.getLong("tnp-transaction-timeout-mins", 60L)).isAfter(filetime)){
			utils.writeTransaction(transaction, "etls/cdr_discards/");
			stopWatch.stop();
			log.info("Time taken to complete CDR transaction:" + msisdn + " crunching:" + stopWatch.getTime() + "ms");
			return;
		}
		
		try {
			String directory = "etls/cdr_txns/";
			String txnfilename = new StringBuilder("cdr^").append(msisdn).append("^").append(creationDate)
					.append("^").append(dateTimeFormatter.format(LocalDateTime.now())).append("^").append(filename).append(".txt").toString();
			topupClient.doTopupInvocation(utils.formatMsisdn(msisdn), txnfilename, balance);
			utils.writeTriggeredTransaction(transaction, msisdn, new StringBuilder(directory).append(txnfilename).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("TopupServiceException:" + e.getMessage());
			undoTransaction(transaction, filename, msisdn);
		}
		
		stopWatch.stop();
		log.info("Time taken to complete CDR transaction:" + msisdn + " crunching:" + stopWatch.getTime() + "ms");
	}
	
	/**
	 * Remove transaction detail from cached filter list.
	 * Write transaction to exception list for further review.
	 * 
	 * @param transaction extracted transaction detail
	 * @param filename CDR file name
	 * @param msisdn subscriber unique MSISDN
	 */
	private void undoTransaction(String transaction, String filename, String msisdn) {
		// TODO Auto-generated method stub
		
		utils.writeTransaction(transaction, "etls/cdr_exceptions/");
		cache.removeMSISDNFromFileTxnCache(filename, msisdn);
		cache.removeMSISDNFromTimeBoundCache(msisdn);
	}

}