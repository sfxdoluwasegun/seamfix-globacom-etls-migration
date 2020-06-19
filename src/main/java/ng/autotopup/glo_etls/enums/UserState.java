package ng.autotopup.glo_etls.enums;

public enum UserState {

	IDLE(0), 
	ACTIVE(1), 
	SUSPENDED(2), 
	DISABLE(3), 
	POOL(4), 
	PWA(5) ;
	
	private int digit ;
	
	private UserState(int digit) {
		// TODO Auto-generated constructor stub
		
		this.setDigit(digit);
	}
	
	public static UserState fromDigit(Integer digit){
		
		if (digit == null)
			return null;
		
		for (UserState userState : UserState.values()){
			if (userState.getDigit() == digit)
				return userState;
		}
		
		return null;
	}

	public int getDigit() {
		return digit;
	}

	public void setDigit(int digit) {
		this.digit = digit;
	}
}
