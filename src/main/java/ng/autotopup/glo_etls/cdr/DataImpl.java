package ng.autotopup.glo_etls.cdr;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import ng.autotopup.glo_etls.enums.UserState;

public class DataImpl implements CdrExtract {
	
	private String[] linedata ;

	private static Pattern delimiter ;
	private static DateTimeFormatter formatter ;

	static{
		delimiter = Pattern.compile("\\|");
		formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	}
	
	public DataImpl(String linedata) {
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
		return Timestamp.valueOf(LocalDateTime.parse(linedata[2], formatter));
	}

	@Override
	public String getCallingPartyNumber() {
		// TODO Auto-generated method stub
		return linedata[4];
	}

	@Override
	public UserState getUserState() {
		// TODO Auto-generated method stub
		try {
			return UserState.fromDigit(Integer.parseInt(linedata[45].substring(0, 1)));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	@Override
	public BigDecimal charge() {
		// TODO Auto-generated method stub
		try {
			return BigDecimal.valueOf(Double.parseDouble(linedata[52]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal accountBalance() {
		// TODO Auto-generated method stub
		try {
			return BigDecimal.valueOf(Double.parseDouble(linedata[53]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * Retrieve URL visited by subscriber.
	 * 
	 * @return URL string
	 */
	public String getURL(){
		return linedata[6];
	}

}