package ng.autotopup.glo_etls.enums;

public enum SmsType {
	
	LOCAL(0), 
	INTRA(1), 
	INTER(2), 
	INTERNATIONAL(3) ;
	
	private int type ;
	
	private SmsType(int type) {
		// TODO Auto-generated constructor stub
		
		this.type = type;
	}
	
	public static SmsType fromType(Integer type){
		
		if (type == null)
			return null;
		
		for (SmsType smsType : SmsType.values()){
			if (smsType.getType() == type)
				return smsType;
		}
		
		return null;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
