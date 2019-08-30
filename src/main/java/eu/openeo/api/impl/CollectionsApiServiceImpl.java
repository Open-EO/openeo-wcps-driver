package eu.openeo.api.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.validation.constraints.Pattern;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;

import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.gdal.gdal.gdal;

import eu.openeo.api.ApiResponseMessage;
import eu.openeo.api.CollectionsApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.ConvenienceHelper;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class CollectionsApiServiceImpl extends CollectionsApiService {
	
	Logger log = Logger.getLogger(this.getClass());
	
	public CollectionsApiServiceImpl() {
		gdal.AllRegister();
	}
	
    @Override
    public Response collectionsCollectionIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~/]+$")String collectionId, SecurityContext securityContext) throws NotFoundException {
    	URL url;
		try {
			url = new URL(ConvenienceHelper.readProperties("wcps-endpoint")
					+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionId);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			SAXBuilder builder = new SAXBuilder();
			Document capabilititesDoc = (Document) builder.build(conn.getInputStream());
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			Namespace gmlNS = null;
			Namespace sweNS = null;
			Namespace gmlCovNS =  null;
			Namespace gmlrgridNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if(current.getPrefix().equals("swe")) {
					sweNS = current;
				}
				if(current.getPrefix().equals("gmlcov")) {
					gmlCovNS = current;
				}
				if(current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
				if(current.getPrefix().equals("gmlrgrid")) {
					gmlrgridNS = current;
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
			
			
			SpatialReference src = new SpatialReference();
			src.ImportFromEPSG(Integer.parseInt(srsDescription));

			SpatialReference dst = new SpatialReference();
			dst.ImportFromEPSG(4326);
			
			
			String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");
			
			
			CoordinateTransformation tx = new CoordinateTransformation(src, dst);
		    
		    String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
		    int xIndex = 0;
		    int yIndex = 0;
		    JSONObject[] dimObjects = new JSONObject[axis.length+1];
		    for(int a = 0; a < axis.length; a++) {
		    	log.debug(axis[a]);
				if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long")){
					xIndex=a;
					dimObjects[0] = new JSONObject();
					dimObjects[0].put("axis", "x");
					dimObjects[0].put("type", "spatial");
					dimObjects[0].put("reference_system", Long.parseLong(srsDescription));
				}
				if(axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
					yIndex=a;
					dimObjects[1] = new JSONObject();
					dimObjects[1].put("axis", "y");
					dimObjects[1].put("type", "spatial");
					dimObjects[1].put("reference_system", Long.parseLong(srsDescription));
				}
				if(axis[a].equals("DATE")  || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
					temporalExtent.put(minValues[a].replaceAll("\"", ""));
					temporalExtent.put(maxValues[a].replaceAll("\"", ""));
					dimObjects[2] = new JSONObject();
					dimObjects[2].put("axis", "temporal");
					dimObjects[2].put("type", "temporal");
					dimObjects[2].put("extent", temporalExtent);
					dimObjects[2].put("step", JSONObject.NULL);
				}
		    }
		    
			log.debug(srsDescription);
			
			double[] c1 = null;
			double[] c2 = null;
			c1 = tx.TransformPoint(Double.parseDouble(minValues[xIndex]), Double.parseDouble(minValues[yIndex]));
			c2 = tx.TransformPoint(Double.parseDouble(maxValues[xIndex]), Double.parseDouble(maxValues[yIndex]));				
			
			spatialExtent.put(c1[0]);
			spatialExtent.put(c1[1]);
			spatialExtent.put(c2[0]);
			spatialExtent.put(c2[1]);
			
			JSONArray xExtent = new JSONArray();
			xExtent.put(c1[0]);
			xExtent.put(c2[0]);
			dimObjects[0].put("extent", xExtent);
			JSONArray yExtent = new JSONArray();
			yExtent.put(c1[1]);
			yExtent.put(c2[1]);
			dimObjects[1].put("extent", yExtent);			
			
			JSONArray links = new JSONArray();
			
			JSONObject linkSelf = new JSONObject();
			linkSelf.put("href", ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionId);
			linkSelf.put("rel", "self");
			
			JSONObject linkAlternate = new JSONObject();
			linkAlternate.put("href", ConvenienceHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionId);
			linkAlternate.put("rel", "alternate");
			
			JSONObject linkLicense = new JSONObject();
			linkLicense.put("href", "https://creativecommons.org/licenses/by/4.0/");
			linkLicense.put("rel", "license");
			
			JSONObject linkAbout = new JSONObject();
			linkAbout.put("href", ConvenienceHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionId);
			linkAbout.put("title", ConvenienceHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + collectionId);
			linkAbout.put("rel", "about");
			
			links.put(linkSelf);
			links.put(linkAlternate);
			links.put(linkLicense);
			links.put(linkAbout);
		
			JSONArray keywords = new JSONArray();
			//String keyword1 = metadataObj.getString("Project");
			//keywords.put(keyword1);
			
			//String providerName = metadataObj.getString("Creator");
			
			JSONArray roles1 = new JSONArray();
			//String role1 = metadataObj.getString("Roles");
			//keywords.put(role1);
			
			JSONArray provider1 = new JSONArray();
			JSONObject provider1Info = new JSONObject();
			provider1Info.put("name", "Eurac Research - Institute for Earth Observation");
			provider1Info.put("roles", roles1);
			provider1Info.put("url", "http://www.eurac.edu");
			provider1.put(provider1Info);
			
			
			JSONObject properties = new JSONObject();
			
			JSONArray bandArray = new JSONArray();
			log.debug("number of bands found: " + bandList.size());
			
			dimObjects[3] = new JSONObject();
			dimObjects[3].put("type", "bands");
			dimObjects[3].put("axis", "spectral");
			JSONArray bandValues = new JSONArray();
			
			for(int c = 0; c < bandList.size(); c++) {
				Element band = bandList.get(c);
				Double bandWave = null;
				try {
					bandWave = Double.parseDouble(band.getChild("Quantity", sweNS).getChildText("description", sweNS).split(" ")[3])/1000d;
				}catch(Exception e) {
					log.error("Error in parsing band wave-lenght:" + e.getMessage());
				}
				
				log.debug("band info: " + band.getName() + ":" + band.getAttributeValue("name"));		
				JSONObject product = new JSONObject();
				String bandId = band.getAttributeValue("name");
				
				product.put("common_name", bandId);
				
				product.put("name", bandId);
				bandValues.put(bandId);
				product.put("center_wavelength", bandWave);
				try {
					//TODO read this from meta data and not from collection name
					product.put("gsd", Double.parseDouble(collectionId.split("_")[3].substring(0, collectionId.split("_")[3].length()-1)));
				}catch(Exception e) {
					log.error("Error in parsing band wave-lenght:" + e.getMessage());
				}
				
				bandArray.put(product);
			}
			
			dimObjects[3].put("values", bandValues);
			
			JSONArray epsg_values = new JSONArray();
			JSONObject epsgvalues = new JSONObject();
			
			epsgvalues.put("values", epsg_values);
			
			JSONArray platform_values = new JSONArray();
			JSONObject pltfrmvalues = new JSONObject();
			pltfrmvalues.put("values", platform_values);
			
			JSONArray cloud_cover = new JSONArray();
			JSONObject cloud_cover_extent = new JSONObject();
			cloud_cover_extent.put("extent", cloud_cover);
			
			JSONObject cube_dimensions = new JSONObject();
			for(JSONObject dim: dimObjects) {
				cube_dimensions.put(dim.getString("axis"), dim);
			}			
			
			properties.put("cube:dimensions", cube_dimensions);
			properties.put("eo:epsg", Double.parseDouble(srsDescription));
			properties.put("sci:citation", "Copernicus Sentinel data 2019");
			properties.put("eo:constellation", "Sentinel-2");
			properties.put("eo:instrument", "MSI");
			properties.put("eo:bands", bandArray);
			
			//other_properties.put("eo:platform", pltfrmvalues);
			//other_properties.put("eo:epsg", epsgvalues);
			//other_properties.put("eo:cloud_cover", cloud_cover_extent);
			
			//String title = metadataObj.getString("Title");
								
			JSONObject coverage = new JSONObject();
			
			//coverage.put("extraMetadata", metadataObj);
			coverage.put("stac_version", "0.6.2");
			coverage.put("id", collectionId);
			coverage.put("title", collectionId);
			coverage.put("description", collectionId);
			coverage.put("license", "CC-BY-4.0");
			coverage.put("keywords", keywords);
			coverage.put("providers", provider1);
			coverage.put("links", links);
			extentCollection.put("spatial", spatialExtent);
			extentCollection.put("temporal", temporalExtent);
			coverage.put("extent", extentCollection);
			coverage.put("properties", properties);
			//coverage.put("other_properties", other_properties);
			

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
    public Response collectionsGet(SecurityContext securityContext) throws NotFoundException {
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
				linkDesc.put("href", ConvenienceHelper.readProperties("openeo-endpoint") + "/collections");
				linkDesc.put("rel", "self");
				
				JSONObject linkCsw = new JSONObject();
				linkCsw.put("href", ConvenienceHelper.readProperties("wcps-endpoint"));
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
						+ "?SERVICE=WCS&VERSION=2.0.1&REQUEST=DescribeCoverage&COVERAGEID=" + coverageID);
				
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
				c1 = tx.TransformPoint(Double.parseDouble(minValues[xIndex]), Double.parseDouble(minValues[yIndex]));
				c2 = tx.TransformPoint(Double.parseDouble(maxValues[xIndex]), Double.parseDouble(maxValues[yIndex]));				
				
				spatialExtent.put(c1[0]);
				spatialExtent.put(c1[1]);
				spatialExtent.put(c2[0]);
				spatialExtent.put(c2[1]);			
									
				extentCollection.put("spatial", spatialExtent);
				extentCollection.put("temporal", temporalExtent);
				
				JSONArray linksPerCollection = new JSONArray();
				
				JSONObject linkDescPerCollection = new JSONObject();
				linkDescPerCollection.put("href", ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + coverageID);
				linkDescPerCollection.put("rel", "self");
				
				JSONObject linkLicensePerCollection = new JSONObject();
				linkLicensePerCollection.put("href", "https://creativecommons.org/licenses/by/4.0/");
				linkLicensePerCollection.put("rel", "license");
				
				linksPerCollection.put(linkDescPerCollection);
				linksPerCollection.put(linkLicensePerCollection);
				
				
				JSONArray keywords = new JSONArray();
				//String keyword1 = metadataObj.getString("Keywords");
				//keywords.put(keyword1);
				
				//String providerName1 = metadataObj.getString("Creator");
				
				JSONArray roles1 = new JSONArray();
				//String role1 = metadataObj.getString("Roles");
				//keywords.put(role1);
				
				JSONArray provider1 = new JSONArray();
				JSONObject provider1Info = new JSONObject();
				provider1Info.put("name", ConvenienceHelper.readProperties("provider-name"));
				provider1Info.put("roles", roles1);
				provider1Info.put("url", ConvenienceHelper.readProperties("provider-url"));
				provider1.put(provider1Info);
				
				//String title = metadataObj.getString("Title");
				//String desc = metadataObj.getString("Description");
				
				product.put("stac_version", "0.6.2");
				product.put("id", coverage.getChildText("CoverageId", defaultNS));
				product.put("title", coverage.getChildText("CoverageId", defaultNS));
				product.put("description", coverage.getChildText("CoverageId", defaultNS));
				product.put("license", "CC-BY-4.0");
				product.put("extent", extentCollection);
				product.put("keywords", keywords);
				product.put("providers", provider1);
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
}
