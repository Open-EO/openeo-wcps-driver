package io.swagger.api.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import eu.openeo.backend.wcps.PropertiesHelper;
import eu.openeo.backend.wcps.WCPSQueryFactory;
import io.swagger.api.ApiResponseMessage;
import io.swagger.api.JobsApiService;
import io.swagger.api.NotFoundException;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class JobsApiServiceImpl extends JobsApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
    @Override
    public Response jobsJobIdCancelGet(String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response jobsJobIdDelete(String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response jobsJobIdGet(String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response jobsJobIdSubscribeGet(String jobId, String upgrade, String connection, String secWebSocketKey, String secWebSocketProtocol, BigDecimal secWebSocketVersion, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response jobsPost( String evaluate, String processGraph,  String format, SecurityContext securityContext) throws NotFoundException {
    	if(evaluate.equals("lazy")) {
    		JSONParser parser = new JSONParser();
			JSONObject processGraphJSON;
			try {
				log.debug("Parsing process Graph \n" + processGraph.toString());
				processGraphJSON = (JSONObject) parser.parse(processGraph.toString());
			} catch (ParseException e) {
				log.error(e.getMessage());
				return Response.serverError().entity("An error occured while parsing input json: " + e.getMessage())
						.build();
			}
			WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON);
			UUID jobID = UUID.randomUUID();
			log.debug("Graph successfully parsed and saved with ID: " + jobID);
			log.debug("WCPS query: " + wcpsFactory.getWCPSString());
			Connection connection = null;
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + PropertiesHelper.readProperties("job-database")); 
	
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30);
				
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS jobs (jobid STRING, jobquery STRING, UNIQUE(jobid))");
				
				statement.executeUpdate("INSERT INTO jobs (jobid, jobquery) VALUES ('" + jobID.toString() + "','" + wcpsFactory.getWCPSString() + "')");
				
				
			} catch (ClassNotFoundException cnfe) {
				log.error("An error occured while loading database driver: " + cnfe.getMessage());
				return Response.serverError().entity("An error occured while loading database driver: " + cnfe.getMessage()).build();
			} catch (SQLException sqle) {
				log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
				return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage()).build();
			} catch (IOException ioe) {
				log.error("An error occured while reading properties file: " + ioe.getMessage());
				return Response.serverError().entity("An error occured while reading properties file: " + ioe.getMessage()).build();
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (SQLException e) {
					log.error("An error occured while attempting to close DB connection: " + e.getMessage());
					return Response.serverError().entity("An error occured while attempting to close DB connection: " + e.getMessage()).build();
				}
			}
	        return Response.ok().entity("{\"job_id\" : \"" + jobID.toString() + "\"}").build();
    	}else if(evaluate.equals("batch")) {
    		log.error("Evaluation type not supported: " + evaluate);
			return Response.serverError().entity("Evaluation type not supported: " + evaluate).build();
    	}else if(evaluate.equals("sync")) {
    		log.error("Evaluation type not supported: " + evaluate);
			return Response.serverError().entity("Evaluation type not supported: " + evaluate).build();
    	}else {
    		log.error("Evaluation type not supported: " + evaluate);
			return Response.serverError().entity("Evaluation type not supported: " + evaluate).build();
    	}
    }
}
