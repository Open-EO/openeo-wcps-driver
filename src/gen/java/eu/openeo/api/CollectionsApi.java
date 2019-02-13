package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.CollectionsApiService;
import eu.openeo.api.factories.CollectionsApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import eu.openeo.model.Error;
import eu.openeo.model.InlineResponse2002;
import eu.openeo.model.InlineResponse2003;

import java.util.Map;
import java.util.List;
import eu.openeo.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path("/collections")


@io.swagger.annotations.Api(description = "the collections API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class CollectionsApi  {
   private final CollectionsApiService delegate;

   public CollectionsApi(@Context ServletConfig servletContext) {
      CollectionsApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("CollectionsApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (CollectionsApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = CollectionsApiServiceFactory.getCollectionsApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/{collection_id}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Full metadata for a specific dataset", notes = "Lists all information about a specific collection specified by the identifier `collection_id`.  This endpoint is compatible with STAC 0.6.2 and all features and extensions of [STAC](https://github.com/radiantearth/stac-spec) can be used here.  **Note:** openEO strives for compatibility with [STAC](https://github.com/radiantearth/stac-spec) and [OGC WFS 3](https://github.com/opengeospatial/WFS_FES) as far as possible. Both standards, as well as openEO, are still under development. Therefore, it is likely that further changes and adjustments will be made in the future.  More information on [data discovery](https://open-eo.github.io/openeo-api/v/0.4.0/collections/), including common relation types for links, are available in the documentation.", response = InlineResponse2003.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "EO Data Discovery", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "JSON object with metadata about the collection.", response = InlineResponse2003.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response collectionsCollectionIdGet(@ApiParam(value = "Collection identifier",required=true, defaultValue="null") @PathParam("collection_id") String collectionId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.collectionsCollectionIdGet(collectionId,securityContext);
    }
    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Basic metadata for all datasets.", notes = "Lists available collections with basic information. To retrieve domain specific information (e.g. SAR) request all information for a specific collection using `GET /collections/{collection_id}`.  This endpoint is compatible with STAC 0.6.2 and all features and extensions of [STAC](https://github.com/radiantearth/stac-spec) can be used here.  **Note:** openEO strives for compatibility with [STAC](https://github.com/radiantearth/stac-spec) and [OGC WFS 3](https://github.com/opengeospatial/WFS_FES) as far as possible. Both standards, as well as openEO, are still under development. Therefore, it is likely that further changes and adjustments will be made in the future.  More information on [data discovery](https://open-eo.github.io/openeo-api/v/0.4.0/collections/), including common relation types for links, are available in the documentation.", response = InlineResponse2002.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "EO Data Discovery", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "A list of collections (basic information only) and related links.", response = InlineResponse2002.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response collectionsGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.collectionsGet(securityContext);
    }
}
