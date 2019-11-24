package eu.openeo.backend.wcps;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

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
			Namespace gmlNS = null;
			Namespace sweNS = null;
			Namespace gmlCovNS = null;
			Namespace gmlrgridNS = null;
			Namespace rasdamanNS = null;
			for (int n = 0; n < namespaces.size(); n++) {
				Namespace current = namespaces.get(n);
				if (current.getPrefix().equals("swe")) {
					sweNS = current;
				}
				if (current.getPrefix().equals("gmlcov")) {
					gmlCovNS = current;
				}
				if (current.getPrefix().equals("gml")) {
					gmlNS = current;
				}
				if (current.getPrefix().equals("gmlrgrid")) {
					gmlrgridNS = current;
				}
				if (current.getPrefix().equals("rasdaman")) {
					rasdamanNS = current;
				}
			}
			log.debug("root node info: " + rootNode.getName());

			Element boundedByElement = rootNode.getChild("boundedBy", gmlNS);
			Element boundingBoxElement = boundedByElement.getChild("Envelope", gmlNS);
			List<Element> gridAxisElementList = rootNode.getChild("domainSet", gmlNS)
					.getChild("ReferenceableGridByVectors", gmlrgridNS).getChildren();

			List<Element> bandsListSwe = rootNode.getChild("rangeType", gmlCovNS).getChild("DataRecord", sweNS)
					.getChildren("field", sweNS);

			String srsDescription = boundingBoxElement.getAttributeValue("srsName");
			try {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG"), srsDescription.indexOf("&"))
						.replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:", "");

			} catch (StringIndexOutOfBoundsException e) {
				srsDescription = srsDescription.substring(srsDescription.indexOf("EPSG")).replace("/0/", ":");
				srsDescription = srsDescription.replaceAll("EPSG:", "");
			}

			String[] minValues = boundingBoxElement.getChildText("lowerCorner", gmlNS).split(" ");
			String[] maxValues = boundingBoxElement.getChildText("upperCorner", gmlNS).split(" ");

			String[] axis = boundingBoxElement.getAttribute("axisLabels").getValue().split(" ");

			int resX = 0;
			int resY = 0;
			log.debug(axis.length);
			JSONArray dimsArray = new JSONArray();
			for (int a = 0; a < axis.length; a++) {
				log.debug(axis[a]);

				if (axis[a].equals("E") || axis[a].equals("X") || axis[a].equals("Long") || axis[a].equals("Lon")) {
					JSONArray longExtent = new JSONArray();
					for (int c = 0; c < gridAxisElementList.size(); c++) {
						Element gridAxis = gridAxisElementList.get(c);
						if (gridAxis.getName().contains("generalGridAxis")
								&& gridAxis.getChild("GeneralGridAxis", gmlrgridNS)
										.getChild("gridAxesSpanned", gmlrgridNS).getValue().equals(axis[a])) {
							String[] resXString = gridAxis.getChild("GeneralGridAxis", gmlrgridNS)
									.getChild("offsetVector", gmlrgridNS).getValue().split(" ");
							resX = Math.abs(Integer.parseInt(resXString[a]));
						}
					}
					for (double c = Double.parseDouble(minValues[a]) + resX; c <= Double
							.parseDouble(maxValues[a]); c = c + resX) {
						longExtent.put(c);
					}
					JSONObject dimObjects = new JSONObject();
					dimObjects.put("name", "x");
					dimObjects.put("coordinates", longExtent);
					dimsArray.put(dimObjects);
				}
				if (axis[a].equals("N") || axis[a].equals("Y") || axis[a].equals("Lat")) {
					JSONArray latExtent = new JSONArray();
					for (int c = 0; c < gridAxisElementList.size(); c++) {
						Element gridAxis = gridAxisElementList.get(c);
						if (gridAxis.getName().contains("generalGridAxis")
								&& gridAxis.getChild("GeneralGridAxis", gmlrgridNS)
										.getChild("gridAxesSpanned", gmlrgridNS).getValue().equals(axis[a])) {
							String[] resYString = gridAxis.getChild("GeneralGridAxis", gmlrgridNS)
									.getChild("offsetVector", gmlrgridNS).getValue().split(" ");
							resY = Math.abs(Integer.parseInt(resYString[a]));
						}
					}
					for (double c = Double.parseDouble(minValues[a]) + resY; c <= Double
							.parseDouble(maxValues[a]); c = c + resY) {
						latExtent.put(c);
					}
					JSONObject dimObjects = new JSONObject();
					dimObjects.put("name", "y");
					dimObjects.put("coordinates", latExtent);
					dimsArray.put(dimObjects);
				}
				if (axis[a].equals("DATE") || axis[a].equals("TIME") || axis[a].equals("ANSI") || axis[a].equals("Time")
						|| axis[a].equals("Date") || axis[a].equals("time") || axis[a].equals("ansi")
						|| axis[a].equals("date") || axis[a].equals("unix")) {
					JSONArray timeExtent = new JSONArray();
					for (int c = 0; c < gridAxisElementList.size(); c++) {
						Element gridAxis = gridAxisElementList.get(c);
						if (gridAxis.getName().contains("generalGridAxis")
								&& gridAxis.getChild("GeneralGridAxis", gmlrgridNS)
										.getChild("gridAxesSpanned", gmlrgridNS).getValue().equals(axis[a])) {
							String[] timeStamps = gridAxis.getChild("GeneralGridAxis", gmlrgridNS)
									.getChild("coefficients", gmlrgridNS).getValue().split(" ");
							for (int t = 0; t < timeStamps.length; t++) {
								timeExtent.put(timeStamps[t]);
							}
						}
					}
					JSONObject dimObjects = new JSONObject();
					dimObjects.put("name", "t");
					dimObjects.put("coordinates", timeExtent);
					dimsArray.put(dimObjects);
				}
			}
			JSONArray bandsArray = new JSONArray();
			for (int c = 0; c < bandsListSwe.size(); c++) {
				Element band = bandsListSwe.get(c);
				String bandId = band.getAttributeValue("name");
				bandsArray.put(bandId);
			}
			JSONObject dimObjects = new JSONObject();
			dimObjects.put("name", "band");
			dimObjects.put("coordinates", bandsArray);

			log.debug(bandsArray);
			dimsArray.put(dimObjects);
			JSONObject hyperCubeArguments = new JSONObject();

			String[] dataElement = rootNode.getChild("rangeSet", gmlNS).getChild("DataBlock", gmlNS)
					.getChildText("tupleList", gmlNS).split(",");
			int valueSize = 0;
			
			//TODO fix data array to correctly represent format as described above
			int[] dimSizes = new int[dimsArray.length()];
			int[] dimPosi = new int[dimsArray.length()];
			for(int d = 0; d < dimsArray.length(); d++) {
				dimSizes[d] = dimsArray.getJSONObject(d).getJSONArray("coordinates").length();
				valueSize *= dimSizes[d];
				dimPosi[d] = 0;
			}

