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
import org.json.JSONObject;

import eu.openeo.api.NotFoundException;
import eu.openeo.api.ResultApiService;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.backend.wcps.WCPSQueryFactory;
import eu.openeo.model.SynchronousResultRequest;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ResultApiServiceImpl extends ResultApiService {	
	
	Logger log = Logger.getLogger(this.getClass());
	
    @Override
    public Response resultPost(SynchronousResultRequest synchronousResultRequest, SecurityContext securityContext) throws NotFoundException {
		WCPSQueryFactory wcpsFactory = null;
		JSONObject processGraphJSON = (JSONObject) synchronousResultRequest.getProcessGraph();
		wcpsFactory = new WCPSQueryFactory(processGraphJSON);
		try {
			URL url = new URL(ConvenienceHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS" + "&VERSION=2.0.1"
					+ "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			byte[] response = IOUtils.toByteArray(conn.getInputStream());
			return Response.ok(response).header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
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
