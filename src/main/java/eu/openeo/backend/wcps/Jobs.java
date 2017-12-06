package eu.openeo.backend.wcps;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.UUID;

@Path("jobs")
public class Jobs {
	
	HashMap<String, String> lazyJobMap = new HashMap<String, String>();
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response getProcesses(JSONObject processGraph) {
		WCPSQueryFactory wcpsFactory =  new WCPSQueryFactory(processGraph);
		UUID jobID = UUID.randomUUID();
		lazyJobMap.put(jobID.toString(), wcpsFactory.getWCPSString());
		return Response.ok("{\"job_id\" : \"" + jobID.toString() + "\"}").build();
	}

}
