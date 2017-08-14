package ng.autotopup.glo_etls.jaxrs;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sf.vas.utils.crypto.CryptoUtil;
import com.sf.vas.utils.exception.VasException;

import ng.autotopup.glo_etls.enums.GeneralSettings;
import ng.autotopup.glo_etls.tools.PropertiesManager;
import ng.autotopup.glo_etls.tools.Utils;

@Stateless
public class TopUpClient {
	
	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private Utils utils ;
	
	@Inject
	private CryptoUtil cryptoUtil ;
	
	@Inject
	private PropertiesManager props ;
	
	/**
	 * Maintain initialization of re-usable objects
	 */
	@PostConstruct
	public void init(){
		initCryptoUtil();
	}
	
	/**
	 * Initialize {@link CryptoUtil} with required authentication file on server
	 */
	private void initCryptoUtil(){
		
		try {
			File file = new File(System.getProperty("jboss.home.dir") + props.getProperty("tp-public-key", GeneralSettings.TPS_KEY.getValue()) + "/vtu.pub");
			if (!file.exists())
				log.error("File:" + file.toPath().toString() + " doesn't exist");
			
			cryptoUtil.init(null, Files.newInputStream(file.toPath()));
		} catch (IOException | VasException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}

	/**
	 * Forwards triggered MSISDN, file name and security token in header of POST request to TopUp service.
	 * 
	 * @param msisdn subscriber MSISDN for top-up
	 * @param filename CDR file name for audit
	 * @param balance subscribers recorded balance post transaction
	 * @return HTTP response 200 if invocation is successful, otherwise returns HTTP response 500
	 */
	public ResponseBuilder doTopupInvocation(String msisdn, String filename, BigDecimal balance) {
		// TODO Auto-generated method stub
		
		Client client = null;
		String jsonstring = null;
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("msisdn", msisdn);
		jsonObject.addProperty("fileName", filename);
		jsonObject.addProperty("threshold", balance);
		
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Africa/Lagos"));
		
		try {
			jsonObject.addProperty("networkCarrier", props.getProperty("home-network", "GLO-NG"));
			
			if (!cryptoUtil.isEncryptionKeyInitialized())
				initCryptoUtil();
			
			client = ClientBuilder.newClient();
			jsonstring = client.target(props.getProperty("tps-endpoint", GeneralSettings.TPS_EDP.getValue())).path(props.getProperty("tps-path", GeneralSettings.TPS_PATH.getValue()))
			.request(MediaType.APPLICATION_JSON)
			.header("SHK", cryptoUtil.encrypt(String.valueOf(zonedDateTime.toInstant().toEpochMilli())))
			.post(Entity.entity(new Gson().toJson(jsonObject), MediaType.APPLICATION_JSON), String.class);
		} catch (WebApplicationException e){
			utils.writeTransaction(new StringBuilder(msisdn).append(",").append(balance).append(",").append(filename).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
			utils.writeTransaction(new StringBuilder(msisdn).append(",").append(balance).append(",").append(filename).toString());
		} finally {
			if (utils.validateParameters(client)) client.close();
		}
		
		if (!utils.validateParameters(jsonstring))
			return Response.serverError();
		
		JsonObject json = new Gson().fromJson(jsonstring, JsonObject.class);
		if (!utils.validateParameters(jsonObject))
			return Response.serverError();
		
		try {
			String code = json.get("responseCode").getAsString();
			if (code != "00")
				return Response.serverError();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
			return Response.serverError();
		}
		
		return Response.ok();
	}

}