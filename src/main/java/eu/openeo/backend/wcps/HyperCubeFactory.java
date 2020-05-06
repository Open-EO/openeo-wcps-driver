package eu.openeo.backend.wcps;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayLong;
import ucar.ma2.ArrayString;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

public class HyperCubeFactory {

	Logger log = LogManager.getLogger();

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
			log.debug(dimsArray);
			JSONObject hyperCubeArguments = new JSONObject();

			String[] dataElement = rootNode.getChild("rangeSet", gmlNS).getChild("DataBlock", gmlNS)
					.getChildText("tupleList", gmlNS).split(",");
			int valueSize = 0;
			
			int[] dimSizes = new int[dimsArray.length()];
			int[] dimPosi = new int[dimsArray.length()];
			for(int d = 0; d < dimsArray.length(); d++) {
				dimSizes[d] = dimsArray.getJSONObject(d).getJSONArray("coordinates").length();
				valueSize *= dimSizes[d];
				dimPosi[d] = 0;
				log.debug("Dimenson: " + dimsArray.getJSONObject(d).getString("name") + " Size: " + dimSizes[d]);
			}
			
			JSONArray dataArray = new JSONArray();
			
			dataArray = createDataArray(dimPosi, dimSizes, dataArray, 0, dataElement);

			hyperCubeArguments.put("id", "hyper_cube");
			hyperCubeArguments.put("dimensions", dimsArray);
			hyperCubeArguments.put("data", dataArray);

			JSONArray hyperCubesArray = new JSONArray();

			hyperCubesArray.put(hyperCubeArguments);

