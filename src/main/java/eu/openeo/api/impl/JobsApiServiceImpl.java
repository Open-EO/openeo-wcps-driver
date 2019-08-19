package eu.openeo.api.impl;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Principal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.swing.event.EventListenerList;
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

import eu.openeo.api.JobsApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.auth.filter.RequireToken;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.backend.wcps.JobScheduler;
import eu.openeo.backend.wcps.WCPSQueryFactory;
import eu.openeo.backend.wcps.events.JobEvent;
import eu.openeo.backend.wcps.events.JobEventListener;
import eu.openeo.model.BatchJobResponse;
import eu.openeo.model.Status;
import eu.openeo.model.UpdateBatchJobRequest;

@RolesAllowed({"PUBLIC", "EURAC"})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class JobsApiServiceImpl extends JobsApiService {

	Logger log = Logger.getLogger(this.getClass());

	private ConnectionSource connection = null;
	private Dao<BatchJobResponse, String> jobDao = null;
	private String wcpsEndpoint = null;
	private EventListenerList listenerList = new EventListenerList();
	private JobScheduler jobScheduler = null;

	public JobsApiServiceImpl() {
		try {
			wcpsEndpoint = ConvenienceHelper.readProperties("wcps-endpoint");
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
			connection = new JdbcConnectionSource(dbURL);
			try {
				TableUtils.createTable(connection, BatchJobResponse.class);
			} catch (SQLException sqle) {
				log.debug("Create Table failed, probably exists already: " + sqle.getMessage());
			}
			jobDao = DaoManager.createDao(connection, BatchJobResponse.class);
			this.jobScheduler = new JobScheduler();
			this.addJobListener(jobScheduler);
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
		} catch (IOException ioe) {
			log.error("An error occured while reading properties file: " + ioe.getMessage());
		}
	}

	@Override
	@RequireToken
	public Response jobsGet(SecurityContext securityContext) throws NotFoundException {
		Principal principal = securityContext.getUserPrincipal();
		if(principal != null) {
			log.debug("The following user asked for list of stored jobs: " + principal.getName());
		}else {
			log.error("No information on authentication found on request for jobs!!!");
		}
		List<BatchJobResponse> storedBatchJobs = null;
		JSONObject jobSummary = new JSONObject();
		JSONArray jobs = new JSONArray();
		JSONArray links = new JSONArray();
		try {
			storedBatchJobs = jobDao.queryForAll();
			for (BatchJobResponse storedBatchJob : storedBatchJobs) {
				jobs.put(new JSONObject((String) storedBatchJob.toString()));
			}
			JSONObject linkSelf = new JSONObject();
			linkSelf.put("href", ConvenienceHelper.readProperties("openeo-endpoint") + "/jobs/");
			linkSelf.put("rel", "self");
			linkSelf.put("title", "Stored Jobs");
			links.put(linkSelf);
			jobSummary.put("jobs", jobs);
			jobSummary.put("links", links);
			return Response.ok(jobSummary.toString(4), MediaType.APPLICATION_JSON).build();
		} catch (SQLException e) {
			log.error("An error occured while performing an SQL-query: " + e.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + e.getMessage())
					.build();
		} catch (JSONException e) {
			log.error("An error occured while serializing job to json: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity("An error occured while serializing job to json: " + e.getMessage())
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
	@RequireToken
	public Response jobsJobIdDelete(@Pattern(regexp = "^[A-Za-z0-9_\\-\\.~]+$") String jobId,
			SecurityContext securityContext) throws NotFoundException {
		BatchJobResponse storedBatchJob = null;
		try {
			storedBatchJob = jobDao.queryForId(jobId);
			if (storedBatchJob == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available."))
						.build();
			}
			jobDao.deleteById(jobId);
			log.debug("The following job was deleted: \n" + storedBatchJob.toString());
			return Response.status(204).entity("The job has been successfully deleted.")
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
					.header("OpenEO-Identifier", storedBatchJob.getId()).build();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
	}

	@Override
	@RequireToken
	public Response jobsJobIdEstimateGet(@Pattern(regexp = "^[A-Za-z0-9_\\-\\.~]+$") String jobId,
			SecurityContext securityContext) throws NotFoundException {
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	@RequireToken
	public Response jobsJobIdGet(@Pattern(regexp = "^[A-Za-z0-9_\\-\\.~]+$") String jobId,
			SecurityContext securityContext) throws NotFoundException {
		BatchJobResponse storedBatchJob = null;
		try {
			storedBatchJob = jobDao.queryForId(jobId);
			if (storedBatchJob == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available."))
						.build();
			}
			log.debug("The following job was retrieved: \n" + storedBatchJob.toString());
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
			log.debug(storedBatchJob.toString());
			log.debug("Serialized json looks like this:");
			log.debug(mapper.writeValueAsString(storedBatchJob));
			return Response.status(201).entity(storedBatchJob.toString())
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
					.header("OpenEO-Identifier", storedBatchJob.getId()).build();
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
	@RequireToken
	public Response jobsJobIdPatch(@Pattern(regexp = "^[A-Za-z0-9_\\-\\.~]+$") String jobId,
			UpdateBatchJobRequest updateBatchJobRequest, SecurityContext securityContext) throws NotFoundException {
		BatchJobResponse storedBatchJob = null;
		try {
			storedBatchJob = jobDao.queryForId(jobId);
			if (storedBatchJob == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available."))
						.build();
			}
			log.debug("The following job was retrieved: \n" + storedBatchJob.toString());
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		if (updateBatchJobRequest.getTitle() != null)
			storedBatchJob.setTitle(updateBatchJobRequest.getTitle());
		if (updateBatchJobRequest.getDescription() != null)
			storedBatchJob.setDescription(updateBatchJobRequest.getDescription());
		if (updateBatchJobRequest.getProcessGraph() != null)
			storedBatchJob.setProcessGraph(updateBatchJobRequest.getProcessGraph());
		if (updateBatchJobRequest.getPlan() != null)
			storedBatchJob.setPlan(updateBatchJobRequest.getPlan());
		if (updateBatchJobRequest.getBudget() != null)
			storedBatchJob.setBudget(updateBatchJobRequest.getBudget());
		try {
			storedBatchJob.setUpdated(new Date());
			jobDao.update(storedBatchJob);
			log.debug("job updated in database: " + storedBatchJob.getId());
			return Response.status(204).entity("Changes to the job applied successfully.")
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
					.header("OpenEO-Identifier", storedBatchJob.getId()).build();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
	}

	@Override
	@RequireToken
	public Response jobsJobIdResultsDelete(@Pattern(regexp = "^[A-Za-z0-9_\\-\\.~]+$") String jobId,
			SecurityContext securityContext) throws NotFoundException {
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	@RequireToken
	public Response jobsJobIdResultsGet(@Pattern(regexp = "^[A-Za-z0-9_\\-\\.~]+$") String jobId,
			SecurityContext securityContext) throws NotFoundException {

		BatchJobResponse job = null;
		String outputFormat = "JSON";
		try {
			job = jobDao.queryForId(jobId);
			if (job == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available."))
						.build();
			}
			log.debug("The following job was retrieved: \n" + job.toString());
			
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			JSONObject processGraphJSON = (JSONObject) job.getProcessGraph();
			WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON);

			String fileName = job.getId() + "." + ConvenienceHelper.getRasNameFromMimeType(wcpsFactory.getOutputFormat());
			
			JSONObject linkProcessGraph = new JSONObject();
			linkProcessGraph.put("job_id", job.getId());
			linkProcessGraph.put("updated", job.getUpdated());
			
			JSONArray links = new JSONArray();
			JSONObject link = new JSONObject();
			link.put("href", ConvenienceHelper.readProperties("openeo-endpoint") + "/tmp/" + fileName);
			link.put("type", ConvenienceHelper.getMimeTypeFromOutput(outputFormat));

			links.put(link);

			linkProcessGraph.put("links", links);
			
			return Response.ok(linkProcessGraph.toString().getBytes("UTF-8"), "application/json")
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
		} catch (IOException e) {
			log.error("An error occured when retrieving query result from WCPS endpoint: " + e.getMessage());
			return Response.serverError()
					.entity("An error occured when retrieving query result from WCPS endpoint: " + e.getMessage())
					.build();
		}
	}

	@Override
	@RequireToken
	public Response jobsJobIdResultsPost(@Pattern(regexp = "^[A-Za-z0-9_\\-\\.~]+$") String jobId,
			SecurityContext securityContext) throws NotFoundException {
		BatchJobResponse job = null;
		try {
			job = jobDao.queryForId(jobId);
			if (job == null) {
				return Response.status(404).entity(new String("A job with the specified identifier is not available."))
						.build();
			}
			log.debug("The following job was retrieved: \n" + job.toString());
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
					.build();
		}
		try {
			job.setStatus(Status.QUEUED);
			job.setUpdated(new Date());
			jobDao.update(job);
			this.fireJobQueuedEvent(job.getId());
			return Response.status(202).entity(new String("The creation of the resource has been queued successfully."))
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
		} catch (SQLException e) {
			log.error("An error occured while performing an SQL-query: " + e.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + e.getMessage())
					.build();
		}
	}

	@Override
	@RequireToken
	public Response jobsPost(BatchJobResponse storeBatchJobRequest, SecurityContext securityContext)
			throws NotFoundException {
		UUID jobID = UUID.randomUUID();
		storeBatchJobRequest.setId(jobID.toString());
		storeBatchJobRequest.setStatus(Status.SUBMITTED);
		// TODO implement a more sophisticated method for date generation...
		storeBatchJobRequest.setSubmitted(new Date());
		JSONObject processGraphJSON;

		log.debug("The following job was submitted: \n" + storeBatchJobRequest.toString());
		processGraphJSON = (JSONObject) storeBatchJobRequest.getProcessGraph();

		WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON);

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
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.setSerializationInclusion(Include.NON_NULL);
			log.debug("Java object to string looks like this:");
			log.debug(storeBatchJobRequest.toString());
			log.debug("Serialized json looks like this:");
			log.debug(mapper.writeValueAsString(storeBatchJobRequest));
			return Response.status(201).entity(mapper.writeValueAsString(storeBatchJobRequest))
					.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
					.header("OpenEO-Identifier", storeBatchJobRequest.getId()).build();
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

	public void addJobListener(JobEventListener listener) {
		try {
			listenerList.add(JobEventListener.class, listener);
			log.debug("JobEventListener successfully added to listenerList!");
		} catch (Exception e) {
			log.error("No Event available: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}

	private void fireJobQueuedEvent(String jobId) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == JobEventListener.class) {
				JobEvent jobEvent = new JobEvent(this, jobId);
				((JobEventListener) listeners[i + 1]).jobQueued(jobEvent);
			}
		}
		log.debug("Job Queue Event fired for job: " + jobId);
	}
}
