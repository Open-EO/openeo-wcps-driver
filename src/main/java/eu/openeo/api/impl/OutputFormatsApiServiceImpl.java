package eu.openeo.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import eu.openeo.api.NotFoundException;
import eu.openeo.api.OutputFormatsApiService;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class OutputFormatsApiServiceImpl extends OutputFormatsApiService {
	
	Logger log = LogManager.getLogger();
	
    @Override
    public Response outputFormatsGet(SecurityContext securityContext) throws NotFoundException {
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("output_formats.json");
		JSONObject outputFormatsShell;
		try {
			outputFormatsShell = new JSONObject(IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
			return Response.ok(outputFormatsShell.toString(4), MediaType.APPLICATION_JSON).build();
		} catch (JSONException e) {
			log.error("Error parsing json: " + e.getMessage());
			return Response.serverError().entity("Error parsing json: " + e.getMessage()).build();
		} catch (IOException e) {
			log.error("Error reading json file: " + e.getMessage());
			return Response.serverError().entity("Error reading json file: " + e.getMessage()).build();
		}
    }
}
