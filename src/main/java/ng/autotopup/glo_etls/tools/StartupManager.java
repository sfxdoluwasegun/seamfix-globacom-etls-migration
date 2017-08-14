package ng.autotopup.glo_etls.tools;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@Startup
@Singleton
public class StartupManager {
	
	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private Utils utils ;
	
	@PostConstruct
	public void start(){
		
		utils.cachePropetiesFileTimeStamp();
		log.info("GLO ETLS SERVICE NOW AVAILABLE");
	}

}
