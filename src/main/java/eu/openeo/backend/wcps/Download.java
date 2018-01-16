package eu.openeo.backend.wcps;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Path("download")
public class Download {

	Logger log = Logger.getLogger(this.getClass());

	@Path("{job-id}")
	@GET
	//@Produces("image/png")
	public Response getDownload(@PathParam("job-id") String jobID) {

		Connection connection = null;
		String jobQuery = null;
		try {
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection("jdbc:sqlite:" + PropertiesHelper.readProperties("job-database"));

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			ResultSet resultSet = statement.executeQuery("SELECT jobquery FROM jobs WHERE jobid='" + jobID + "'");
			while (resultSet.next()) {
				jobQuery=resultSet.getString("jobquery");
				log.debug("The job with id \"" + jobID + "\" was found: " + jobQuery);
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
			return Response.serverError().entity("An error occured while reading properties file: " + ioe.getMessage()).build();
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
			url = new URL(PropertiesHelper.readProperties("wcps-endpoint") + 
							"?SERVICE=WCS" + 
							"&VERSION=2.0.1" + 
							"&REQUEST=ProcessCoverages" + 
							"&QUERY=" + 
							URLEncoder.encode(jobQuery, "UTF-8").replace("+", "%20"));
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			byte[] response = IOUtils.toByteArray(conn.getInputStream());

			return Response.ok(response, MediaType.WILDCARD).build();
		} catch (MalformedURLException e) {
			log.error("An error occured when creating URL from job query: " + e.getMessage());
			return Response.serverError()
					.entity("An error occured when creating URL from job query: " + e.getMessage()).build();
		} catch (IOException e) {
			log.error("An error occured when retrieving query result from WCPS endpoint: " + e.getMessage());
			return Response.serverError()
					.entity("An error occured when retrieving query result from WCPS endpoint: " + e.getMessage()).build();
		}

	}

}
