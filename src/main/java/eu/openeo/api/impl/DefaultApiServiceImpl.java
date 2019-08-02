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

    	JSONArray defaultMethods = new JSONArray();
		defaultMethods.put(new String("GET"));
		
		JSONArray wellKnownMethods = new JSONArray();
		wellKnownMethods.put(new String("GET"));
				
		JSONArray outputFormatsMethods = new JSONArray();
		outputFormatsMethods.put(new String("GET"));
		
		JSONArray collectionsMethods = new JSONArray();
		collectionsMethods.put(new String("GET"));
		
		JSONArray collectionIdMethods = new JSONArray();
		collectionIdMethods.put(new String("GET"));
		
		JSONArray resultMethods = new JSONArray();
		resultMethods.put(new String("GET"));
		resultMethods.put(new String("POST"));
		
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
		
		JSONObject defaultEndpoint = new JSONObject();
		defaultEndpoint.put("path", "/");
		defaultEndpoint.put("methods", defaultMethods);
		
		JSONObject wellKnownEndpoint = new JSONObject();
		wellKnownEndpoint.put("path", "/.well-known/openeo");
		wellKnownEndpoint.put("methods", wellKnownMethods);
		
		JSONObject outputFormatsEndpoint = new JSONObject();
		outputFormatsEndpoint.put("path", "/output_formats");
		outputFormatsEndpoint.put("methods", outputFormatsMethods);
		
		JSONObject collectionsEndpoint = new JSONObject();
		collectionsEndpoint.put("path", "/collections");
		collectionsEndpoint.put("methods", collectionsMethods);
		
		JSONObject collectionIdEndpoint = new JSONObject();
		collectionIdEndpoint.put("path", "/collections/{collection_id}");
		collectionIdEndpoint.put("methods", collectionIdMethods);
		
		JSONObject jobsEndpoint = new JSONObject();
		jobsEndpoint.put("path", "/jobs");
		jobsEndpoint.put("methods", jobsMethods);
		
		JSONObject resultsEndpoint = new JSONObject();
		resultsEndpoint.put("path", "/results");
		resultsEndpoint.put("methods", resultMethods);
		
		JSONObject jobsIdEndpoint = new JSONObject();
		jobsIdEndpoint.put("path", "/jobs/{job_id}");
		jobsIdEndpoint.put("methods", jobsIDMethods);		
		
		JSONObject jobsIdResultsEndpoint = new JSONObject();
		jobsIdResultsEndpoint.put("path", "/jobs/{job_id}/results");
		jobsIdResultsEndpoint.put("methods", jobsIDdownloadMethods);
		
		JSONObject processesEndpoint = new JSONObject();
		processesEndpoint.put("path", "/processes");
		processesEndpoint.put("methods", processesMethods);
		
		JSONArray endpointList = new JSONArray();
		endpointList.put(defaultEndpoint);
		endpointList.put(wellKnownEndpoint);
		endpointList.put(outputFormatsEndpoint);
		endpointList.put(collectionsEndpoint);
		endpointList.put(collectionIdEndpoint);
		endpointList.put(processesEndpoint);
		endpointList.put(jobsEndpoint);
		endpointList.put(resultsEndpoint);
		endpointList.put(jobsIdEndpoint);
		endpointList.put(jobsIdResultsEndpoint);
		
		JSONObject plans = new JSONObject();
		plans.put("name", "free");
		plans.put("description", "Currently the service provided is free of Charge");
		plans.put("url", "");
		plans.put("paid", false);
		
		JSONArray plan = new JSONArray();
		plan.put(plans);
		
		JSONObject link1 = new JSONObject();
		link1.put("href", "http://sao.eurac.edu");
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
		mainObj.put("backend_version", "0.4.0");
		mainObj.put("title", "Eurac Research - openEO - backend");
		mainObj.put("description", "The Eurac Research backend provides EO data available for processing using OGC WC(P)S");
		mainObj.put("endpoints", endpointList);
		//mainObj.put("billing", billing);
		mainObj.put("links", link);
		
		return Response.ok(mainObj.toString(4), MediaType.APPLICATION_JSON).build();
    }
}
