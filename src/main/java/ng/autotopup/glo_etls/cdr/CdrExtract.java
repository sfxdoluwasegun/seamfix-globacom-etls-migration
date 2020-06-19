package ng.autotopup.glo_etls.cdr;

import java.math.BigDecimal;
import java.sql.Timestamp;

import ng.autotopup.glo_etls.enums.UserState;

public interface CdrExtract {
	
	/**
	 * Retrieve CDR generation serial number.
	 * 
	 * @return serial number
	 */
	public int getSerialno();
	
	/**
	 * Retrieve time stamp of event from CDR data.
	 * 
	 * @return event time stamp
	 */
	public Timestamp getTimestamp();
	
	/**
	 * Retrieve subscriber MSISDN.
	 * 
	 * @return subscriber unique MSISDN
	 */
	public String getCallingPartyNumber();
	
	/**
	 * Retrieve subscriber user state from CDR data.
	 * 
	 * @return subscriber state
	 */
	public UserState getUserState();
	
	/**
	 * Retrieve amount deducted from subscriber balance.
	 * 
	 * @return charge for transaction
	 */
	public BigDecimal charge();
	
	/**
	 * Retrieve subscriber current account balance.
	 * 
	 * @return current account balance
	 */
	public BigDecimal accountBalance();

}