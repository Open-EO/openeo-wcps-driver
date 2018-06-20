package eu.openeo.api.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.openeo.api.DataApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.ConvenienceHelper;
import eu.openeo.api.ApiResponseMessage;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class DataApiServiceImpl extends DataApiService {

	Logger log = Logger.getLogger(this.getClass());
	
	private SAXBuilder builder = null;
	
	public DataApiServiceImpl() {
		this.builder = new SAXBuilder();
	}

	@Override
	public Response dataGet(String qname, String qgeom, String qstartdate, String qenddate,
			SecurityContext securityContext) throws NotFoundException {
		try {
			URL url;
			url = new URL(ConvenienceHelper.readProperties("wcps-endpoint")
					+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=GetCapabilities");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			Document capabilititesDoc = (Document) this.builder.build(conn.getInputStream()); 
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			log.debug("root node info: " + rootNode.getName());		
			List<Element> coverageList = rootNode.getChildren("Contents", defaultNS).get(0).getChildren("CoverageSummary", defaultNS);
			JSONArray productArray = new JSONArray();
			log.debug("number of coverages found: " + coverageList.size());
			for(int c = 0; c < coverageList.size(); c++) {
				Element coverage = coverageList.get(c);
				log.debug("root node info: " + coverage.getName() + ":" + coverage.getChildText("CoverageId", defaultNS));		
				JSONObject product = new JSONObject();
				product.put("product_id", coverage.getChildText("CoverageId", defaultNS));
				productArray.put(product);
			}
			return Response.ok(productArray.toString(4), MediaType.APPLICATION_JSON).build();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (JDOMException e) {
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage())).build();
		}
	}

	@Override
	public Response dataOpensearchGet(String q, Integer start, Integer rows, SecurityContext securityContext)
			throws NotFoundException {
		URL url;
		log.debug("Recieved the following query: " + q);
		try {
			url = new URL(ConvenienceHelper.readProperties("os-endpoint") + "?any=" + q + "&resultType=hits&_content_type=json");
			//"http://localhost:8080/euracgeonet/srv/eng/rss.search?any=eurac&resultType=hits&_content_type=json&fast="
			log.debug("The following request was send to geonetwork: " + url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			InputStream input = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(input, "UTF-8");
			log.debug("The result received: " + result);
			JSONObject queryResult = new JSONObject(result);

			return Response.ok(queryResult.toString(4), MediaType.APPLICATION_JSON).build();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while describing coverage from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while describing coverage from WCPS endpoint: " + e.getMessage()))
					.build();
		}
	}

	@Override
	public Response dataOpensearchOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().build();
	}

	@Override
	public Response dataOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().build();
	}

	@Override
	public Response dataProductIdGet(String productId, SecurityContext securityContext) throws NotFoundException {
		URL url;
		try {
			url = new URL(ConvenienceHelper.readProperties("wcps-endpoint")
					+ "?&SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + productId);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			Document capabilititesDoc = (Document) this.builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			Namespace sweNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("swe")) {
					sweNS = current;
				}
				if(current.getPrefix().equals("gmlcov")) {
					gmlNS = current;
				}
			}
			
			log.debug("root node info: " + rootNode.getName());		
			List<Element> bandList = rootNode.getChild("CoverageDescription", defaultNS).getChild("rangeType", gmlNS).getChild("DataRecord", sweNS).getChildren("field", sweNS);
			Element coverageDescElement = rootNode.getChild("CoverageDescription", defaultNS);
			Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
			Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
			
			String srsDescription = boundingBoxElement.getAttributeValue("srsName");
			try {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG"), srsDescription.indexOf("&")).replace("/0/", ":");
			}catch(StringIndexOutOfBoundsException e) {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG")).replace("/0/", ":");
			}
			
			String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");
			JSONObject extent = new JSONObject();
			JSONObject time =  new JSONObject();
			String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
			for(int a = 0; a < axis.length; a++) {
				log.debug(axis[a]);
				if(axis[a].equals("E") || axis[a].equals("X")){
					extent.put("left", Double.parseDouble(minValues[a]));
					extent.put("right", Double.parseDouble(maxValues[a]));
				}
				if(axis[a].equals("N") || axis[a].equals("Y")){
					extent.put("bottom", Double.parseDouble(minValues[a]));
					extent.put("top", Double.parseDouble(maxValues[a]));
				}
				if(axis[a].equals("DATE")  || axis[a].equals("ansi")){
					time.put("from", minValues[a].replaceAll("\"", ""));
					time.put("to", maxValues[a].replaceAll("\"", ""));
				}
			}
			extent.put("srs", srsDescription);			
			JSONObject coverage = new JSONObject();
			coverage.put("product_id", productId);
			coverage.put("extent", extent);
			coverage.put("time", time);
			JSONArray bandArray = new JSONArray();
			log.debug("number of bands found: " + bandList.size());
			for(int c = 0; c < bandList.size(); c++) {
				Element band = bandList.get(c);
				log.debug("band info: " + band.getName() + ":" + band.getAttributeValue("name"));		
				JSONObject product = new JSONObject();
				product.put("band_id", band.getAttributeValue("name"));
				bandArray.put(product);
			}
			coverage.put("bands", bandArray);
			return Response.ok(coverage.toString(4), MediaType.APPLICATION_JSON).build();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while describing coverage from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while describing coverage from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (JDOMException e) {
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage())).build();
		}
	}

	@Override
	public Response dataProductIdOptions(String productId, SecurityContext securityContext) throws NotFoundException {
		return Response.ok().build();
	}
}
