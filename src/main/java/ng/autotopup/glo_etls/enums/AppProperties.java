package ng.autotopup.glo_etls.enums;

public enum AppProperties {
	
	JMS("etls.forward.jms.engagement", "false"), 
	FILE_CACHING_LIMIT("file.caching.limit.hours", "4"), 
	CACHING_LIMIT("msisdn.caching.limit.minutes", "10");
	
	private String property ;
	private String value ;
	
	private AppProperties(String property, String value) {
		// TODO Auto-generated constructor stub
		
		this.property = property;
		this.value = value;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
