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
			//TODO do the parsing to fill json object with infromation from gml
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
					
			Element boundedByElement = rootNode.getChild("boundedBy", gmlNS);
			Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
			Element metadataElement = null;
			try {
			metadataElement = rootNode.getChild("CoverageDescription", defaultNS).getChild("metadata", gmlNS).getChild("Extension", gmlNS).getChild("covMetadata", gmlNS);
		    }catch(Exception e) {
			log.error("Error in parsing bands :" + e.getMessage());
		    }
			
			List<Element> bandsList = null;
			Boolean bandsMeta = false;
			try {
			bandsList = metadataElement.getChild("bands", gmlNS).getChildren();
			bandsMeta = true;
		    }catch(Exception e) {
			log.error("Error in parsing bands :" + e.getMessage());
		    }
			List<Element> bandsListSwe = rootNode.getChild("CoverageDescription", defaultNS).getChild("rangeType", gmlNS).getChild("DataRecord", sweNS).getChildren("field", sweNS);
			
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
					dimObjects[0].put("axis", axis[a]);
					dimObjects[0].put("type", "spatial");
					dimObjects[0].put("reference_system", Long.parseLong(srsDescription));
				}
				if(axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")){
					yIndex=a;
					dimObjects[1] = new JSONObject();
					dimObjects[1].put("axis", axis[a]);
					dimObjects[1].put("type", "spatial");
					dimObjects[1].put("reference_system", Long.parseLong(srsDescription));
				}
				if(axis[a].equals("DATE")  || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time") || axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi") || axis[a].equals("date") || axis[a].equals("unix")){
					temporalExtent.put(minValues[a].replaceAll("\"", ""));
					temporalExtent.put(maxValues[a].replaceAll("\"", ""));
					dimObjects[2] = new JSONObject();
					dimObjects[2].put("axis", axis[a]);
					dimObjects[2].put("type", "temporal");
					dimObjects[2].put("extent", temporalExtent);
					dimObjects[2].put("step", JSONObject.NULL);
				}
		    }
		
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
