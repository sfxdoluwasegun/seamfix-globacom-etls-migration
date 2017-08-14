package ng.autotopup.glo_etls.tools;

import java.time.LocalDateTime;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
public class TaskManager {
	
	@Inject
	private ApplicationBean appBean ;
	
	@Inject
	private Utils utils ;
	
	@Inject
	private PropertiesManager props ;

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void checkPropertiesFile() {
		// TODO Auto-generated method stub
		
		LocalDateTime lastModified = appBean.getLastModified();
		LocalDateTime currentTimestamp = utils.getPropertiesFileCurrentModificationDate();
		
		if (currentTimestamp.isAfter(lastModified)){
			appBean.setLastModified(currentTimestamp);
			props.loadProperties();
		}
	}

}