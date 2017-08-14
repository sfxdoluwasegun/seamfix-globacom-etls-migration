package ng.autotopup.glo_etls.cache;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.jboss.logging.Logger;

import ng.autotopup.glo_etls.enums.AppProperties;
import ng.autotopup.glo_etls.tools.PropertiesManager;

@Singleton
@AccessTimeout(unit = TimeUnit.MINUTES, value = 3)
@Lock(LockType.READ)
public class InfinispanBucketCache {

	private Logger log = Logger.getLogger(getClass());

	@Resource(lookup = "java:jboss/infinispan/distributed_cache/topup")
	private Cache<String, BigDecimal> cache;
	
	@Resource(lookup = "java:jboss/infinispan/distributed_cache/triggered/files")
	private Cache<String, Set<String>> cache2 ;
	
	@Resource(lookup = "java:jboss/infinispan/distributed_cache/triggered/time")
	private Cache<String, String> cache3 ;
	
	@Inject
	private PropertiesManager props ;
	
	private long filetriggerMonitor ;
	private long timeMonitor ;
	
	@PostConstruct
	public void init(){
		filetriggerMonitor = props.getLong(AppProperties.FILE_CACHING_LIMIT.getProperty(), Long.parseLong(AppProperties.FILE_CACHING_LIMIT.getValue()));
		timeMonitor = props.getLong(AppProperties.CACHING_LIMIT.getProperty(), Long.parseLong(AppProperties.CACHING_LIMIT.getValue()));
	}	

	/**
	 * Add auto top-up configuration to cache. 
	 * If configuration already exists in cache, refresh with details of passed argument.
	 * 
	 * @param msisdn - unique subscriber MSISDN
	 * @param threshold - entry value
	 */
	@Lock(LockType.WRITE)
	public void addTopupConfigurationToCache(String msisdn, BigDecimal threshold) {

		log.info("Adding msisdn:" + msisdn + " to bucketCache with threshold:" + threshold);
		cache.put(msisdn, threshold);
	}
	
	/**
	 * Add triggered information to cache.
	 * Cache is used as screen to avoid triggering duplicate data.
	 * 
	 * @param filename - filename key which has triggered one or more MSISDN
	 * @param msisdn - triggered MSISDN to be added to list
	 */
	@Lock(LockType.WRITE)
	public void addMsisdnTriggerAndFileDataToCache(String filename, String msisdn){
		
		log.info("Adding trigger info for msisdn:" + msisdn + " for file:" + filename);
		Set<String> msisdns = cache2.get(filename);
		if (msisdns == null)
			msisdns = new HashSet<>();
		
		msisdns.add(msisdn);
		cache2.put(filename, msisdns, filetriggerMonitor, TimeUnit.HOURS);
	}
	
	/**
	 * Add triggered MSISDN to time bound cache.
	 * 
	 * @param msisdn - entry key and value
	 */
	@Lock(LockType.WRITE)
	public void addMSISDNToTimeboundCache(String msisdn){
		
		cache3.putIfAbsent(msisdn, msisdn, timeMonitor, TimeUnit.MINUTES);
	}

