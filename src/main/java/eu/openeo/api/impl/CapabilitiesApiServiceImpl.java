package eu.openeo.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.openeo.api.CapabilitiesApiService;
import eu.openeo.api.NotFoundException;



@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class CapabilitiesApiServiceImpl extends CapabilitiesApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public Response capabilitiesGet(SecurityContext securityContext) throws NotFoundException {
		
		
		JSONArray getCapabilitiesMethods = new JSONArray();
		getCapabilitiesMethods.put(new String("GET"));
				
		JSONArray outputFormatsMethods = new JSONArray();
		outputFormatsMethods.put(new String("GET"));
		
		JSONArray dataMethods = new JSONArray();
		dataMethods.put(new String("GET"));
		
		JSONArray dataIDMethods = new JSONArray();
		dataIDMethods.put(new String("GET"));
		
		JSONArray executeMethods = new JSONArray();
		executeMethods.put(new String("GET"));
		
		JSONArray jobsMethods = new JSONArray();
		//jobsMethods.put(new String("GET"));
		jobsMethods.put(new String("POST"));
				
		JSONArray jobsIDMethods = new JSONArray();
		jobsIDMethods.put(new String("GET"));
		jobsIDMethods.put(new String("DELETE"));
		jobsIDMethods.put(new String("PATCH"));
		
		JSONArray jobsIDdownloadMethods = new JSONArray();
		jobsIDdownloadMethods.put(new String("GET"));
		
		JSONArray processesMethods = new JSONArray();
		processesMethods.put(new String("GET"));
		
		JSONArray processesIDMethods = new JSONArray();
		processesIDMethods.put(new String("GET"));
		
		JSONObject getCapabilities = new JSONObject();
		getCapabilities.put("path", "/");
		getCapabilities.put("methods", getCapabilitiesMethods);
		
		JSONObject outputFormats = new JSONObject();
		outputFormats.put("path", "/output_formats");
		outputFormats.put("methods", outputFormatsMethods);
		
		JSONObject data = new JSONObject();
		data.put("path", "/collections");
		data.put("methods", dataMethods);
		
		JSONObject dataID = new JSONObject();
		dataID.put("path", "/collections/{name}");
		dataID.put("methods", dataIDMethods);
		
		JSONObject execute = new JSONObject();
		execute.put("path", "/execute");
		execute.put("methods", executeMethods);
		
		JSONObject jobs = new JSONObject();
		jobs.put("path", "/jobs");
		jobs.put("methods", jobsMethods);
		
		JSONObject jobsID = new JSONObject();
		jobsID.put("path", "/jobs/{job_id}");
		jobsID.put("methods", jobsIDMethods);
		
		JSONObject jobsIDdownload = new JSONObject();
		jobsIDdownload.put("path", "/jobs/{job_id}/download");
		jobsIDdownload.put("methods", jobsIDdownloadMethods);
		
		JSONObject processes = new JSONObject();
		processes.put("path", "/processes");
		processes.put("methods", processesMethods);
		
		JSONObject processesID = new JSONObject();
		processesID.put("path", "/processes/{process_id}");
		processesID.put("methods", processesIDMethods);
		
		JSONArray endpointList = new JSONArray();
		endpointList.put(getCapabilities);
		endpointList.put(outputFormats);
		endpointList.put(data);
		endpointList.put(dataID);
		//endpointList.put(execute);
		//endpointList.put(jobs);
		//endpointList.put(jobsID);
		endpointList.put(processes);
		endpointList.put(processesID);
		
		
		
		JSONObject plans = new JSONObject();
		plans.put("name", "free");
		plans.put("description", "Currently the service provided is free of Charge");
		plans.put("url", "");
		
		JSONArray plan = new JSONArray();
		plan.put(plans);
		
		
		
		JSONObject billing = new JSONObject();
		billing.put("currency", "EUR");
		billing.put("plans", plan);
		
		
		
		JSONObject mainObj = new JSONObject();
<<<<<<< HEAD
		mainObj.put("version", "0.3.0");
		mainObj.put("endpoints", endpointList);
		mainObj.put("billing", billing);
=======
		mainObj.put("version", "0.3.1");
		mainObj.put("endpoints", endpointList);
		//mainObj.put("billing", billing);
>>>>>>> branch '0.3.0' of git@github.com:Open-EO/openeo-wcps-driver.git
		
		return Response.ok(mainObj.toString(4), MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response capabilitiesOutputFormatsGet(SecurityContext securityContext) throws NotFoundException {
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