//			for (int a = 0; a < axis.length+1; a++) {
//				dataArray.put(new JSONArray(bandsArray.length()));
//			}
			
			JSONArray dataArray = new JSONArray();
			
			dataArray = createDataArray(dimSizes, dimPosi, dataArray, dimPosi[0], dimSizes[0], dataElement);
			
//			for (int a = 0; a < dataElement.length; a++) {
//				JSONArray dataStringtoArray = new JSONArray();
//				String[] dataString = dataElement[a].split(" ");
//				
//				for (int p = 0; p < bandsArray.length(); p++) {
//					double data = Double.parseDouble(dataString[p]);
//					dataStringtoArray.put(data);
//				}
//				dataArray.put(dataStringtoArray);
//			}

			hyperCubeArguments.put("id", "hyperCube1");
			hyperCubeArguments.put("dimensions", dimsArray);
			hyperCubeArguments.put("data", dataArray);

			JSONArray hyperCubesArray = new JSONArray();

			hyperCubesArray.put(hyperCubeArguments);

			resultJSON.put("id", "hypercube_example");
			resultJSON.put("proj", "EPSG:" + srsDescription);
			resultJSON.put("hypercubes", hyperCubesArray);
			log.debug(resultJSON);

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
	
	
	private JSONArray createDataArray(int[] dimSizes, int[] dimPosi, JSONArray dataArray, int currentDimIndex, int currentDimSize, String[] values) {
		String dimPosiString = "";
		int[] dimPosiLocal = new int[dimPosi.length];
		for(int d = 1; d < dimSizes.length-1; d++) {
			dimPosiLocal[d] = dimPosi[d];
			dimPosiString += dimPosiLocal[d] + " ";
		}
		log.debug("index: " + currentDimIndex + " size: " + currentDimSize + "posis: " +dimPosiString);
		if(currentDimIndex == dimSizes.length -1) {
			int valueIndex = 0;
			for(int d = 1; d < dimSizes.length-1; d++) {
				valueIndex *=(dimPosiLocal[d]+dimSizes[d]*dimPosiLocal[d-1]);
			}
//			System.out.println(valueIndex);
			for(int s = 0; s < currentDimSize; s++) {				
				dataArray.put(values[valueIndex].split(" ")[s]);
//				System.out.print (values[valueIndex]);
			}
//			System.out.println();
		}else {
			for(int index = 0; index < currentDimSize; index++) {
				dimPosiLocal[currentDimIndex] = index;
				JSONArray subDataArray = new JSONArray();
				dataArray.put(createDataArray(dimSizes, dimPosiLocal, subDataArray, currentDimIndex+1, dimSizes[currentDimIndex+1], values));
			}
		}
		return dataArray;
	}

}
