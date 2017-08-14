package ng.autotopup.glo_etls.enums;

public enum PayType {
	
	PREPAID("0"), 
	POSTPAID("1"), 
	HYBRID("2") ;
	
	private String flag ;
	
	private PayType(String flag) {
		// TODO Auto-generated constructor stub
		
		this.flag = flag;
	}
	
	public static PayType fromFlag(String flag){
		
		if (flag == null)
			return null;
		
		for (PayType payType : PayType.values()){
			if (payType.getFlag().equalsIgnoreCase(flag))
				return payType;
		}
		
		return null;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
