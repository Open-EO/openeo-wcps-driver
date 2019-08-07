package eu.openeo.api.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import eu.openeo.api.JobsApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.backend.wcps.WCPSQueryFactory;
import eu.openeo.dao.JSONObjectSerializer;
import eu.openeo.model.JobFull;
import eu.openeo.model.JobStatus;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class JobsApiServiceImpl extends JobsApiService {

	Logger log = Logger.getLogger(this.getClass());
	
	private ConnectionSource connection = null;
	private Dao<JobFull,String> jobDao = null;
	private String wcpsEndpoint = null;
	
	private ObjectMapper mapper = null;
	
	public JobsApiServiceImpl() {
		try {
			wcpsEndpoint = ConvenienceHelper.readProperties("wcps-endpoint");
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
			connection =  new JdbcConnectionSource(dbURL);
			try {
				TableUtils.createTable(connection, JobFull.class);
			}catch(SQLException sqle) {
				log.debug("Create Table failed, probably exists already: " + sqle.getMessage());
			}
			jobDao = DaoManager.createDao(connection, JobFull.class);
			 mapper = new ObjectMapper();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
		} catch (IOException ioe) {
			log.error("An error occured while reading properties file: " + ioe.getMessage());
		}
	}

	@Override
	public Response jobsJobIdCancelOptions(String jobId, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdCancelPatch(String jobId, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}
	
	@Override
	public Response jobsJobIdResultsGet(String jobId, String format, SecurityContext securityContext)
			throws NotFoundException {
		JobFull job = null;
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
			job.setUpdated(new Date().toGMTString());
			jobDao.update(job);

			url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1"
					+ "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
			
			JSONObject linkProcessGraph = new JSONObject();
			linkProcessGraph.put("job_id", job.getJobId());
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
	public Response jobsJobIdResultsPost(String jobId, SecurityContext securityContext)
			throws NotFoundException {
		JobFull job = null;
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
			job.setStatus(JobStatus.QUEUED);
			job.setUpdated(new Date().toGMTString());
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
	public Response jobsJobIdResultsOptions(String jobId, String format, SecurityContext securityContext)
			throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdGet(String jobId, SecurityContext securityContext) throws NotFoundException {
		JobFull job = null;
		try {
			job = jobDao.queryForId(jobId);
			if(job == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available.")).build();
			}
			log.debug("The following job was retrieved: \n" + job.toString());
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			SimpleModule module = new SimpleModule("JSONObjectSerializer", new Version(1, 0, 0, null, null, null));
			module.addSerializer(JSONObject.class, new JSONObjectSerializer());
			mapper.registerModule(module);
			return Response.status(201).entity(mapper.writeValueAsString(job)).header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
//			return Response.ok().entity(mapper.writeValueAsString(job)).build();
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			return Response.serverError().entity("An error occured while serializing job to json: " + e.getMessage())
					.build();
		}
	}

	@Override
	public Response jobsJobIdOptions(String jobId, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdPatch(String jobId, JobFull job, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdPauseOptions(String jobId, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdPausePatch(String jobId, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdQueueOptions(String jobId, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdQueuePatch(String jobId, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdSubscribeGet(String jobId, String upgrade, String connection, String secWebSocketKey,
			String secWebSocketProtocol, BigDecimal secWebSocketVersion, SecurityContext securityContext)
			throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdSubscribeOptions(String jobId, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
	}

	@Override
	public Response jobsPost(JobFull job, SecurityContext securityContext) throws NotFoundException {
		UUID jobID = UUID.randomUUID();
		job.setJobId(jobID.toString());
		job.setStatus(JobStatus.SUBMITTED);
		//TODO implement a more sophisticated method for date generation...
		job.setSubmitted(new Date().toGMTString());
		JSONObject processGraphJSON;
		String outputFormat = "json";
		log.debug("The following job was submitted: \n" + job.toString());
		processGraphJSON = (JSONObject) job.getProcessGraph();
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
		WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON, outputFormat);
		
		log.debug("Graph successfully parsed and saved with ID: " + jobID);
		log.debug("WCPS query: " + wcpsFactory.getWCPSString());

		try {
			jobDao.create(job);
			log.debug("job saved to database: " + job.getJobId());
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			SimpleModule module = new SimpleModule("JSONObjectSerializer", new Version(1, 0, 0, null, null, null));
			module.addSerializer(JSONObject.class, new JSONObjectSerializer());
			mapper.registerModule(module);
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			return Response.status(201).entity(mapper.writeValueAsString(job)).header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").header("OpenEO-Identifier", job.getJobId()).build();
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			return Response.serverError().entity("An error occured while serializing job to json: " + e.getMessage())
					.build();
		}
	}

}
