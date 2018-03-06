package eu.openeo.api.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import eu.openeo.api.ExecuteApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.backend.wcps.WCPSQueryFactory;
import eu.openeo.model.JobFull;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ExecuteApiServiceImpl extends ExecuteApiService {
	
	
	Logger log = Logger.getLogger(this.getClass());
	
	private String wcpsEndpoint = null;
	
	public ExecuteApiServiceImpl() {
		try {
			wcpsEndpoint = ConvenienceHelper.readProperties("wcps-endpoint");
		} catch (IOException ioe) {
			log.error("An error occured while reading properties file: " + ioe.getMessage());
		}
	}
	
	
	@Override
	public Response executeOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().build();
	}

	@Override
	public Response executePost(JobFull job, SecurityContext securityContext) throws NotFoundException {
		JSONObject processGraphJSON;
		String outputFormat = "json";
		log.debug("The following job was submitted: \n" + job.toString());
		processGraphJSON = (JSONObject) job.getProcessGraph();
		try {
			outputFormat = (String)(((JSONObject) job.getOutput()).get(new String("format")));
		}catch(Exception e) {
			log.error("An error occured while parsing output type: " + e.getMessage());
			log.info("assigning standard output type: json");
		}
		WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON, outputFormat);
		try {
			URL url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1"
					+ "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			byte[] response = IOUtils.toByteArray(conn.getInputStream());
			return Response.ok(response, ConvenienceHelper.getMimeTypeFromOutput(outputFormat)).build();
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
}
