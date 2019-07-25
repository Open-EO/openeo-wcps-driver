package eu.openeo.api.impl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.openeo.api.DefaultApiService;
import eu.openeo.api.NotFoundException;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class DefaultApiServiceImpl extends DefaultApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
    @Override
    public Response rootGet(SecurityContext securityContext) throws NotFoundException {

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
		
		JSONArray previewMethods = new JSONArray();
		previewMethods.put(new String("POST"));
				
		JSONArray jobsIDMethods = new JSONArray();
		jobsIDMethods.put(new String("GET"));
		//jobsIDMethods.put(new String("DELETE"));
		//jobsIDMethods.put(new String("PATCH"));
		
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
		dataID.put("path", "/collections/{collection_id}");
		dataID.put("methods", dataIDMethods);
		
		JSONObject jobs = new JSONObject();
		jobs.put("path", "/jobs");
		jobs.put("methods", jobsMethods);
		
		JSONObject results = new JSONObject();
		results.put("path", "/preview");
		results.put("methods", previewMethods);
		
		JSONObject jobsID = new JSONObject();
		jobsID.put("path", "/jobs/{job_id}");
		jobsID.put("methods", jobsIDMethods);
		
		
		
		JSONObject jobsIDdownload = new JSONObject();
		jobsIDdownload.put("path", "/jobs/{job_id}/results");
		jobsIDdownload.put("methods", jobsIDdownloadMethods);
		
		JSONObject processes = new JSONObject();
		processes.put("path", "/processes");
		processes.put("methods", processesMethods);
		
		JSONArray endpointList = new JSONArray();
		endpointList.put(getCapabilities);
		endpointList.put(outputFormats);
		endpointList.put(data);
		endpointList.put(dataID);
		endpointList.put(jobs);
		endpointList.put(results);
		endpointList.put(jobsID);
		endpointList.put(jobsIDdownload);
		endpointList.put(processes);
		
		
		
		JSONObject plans = new JSONObject();
		plans.put("name", "free");
		plans.put("description", "Currently the service provided is free of Charge");
		plans.put("url", "");
		plans.put("paid", false);
		
		JSONArray plan = new JSONArray();
		plan.put(plans);
		
		JSONObject link1 = new JSONObject();
		link1.put("href", "http://saocompute.eurac.edu/rasdaman/ows");
		link1.put("rel", "Sentinel Alpine Observatory");
		link1.put("type", "text/html");
		link1.put("title", "Homepage of the service provider");
		
		JSONArray link = new JSONArray();
		link.put(link1);
		
		
		JSONObject billing = new JSONObject();
		billing.put("currency", "EUR");
		billing.put("default_plan", "free");
		billing.put("plans", plan);
		
		
		
		JSONObject mainObj = new JSONObject();
		mainObj.put("api_version", "0.4.2");
		mainObj.put("backend_version", "0.4.2");
		mainObj.put("title", "EURAC Backend");
		mainObj.put("description", "EURAC backend provides Sentinel 2 Data available for processing through WCPS powered by Rasdaman");
		mainObj.put("endpoints", endpointList);
		//mainObj.put("billing", billing);
		mainObj.put("links", link);
		
		return Response.ok(mainObj.toString(4), MediaType.APPLICATION_JSON).build();
    }
}
