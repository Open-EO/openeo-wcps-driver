package eu.openeo.api;

import eu.openeo.model.*;
import eu.openeo.api.SubscriptionApiService;
import eu.openeo.api.factories.SubscriptionApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import java.math.BigDecimal;
import eu.openeo.model.Error;

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

@Path("/subscription")


@io.swagger.annotations.Api(description = "the subscription API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class SubscriptionApi  {
   private final SubscriptionApiService delegate;

   public SubscriptionApi(@Context ServletConfig servletContext) {
      SubscriptionApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("SubscriptionApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (SubscriptionApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = SubscriptionApiServiceFactory.getSubscriptionApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Subscribe to notifications", notes = "Get notified when execution updates occure, e.g. status updates for a job, recently updated EO collections or added/deleted resources like files and process graphs.  **Note:** There are two main differences for this request compared to regular API requests:  1. WebSockets use the `wss://` protocol instead of `https://` - `ws://` is not allowed! 2. The authorization token is sent in the web socket requests, not as HTTP header. See the `authorization` field in the openEO API for Subscriptions for more details.  After upgrading from HTTP to the WebSockets protocol, the [openEO API for Subscriptions](https://open-eo.github.io/openeo-api/v/0.4.0/apireference-subscriptions/) applies for requests and responses.", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "Bearer")
    }, tags={ "Batch Job Management","File Management","Process Graph Management","Secondary Services Management","EO Data Discovery", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 101, message = "Successful subscription to notifications returns in a protocol change to a web socket connection. The web socket connection is described in the referenced openEO API for Subscriptions documentation.", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "The request can't be fulfilled due to an error on client-side, i.e. the request is invalid. The client should not repeat the request without modifications. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6). This request MUST respond with HTTP status codes 401 if authorization is required or 403 if the authorization failed or access is forbidden in general to the authenticated user. HTTP status code 404 should be used if the value of a path parameter is invalid.", response = Error.class),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "The request can't be fulfilled due to an error at the back-end. The error is never the client’s fault and therefore it is reasonable for the client to retry the exact same request that triggered this response. The response body SHOULD contain a JSON error object. MUST be any HTTP status code specified in [RFC 7231](https://tools.ietf.org/html/rfc7231#section-6.6).", response = Error.class) })
    public Response subscriptionGet(@ApiParam(value = "WebSocket handshake request to indicate that an upgrade is requested." ,required=true, allowableValues="Upgrade")@HeaderParam("Connection") String connection
,@ApiParam(value = "WebSocket handshake request to indicate the protocol to upgrade to." ,required=true, allowableValues="websocket")@HeaderParam("Upgrade") String upgrade
,@ApiParam(value = "The Sec-WebSocket-Protocol header specifies the WebSocket protocols that you wish to use. The protocol to use is always `openeo-{api_version}`." ,required=true, allowableValues="openeo-v0.4")@HeaderParam("Sec-WebSocket-Protocol") String secWebSocketProtocol
,@ApiParam(value = "The WebSocket protocol version the client wishes to use when communicating with the server. This number should be the most recent version possible listed in the IANA WebSocket Version Number Registry." ,required=true, allowableValues="13")@HeaderParam("Sec-WebSocket-Version") BigDecimal secWebSocketVersion
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.subscriptionGet(connection,upgrade,secWebSocketProtocol,secWebSocketVersion,securityContext);
    }
}
