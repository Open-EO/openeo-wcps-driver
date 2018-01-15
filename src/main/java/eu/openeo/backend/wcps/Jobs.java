package eu.openeo.backend.wcps;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import java.util.HashMap;
import java.util.UUID;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Path("jobs")
public class Jobs {

	//HashMap<String, String> lazyJobMap = new HashMap<String, String>();
	Logger log = Logger.getLogger(this.getClass());

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response getProcesses(String processGraphString) {
		JSONParser parser = new JSONParser();
		JSONObject processGraph;
		try {
			log.debug("Parsing process Graph \n" + processGraphString);
			processGraph = (JSONObject) parser.parse(processGraphString);
		} catch (ParseException e) {
			log.error(e.getMessage());
			return Response.serverError().entity("An error occured while parsing input json: " + e.getMessage())
					.build();
		}
		WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraph);
		UUID jobID = UUID.randomUUID();
		log.debug("Graph successfully parsed and saved with ID: " + jobID);
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection("jdbc:sqlite:openeo.db");

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS jobs (job-id STRING, job-query STRING, UNIQUE(job-id))");
			
			statement.executeUpdate("INSERT INTO jobs (job-id, job-query) VALUES ('" + jobID.toString() + "','" + wcpsFactory.getWCPSString() + "')");
			
			
		} catch (ClassNotFoundException cnfe) {
			log.error("An error occured while loading database driver: " + cnfe.getMessage());
			return Response.serverError().entity("An error occured while loading database driver: " + cnfe.getMessage()).build();
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage()).build();
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				log.error("An error occured while attempting to close DB connection: " + e.getMessage());
				return Response.serverError().entity("An error occured while attempting to close DB connection: " + e.getMessage()).build();
			}
		}
		//lazyJobMap.put(jobID.toString(), wcpsFactory.getWCPSString());
		return Response.ok("{\"job_id\" : \"" + jobID.toString() + "\"}").build();
	}

}
