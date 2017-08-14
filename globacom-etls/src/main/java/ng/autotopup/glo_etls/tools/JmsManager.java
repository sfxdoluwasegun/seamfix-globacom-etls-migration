package ng.autotopup.glo_etls.tools;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.DependsOn;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;

import org.jboss.logging.Logger;

@Startup
@DependsOn(value = {"StartupManager"})
@Singleton
@AccessTimeout(unit = TimeUnit.MINUTES, value = 3)
@Lock(LockType.WRITE)
public class JmsManager {
	
	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private JMSContext jmsContext ;
	
	@Resource(mappedName = "java:/jms/queue/tnptxns")
	private Queue queue ;
	
	/**
	 * Forward file transaction extract to JMS destination for subscriber engagement.
	 * 
	 * @param filetxn - extracted file transaction line
	 */
	public void sendMessage(String filetxn){
		
		try {
			MapMessage mapMessage = jmsContext.createMapMessage();
			mapMessage.setString("filetxn", filetxn);
			
			jmsContext.createProducer().send(queue, mapMessage);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}

}
