package ng.autotopup.glo_etls.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum GeneralSettings {
	
	TPS_EDP("TP SERIVCE EDP", "http://52.8.73.101:18082/vtu-service", "Endpoint for topup microservice"), 
	WLIST_EDP("WLIST SERIVCE EDP", "http://52.8.73.101:18082/vtu-service", "Endpoint for watchlist microservice"), 
	VTU_PING("VTU PING EDP", "http://52.8.73.101:18082/vtu-service", "Endpoint for PING confirmation"), 
	RPRCSNG_EDP("TNP REPROCESSING EDP", "http://52.8.73.101:18082/vtu-service", "Endpoint for CDR file reprocessing"), 
	TPS_PATH("TP SERIVCE PATH", "/api/v1/vtu/autoTopUpRequest", "Service path for topup microservice"), 
	WLIST_PATH("WLIST SERIVCE PATH", "/api/v1/vtu/retrieveValidMsisdnThresholds", "Service path for topup microservice"), 
	PING_PATH("VTU PING PATH", "/api/v1/vtu/ping", "Service path for VTU PING"), 
	RPRCSNG_PATH("TNP REPROCESS SERVICE PATH", "/api/v1/vtu/bulkAutoTopUpRequest", "Service path for CDR reprocessing"), 
	TPS_KEY("TP PUBLIC KEY", "/vtu/keys/demo", "Public key relative path to jboss.home.dir"), 
	BUCKET_MAX("BUCKET MAX", "250", "Watchlist query fetch size");
	
	private String name ;
	private String description ;
	private String value ;
	
	/**
	 * Instantiates a new general settings.
	 *
	 * @param name the name
	 * @param value the value
	 * @param description the description
	 */
	private GeneralSettings(String name, 
			String value, String description){
		
		this.setDescription(description);
		this.setName(name);
		this.setValue(value);
	}
	
	/**
	 * Retrieve enumeration from name.
	 *
	 * @param name the name
	 * @return the general settings
	 */
	public static GeneralSettings fromName(String name){
		if (name != null && !name.isEmpty())
			for (GeneralSettings generalSettings : GeneralSettings.values()){
				if (generalSettings.getName().equalsIgnoreCase(name))
					return generalSettings;
			}
		
		return null;
	}
	
	/**
	 * Retrieve literals of all declared enumeration name property
	 *
	 * @return the list
	 */
	public static List<String> literals(){
		List<String> literals = new ArrayList<String>();

		for (GeneralSettings generalSettings : GeneralSettings.values()){
			literals.add(generalSettings.getName());
		}
		
		Collections.sort(literals, new Comparator<String>() {
			public int compare(String a, String b){
				return a.compareToIgnoreCase(b);
			}
		});
		
		return literals;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}