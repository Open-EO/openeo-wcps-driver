package eu.openeo.backend.wcps;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

@Path("/processes")
public class Processes {
	
	@GET
	@Produces("application/json")
	public Response getProcesses() {
		return Response.ok().build();
	}
	
	@Path("{process}")
	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postProcess(JSONObject processGraph) {
		
		WCPSQueryFactory wcpsFactory =  new WCPSQueryFactory(processGraph);
		
		return Response.ok(wcpsFactory.getWCPSString()).build();
	}

}
