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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import eu.openeo.api.JobsApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.PropertiesHelper;
import eu.openeo.backend.wcps.WCPSQueryFactory;
import eu.openeo.model.JobFull;
import eu.openeo.model.JobStatus;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class JobsApiServiceImpl extends JobsApiService {

	Logger log = Logger.getLogger(this.getClass());
	
	private ConnectionSource connection = null;
	private Dao<JobFull,String> jobDao = null;
	private String wcpsEndpoint = null;
	
	private ObjectMapper mapper = null;
	private JSONParser parser = null;
	
	public JobsApiServiceImpl() {
		try {
			wcpsEndpoint = PropertiesHelper.readProperties("wcps-endpoint");
			String dbURL = "jdbc:sqlite:" + PropertiesHelper.readProperties("job-database");
			connection =  new JdbcConnectionSource(dbURL);
			try {
				TableUtils.createTable(connection, JobFull.class);
			}catch(SQLException sqle) {
				log.debug("Create Table failed, probably exists already: " + sqle.getMessage());
			}
			jobDao = DaoManager.createDao(connection, JobFull.class);
			 mapper = new ObjectMapper();
			 parser = new JSONParser();
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
	public Response jobsJobIdDownloadGet(String jobId, String format, SecurityContext securityContext)
			throws NotFoundException {
		JobFull job = null;
		WCPSQueryFactory wcpsFactory = null;
		try {
			job = jobDao.queryForId(jobId);
			log.debug("The following job was retrieved: \n" + job.toString());
			JSONObject processGraphJSON;
			String outputFormat = format;
			processGraphJSON = (JSONObject) job.getProcessGraph();
			outputFormat = (String)(((JSONObject) job.getOutput()).get(new String("format")));
			wcpsFactory = new WCPSQueryFactory(processGraphJSON, outputFormat);
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		URL url;
		try {
			job.setStatus(JobStatus.RUNNING);
			job.setUpdated(new Date().toGMTString());
			jobDao.update(job);
			url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1"
					+ "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			byte[] response = IOUtils.toByteArray(conn.getInputStream());
			job.setStatus(JobStatus.FINISHED);
			job.setUpdated(new Date().toGMTString());
			jobDao.update(job);
			return Response.ok(response, MediaType.WILDCARD).build();
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
	public Response jobsJobIdDownloadOptions(String jobId, String format, SecurityContext securityContext)
			throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response jobsJobIdGet(String jobId, SecurityContext securityContext) throws NotFoundException {
		JobFull job = null;
		try {
			job = jobDao.queryForId(jobId);
			log.debug("The following job was retrieved: \n" + job.toString());
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			return Response.ok().entity(mapper.writeValueAsString(job)).build();
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
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
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
		log.debug("" + job.getProcessGraph().getClass().getSimpleName());
		processGraphJSON = (JSONObject) job.getProcessGraph();
		outputFormat = (String)(((JSONObject) job.getOutput()).get(new String("format")));
		WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON, outputFormat);
		
		log.debug("Graph successfully parsed and saved with ID: " + jobID);
		log.debug("WCPS query: " + wcpsFactory.getWCPSString());

		try {
			jobDao.create(job);			
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			return Response.ok().entity(mapper.writeValueAsString(job)).build();
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			return Response.serverError().entity("An error occured while serializing job to json: " + e.getMessage())
					.build();
		}
	}
}