			resultJSON.put("id", "hypercube_example");
			//TODO check if we want epsg: or not
			resultJSON.put("proj", "EPSG:" + srsDescription);
			//resultJSON.put("proj", srsDescription);
			resultJSON.put("hypercubes", hyperCubesArray);
			//log.debug(resultJSON);

		} catch (JDOMException e) {
			log.error("Error when parsing XML: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (java.io.IOException e) {
			log.error("Error when receiving input stream" + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		return resultJSON;
	}
	
	
	private JSONArray createDataArray(int[] dimPosi, int[] dimSizes, JSONArray dataArray, int currentDimIndex, String[] values) {
		if(currentDimIndex == dimSizes.length -1) {
			int valueIndex = 0;
			for(int d = 0; d < dimSizes.length-1; d++) {
				int multiplier=1;
				for(int m = d+1; m < dimSizes.length-1; m++ ) {
					multiplier*=dimSizes[m];
				}
				valueIndex += dimPosi[d]*multiplier;
			}
			for(int s = 0; s < dimSizes[currentDimIndex]; s++) {				
				dataArray.put(Double.parseDouble(values[valueIndex].split(" ")[s]));
			}
		}else {
			for(int index = 0; index < dimSizes[currentDimIndex]; index++) {
				dimPosi[currentDimIndex] = index;
				JSONArray subDataArray = new JSONArray();
				dataArray.put(createDataArray(dimPosi, dimSizes, subDataArray, currentDimIndex+1, values));
			}
		}
		return dataArray;
	}
	
	public int writeHyperCubeToNetCDFBandAsDimension(JSONObject hyperCube, String srs, String path) {
		try {
			NetcdfFileWriter writer = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf4, path, null);
			JSONArray bandsArray = new JSONArray();
			JSONArray dimensionsArray = hyperCube.getJSONArray("dimensions");
			List<Dimension> dims = new ArrayList<Dimension>();
			HashMap<String, Variable> dimVars =  new HashMap<String, Variable>();
			HashMap<String, Object> coordinateArray = new HashMap<String,Object>();
			Iterator iterator = dimensionsArray.iterator();
			while(iterator.hasNext()) {
				JSONObject dimensionDescriptor = (JSONObject) iterator.next();
				JSONArray coordinateLables = dimensionDescriptor.getJSONArray("coordinates");
				Dimension dimension = writer.addDimension(dimensionDescriptor.getString("name"), coordinateLables.length());
				dims.add(dimension);
				List<Dimension> currentDim = new ArrayList<Dimension>();
				currentDim.add(dimension);
				if(dimensionDescriptor.getString("name").equals("t")) {
					Variable dimVar = writer.addVariable(dimensionDescriptor.getString("name"), DataType.LONG, currentDim);
					dimVars.put(dimensionDescriptor.getString("name"), dimVar);
					ArrayLong dataArray = new ArrayLong(new int[] {dimension.getLength()}, false);
					for(int i = 0; i < dimension.getLength(); i++) {
						String timeLabel = coordinateLables.getString(i).replace('"', ' ').trim();
						if(!timeLabel.contains("T")) {
							timeLabel =  timeLabel + "T00:00:00.000Z";
						}
						Long epoch = ZonedDateTime.parse(timeLabel).toEpochSecond();
						dataArray.setLong(i, epoch);
					}
					coordinateArray.put(dimensionDescriptor.getString("name"),dataArray);
				}else if(dimensionDescriptor.getString("name").equals("band")) {
					Variable dimVar = writer.addVariable(dimensionDescriptor.getString("name"), DataType.LONG, currentDim);
					dimVars.put(dimensionDescriptor.getString("name"), dimVar);
					ArrayLong dataArray = new ArrayLong(new int[] {dimension.getLength()}, false);
					for(int i = 0; i < dimension.getLength(); i++) {
						//TODO fix this to put the actual band name as string instead of integer ID of band
						dataArray.setLong(i, (long)i+1);
						bandsArray.put(coordinateLables.getString(i));
					}
					coordinateArray.put(dimensionDescriptor.getString("name"),dataArray);
				}else {					
					Variable dimVar = writer.addVariable(dimensionDescriptor.getString("name"), DataType.DOUBLE, currentDim);
					log.debug(dimVar.getFullName());
					dimVar.addAttribute(new Attribute("resolution", new Double(coordinateLables.getDouble(1) -coordinateLables.getDouble(0))));
					dimVars.put(dimensionDescriptor.getString("name"), dimVar);
					ArrayDouble dataArray = new ArrayDouble(new int[] {dimension.getLength()});
					for(int i = 0; i < dimension.getLength(); i++) {
						dataArray.setDouble(i, coordinateLables.getDouble(i));
					}
					coordinateArray.put(dimensionDescriptor.getString("name"),dataArray);
				}
			}
			Variable values = writer.addVariable(hyperCube.getString("id"), DataType.DOUBLE, dims);
			
			writer.addGlobalAttribute(new Attribute("EPSG", Integer.parseInt(srs.replace("EPSG:", ""))));
			writer.addGlobalAttribute(new Attribute("JOB", path.substring(path.lastIndexOf('/')+1, path.lastIndexOf('.'))));
			log.debug(hyperCube);
			log.debug(bandsArray);
			log.debug(coordinateArray);
			for(int i = 0; i < bandsArray.length(); i++) {
				writer.addGlobalAttribute(new Attribute("BAND"+i+1, bandsArray.getString(i)));
			}
			writer.create();
			writer.flush();
						
			int[] shape = values.getShape();
			int[] indexND =  new int[shape.length];
			for(String key: coordinateArray.keySet()) {
				if(key.equals("t")) {
					ArrayLong dataArray = (ArrayLong) coordinateArray.get(key);
					writer.write(dimVars.get(key), new int[shape.length], dataArray);
				}else if(key.equals("band")) {
					ArrayLong dataArray = (ArrayLong) coordinateArray.get(key);
					writer.write(dimVars.get(key), new int[shape.length], dataArray);
				}else{
					ArrayDouble dataArray = (ArrayDouble) coordinateArray.get(key);
					writer.write(dimVars.get(key), new int[shape.length], dataArray);
				}
			}
			
			log.debug("Number of dimensions: " + shape.length);
			
			ArrayDouble dataArray = new ArrayDouble(shape);
			Index dataIndex = dataArray.getIndex();
			
			for (long index1D = 0; index1D < values.getSize(); index1D++) {				
				JSONArray subDimArray = hyperCube.getJSONArray("data");
				long divider = index1D;
				for(int n = shape.length-1; n >= 0; n--) {
					indexND[n] =  (int) (divider % shape[n]);
					divider /= shape[n];
				}
				String indexS = "";
				for(int k = 0; k < shape.length-1; k++) {
					subDimArray = subDimArray.getJSONArray(indexND[k]);
					indexS += indexND[k] + " ";
				}
				indexS += indexND[shape.length-1];				
				double value = subDimArray.getDouble(indexND[shape.length-1]);
				//log.debug(index1D + " | " + indexS + " : " + value);
				dataArray.setDouble(dataIndex.set(indexND), value);
			}
			
			writer.write(values, new int[shape.length], dataArray);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			log.error("Error when writing hypercube to netcdf file: " + path + " " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return -1;
		} catch (InvalidRangeException e) {
			log.error("Error when writing hypercube to netcdf file: " + path + " " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return -1;
		}
		return 0;
	}
	
	public int writeHyperCubeToNetCDFBandAsVariable(JSONObject hyperCube, String srs, String path) {
		try {
			NetcdfFileWriter writer = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf4, path, null);
			JSONArray dimensionsArray = hyperCube.getJSONArray("dimensions");
			List<Dimension> dims = new ArrayList<Dimension>();
			HashMap<String, Variable> dimVars =  new HashMap<String, Variable>();
			HashMap<String, Variable> bandVars =  new HashMap<String, Variable>();
			HashMap<String, Integer> bandIndices =  new HashMap<String, Integer>();
			HashMap<String, Object> coordinateArray = new HashMap<String,Object>();
			Iterator iterator = dimensionsArray.iterator();
			//Iterate over all dimensions in json representation of hypercube
			Integer bandDimensionIndex = -1;
			//for( int d = 0; d<dimensionsArray.length(); d++) {
			int d = 0;
			while(iterator.hasNext()) {
				JSONObject dimensionDescriptor = (JSONObject) iterator.next();
				//JSONObject dimensionDescriptor = dimensionsArray.getJSONObject(d);
				JSONArray coordinateLables = dimensionDescriptor.getJSONArray("coordinates");
				Dimension dimension = writer.addDimension(dimensionDescriptor.getString("name"), coordinateLables.length());
				List<Dimension> currentDim = new ArrayList<Dimension>();
				currentDim.add(dimension);
				//If it contains a temporal axis create a corresponding dimension for the outgoing netcdf file
				if(dimensionDescriptor.getString("name").toLowerCase().equals("t")) {
					log.debug("Creating temporal dimension: " + dimensionDescriptor.getString("name"));
					Variable dimVar = writer.addVariable(dimensionDescriptor.getString("name"), DataType.LONG, currentDim);
					dimVars.put(dimensionDescriptor.getString("name"), dimVar);
					ArrayLong dataArray = new ArrayLong(new int[] {coordinateLables.length()}, false);
					for(int i = 0; i < coordinateLables.length(); i++) {
						String timeLabel = coordinateLables.getString(i).replace('"', ' ').trim();
						if(!timeLabel.contains("T")) {
							timeLabel =  timeLabel + "T00:00:00.000Z";
						}
						log.trace(timeLabel);
						Long epoch = ZonedDateTime.parse(timeLabel).toEpochSecond();
						dataArray.setLong(i, epoch);
					}
					coordinateArray.put(dimensionDescriptor.getString("name"),dataArray);
					dims.add(dimension);
				//If it contains a band axis create a corresponding variable for each band for the outgoing netcdf file
				}else if(dimensionDescriptor.getString("name").toLowerCase().equals("band")) {
					log.debug("Found band dimension: " + dimensionDescriptor.getString("name"));
					for(int i = 0; i < coordinateLables.length(); i++) {
						log.debug("Registering band with name: " + coordinateLables.getString(i));
						bandIndices.put(coordinateLables.getString(i), new Integer(i));
					}
					bandDimensionIndex = new Integer(d);
					log.debug("Band Dimension Index: " + bandDimensionIndex);
				//For any other axis just create it and add it as a dimension to the outgoing netcdf file
				}else {	
					log.debug("Creating dimension: " + dimensionDescriptor.getString("name"));
					Variable dimVar = writer.addVariable(dimensionDescriptor.getString("name"), DataType.DOUBLE, currentDim);
					dimVar.addAttribute(new Attribute("resolution", new Double(coordinateLables.getDouble(1) -coordinateLables.getDouble(0))));
					dimVars.put(dimensionDescriptor.getString("name"), dimVar);
					ArrayDouble dataArray = new ArrayDouble(new int[] {coordinateLables.length()});
					for(int i = 0; i < coordinateLables.length(); i++) {
						log.trace(coordinateLables.getDouble(i));
						dataArray.setDouble(i, coordinateLables.getDouble(i));
					}
					coordinateArray.put(dimensionDescriptor.getString("name"),dataArray);
					dims.add(dimension);
				}
				d++;
			}
			int[] shape = null;
			int i=0;
			for(String bandName: bandIndices.keySet()){
				Variable bandVar = writer.addVariable(bandName, DataType.DOUBLE, dims);
				bandVars.put(bandName, bandVar);
				shape = bandVar.getShape();
				writer.addGlobalAttribute(new Attribute("Band"+i, bandName));
				i=i+1;
			}
			// add metadata attributes to file concerning openEO job and crs
			//writer.addGlobalAttribute(new Attribute("EPSG", srs));
			writer.addGlobalAttribute(new Attribute("Bands", i));
			writer.addGlobalAttribute(new Attribute("EPSG", Integer.parseInt(srs.replace("EPSG:", ""))));
			writer.addGlobalAttribute(new Attribute("JOB", path.substring(path.lastIndexOf('/')+1, path.lastIndexOf('.'))));
			// write header of netcdf file to disk.
			writer.create();
			writer.flush();
			
			// write coordinate labels to netcdf file
			for(String key: coordinateArray.keySet()) {
				log.debug("Writing dimension axis labels to file: " + key);
				if(key.equals("t")) {
					ArrayLong dataArray = (ArrayLong) coordinateArray.get(key);
					writer.write(dimVars.get(key), new int[shape.length], dataArray);
				}else{
					ArrayDouble dataArray = (ArrayDouble) coordinateArray.get(key);
					writer.write(dimVars.get(key), new int[shape.length], dataArray);
				}
				log.debug("Finished writing dimension axis labels to file: " + key);
			}		
			
			writer.flush();
			
			int noOfBands = bandIndices.size();
			log.debug("Band Dimension Index: " + bandDimensionIndex);
			// now write data of each individual band to disk:
			for(String bandName: bandIndices.keySet()){
				log.debug("Writing data of band: " + bandName);
				// First create variable for band
				Variable bandVar = bandVars.get(bandName);
				Integer bandIndex = bandIndices.get(bandName);
				
//				int[] shape = bandVar.getShape();
				int[] indexND =  new int[shape.length];
				
				
				log.debug("Number of dimensions: " + shape.length);
				
				ArrayDouble dataArray = new ArrayDouble(shape);
				Index dataIndex = dataArray.getIndex();
				
				// run loop over all pixels in band variable as one linear dimension
				for (long index1D = 0; index1D < bandVar.getSize(); index1D++) {				
					JSONArray subDimArray = hyperCube.getJSONArray("data");
					long divider = index1D;
					
					// find mapping between the mapping of the linear representation and the n-dimensional array description
					for(int n = shape.length-1; n >= 0; n--) {
						indexND[n] =  (int) (divider % shape[n]);
						divider /= shape[n];
					}
					String indexS = "";
					
					// find value in n-dimensional hypercube at current position in linear domain
					int bandFoundSubstractor = 0;
					for(int k = 0; k < shape.length; k++) {
						if(k == bandDimensionIndex) {
							subDimArray = subDimArray.getJSONArray(bandIndex);
							indexS += bandIndex + " ";
							bandFoundSubstractor = 1;
						}else {
							subDimArray = subDimArray.getJSONArray(indexND[k - bandFoundSubstractor]);
							indexS += indexND[k] + " ";
						}
					}
					Double value = null; 
					if(noOfBands-1 == bandDimensionIndex) {
						indexS += bandIndex;				
						value = subDimArray.getDouble(bandIndex);
					}else {
						indexS += indexND[shape.length-1];				
						value = subDimArray.getDouble(indexND[shape.length-1]);
					}
					log.trace(index1D + " | " + indexS + " : " + value);
					dataArray.setDouble(dataIndex.set(indexND), value);
				}
				
				writer.write(bandVar, new int[shape.length], dataArray);
				writer.flush();
				log.debug("Finished writing data of band: " + bandName);
			}
			writer.close();
		} catch (IOException e) {
			log.error("Error when writing hypercube to netcdf file: " + path + " " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return -1;
		} catch (InvalidRangeException e) {
			log.error("Error when writing hypercube to netcdf file: " + path + " " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return -1;
		}
		return 0;
	}

}
