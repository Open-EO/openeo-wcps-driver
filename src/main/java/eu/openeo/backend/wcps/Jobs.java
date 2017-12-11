package eu.openeo.backend.wcps;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.UUID;

@Path("jobs")
public class Jobs {
	
	HashMap<String, String> lazyJobMap = new HashMap<String, String>();
	Logger log = Logger.getLogger(this.getClass());
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response getProcesses(String processGraphString) {
		JSONParser parser = new JSONParser();
		JSONObject processGraph;
		try {
			log.debug("Parsing process Graph \n" + processGraphString);
			processGraph = (JSONObject) parser.parse(processGraphString);
		} catch (ParseException e) {
			log.error(e.getMessage());
			log.error(e.getStackTrace());
			return Response.serverError()
			.entity("An error occured while parsing input json: " + e.getMessage())
			.build();
		}
		WCPSQueryFactory wcpsFactory =  new WCPSQueryFactory(processGraph);
		UUID jobID = UUID.randomUUID();
		log.debug("Graph successfully parsed and saved with ID: " + jobID);
		lazyJobMap.put(jobID.toString(), wcpsFactory.getWCPSString());
		return Response.ok("{\"job_id\" : \"" + jobID.toString() + "\"}").build();
	}

}
