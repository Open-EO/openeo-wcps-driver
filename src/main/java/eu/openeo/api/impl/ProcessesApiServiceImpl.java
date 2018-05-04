package eu.openeo.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.openeo.api.NotFoundException;
import eu.openeo.api.ProcessesApiService;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.model.ProcessDescription;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ProcessesApiServiceImpl extends ProcessesApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private Map<String, ProcessDescription> processes = null;
	private ObjectMapper mapper = null;
	
	private SAXBuilder builder = null;
	
	public ProcessesApiServiceImpl() {
		this.builder = new SAXBuilder();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("processes.json");
		this.mapper = new ObjectMapper();
		this.processes = new HashMap<String, ProcessDescription>();
		try {
			
			ProcessDescription[] processArray = this.mapper.readValue(stream, ProcessDescription[].class);
			for(int p = 0; p < processArray.length; p++) {
				this.processes.put(processArray[p].getProcessId(), processArray[p]);
				log.debug("Found and stored process: " + processArray[p].getProcessId());
			}
			URL url;
			url = new URL( ConvenienceHelper.readProperties("wps-endpoint") + 
					"?service=wps&version=1.0.0&request=GetCapabilities");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			String userpass = ConvenienceHelper.readProperties("wps-user") + ":" + ConvenienceHelper.readProperties("wps-password");
			String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
			conn.setRequestProperty ("Authorization", basicAuth);
			Document capabilititesDoc = (Document) this.builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace owsNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("ows")) {
					owsNS = current;
				}
			}
			log.debug("root node info: " + rootNode.getName());		
			List<Element> processList = rootNode.getChildren("ProcessOfferings", defaultNS).get(0).getChildren("Process", defaultNS);
			log.debug("number of processes found: " + processList.size());
			for(int c = 0; c < processList.size(); c++) {
				Element process = processList.get(c);
				log.debug("root node info: " + process.getName() + ":" + process.getChildText("Identifier", owsNS));
				JSONObject processObject = new JSONObject();
				processObject.put("process_id", process.getChildText("Identifier", owsNS));
				processObject.put("description", process.getChildText("Abstract", owsNS));
				ProcessDescription processDescription = this.mapper.readValue(processObject.toString(), ProcessDescription.class);
				this.processes.put(processDescription.getProcessId(), processDescription);
			}
		} catch (JsonParseException e) {
			log.error("Error parsing json: " + e.getMessage());
		} catch (JsonMappingException e) {
			log.error("Error mapping json to java: " + e.getMessage());
		} catch (IOException e) {
			log.error("Error reading json file: " + e.getMessage());
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public Response processesGet(String qname, SecurityContext securityContext) throws NotFoundException {
		JSONArray processArray = new JSONArray();		
		for(String key : this.processes.keySet()){
			JSONObject process = new JSONObject();
			ProcessDescription processDesc = this.processes.get(key);
			process.put("process_id", processDesc.getProcessId());
			process.put("description", processDesc.getDescription());
			processArray.put(process);
		}
		return Response.ok(processArray.toString(4), MediaType.APPLICATION_JSON).build();
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
				return Response.ok(this.mapper.writeValueAsString(process)).build();
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
