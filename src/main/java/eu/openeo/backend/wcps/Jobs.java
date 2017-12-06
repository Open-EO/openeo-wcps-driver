package eu.openeo.backend.wcps;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.UUID;

@Path("jobs")
public class Jobs {
	
	HashMap<String, String> lazyJobMap = new HashMap<String, String>();
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response getProcesses(String processGraphString) {
		JSONParser parser = new JSONParser();
		JSONObject processGraph;
		try {
			processGraph = (JSONObject) parser.parse(processGraphString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
			.entity("An error occured while parsing input json: " + e.getMessage())
			.build();
		}
		WCPSQueryFactory wcpsFactory =  new WCPSQueryFactory(processGraph);
		UUID jobID = UUID.randomUUID();
		lazyJobMap.put(jobID.toString(), wcpsFactory.getWCPSString());
		return Response.ok("{\"job_id\" : \"" + jobID.toString() + "\"}").build();
	}

}
