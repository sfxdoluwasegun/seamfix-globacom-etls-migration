package ng.autotopup.glo_etls.enums;

public enum CdrType {
	
	REC("rec", "ng.autotopup.glo_etls.cdr.RecImpl"), 
	DATA("data", "ng.autotopup.glo_etls.cdr.DataImpl"), 
	SMS("sms", "ng.autotopup.glo_etls.cdr.SmsImpl"), 
	MMS("mms", "ng.autotopup.glo_etls.cdr.MmsImpl"), 
	COM("com", "ng.autotopup.glo_etls.cdr.ComImpl"), 
	MON("mon", "ng.autotopup.glo_etls.cdr.MonImpl"), 
	OREC("orec", ""), 
	ODATA("odata", ""), 
	OSMS("osms", ""), 
	OMMS("omms", ""), 
	OCOM("ocom", ""), 
	OMON("omon", "");
	
	private String prefix ;
	private String clazz ;
	
	private CdrType(String prefix, String clazz) {
		// TODO Auto-generated constructor stub
		
		this.prefix = prefix;
		this.clazz = clazz;
	}
	
	public static CdrType getCDRType(String filename){
		
		if (filename == null)
			return null;
		
		for (CdrType cdrType : CdrType.values()){
			if (filename.startsWith(cdrType.getPrefix()))
				return cdrType;
		}
		
		return null;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

}