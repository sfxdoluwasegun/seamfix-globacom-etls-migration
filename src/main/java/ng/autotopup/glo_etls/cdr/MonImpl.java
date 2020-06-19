package ng.autotopup.glo_etls.cdr;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import ng.autotopup.glo_etls.enums.UserState;

public class MonImpl implements CdrExtract {
	
	private String[] linedata ;

	private static Pattern delimiter ;
	private static DateTimeFormatter formatter ;

	static{
		delimiter = Pattern.compile("\\|");
		formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	}
	
	public MonImpl(String linedata) {
		// TODO Auto-generated constructor stub
		
		this.linedata = delimiter.split(linedata);
	}

	@Override
	public int getSerialno() {
		// TODO Auto-generated method stub
		try {
			return Integer.parseInt(linedata[0]);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return 0;
		}
	}

	@Override
	public Timestamp getTimestamp() {
		// TODO Auto-generated method stub
		return Timestamp.valueOf(LocalDateTime.parse(linedata[1], formatter));
	}

	@Override
	public String getCallingPartyNumber() {
		// TODO Auto-generated method stub
		return linedata[5];
	}

	@Override
	public UserState getUserState() {
		// TODO Auto-generated method stub
		try {
			return UserState.fromDigit(Integer.parseInt(linedata[18].substring(0, 1)));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	@Override
	public BigDecimal charge() {
		// TODO Auto-generated method stub
		try {
			return BigDecimal.valueOf(Double.parseDouble(linedata[26]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal accountBalance() {
		// TODO Auto-generated method stub
		try {
			return BigDecimal.valueOf(Double.parseDouble(linedata[27]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * Retrieve monthly cycle begin date.
	 * 
	 * @return cycle start time
	 */
	public Timestamp getCycleBeginTime(){
		try {
			return Timestamp.valueOf(LocalDateTime.parse(linedata[9], formatter));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	/**
	 * Retrieve monthly cycle end date.
	 * 
	 * @return cycle stop time
	 */
	public Timestamp getCycleEndTime(){
		try {
			return Timestamp.valueOf(LocalDateTime.parse(linedata[10], formatter));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

}