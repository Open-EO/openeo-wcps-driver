package eu.openeo.api.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
		Connection connection = null;
		String jobQuery = null;
		try {
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection("jdbc:sqlite:" + PropertiesHelper.readProperties("job-database"));

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			ResultSet resultSet = statement.executeQuery("SELECT jobquery FROM jobs WHERE jobid='" + jobId + "'");
			while (resultSet.next()) {
				jobQuery = resultSet.getString("jobquery");
				log.debug("The job with id \"" + jobId + "\" was found: " + jobQuery);
			}

		} catch (ClassNotFoundException cnfe) {
			log.error("An error occured while loading database driver: " + cnfe.getMessage());
			return Response.serverError().entity("An error occured while loading database driver: " + cnfe.getMessage())
					.build();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		} catch (IOException ioe) {
			log.error("An error occured while reading properties file: " + ioe.getMessage());
			return Response.serverError().entity("An error occured while reading properties file: " + ioe.getMessage())
					.build();
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				log.error("An error occured while attempting to close DB connection: " + e.getMessage());
				return Response.serverError()
						.entity("An error occured while attempting to close DB connection: " + e.getMessage()).build();
			}
		}
		URL url;
		try {
			url = new URL(PropertiesHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS" + "&VERSION=2.0.1"
					+ "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(jobQuery, "UTF-8").replace("+", "%20"));

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			byte[] response = IOUtils.toByteArray(conn.getInputStream());

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
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
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
		job.setSubmitted(new Date().toGMTString());
		JSONParser parser = new JSONParser();
		JSONObject processGraphJSON;
		String outputFormat = "json";
		ObjectMapper mapper = new ObjectMapper();
		try {
			log.debug("The following job was submitted: \n" + job.toString());
			processGraphJSON = (JSONObject) parser.parse(mapper.writeValueAsString(job.getProcessGraph()));
			outputFormat = (String)(((JSONObject) parser.parse(mapper.writeValueAsString(job.getOutput()))).get(new String("format")));
		} catch (ParseException e) {
			log.error(e.getMessage());
			return Response.serverError().entity("An error occured while parsing input json: " + e.getMessage())
					.build();
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			return Response.serverError().entity("An error occured while parsing the process graph json: " + e.getMessage())
					.build();
		}
		WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON, outputFormat);
		
		log.debug("Graph successfully parsed and saved with ID: " + jobID);
		log.debug("WCPS query: " + wcpsFactory.getWCPSString());
//		ConnectionSource connection = null;
		Connection connection = null;	
		try {
//			String dbURL = "jdbc:sqlite:" + PropertiesHelper.readProperties("job-database");
//			connection =  new JdbcConnectionSource(dbURL);
//			
//			TableUtils.createTable(connection, JobFull.class);
//			
//			Dao<JobFull,String> jobDao = DaoManager.createDao(connection, JobFull.class);
//			
//			jobDao.create(job);			
	
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + PropertiesHelper.readProperties("job-database"));

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			statement.executeUpdate("CREATE TABLE IF NOT EXISTS jobs (jobid STRING, jobquery STRING, UNIQUE(jobid))");

			statement.executeUpdate("INSERT INTO jobs (jobid, jobquery) VALUES ('" + jobID.toString() + "','"
					+ wcpsFactory.getWCPSString() + "')");
			
		} catch (ClassNotFoundException cnfe) {
			log.error("An error occured while loading database driver: " + cnfe.getMessage());
			return Response.serverError().entity("An error occured while loading database driver: " + cnfe.getMessage())
					.build();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		} catch (IOException ioe) {
			log.error("An error occured while reading properties file: " + ioe.getMessage());
			return Response.serverError().entity("An error occured while reading properties file: " + ioe.getMessage())
					.build();
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				log.error("An error occured while attempting to close DB connection: " + e.getMessage());
				return Response.serverError()
						.entity("An error occured while attempting to close DB connection: " + e.getMessage()).build();
//			} catch (IOException e) {
//				log.error("An error occured while attempting to close DB connection: " + e.getMessage());
//				return Response.serverError()
//						.entity("An error occured while attempting to close DB connection: " + e.getMessage()).build();
			}
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
