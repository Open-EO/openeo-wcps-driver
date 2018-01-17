package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.CapabilitiesApiService;
import io.swagger.api.factories.CapabilitiesApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;


import java.util.Map;
import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;

@Path("/capabilities")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the capabilities API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class CapabilitiesApi  {
   private final CapabilitiesApiService delegate;

   public CapabilitiesApi(@Context ServletConfig servletContext) {
      CapabilitiesApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("CapabilitiesApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (CapabilitiesApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = CapabilitiesApiServiceFactory.getCapabilitiesApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Returns the capabilities, i.e., which OpenEO API features are supported  by the back-end.", notes = "The request will ask the back-end which features of the OpenEO API are supported and return a simple JSON description with available endpoints.", response = Void.class, tags={ "API Information", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "An array of implemented API endpoints", response = Void.class) })
    public Response capabilitiesGet(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.capabilitiesGet(securityContext);
    }
}
