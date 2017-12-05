package eu.openeo.backend.wcps;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/capabilities")
public class Capabilities {
	
	
	@GET
	@Produces("application/json")
	public String getCapabilities() {
		return "body:{version:{0.0.1}}";
	}
	
	@Path("/version")
	@GET
	@Produces("application/json")
	public String getVersion() {
		return "body:{version:{0.0.1}}";
	}

}
