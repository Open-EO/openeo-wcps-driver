package eu.openeo.api.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Date;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import eu.openeo.api.PreviewApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.backend.wcps.WCPSQueryFactory;
import eu.openeo.model.JobFull;
import eu.openeo.model.JobStatus;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class PreviewApiServiceImpl extends PreviewApiService {
	
	
	Logger log = Logger.getLogger(this.getClass());
	
	private String wcpsEndpoint = null;
	
	public PreviewApiServiceImpl() {
		try {
			wcpsEndpoint = ConvenienceHelper.readProperties("wcps-endpoint");
		} catch (IOException ioe) {
			log.error("An error occured while reading properties file: " + ioe.getMessage());
		}
	}
	
	

	@Override
	public Response previewOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
	}

	@Override
	public Response previewPost(JobFull job, SecurityContext securityContext) throws NotFoundException {
		JSONObject processGraphJSON;
		String outputFormat = "JSON";
		log.debug("The following job was submitted for preview: \n" + job.toString());
		processGraphJSON = (JSONObject) job.getProcessGraph();
		try {
			outputFormat = (String)(((JSONObject) job.getOutput()).get(new String("format")));
		}catch(Exception e) {
			log.error("An error occured while parsing preview output type: " + e.getMessage());
			log.info("assigning standard output type: json");
		}
		WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON, outputFormat);
		log.debug("WCPS: " + wcpsFactory.getWCPSString());
		URL url;
		try {
			job.setStatus(JobStatus.RUNNING);
			job.setUpdated(new Date().toGMTString());
//			jobDao.update(job);
			url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1"
					+ "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			byte[] response = IOUtils.toByteArray(conn.getInputStream());
			job.setStatus(JobStatus.FINISHED);
			job.setUpdated(new Date().toGMTString());
//			jobDao.update(job);
			return Response.ok(response, ConvenienceHelper.getMimeTypeFromOutput(outputFormat)).header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
		} catch (MalformedURLException e) {
			log.error("An error occured when creating URL from job preview query: " + e.getMessage());
			return Response.serverError().entity("An error occured when creating URL from job query: " + e.getMessage())
					.build();
		} catch (IOException e) {
			log.error("An error occured when retrieving query preview result from WCPS endpoint: " + e.getMessage());
			return Response.serverError()
					.entity("An error occured when retrieving preview query result from WCPS endpoint: " + e.getMessage())
					.build();
		}
//		} catch (SQLException e) {
//			log.error("An error occured while performing a preview SQL-query: " + e.getMessage());
//			return Response.serverError().entity("An error occured while performing a preview SQL-query: " + e.getMessage())
//					.build();
//		}
	}
}
