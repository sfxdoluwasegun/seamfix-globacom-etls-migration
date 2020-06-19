package ng.autotopup.glo_etls.cruncher;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.time.StopWatch;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import ng.autotopup.glo_etls.cdr.CdrExtract;
import ng.autotopup.glo_etls.enums.CdrType;

@Stateless
public class CdrCruncher {
	
	private Logger log = Logger.getLogger(getClass());

	private Pattern nextLine ;
	
	@Inject
	private CdrTransactions cdrTransactions ;

	/**
	 * Maintain initialization of re-usable objects
	 */
	@PostConstruct
	public void init(){

		nextLine = Pattern.compile("\n");
	}

	/**
	 * Conduct CDR file data crunching.
	 * Break file data into line transactions.
	 * Forward extracted transaction for asynchronous processing.
	 * 
	 * @param filedata all lines found in CDR file
	 * @param filename CDR file name
	 * @param creationDate time stamp CDR file was received on server
	 * @throws ClassNotFoundException Thrown when an application tries to load in a class through its string name using (1) the forName method in class Class, 
	 * (2) the findSystemClass method in class ClassLoader or (3) the loadClass method in class ClassLoader. 
	 * @throws SecurityException Thrown by the security manager to indicate a security violation.
	 * @throws NoSuchMethodException Thrown when a particular method cannot be found
	 * @throws InvocationTargetException checked exception that wraps an exception thrown by an invoked method or constructor
	 * @throws IllegalArgumentException Thrown to indicate that a method has been passed an illegal or inappropriate argument
	 * @throws IllegalAccessException Thrown when an application tries to reflectively create an instance (other than an array), 
	 * set or get a field, or invoke a method, but the currently executing method does not have access to the definition of the specified class, 
	 * field, method or constructor
	 * @throws InstantiationException Thrown when an application tries to create an instance of a class using the newInstance method in class Class, 
	 * but the specified class object cannot be instantiated
	 */
	@Asynchronous
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 10)
	public void crunchCDRFile(String filedata, String filename, String creationDate) 
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException{

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		String[] linedata = nextLine.split(filedata);

		for (int i = 0; i < linedata.length ; i++){

			Class<?> clazz = Class.forName(CdrType.getCDRType(filename).getClazz());
			CdrExtract cdrExtract = (CdrExtract) clazz.getDeclaredConstructor(String.class).newInstance(linedata[i]);
			cdrTransactions.processCDRTransaction(cdrExtract.getCallingPartyNumber(), linedata[i], filename, creationDate, 
					cdrExtract.getTimestamp(), cdrExtract.accountBalance());
		}

		stopWatch.stop();
		log.info("Time taken to complete CDR file:" + filename + " crunching:" + stopWatch.getTime() + "ms");
	}

}