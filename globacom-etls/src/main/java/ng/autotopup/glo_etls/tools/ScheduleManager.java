package ng.autotopup.glo_etls.tools;

import javax.ejb.DependsOn;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
@DependsOn(value = {"StartupManager"})
public class ScheduleManager {
	
	@Inject
	private TaskManager taskManager ;
	
	@Schedule(hour = "*", minute = "*/15")
	public void refreshCachedSettings(){
		
		taskManager.checkPropertiesFile();
	}

}
