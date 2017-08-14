package ng.autotopup.glo_etls.jaxrs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ng.autotopup.glo_etls.cruncher.CdrCruncher;
import ng.autotopup.glo_etls.tools.Utils;

@Path("/cdr")
public class FileService {
	
	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private Utils utils ;
	
	@Inject
	private CdrCruncher cruncher ;
	
	@GET
	@Path(value = "/crunch/test")
	public Response doTNPTest(){
		
		return Response.ok().entity("Test service accessed successfully").build();
	}
	
	/**
	 * Handle crunching of CDR file information received from TALEND jobs.
	 * Validate JSON pay-load.
	 * Extract file details and name from pay-load.
	 * 
	 * @param jsonstring JSON pay-load
	 * @return HTTP response 400 if pay-load is corrupt; HTTP response 500 is sent for any other error, else HTTP 200 OK
	 */
	@POST
	@Path(value = "/crunch")
	public Response doNewTNPFileCrunching(String jsonstring){
		
		if (!utils.validateParameters(jsonstring)){
			log.error("Bad request:" + jsonstring);
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		JsonObject jsonObject = new Gson().fromJson(jsonstring, JsonObject.class);
		if (!utils.validateParameters(jsonObject))
			return Response.serverError().build();
		
		String creationDate = "";
		try {
			creationDate = jsonObject.get("dateCreated").getAsString();
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			log.error("dateCreated attribute not in JSON payload");
		}
		
		try {
			String filedata = jsonObject.get("filedata").getAsString();
			String filename = jsonObject.get("filename").getAsString();
			cruncher.crunchCDRFile(filedata, filename, creationDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
			return Response.serverError().build();
		}
		
		return Response.ok().build();
	}

}