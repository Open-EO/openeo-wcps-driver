package eu.openeo.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.openeo.api.ApiResponseMessage;
import eu.openeo.api.CapabilitiesApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.model.OutputFormatBody;
import eu.openeo.model.ProcessDescription;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class CapabilitiesApiServiceImpl extends CapabilitiesApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private Map<String, OutputFormatBody> outputFormats = null;
	private ObjectMapper mapper = null;
	
	public CapabilitiesApiServiceImpl() {
//		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//		InputStream stream = classLoader.getResourceAsStream("output_formats.json");
		this.mapper = new ObjectMapper();
//		this.outputFormats = new HashMap<String, OutputFormatBody>();
//		JSONObject outputFormatObject = new JSONObject(stream);
//		Iterator<String> outputIter = outputFormatObject.keys();
//		while(outputIter.hasNext()) {
//			String format = outputIter.next();
//			try {
////				OutputFormatBody currentFormat = this.mapper.readValue(outputFormatObject.get(format).toString(), OutputFormatBody.class);
//			} catch (JsonParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JsonMappingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
	}
	
	
	@Override
	public Response capabilitiesGet(SecurityContext securityContext) throws NotFoundException {
		JSONArray endpointList = new JSONArray();
		endpointList.put(new String("/capabilities"));
		endpointList.put(new String("/capabilities/output_formats"));
		endpointList.put(new String("/data"));
		endpointList.put(new String("/data/{product_id}"));
		endpointList.put(new String("/execute"));
		endpointList.put(new String("/jobs"));
		endpointList.put(new String("/jobs/{job_id}"));
		endpointList.put(new String("/jobs/{job_id}/download"));
		endpointList.put(new String("/processes"));
		endpointList.put(new String("/processes/{process_id}"));
		return Response.ok(endpointList.toString(4), MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response capabilitiesOutputFormatsGet(SecurityContext securityContext) throws NotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("output_formats.json");
		JSONObject outputFormatsShell;
		try {
			outputFormatsShell = new JSONObject(IOUtils.toString(stream));
			return Response.ok(outputFormatsShell.toString(4), MediaType.APPLICATION_JSON).build();
		} catch (JSONException e) {
			log.error("Error parsing json: " + e.getMessage());
			return Response.serverError().entity("Error parsing json: " + e.getMessage()).build();
		} catch (IOException e) {
			log.error("Error reading json file: " + e.getMessage());
			return Response.serverError().entity("Error reading json file: " + e.getMessage()).build();
		}		
	}

	@Override
	public Response capabilitiesOutputFormatsOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().build();
	}

	@Override
	public Response capabilitiesServicesGet(SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response capabilitiesServicesOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().build();
	}
}
