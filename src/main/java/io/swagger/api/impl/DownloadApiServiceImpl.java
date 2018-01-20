package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.model.*;


import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.openeo.backend.wcps.PropertiesHelper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class DownloadApiServiceImpl extends DownloadApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
    @Override
    public Response downloadWcsJobIdGet(String jobId, SecurityContext securityContext) throws NotFoundException {
    	Connection connection = null;
		String jobQuery = null;
		try {
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection("jdbc:sqlite:" + PropertiesHelper.readProperties("job-database"));

			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			ResultSet resultSet = statement.executeQuery("SELECT jobquery FROM jobs WHERE jobid='" + jobId + "'");
			while (resultSet.next()) {
				jobQuery=resultSet.getString("jobquery");
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
    @Override
    public Response downloadWmtsJobIdGet(String jobId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
