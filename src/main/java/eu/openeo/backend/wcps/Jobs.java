package eu.openeo.backend.wcps;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

@Path("jobs")
public class Jobs {
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response getProcesses(JSONObject processGraph) {
		WCPSQueryFactory wcpsFactory =  new WCPSQueryFactory(processGraph);		
		return Response.ok(wcpsFactory.getWCPSString()).build();
	}

}
