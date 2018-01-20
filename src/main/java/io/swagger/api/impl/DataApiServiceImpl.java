package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.model.*;

import io.swagger.model.InlineResponse200;
import io.swagger.model.InlineResponse2001;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.backend.wcps.PropertiesHelper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class DataApiServiceImpl extends DataApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
    @Override
    public Response dataGet( String qname,  String qgeom,  String qstartdate,  String qenddate, SecurityContext securityContext) throws NotFoundException {
    	try {
			StringBuilder result = new StringBuilder();
			URL url;
			url = new URL(PropertiesHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=GetCapabilities");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			return Response.ok(new ApiResponseMessage(ApiResponseMessage.OK, result.toString()), MediaType.APPLICATION_XML).build();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR, "An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR, "An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage()))
					.build();
		}
    }
    @Override
    public Response dataOpensearchGet( String q,  Integer start,  Integer rows, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response dataProductIdGet(String productId, SecurityContext securityContext) throws NotFoundException {
    	StringBuilder result = new StringBuilder();
		URL url;
		try {
			url = new URL(
					PropertiesHelper.readProperties("wcps-endpoint") + "?&SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID="
							+ productId);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			return Response.ok(new ApiResponseMessage(ApiResponseMessage.OK, result.toString()), MediaType.APPLICATION_XML).build();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"An error occured while describing coverage from WCPS endpoint: " + e.getMessage())).build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"An error occured while describing coverage from WCPS endpoint: " + e.getMessage())).build();
		}
    }
}