	/**
	 * Remove configuration from cache.
	 * 
	 * @param msisdn - entry key
	 * @param threshold - entry value
	 */
	public void removeTopupConfigurationFromCache(String msisdn, BigDecimal threshold) {
		// TODO Auto-generated method stub

		try {
			log.info("Removing msisdn:" + msisdn + " from bucketCache with threshold:" + threshold);
			cache.remove(msisdn);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}
	
	/**
	 * Remove MSISDN from file transaction cache list.
	 * 
	 * @param filename - entry key
	 * @param msisdn - contained in entry value
	 */
	public void removeMSISDNFromFileTxnCache(String filename, String msisdn){
		
		Set<String> msisdns = cache2.get(filename);
		if (msisdns == null)
			return;
		
		msisdns.remove(msisdn);
		addMsisdnTriggerAndFileDataToCache(filename, msisdns);
	}
	
	/**
	 * Manually remove MSISDN from time bound cache.
	 * 
	 * @param msisdn - entry key
	 */
	public void removeMSISDNFromTimeBoundCache(String msisdn){
		
		cache3.remove(msisdn);
	}
	
	/**
	 * Clear cache of all mapped entries.
	 * 
	 */
	public void removeAllTopupConfigurationFromCache() {
		// TODO Auto-generated method stub

		log.info("Removing all MSISDNs from watchlist");
		cache.clear();
	}

	/**
	 * Fetch top-up configuration from cache.
	 * 
	 * @param msisdn - unique subscriber MSISDN
	 * @return threshold - retrieved entry value or null if no entry exists with specified key
	 */
	public BigDecimal getToupConfigurationFromCache(String msisdn) {
		// TODO Auto-generated method stub

		try {
			return cache.get(msisdn);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	/**
	 * Confirm if MSISDN exists in cache for same filename key.
	 * If false, add MSISDN to cache entry list
	 * 
	 * @param filename - entry key
	 * @param msisdn - value to be found in entry value list
	 * @return MSISDN if found in cache, else return null
	 */
	@Lock(LockType.WRITE)
	public String getMSISDNFromTriggeredFileCache(String filename, String msisdn){
		
		Set<String> msisdns = cache2.get(filename);
		if (msisdns == null)
			msisdns = new HashSet<>();
		
		if (msisdns.stream().anyMatch(entry -> msisdn.equalsIgnoreCase(entry))){
			log.info("MSISDN:" + msisdn + " already triggered for file:" + filename);
			return msisdn;
		}
		
		msisdns.add(msisdn);
		addMsisdnTriggerAndFileDataToCache(filename, msisdns);
		
		return null;
	}
	
	/**
	 * Confirm if MSISDN is in cache.
	 * Add to cache if not found.
	 * 
	 * @param msisdn - entry key
	 * @return MSISDN if found in cache, else returns null
	 */
	@Lock(LockType.WRITE)
	public String getMSISDNFromTimeboundCache(String msisdn){
		
		String entry = cache3.get(msisdn);
		if (entry != null){
			log.info("MSISDN:" + msisdn + " already triggered within last:" + timeMonitor + " mins");
			return msisdn;
		}
		
		addMSISDNToTimeboundCache(msisdn);
		
		return null;
	}

	/**
	 * Put entry in cache. Refresh value if entry already exists.
	 * 
	 * @param filename - entry key
	 * @param msisdns - entry value
	 */
	@Lock(LockType.WRITE)
	private void addMsisdnTriggerAndFileDataToCache(String filename, Set<String> msisdns) {
		// TODO Auto-generated method stub
		
		cache2.put(filename, msisdns, filetriggerMonitor, TimeUnit.HOURS);
	}

	/**
	 * Refresh details of cache with passed arguments.
	 * 
	 * @param msisdn - entry key
	 * @param threshold - entry value
	 */
	@Lock(LockType.WRITE)
	public void updateTopupConfiguration(String msisdn, BigDecimal threshold) {
		// TODO Auto-generated method stub

		try {
			cache.replace(msisdn, threshold);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}

	/**
	 * Confirm if subscriber data exists in cache.
	 * 
	 * @param msisdn - entry key
	 * @return true if exists
	 */
	public boolean isSuscriberInBucketList(String msisdn) {
		// TODO Auto-generated method stub

		return cache.containsKey(msisdn);
	}

	/**
	 * Fetch list of all keys cached keys.
	 * 
	 * @return list of entries keys
	 */
	public List<String> getAllKeysFromCache() {
		// TODO Auto-generated method stub

		Set<String> keys = cache.keySet();
		if (keys != null)
			return keys.stream().collect(Collectors.toList());

		return new LinkedList<String>();
	}
	
}