package eu.openeo.backend.wcps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/data")
public class Data {

	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	@GET
	@Produces("application/xml")
	public Response getData() {

		try {
			StringBuilder result = new StringBuilder();
			URL url;
			url = new URL("http://10.8.244.147:8080/rasdaman/ows?SERVICE=WCS&VERSION=2.0.1&REQUEST=GetCapabilities");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			return Response.ok(result.toString(), MediaType.APPLICATION_XML).build();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage())
					.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage())
					.build();
		}
	}

	@Path("{coverage}")
	@GET
	@Produces("application/xml")
	public Response getData(@PathParam("coverage") String coverageName) {
		StringBuilder result = new StringBuilder();
		URL url;
		try {
			url = new URL(
					"http://10.8.244.147:8080/rasdaman/ows?&SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID="
							+ coverageName);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			return Response.ok(result.toString(), MediaType.APPLICATION_XML).build();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity("An error occured while describing coverage from WCPS endpoint: " + e.getMessage()).build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity("An error occured while describing coverage from WCPS endpoint: " + e.getMessage()).build();
		}
	}

}
