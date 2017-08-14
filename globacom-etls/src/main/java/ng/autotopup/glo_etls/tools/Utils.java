package ng.autotopup.glo_etls.tools;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;

@Stateless
public class Utils {
	
	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private ApplicationBean appBean ;
	
	/**
	 * Store current DateModified property of file.
	 */
	@Asynchronous
	public void cachePropetiesFileTimeStamp(){
		
		LocalDateTime localDateTime = getPropertiesFileCurrentModificationDate();
		appBean.setLastModified(localDateTime);
	}
	
	/**
	 * Retrieved current Date Modified property of file.
	 * 
	 * @return {@link LocalDateTime} representing time-stamp of lastModified property of file
	 */
	public LocalDateTime getPropertiesFileCurrentModificationDate() {
		// TODO Auto-generated method stub
		
		File file = new File(System.getProperty("jboss.home.dir") + "/app.properties");
		long milliseconds = 0;
		
		if (file.exists())
			milliseconds = file.lastModified();
		
		if (milliseconds == 0)
			return LocalDateTime.now();
		
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
	}
	
	/**
	 * Validate parameters for null, empty or zero value.
	 * 
	 * @param params objects to be validated for null or other instance specific qualities
	 * @return true if parameter(s) are valid
	 */
	public boolean validateParameters(Object...params){
		
		for (Object param : params){
			if (param == null)
				return false;
			if (param instanceof String && ((String) param).isEmpty())
				return false;
			if (param instanceof Long && ((Long) param).compareTo(0L) == 0)
				return false;
			if (param instanceof Integer && ((Integer) param).compareTo(0) == 0)
				return false;
			if (param instanceof Collection<?> && ((Collection<?>) param).isEmpty())
				return false;
			if (param instanceof BigDecimal && ((BigDecimal) param).compareTo(BigDecimal.ZERO) == 0)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Format MSISDN to accepted persisted format.
	 * 
	 * @param msisdn subscriber unique MSISDN
	 * @return formatted MSISDN
	 */
	public String formatMsisdn(String msisdn) {
		// TODO Auto-generated method stub
		
		if (msisdn.startsWith("+234"))
			return msisdn;
		
		if (msisdn.startsWith("0"))
			msisdn = "+234" + msisdn.substring(1);
		
		return "+234" + msisdn ;
	}

	/**
	 * Write CDR transaction to specified archive directory.
	 * 
	 * @param transaction data extracted from CDR file
	 * @param path directory path to which file is to be written
	 */
	public void writeTransaction(String transaction, String path) {
		// TODO Auto-generated method stub
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

		String filename = new StringBuilder(path).append("tnp^")
				.append(dateTimeFormatter.format(LocalDateTime.now())).append(".txt").toString();
		File file = new File(filename);

		try {
			FileUtils.writeStringToFile(file, transaction, StandardCharsets.UTF_8, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}

		if (!file.exists())
			log.error("Error creating transaction file");
	}
	
	/**
	 * Write trigger transaction to archive directory.
	 * 
	 * @param transaction transaction data extracted from CDR file
	 * @param msisdn MSISDN of subscriber for autoTopup
	 */
	@Asynchronous
	public void writeTriggeredTransaction(String transaction, String msisdn, String filename) {
		// TODO Auto-generated method stub
		
		File file = new File(filename);

		try {
			FileUtils.writeStringToFile(file, transaction, StandardCharsets.UTF_8, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}
	
	/**
	 * Write CDR transaction to specified archive directory for re-processing.
	 * 
	 * @param transaction - data extracted from CDR file
	 */
	@Asynchronous
	public void writeTransaction(String transaction) {
		// TODO Auto-generated method stub
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		String filename = new StringBuilder("etls/cdr_reprocess/").append("cdr^")
				.append(dateTimeFormatter.format(LocalDate.now())).append(".txt").toString();
		File file = new File(filename);

		try {
			FileUtils.writeStringToFile(file, transaction + "\n", StandardCharsets.UTF_8, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}

		if (!file.exists())
			log.error("Error creating transaction file");
	}

}