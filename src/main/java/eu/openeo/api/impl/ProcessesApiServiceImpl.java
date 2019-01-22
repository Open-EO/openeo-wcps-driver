package eu.openeo.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.openeo.api.NotFoundException;
import eu.openeo.api.ProcessesApiService;
import eu.openeo.dao.ItemsDeserializer;
import eu.openeo.model.Items;
import eu.openeo.model.ProcessDescription;
import eu.openeo.model.LinksDesc;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ProcessesApiServiceImpl extends ProcessesApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private Map<String, ProcessDescription> processes = null;
	private Map<String, LinksDesc> links = null;
	private ObjectMapper mapper = null;
	
	public ProcessesApiServiceImpl() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("processes.json");
		InputStream linkstream = classLoader.getResourceAsStream("links.json");
		this.mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("ItemsDeserializer", new Version(1, 0, 0, null, null, null));
		module.addDeserializer(Items.class, new ItemsDeserializer());
		mapper.registerModule(module);
		this.processes = new HashMap<String, ProcessDescription>();
		this.links = new HashMap<String, LinksDesc>();
		try {
			ProcessDescription[] processArray = this.mapper.readValue(stream, ProcessDescription[].class);
			LinksDesc[] linksArray = this.mapper.readValue(linkstream, LinksDesc[].class);
			
			for(int p = 0; p < linksArray.length; p++) {
				this.links.put(linksArray[p].getRel(), linksArray[p]);
				
				log.debug("Found and stored process: " + linksArray[p].getRel());
			}
			
			for(int p = 0; p < processArray.length; p++) {
				this.processes.put(processArray[p].getProcessId(), processArray[p]);
				
				log.debug("Found and stored process: " + processArray[p].getProcessId());
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
	public Response processesGet(String qname, SecurityContext securityContext) throws NotFoundException {
		JSONObject processes = new JSONObject();
		JSONArray processArray = new JSONArray();
		JSONArray linksArray = new JSONArray();
		
		for(String key : this.processes.keySet()){
			JSONObject process = new JSONObject();
						
			ProcessDescription processDesc = this.processes.get(key);
			process.put("name", processDesc.getProcessId());			
			process.put("summary", processDesc.getSummary());			
			process.put("description", processDesc.getDescription());			
			process.put("parameters", processDesc.getParameters());			
			process.put("min_parameters", processDesc.getMinParameters());
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
						
			LinksDesc linkDesc = this.links.get(key);
			
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

	@Override
	public Response processesOpensearchGet(String q, Integer start, Integer rows, SecurityContext securityContext)
			throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response processesOpensearchOptions(SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response processesOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().build();
	}

	@Override
	public Response processesProcessIdGet(String processId, SecurityContext securityContext) throws NotFoundException {
		ProcessDescription process = this.processes.get(processId);
		if(process != null) {
			try {
				return Response.ok(this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(process)).build();
			} catch (JsonProcessingException e) {
				log.error("Error parsing json: " + e.getMessage());
				return Response.serverError().entity("Error parsing json: " + e.getMessage()).build();
			}
		}else {
			return Response.status(404).entity(new String("A process with the specified identifier is not available.")).build();
		}
	}

	@Override
	public Response processesProcessIdOptions(String processId, SecurityContext securityContext)
			throws NotFoundException {
		return Response.ok().build();
	}
}