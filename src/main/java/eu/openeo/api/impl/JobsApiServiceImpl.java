package eu.openeo.api.impl;

import eu.openeo.api.*;
import eu.openeo.model.*;

import eu.openeo.model.BatchJobEstimateResponse;
import eu.openeo.model.BatchJobListResponse;
import eu.openeo.model.BatchJobResponse;
import eu.openeo.model.BatchJobResultsResponse;
import eu.openeo.model.Error;
import eu.openeo.model.JobError;
import eu.openeo.model.StoreBatchJobRequest;
import eu.openeo.model.UpdateBatchJobRequest;
import eu.openeo.model.Status;

import java.util.Date;
import java.util.UUID;

import java.util.List;
import eu.openeo.api.NotFoundException;
import org.apache.log4j.Logger;

import java.io.InputStream;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.backend.wcps.WCPSQueryFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

import eu.openeo.dao.JSONObjectSerializer;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class JobsApiServiceImpl extends JobsApiService {
	
Logger log = Logger.getLogger(this.getClass());
	
	private ConnectionSource connection = null;
	private Dao<BatchJobResponse,String> jobDao = null;
	private String wcpsEndpoint = null;
	
	private ObjectMapper mapper = null;
	
	public JobsApiServiceImpl() {
		try {
			wcpsEndpoint = ConvenienceHelper.readProperties("wcps-endpoint");
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
			connection =  new JdbcConnectionSource(dbURL);
			try {
				TableUtils.createTable(connection, BatchJobResponse.class);
			}catch(SQLException sqle) {
				log.debug("Create Table failed, probably exists already: " + sqle.getMessage());
			}
			jobDao = DaoManager.createDao(connection, BatchJobResponse.class);
			 mapper = new ObjectMapper();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
		} catch (IOException ioe) {
			log.error("An error occured while reading properties file: " + ioe.getMessage());
		}
	}
	
    @Override
    public Response jobsGet(SecurityContext securityContext) throws NotFoundException {
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
    	BatchJobResponse storedBatchJob = null;
		try {
			storedBatchJob = jobDao.queryForId(jobId);
			if(storedBatchJob == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available.")).build();
			}
			jobDao.deleteById(jobId);
			log.debug("The following job was deleted: \n" + storedBatchJob.toString());
			return Response.status(204).entity("The job has been successfully deleted.").header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").header("OpenEO-Identifier", storedBatchJob.getId()).build();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage()).build();
		}
    }
    @Override
    public Response jobsJobIdEstimateGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        BatchJobResponse storedBatchJob = null;
		try {
			storedBatchJob = jobDao.queryForId(jobId);
			if(storedBatchJob == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available.")).build();
			}
			log.debug("The following job was retrieved: \n" + storedBatchJob.toString());			
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
//			ObjectMapper mapper = new ObjectMapper();
//			SimpleModule module = new SimpleModule("JSONObjectSerializer", new Version(1, 0, 0, null, null, null));
//			module.addSerializer(JSONObject.class, new JSONObjectSerializer());
//			mapper.registerModule(module);
//			return Response.status(201).entity(mapper.writeValueAsString(job)).header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
			ObjectMapper mapper = new ObjectMapper();
			/*SimpleModule module = new SimpleModule("JSONObjectSerializer", new Version(1, 0, 0, null, null, null));
			module.addSerializer(JSONObject.class, new JSONObjectSerializer());
			mapper.registerModule(module);*/
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			log.debug("Java object to string looks like this:");
			log.debug(storedBatchJob.toString());
			log.debug("Serialized json looks like this:");
			log.debug(mapper.writeValueAsString(storedBatchJob));			
			return Response.status(201).entity(mapper.writeValueAsString(storedBatchJob)).header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").header("OpenEO-Identifier", storedBatchJob.getId()).build();
		} catch (JsonProcessingException e) {
			log.error("An error occured while serializing job to json: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity("An error occured while serializing job to json: " + e.getMessage())
					.build();
		}

    }
    @Override
    public Response jobsJobIdPatch( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, UpdateBatchJobRequest updateBatchJobRequest, SecurityContext securityContext) throws NotFoundException {
    	BatchJobResponse storedBatchJob = null;
		try {
			storedBatchJob = jobDao.queryForId(jobId);
			if(storedBatchJob == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available.")).build();
			}
			log.debug("The following job was retrieved: \n" + storedBatchJob.toString());			
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		if(updateBatchJobRequest.getTitle() != null) storedBatchJob.setTitle(updateBatchJobRequest.getTitle());
		if(updateBatchJobRequest.getDescription() != null) storedBatchJob.setDescription(updateBatchJobRequest.getDescription());
		if(updateBatchJobRequest.getProcessGraph() != null) storedBatchJob.setProcessGraph(updateBatchJobRequest.getProcessGraph());
		if(updateBatchJobRequest.getPlan() != null) storedBatchJob.setPlan(updateBatchJobRequest.getPlan());
		if(updateBatchJobRequest.getBudget() != null) storedBatchJob.setBudget(updateBatchJobRequest.getBudget());
		try {
			jobDao.update(storedBatchJob);
			log.debug("job updated in database: " + storedBatchJob.getId());
			return Response.status(204).entity("Changes to the job applied successfully.")
					                   .header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
					                   .header("OpenEO-Identifier", storedBatchJob.getId())
					                   .build();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}		
    }
    @Override
    public Response jobsJobIdResultsDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response jobsJobIdResultsGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, String format, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
    	
    	BatchJobResponse job = null;
		WCPSQueryFactory wcpsFactory = null;
		String outputFormat = "JSON";
		try {
			job = jobDao.queryForId(jobId);
			if(job == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available.")).build();
			}
			log.debug("The following job was retrieved: \n" + job.toString());
			JSONObject processGraphJSON;			
			if(format != null) {
				outputFormat = format;
			}else {
				try {
					outputFormat = (String)(((JSONObject) job.getOutput()).get(new String("format")));
					
					outputFormat = outputFormat.toUpperCase();
					
					if (outputFormat.equals("NETCDF"))
					{
						outputFormat="netCDF";
					}
					if (outputFormat.equals("GTIFF"))
					{
						outputFormat="GTiff";
					}
					log.info("assigning output type: " + outputFormat);
				}catch(Exception e) {
					log.error("An error occured while parsing output type: " + e.getMessage());
					log.info("assigning standard output type: json");
				}
			}
			processGraphJSON = (JSONObject) job.getProcessGraph();
			wcpsFactory = new WCPSQueryFactory(processGraphJSON, outputFormat);
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		URL url;
		try {
			job.setUpdated(new Date());
			jobDao.update(job);

			url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1"
					+ "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
			
			JSONObject linkProcessGraph = new JSONObject();
			linkProcessGraph.put("job_id", job.getId());
//			linkProcessGraph.put("title", "NDVI Test"); //Nullable
//			linkProcessGraph.put("description","Deriving minimum ndvi measurements over pixel time series of sentinel 2 imagery."); //Nullable
			linkProcessGraph.put("updated", job.getUpdated());
			
			JSONObject link = new JSONObject();
			link.put("href", url);
			link.put("type", ConvenienceHelper.getMimeTypeFromOutput(outputFormat));
			
			linkProcessGraph.put("links", link);
			
//			byte[] response = IOUtils.toByteArray(linkProcessGraph.toString().getBytes("UTF-8"));			
			
			return Response.ok(linkProcessGraph.toString().getBytes("UTF-8"), "application/json").header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
		} catch (MalformedURLException e) {
			log.error("An error occured when creating URL from job query: " + e.getMessage());
			return Response.serverError().entity("An error occured when creating URL from job query: " + e.getMessage())
					.build();
		} catch (IOException e) {
			log.error("An error occured when retrieving query result from WCPS endpoint: " + e.getMessage());
			return Response.serverError()
					.entity("An error occured when retrieving query result from WCPS endpoint: " + e.getMessage())
					.build();
		} catch (SQLException e) {
			log.error("An error occured while performing an SQL-query: " + e.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + e.getMessage())
					.build();
		}
    	
        
    }
    @Override
    public Response jobsJobIdResultsPost( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
    	BatchJobResponse job = null;
		WCPSQueryFactory wcpsFactory = null;
		String outputFormat = "JSON";
		try {
			job = jobDao.queryForId(jobId);
			if(job == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available.")).build();
			}
			log.debug("The following job was retrieved: \n" + job.toString());
			JSONObject processGraphJSON;			
			try {
				outputFormat = (String)(((JSONObject) job.getOutput()).get(new String("format")));
				
				outputFormat = outputFormat.toUpperCase();
				
				if (outputFormat.equals("NETCDF"))
				{
					outputFormat="netCDF";
				}
				if (outputFormat.equals("GTIFF"))
				{
					outputFormat="GTiff";
				}
			}catch(Exception e) {
				log.error("An error occured while parsing output type: " + e.getMessage());
				log.info("assigning standard output type: json");
			}
			processGraphJSON = (JSONObject) job.getProcessGraph();
			wcpsFactory = new WCPSQueryFactory(processGraphJSON, outputFormat);
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			job.setStatus(Status.QUEUED);
			job.setUpdated(new Date());
			jobDao.update(job);
			//TODO add job to execute queue
			return Response.status(202).entity(new String("The creation of the resource has been queued successfully.")).header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
			/*
			job.setStatus(JobStatus.RUNNING);
			job.setUpdated(new Date().toGMTString());
			jobDao.update(job);
			Url url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1"
					+ "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			//TODO save result from rasdaman to ckan backend!
			byte[] response = IOUtils.toByteArray(conn.getInputStream());
			job.setStatus(JobStatus.FINISHED);
			job.setUpdated(new Date().toGMTString());
			jobDao.update(job);
			return Response.ok().header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
		} catch (MalformedURLException e) {
			log.error("An error occured when creating URL from job query: " + e.getMessage());
			return Response.serverError().entity("An error occured when creating URL from job query: " + e.getMessage())
					.build();
		} catch (IOException e) {
			log.error("An error occured when retrieving query result from WCPS endpoint: " + e.getMessage());
			return Response.serverError()
					.entity("An error occured when retrieving query result from WCPS endpoint: " + e.getMessage())
					.build();*/
		} catch (SQLException e) {
			log.error("An error occured while performing an SQL-query: " + e.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + e.getMessage())
					.build();
		}
    }
    @Override
    public Response jobsPost(BatchJobResponse storeBatchJobRequest, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        
    	UUID jobID = UUID.randomUUID();
    	storeBatchJobRequest.setId(jobID.toString());
    	storeBatchJobRequest.setStatus(Status.SUBMITTED);
		//TODO implement a more sophisticated method for date generation...
    	storeBatchJobRequest.setSubmitted(new Date());
		JSONObject processGraphJSON;
		String outputFormat = "json";
		log.debug("The following job was submitted: \n" + storeBatchJobRequest.toString());
		processGraphJSON = (JSONObject)storeBatchJobRequest.getProcessGraph();
		try {
			outputFormat = (String)(((JSONObject) storeBatchJobRequest.getOutput()).get(new String("format")));
			
            outputFormat = outputFormat.toUpperCase();
			
			if (outputFormat.equals("NETCDF"))
			{
				outputFormat="netCDF";
			}
			if (outputFormat.equals("GTIFF"))
			{
				outputFormat="GTiff";
			}
		}catch(Exception e) {
			log.error("An error occured while parsing output type: " + e.getMessage());
			log.info("assigning standard output type: json");
		}
		WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON, outputFormat);
		
		log.debug("Graph successfully parsed and saved with ID: " + jobID);
		log.debug("WCPS query: " + wcpsFactory.getWCPSString());

		try {
			jobDao.create(storeBatchJobRequest);
			log.debug("job saved to database: " + storeBatchJobRequest.getId());
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			/*SimpleModule module = new SimpleModule("JSONObjectSerializer", new Version(1, 0, 0, null, null, null));
			module.addSerializer(JSONObject.class, new JSONObjectSerializer());
			mapper.registerModule(module);*/
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			log.debug("Java object to string looks like this:");
			log.debug(storeBatchJobRequest.toString());
			log.debug("Serialized json looks like this:");
			log.debug(mapper.writeValueAsString(storeBatchJobRequest));			
			return Response.status(201).entity(mapper.writeValueAsString(storeBatchJobRequest)).header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").header("OpenEO-Identifier", storeBatchJobRequest.getId()).build();
		} catch (JsonProcessingException e) {
			log.error("An error occured while serializing job to json: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity("An error occured while serializing job to json: " + e.getMessage())
					.build();
		}
    }
}
