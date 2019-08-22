package eu.openeo.api.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Pattern;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import eu.openeo.api.NotFoundException;
import eu.openeo.api.ProcessGraphsApiService;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.model.BatchJobResponse;
import eu.openeo.model.StoredProcessGraphResponse;
import eu.openeo.model.UpdateStoredProcessGraphRequest;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ProcessGraphsApiServiceImpl extends ProcessGraphsApiService {
	
	
	Logger log = Logger.getLogger(this.getClass());
	private ConnectionSource connection = null;
	private Dao<StoredProcessGraphResponse, String> graphDao = null;
	
	public ProcessGraphsApiServiceImpl() {
		try {
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
			connection = new JdbcConnectionSource(dbURL);
			try {
				TableUtils.createTable(connection, StoredProcessGraphResponse.class);
			} catch (SQLException sqle) {
				log.debug("Create Table failed, probably exists already: " + sqle.getMessage());
			}
			graphDao = DaoManager.createDao(connection, StoredProcessGraphResponse.class);
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
		} catch (IOException ioe) {
			log.error("An error occured while reading properties file: " + ioe.getMessage());
		}
	}
	
    @Override
    public Response processGraphsGet(SecurityContext securityContext) throws NotFoundException {
    	List<StoredProcessGraphResponse> storedProcessGraphs = null;
		JSONObject graphSummary = new JSONObject();
		JSONArray graphs = new JSONArray();
		JSONArray links = new JSONArray();
		try {
			storedProcessGraphs = graphDao.queryForAll();
			for (StoredProcessGraphResponse storedBatchJob : storedProcessGraphs) {
				graphs.put(new JSONObject((String) storedBatchJob.toString()));
			}
			JSONObject linkSelf = new JSONObject();
			linkSelf.put("href", ConvenienceHelper.readProperties("openeo-endpoint") + "/process_graphs/");
			linkSelf.put("rel", "self");
			linkSelf.put("title", "Stored Process Graphs");
			links.put(linkSelf);
			graphSummary.put("process_graphs", graphs);
			graphSummary.put("links", links);
			return Response.ok(graphSummary.toString(4), MediaType.APPLICATION_JSON).build();
		} catch (SQLException e) {
			log.error("An error occured while performing an SQL-query: " + e.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + e.getMessage())
					.build();
		} catch (JSONException e) {
			log.error("An error occured while serializing graph to json: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity("An error occured while serializing graph to json: " + e.getMessage())
					.build();
		} catch (IOException e) {
			log.error("An error occured while accessing properties file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity("An error occured while accessing properties file: " + e.getMessage())
					.build();
		}
    }
    
    @Override
    public Response processGraphsPost(StoredProcessGraphResponse storeProcessGraphRequest, SecurityContext securityContext) throws NotFoundException {
    	UUID jobID = UUID.randomUUID();
    	storeProcessGraphRequest.setId(jobID.toString());
		try {
			graphDao.create(storeProcessGraphRequest);
			log.debug("graph saved to database: " + storeProcessGraphRequest.getId());
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			log.debug("Java object to string looks like this:");
			log.debug(storeProcessGraphRequest.toString());
			log.debug("Serialized json looks like this:");
			log.debug(mapper.writeValueAsString(storeProcessGraphRequest));
			return Response.status(201).entity(mapper.writeValueAsString(storeProcessGraphRequest))
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
					.header("OpenEO-Identifier", storeProcessGraphRequest.getId()).build();
		} catch (JsonProcessingException e) {
			log.error("An error occured while serializing graph to json: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity("An error occured while serializing graph to json: " + e.getMessage())
					.build();
		}
    }
    
    @Override
    public Response processGraphsProcessGraphIdDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String processGraphId, SecurityContext securityContext) throws NotFoundException {
    	StoredProcessGraphResponse storedProcessGraph = null;
		try {
			storedProcessGraph = graphDao.queryForId(processGraphId);
			if (storedProcessGraph == null) {
				return Response.status(404).entity(new String("A process graph with the specified identifier is not available."))
						.build();
			}
			graphDao.deleteById(processGraphId);
			log.debug("The following process graph was deleted: \n" + storedProcessGraph.toString());
			return Response.status(204).entity("The process graph has been successfully deleted.")
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
					.header("OpenEO-Identifier", storedProcessGraph.getId()).build();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
    }
    
    @Override
    public Response processGraphsProcessGraphIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String processGraphId, SecurityContext securityContext) throws NotFoundException {
    	StoredProcessGraphResponse storedProcessGraph = null;
		try {
			storedProcessGraph = graphDao.queryForId(processGraphId);
			if (storedProcessGraph == null) {
				return Response.status(404).entity(new String("A process graph with the specified identifier is not available."))
						.build();
			}
			log.debug("The following process graph was retrieved: \n" + storedProcessGraph.toString());
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			log.debug("Java object to string looks like this:");
			log.debug(storedProcessGraph.toString());
			log.debug("Serialized json looks like this:");
			log.debug(mapper.writeValueAsString(storedProcessGraph));
			return Response.status(201).entity(storedProcessGraph.toString())
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
					.header("OpenEO-Identifier", storedProcessGraph.getId()).build();
		} catch (JsonProcessingException e) {
			log.error("An error occured while serializing job to json: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity("An error occured while serializing job to json: " + e.getMessage())
					.build();
		}
    }
    @Override
    public Response processGraphsProcessGraphIdPatch( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String processGraphId, UpdateStoredProcessGraphRequest updateStoredProcessGraphRequest, SecurityContext securityContext) throws NotFoundException {
    	StoredProcessGraphResponse storedProcessGraph = null;
		try {
			storedProcessGraph = graphDao.queryForId(processGraphId);
			if (storedProcessGraph == null) {
				return Response.status(404).entity(new String("A process graph with the specified identifier is not available."))
						.build();
			}
			log.debug("The following process graph was retrieved: \n" + storedProcessGraph.toString());
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		if (updateStoredProcessGraphRequest.getTitle() != null)
			storedProcessGraph.setTitle(updateStoredProcessGraphRequest.getTitle());
		if (updateStoredProcessGraphRequest.getDescription() != null)
			storedProcessGraph.setDescription(updateStoredProcessGraphRequest.getDescription());
		if (updateStoredProcessGraphRequest.getProcessGraph() != null)
			storedProcessGraph.setProcessGraph(updateStoredProcessGraphRequest.getProcessGraph());
		try {
			graphDao.update(storedProcessGraph);
			log.debug("process graph updated in database: " + storedProcessGraph.getId());
			return Response.status(204).entity("Changes to the process graph applied successfully.")
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
					.header("OpenEO-Identifier", storedProcessGraph.getId()).build();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
    }
}
