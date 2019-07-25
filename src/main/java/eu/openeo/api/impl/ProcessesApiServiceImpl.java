package eu.openeo.api.impl;

import eu.openeo.api.*;
import eu.openeo.model.*;

import eu.openeo.model.Error;
import eu.openeo.model.ProcessesResponse;

import java.util.List;
import eu.openeo.api.NotFoundException;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;
import eu.openeo.model.Process;
import eu.openeo.model.Link;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ProcessesApiServiceImpl extends ProcessesApiService {
    
Logger log = Logger.getLogger(this.getClass());
	
	private Map<String, Process> processes = null;
	private Map<String, Link> links = null;
	private ObjectMapper mapper = null;
	
	public ProcessesApiServiceImpl() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("processes.json");
		InputStream linkstream = classLoader.getResourceAsStream("links.json");
		this.mapper = new ObjectMapper();
		
		
		this.processes = new HashMap<String, Process>();
		this.links = new HashMap<String, Link>();
		try {
			Process[] processArray = this.mapper.readValue(stream, Process[].class);
			Link[] linksArray = this.mapper.readValue(linkstream, Link[].class);
			
			for(int p = 0; p < linksArray.length; p++) {
				this.links.put(linksArray[p].getRel(), linksArray[p]);
				
				log.debug("Found and stored process: " + linksArray[p].getRel());
			}
			
			for(int p = 0; p < processArray.length; p++) {
				this.processes.put(processArray[p].getId(), processArray[p]);
				
				log.debug("Found and stored process: " + processArray[p].getId());
			}
		} catch (JsonParseException e) {
			log.error("Error parsing json: " + e.getMessage());
		} catch (JsonMappingException e) {
			log.error("Error mapping json to java: " + e.getMessage());
		} catch (IOException e) {
			log.error("Error reading json file: " + e.getMessage());
		}
		
	}
	
	@Override
    public Response processesGet(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
    	
		JSONObject processes = new JSONObject();
		JSONArray processArray = new JSONArray();
		JSONArray linksArray = new JSONArray();
		
		for(String key : this.processes.keySet()){
			JSONObject process = new JSONObject();
						
			Process processDesc = this.processes.get(key);
			process.put("id", processDesc.getId());			
			process.put("summary", processDesc.getSummary());			
			process.put("description", processDesc.getDescription());			
			process.put("parameters", processDesc.getParameters());			
			
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap.put("description", processDesc.getReturns().getDescription());
			returnMap.put("schema", processDesc.getReturns().getSchema());
			
			process.put("returns", returnMap);
			process.put("exceptions", processDesc.getExceptions());
			processArray.put(process);
			processes.put("processes", processArray);
			
		}
		
		
		for(String key : this.links.keySet()){
			JSONObject links = new JSONObject();
						
			Link linkDesc = this.links.get(key);
			
			links.put("rel", linkDesc.getRel());
			links.put("href", linkDesc.getHref());
			links.put("type", linkDesc.getType());
			links.put("title", linkDesc.getTitle());
			linksArray.put(links);
			processes.put("links", linksArray);
			
		}
		
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			log.debug("from object mapper: " + mapper.writeValueAsString(this.processes));
		} catch (JsonProcessingException e) {
			log.error("did not manage to serialize process map to json: " + e.getMessage());
		}
    	
		return Response.ok(processes.toString(4), MediaType.APPLICATION_JSON).build();
    }
}
