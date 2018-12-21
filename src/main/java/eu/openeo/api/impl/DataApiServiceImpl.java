package eu.openeo.api.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

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
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.osrJNI;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class DataApiServiceImpl extends DataApiService {

	Logger log = Logger.getLogger(this.getClass());
	
//	private SAXBuilder builder = null;
//	
//	public DataApiServiceImpl() {
//		this.builder = new SAXBuilder();
//	}

	@Override
	public Response dataGet(String qname, String qgeom, String qstartdate, String qenddate,
			SecurityContext securityContext) throws NotFoundException {
		try {
						
			URL url;
			url = new URL(ConvenienceHelper.readProperties("wcps-endpoint")
					+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=GetCapabilities");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = (Document) builder.build(conn.getInputStream()); 
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			log.debug("root node info: " + rootNode.getName());
			List<Element> coverageList = rootNode.getChildren("Contents", defaultNS).get(0).getChildren("CoverageSummary", defaultNS);
			JSONObject data = new JSONObject ();
			JSONArray linksCollections = new JSONArray();
				
				JSONObject linkDesc = new JSONObject();
				linkDesc.put("href", "http://saocompute.eurac.edu/rasdaman/ows");
				linkDesc.put("rel", "self");
				
				JSONObject linkCsw = new JSONObject();
				linkCsw.put("href", "https://openeo.org/api/csw");
				linkCsw.put("rel", "alternate");
				linkCsw.put("title", "openEO catalog (OGC Catalogue Services 3.0)");
				
				linksCollections.put(linkDesc);
				linksCollections.put(linkCsw);
			
								
			JSONArray productArray = new JSONArray();
			log.debug("number of coverages found: " + coverageList.size());
			
						
			for(int c = 0; c < coverageList.size(); c++) {
				Element coverage = coverageList.get(c);
				log.debug("root node info: " + coverage.getName() + ":" + coverage.getChildText("CoverageId", defaultNS));		
				
				JSONObject product = new JSONObject();
				
                JSONObject extentCollection = new JSONObject();
				
				JSONArray spatialExtent = new JSONArray();
				JSONArray temporalExtent =  new JSONArray();
				
				String coverageID = coverage.getChildText("CoverageId", defaultNS);
				
				URL urlCollections = new URL(ConvenienceHelper.readProperties("wcps-endpoint")
						+ "?&SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + coverageID);
				
				HttpURLConnection connCollections = (HttpURLConnection) urlCollections.openConnection();
				connCollections.setRequestMethod("GET");
				SAXBuilder builderInt = new SAXBuilder();
				Document capabilititesDocCollections = (Document) builderInt.build(connCollections.getInputStream());
				List<Namespace> namespacesCollections = capabilititesDocCollections.getNamespacesIntroduced();
				Element rootNodeCollections = capabilititesDocCollections.getRootElement();
				Namespace defaultNSCollections = rootNodeCollections.getNamespace();
				Namespace gmlNS = null;
				Namespace sweNS = null;
				for (int n = 0; n < namespacesCollections.size(); n++) {
					Namespace current = namespacesCollections.get(n);
					if(current.getPrefix().equals("swe")) {
						sweNS = current;
					}
					if(current.getPrefix().equals("gmlcov")) {
						gmlNS = current;
					}
				}
				
				log.debug("root node info: " + rootNodeCollections.getName());		
				
				Element coverageDescElement = rootNodeCollections.getChild("CoverageDescription", defaultNSCollections);
				Element boundedByElement = coverageDescElement.getChild("boundedBy", gmlNS);
				Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
				Element metadataElement = rootNodeCollections.getChild("CoverageDescription", defaultNSCollections).getChild("metadata", gmlNS).getChild("Extension", gmlNS);
				
				
					//metadataObj = new JSONObject(metadataString1);
					//String metadataString2 = metadataString1.replaceAll("\\n","");
					//String metadataString3 = metadataString2.replaceAll("\"\"","\"");
					//metadataObj = new JSONObject(metadataString3);
					//JSONArray slices = metadataObj.getJSONArray("slices");
				
				String srsDescription = boundingBoxElement.getAttributeValue("srsName");
				log.debug(srsDescription);
				try {
					srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG"), srsDescription.indexOf("&")).replace("/0/", ":");
					srsDescription = srsDescription.replaceAll("EPSG:","");
					
				}catch(StringIndexOutOfBoundsException e) {
					srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG")).replace("/0/", ":");
					srsDescription = srsDescription.replaceAll("EPSG:","");			
				}
				log.debug(srsDescription);				
				
				SpatialReference src = new SpatialReference();
				src.ImportFromEPSG(Integer.parseInt(srsDescription));

				SpatialReference dst = new SpatialReference();
				dst.ImportFromEPSG(4326);
				
				log.debug(boundingBoxElement.getChildText("lowerCorner", gmlNS));
			    log.debug(boundingBoxElement.getChildText("upperCorner", gmlNS));
				String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
				String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");
				
			    CoordinateTransformation tx = new CoordinateTransformation(src, dst);
			    
			    String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
			    int xIndex = 0;
			    int yIndex = 0;
			    for(int a = 0; a < axis.length; a++) {
			    	log.debug(axis[a]);
					if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long")){
						xIndex=a;
					}
					if(axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
						yIndex=a;
					}
					if(axis[a].equals("DATE")  || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
						temporalExtent.put(minValues[a].replaceAll("\"", ""));
						temporalExtent.put(maxValues[a].replaceAll("\"", ""));
					}
			    }
			    
				double[] c1 = null;
				double[] c2 = null;
				c1 = tx.TransformPoint(Double.parseDouble(minValues[xIndex]), Double.parseDouble(maxValues[yIndex]));
				c2 = tx.TransformPoint(Double.parseDouble(maxValues[xIndex]), Double.parseDouble(minValues[yIndex]));				
				
				spatialExtent.put(c1[0]);
				spatialExtent.put(c2[1]);
				spatialExtent.put(c2[0]);
				spatialExtent.put(c1[1]);			
									
				extentCollection.put("spatial", spatialExtent);
				extentCollection.put("temporal", temporalExtent);
				
				JSONArray linksPerCollection = new JSONArray();
				
				JSONObject linkDescPerCollection = new JSONObject();
				linkDescPerCollection.put("href", "http://saocompute.eurac.edu/rasdaman/ows?&SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + coverageID);
				linkDescPerCollection.put("rel", "self");
				
				JSONObject linkLicensePerCollection = new JSONObject();
				linkLicensePerCollection.put("href", "https://creativecommons.org/licenses/by/4.0/");
				linkLicensePerCollection.put("rel", "license");
				
				linksPerCollection.put(linkDescPerCollection);
				linksPerCollection.put(linkLicensePerCollection);
				
				
				product.put("name", coverage.getChildText("CoverageId", defaultNS));
				product.put("title", coverage.getChildText("CoverageId", defaultNS));
				product.put("description", coverage.getChildText("CoverageId", defaultNS));
				product.put("license", "CC-BY-4.0");
				product.put("extent", extentCollection);
				product.put("links", linksPerCollection);
				productArray.put(product);
			}
			
			data.put("collections", productArray);
			data.put("links", linksCollections);
			return Response.ok(data.toString(4), MediaType.APPLICATION_JSON).build();
			
		} catch (MalformedURLException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
			for( StackTraceElement element: e.getStackTrace()) {
				log.error(element.toString());
			}
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (IOException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
			for( StackTraceElement element: e.getStackTrace()) {
				log.error(element.toString());
			}
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (JDOMException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
			for( StackTraceElement element: e.getStackTrace()) {
				log.error(element.toString());
			}
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage())).build();
		}
	}

	@Override
	public Response dataOpensearchGet(String q, Integer start, Integer rows, SecurityContext securityContext)
			throws NotFoundException {
		// do some magic
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
		//return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
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
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = (Document) builder.build(conn.getInputStream());
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
			Element metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS).getChild("Extension", gmlNS);
			JSONObject metadataObj = new JSONObject();
			if(metadataElement != null) {
				String metadataString1 = metadataElement.getChildText("covMetadata", gmlNS);
				//metadataObj = new JSONObject(metadataString1);
				//String metadataString2 = metadataString1.replaceAll("\\n","");
				//String metadataString3 = metadataString2.replaceAll("\"\"","\"");
				//metadataObj = new JSONObject(metadataString3);
				//JSONArray slices = metadataObj.getJSONArray("slices");
			}
			
			
			String srsDescription = boundingBoxElement.getAttributeValue("srsName");
			try {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG"), srsDescription.indexOf("&")).replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:","");
				
			}catch(StringIndexOutOfBoundsException e) {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG")).replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:","");
							
			}
			
			
            JSONObject extentCollection = new JSONObject();
			
			JSONArray spatialExtent = new JSONArray();
			JSONArray temporalExtent =  new JSONArray();
			
			String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");
			
			String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
			for(int a = 0; a < axis.length; a++) {
				log.debug(axis[a]);
				if(axis[a].equals("E") || axis[a].equals("X")){
					spatialExtent.put(Double.parseDouble(minValues[a]));
					spatialExtent.put(Double.parseDouble(maxValues[a]));
				}
				if(axis[a].equals("N") || axis[a].equals("Y")){
					spatialExtent.put(Double.parseDouble(minValues[a]));
					spatialExtent.put(Double.parseDouble(maxValues[a]));
				}
				if(axis[a].equals("DATE")  || axis[a].equals("ansi") || axis[a].equals("date")){
					temporalExtent.put(minValues[a].replaceAll("\"", ""));
					temporalExtent.put(maxValues[a].replaceAll("\"", ""));
				}
			}
			
			
			
			
			JSONArray links = new JSONArray();
			
			JSONObject linkSelf = new JSONObject();
			linkSelf.put("href", "http://saocompute.eurac.edu/rasdaman/ows?&SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + productId);
			linkSelf.put("rel", "self");
			
			JSONObject linkLicense = new JSONObject();
			linkLicense.put("href", "https://creativecommons.org/licenses/by/4.0/");
			linkLicense.put("rel", "license");
			
			JSONObject linkAbout = new JSONObject();
			linkAbout.put("href", "http://saocompute.eurac.edu/rasdaman/ows?&SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + productId);
			linkAbout.put("title", "http://saocompute.eurac.edu/rasdaman/ows?&SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + productId);
			linkAbout.put("rel", "about");
			
			links.put(linkSelf);
			links.put(linkLicense);
			links.put(linkAbout);
		
			
			JSONArray keywords = new JSONArray();
			//String keyword1 = metadataObj.getString("Project");
			//keywords.put(keyword1);
			
			//String providerName = metadataObj.getString("Creator");
			
			JSONArray provider = new JSONArray();
			JSONObject providerInfo = new JSONObject();
			//providerInfo.put("name", providerName);
			providerInfo.put("url", productId);
			provider.put(providerInfo);
			
			//String title = metadataObj.getString("Title");
								
			JSONObject coverage = new JSONObject();
			
			coverage.put("name", productId);
			//coverage.put("title", title);
			coverage.put("description", productId);
			coverage.put("license", "CC-BY-4.0");
			coverage.put("keywords", keywords);
			coverage.put("provider", provider);
			coverage.put("links", links);
			extentCollection.put("spatial", spatialExtent);
			extentCollection.put("temporal", temporalExtent);
			
			coverage.put("extent", extentCollection);
			coverage.put("eo:epsg", Double.parseDouble(srsDescription));
			coverage.put("sci:citation", productId);
			coverage.put("eo:platform", productId);
			coverage.put("eo:constellation", "Sentinel");
			
			
			JSONObject bandObject = new JSONObject();
			log.debug("number of bands found: " + bandList.size());
			
			for(int c = 0; c < bandList.size(); c++) {
				Element band = bandList.get(c);
				log.debug("band info: " + band.getName() + ":" + band.getAttributeValue("name"));		
				JSONObject product = new JSONObject();
				String bandId = band.getAttributeValue("name");
				//JSONObject bands = metadataObj.getJSONObject("bands");
				//JSONObject bandName = bands.getJSONObject(bandId);
				//String bandWavelength = bandName.getString("WAVELENGTH");
				
				product.put("common_name", bandId);
				//product.put("center_wavelength", bandWavelength);
				product.put("resolution", bandId);
				product.put("scale", bandId);
				product.put("offset", bandId);
				
				bandObject.put(bandId, product);
			}
			
			coverage.put("eo:bands", bandObject);
			//coverage.put("extraMetadata", metadataObj);
			return Response.ok(coverage.toString(4), MediaType.APPLICATION_JSON).build();
		} catch (MalformedURLException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while describing coverage from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (IOException e) {
			log.error("An error occured while describing coverage from WCPS endpoint: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
			return Response.serverError()
					.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,
							"An error occured while describing coverage from WCPS endpoint: " + e.getMessage()))
					.build();
		} catch (JDOMException e) {
			log.error("An error occured while requesting capabilities from WCPS endpoint: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
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