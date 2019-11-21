package eu.openeo.backend.wcps;

import java.io.InputStream;
import java.util.List;

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

import io.jsonwebtoken.io.IOException;

public class HyperCubeFactory {
	
	Logger log = Logger.getLogger(this.getClass());
	
	public HyperCubeFactory() {
		
	}
	
	public JSONObject getHyperCubeFromGML(InputStream inputStream) {
		JSONObject resultJSON = new JSONObject();
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document capabilititesDoc = (Document) saxBuilder.build(inputStream);
			List<Namespace> namespaces = capabilititesDoc.getNamespacesIntroduced();
			Element rootNode = capabilititesDoc.getRootElement();
			Namespace defaultNS = rootNode.getNamespace();
			//TODO do the parsing to fill HyperCube JSON object with information from GML
			Namespace gmlNS = null;
			Namespace sweNS = null;
			Namespace gmlCovNS =  null;
			Namespace gmlrgridNS = null;
			Namespace rasdamanNS = null;
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
				if(current.getPrefix().equals("rasdaman")) {
					rasdamanNS = current;
				}
			}			
			log.debug("root node info: " + rootNode.getName());		
					
			Element boundedByElement = rootNode.getChild("boundedBy", gmlNS);
			Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
			List<Element> gridAxisElementList = rootNode.getChild("domainSet", gmlNS).getChild("ReferenceableGridByVectors", gmlrgridNS).getChildren();
			Element metadataElement = null;
			try {
			metadataElement = rootNode.getChild("metadata", gmlCovNS).getChild("Extension", gmlCovNS).getChild("covMetadata", rasdamanNS);
		    }catch(Exception e) {
			log.error("Error in parsing Metadata :" + e.getMessage());
		    }
			
			List<Element> bandsList = null;
			Boolean bandsMeta = false;
			try {
			bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
			bandsMeta = true;
		    }catch(Exception e) {
			log.error("Error in parsing bands :" + e.getMessage());
		    }
			List<Element> bandsListSwe = rootNode.getChild("rangeType", gmlCovNS).getChild("DataRecord", sweNS).getChildren("field", sweNS);
			
			//metadataObj = new JSONObject(metadataString1);
			//String metadataString2 = metadataString1.replaceAll("\\n","");
			//String metadataString3 = metadataString2.replaceAll("\"\"","\"");
			//metadataObj = new JSONObject(metadataString3);
			//JSONArray slices = metadataObj.getJSONArray("slices");
			
			String srsDescription = boundingBoxElement.getAttributeValue("srsName");
			try {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG"), srsDescription.indexOf("&")).replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:","");
				
			}catch(StringIndexOutOfBoundsException e) {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG")).replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:","");							
			}			
			
            String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");			
			
			String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");
		    JSONArray longExtent = new JSONArray();			
			JSONArray latExtent = new JSONArray();
			JSONArray timeExtent = new JSONArray();		
			int resX = 0;
			int resY = 0;
		    JSONObject[] dimObjects = new JSONObject[axis.length+1];
		    JSONArray dimsArray = new JSONArray();
		    for(int a = 0; a < axis.length; a++) {
		    	log.debug(axis[a]);
				if(axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long")){
					
					for(int c = 0; c < gridAxisElementList.size(); c++) {
						Element gridAxis = gridAxisElementList.get(c);
						if (gridAxis.getName().contains("generalGridAxis") && gridAxis.getChild("GeneralGridAxis", gmlrgridNS).getChild("gridAxesSpanned", gmlrgridNS).getValue().equals(axis[a])) {
							String[] resXString = gridAxis.getChild("GeneralGridAxis", gmlrgridNS).getChild("offsetVector", gmlrgridNS).getValue().split(" ");
							resX = Integer.parseInt(resXString[a]);
						}
					}
					
					
					for(int c = Integer.parseInt(minValues[a]); c <= Integer.parseInt(maxValues[a]); c=c+resX) {
						longExtent.put(c);						
					}					
					
					dimObjects[2] = new JSONObject();
					dimObjects[2].put("name", axis[a]);
					dimObjects[2].put("coordinates", longExtent);
					}
				if(axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
					
					for(int c = 0; c < gridAxisElementList.size(); c++) {
						Element gridAxis = gridAxisElementList.get(c);
						if (gridAxis.getName().contains("generalGridAxis") && gridAxis.getChild("GeneralGridAxis", gmlrgridNS).getChild("gridAxesSpanned", gmlrgridNS).getValue().equals(axis[a])) {
							String[] resXString = gridAxis.getChild("GeneralGridAxis", gmlrgridNS).getChild("offsetVector", gmlrgridNS).getValue().split(" ");
							resY = Integer.parseInt(resXString[a]);
						}
					}
					
					for(int c = Integer.parseInt(minValues[a]); c <= Integer.parseInt(maxValues[a]); c=c+resY) {
						latExtent.put(c);						
					}
					
					dimObjects[1] = new JSONObject();
					dimObjects[1].put("name", axis[a]);
					dimObjects[1].put("coordinates", latExtent);
					}
				if(axis[a].equals("DATE")  || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time") || axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
//					temporalExtent.put(minValues[a].replaceAll("\"", ""));
//					temporalExtent.put(maxValues[a].replaceAll("\"", ""));
					
					for(int c = 0; c < gridAxisElementList.size(); c++) {
						Element gridAxis = gridAxisElementList.get(c);
						if (gridAxis.getName().contains("generalGridAxis") && gridAxis.getChild("GeneralGridAxis", gmlrgridNS).getChild("gridAxesSpanned", gmlrgridNS).getValue().equals(axis[a])) {
							String[] timeStamps = gridAxis.getChild("GeneralGridAxis", gmlrgridNS).getChild("coefficients", gmlrgridNS).getValue().split(" ");
							for(int t = 0; t < axis.length; t++) {
								timeExtent.put(axis[t]);
							}
						}
					}
					
					dimObjects[0] = new JSONObject();
					dimObjects[0].put("name", axis[a]);
					dimObjects[0].put("coordinates", timeExtent);
				}
		    }
		    JSONArray bandsArray = new JSONArray();
		    for(int c = 0; c < bandsList.size(); c++) {
				JSONObject product = new JSONObject();				
				String bandWave = null;
				Element band = bandsList.get(c);
				String bandId = band.getName();
			    bandsArray.put(bandId);
				
		    }
		    dimObjects[0] = new JSONObject();
			dimObjects[0].put("name", "band");
			dimObjects[0].put("coordinates", bandsArray);
		    dimsArray.put(dimObjects);
		    JSONObject[] hyperCubeArguments = new JSONObject[2];		    
		    JSONArray hyperCubeData = new JSONArray();
		    
		    String[] dataElement = rootNode.getChild("rangeSet", gmlNS).getChild("DataBlock", gmlNS).getChildText("tupleList", gmlNS).split(",");
		    
		    hyperCubeArguments[0].put("dimensions", dimsArray);
		    hyperCubeArguments[1].put("array", dataElement);
		    
		    JSONArray hyperCubesArray = new JSONArray();
		    JSONObject hyperCubesObject = new JSONObject();
		    
		    hyperCubesArray.put(hyperCubeArguments);
		    
		    hyperCubesObject.put("id", "hypercube_example");
		    hyperCubesObject.put("hypercubes", hyperCubesArray);
		
		} catch (JDOMException e) {
			log.error("Error when parsing XML");
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (java.io.IOException e) {
			log.error("Error when receiving input stream");
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		return resultJSON;
	}
	
}