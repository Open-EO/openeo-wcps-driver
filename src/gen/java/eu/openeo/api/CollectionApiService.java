package eu.openeo.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public abstract class CollectionApiService {
	public abstract Response collectionGet(String qname, String qgeom, String qstartdate, String qenddate,
			SecurityContext securityContext) throws NotFoundException;

	public abstract Response collectionOpensearchGet(String q, Integer start, Integer rows, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response collectionOpensearchOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response collectionOptions(SecurityContext securityContext) throws NotFoundException;

	public abstract Response collectionProductIdGet(String productId, SecurityContext securityContext)
			throws NotFoundException;
	
	public abstract Response collectionProductIdCoveragesGet(String productId, SecurityContext securityContext)
			throws NotFoundException;
	
	public abstract Response collectionProductIdCoveragesCoverageIdGet(String productId, String coverageId, SecurityContext securityContext)
			throws NotFoundException;

	public abstract Response collectionProductIdOptions(String productId, SecurityContext securityContext)
			throws NotFoundException;
	
	public abstract Response coveragesCoverageIdGet(String coverageID, SecurityContext securityContext)
			throws NotFoundException;
	
	public abstract Response coveragesGet(String qname, String qgeom, String qstartdate, String qenddate, SecurityContext securityContext)
			throws NotFoundException;
}
