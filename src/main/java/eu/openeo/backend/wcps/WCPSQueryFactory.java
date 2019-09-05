package eu.openeo.backend.wcps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.openeo.backend.wcps.domain.Aggregate;
import eu.openeo.backend.wcps.domain.Collection;
import eu.openeo.backend.wcps.domain.Filter;

public class WCPSQueryFactory {

	private StringBuilder wcpsStringBuilder;
	private Vector<Collection> collectionIDs;
	private Vector<Filter> filters;
	private Vector<Aggregate> aggregates;
	private String outputFormat = "json";
	private JSONObject processGraph;

	Logger log = Logger.getLogger(this.getClass());

	/**
	 * Creates WCPS query from openEO process Graph
	 * 
	 * @param openEOGraph
	 */
	public WCPSQueryFactory(JSONObject openEOGraph) {
		collectionIDs = new Vector<Collection>();
		aggregates = new Vector<Aggregate>();
		filters = new Vector<Filter>();
		wcpsStringBuilder = new StringBuilder("for ");
		this.processGraph = openEOGraph;
		this.build();
	}	

	public String getOutputFormat() {
		return outputFormat;
	}
	
	private StringBuilder basicWCPSStringBuilder() {
	
	StringBuilder basicWCPS;
	basicWCPS = new StringBuilder("for ");
	for (int c = 1; c <= collectionIDs.size(); c++) {
		basicWCPS.append("$c" + c);
		if (c > 1) {
			basicWCPS.append(", ");
		}
	}
	basicWCPS.append(" in ( ");
	for (int c = 1; c <= collectionIDs.size(); c++) {
		basicWCPS.append(collectionIDs.get(c - 1).getName() + " ");
	}
	basicWCPS.append(") return encode ( ");
	
	return basicWCPS;
	}

	private void build2(JSONObject openEOGraph) {
		log.debug(openEOGraph.toString());
		parseOpenEOProcessGraph();
		for (int c = 1; c <= collectionIDs.size(); c++) {
			wcpsStringBuilder.append("$c" + c);
			if (c > 1) {
				wcpsStringBuilder.append(", ");
			}
		}
		wcpsStringBuilder.append(" in ( ");
		for (int c = 1; c <= collectionIDs.size(); c++) {
			wcpsStringBuilder.append(collectionIDs.get(c - 1).getName() + " ");
		}
		wcpsStringBuilder.append(") return encode ( ");

		for (String keyNode : openEOGraph.keySet()) {
			
		 JSONObject processKeyNode = openEOGraph.getJSONObject(keyNode);
		 
		  for (Object key : processKeyNode.keySet()) {
			
			String keyStr = (String) key;
			if (keyStr.equals("process_id")) {
				String name = (String) processKeyNode.get(keyStr);
				log.debug("currently working on: " + name);
				if (name.contains("stretch_colors")) {

					double min = 0;
					double max = 0;

					for (Object Val : processKeyNode.keySet()) {
						String ValStr = (String) Val;

						if (ValStr.equals("min")) {
							min = processKeyNode.getDouble(ValStr);
						}
						if (ValStr.equals("max")) {
							max = processKeyNode.getDouble(ValStr);
						}
					}

					StringBuilder stretchBuilder = new StringBuilder("(");

					for (int a = 0; a < aggregates.size(); a++) {
						if (aggregates.get(a).getAxis().equals("DATE")) {
							stretchBuilder.append(createTempAggWCPSString("$c1", aggregates.get(a)));
						}
						if (aggregates.get(a).getOperator().equals("NDVI")) {
							String filterString = createFilteredCollectionString("$c1");
							stretchBuilder.append(createNDVIWCPSString(filterString, "$c1", aggregates.get(a)));
						}
					}
					stretchBuilder.append(")");
					String stretchString = stretchBuilder.toString();

					StringBuilder stretchBuilderExtend = new StringBuilder("(unsigned char)(");

					stretchBuilderExtend.append("(" + stretchString + " + " + (-min) + ")");
					stretchBuilderExtend.append("*(255" + "/" + (max - min) + ")");
					stretchBuilderExtend.append(" + 0)");

					String stretchExtendString = stretchBuilderExtend.toString();

					wcpsStringBuilder.append(stretchExtendString);

				} else if (name.contains("linear_stretch")) {

					int min = 0;
					int max = 0;

					for (Object Val : processKeyNode.keySet()) {
						String ValStr = (String) Val;

						if (ValStr.equals("min")) {
							min = (int) processKeyNode.get(ValStr);
						}
						if (ValStr.equals("max")) {
							max = (int) processKeyNode.get(ValStr);
						}
					}

					StringBuilder stretchBuilder = new StringBuilder("(");

					for (int a = 0; a < aggregates.size(); a++) {
						if (aggregates.get(a).getAxis().equals("DATE")) {
							stretchBuilder.append(createTempAggWCPSString("$c1", aggregates.get(a)));
						}
						if (aggregates.get(a).getOperator().equals("NDVI")) {
							String filterString = createFilteredCollectionString("$c1");
							stretchBuilder.append(createNDVIWCPSString(filterString, "$c1", aggregates.get(a)));
						}
					}
					stretchBuilder.append(")");
					String stretchString = stretchBuilder.toString();

					String stretch1 = stretchString.replace("$pm", "$pm1");
					String stretch2 = stretchString.replace("$pm", "$pm2");
					String stretch3 = stretchString.replace("$pm", "$pm3");
					String stretch4 = stretchString.replace("$pm", "$pm4");

					StringBuilder stretchBuilderExtend = new StringBuilder("(");

					stretchBuilderExtend.append(stretch1 + " - " + "min" + stretch2 + ")*((" + max + "-" + min + ")"
							+ "/(max" + stretch3 + "-min" + stretch4 + ")) + 0");

					String stretchExtendString = stretchBuilderExtend.toString();

					wcpsStringBuilder.append(stretchExtendString);
				} 
			}
		}
	}
		
		for (int a = 0; a < aggregates.size(); a++) {
			if (aggregates.get(a).getAxis().equals("DATE")) {
				wcpsStringBuilder.append(createTempAggWCPSString("$c1", aggregates.get(a)));
				log.debug("Aggregate Temp " + aggregates.get(a).getAxis());
				log.debug("Temp WCPS added " + wcpsStringBuilder);
			}
			if (aggregates.get(a).getOperator().equals("NDVI")) {
				String filterString = createFilteredCollectionString("$c1");
				wcpsStringBuilder.append(createNDVIWCPSString(filterString, "$c1", aggregates.get(a)));
				log.debug("Aggregate NDVI " + aggregates.get(a).getOperator());
				log.debug("NDVI WCPS  added " + wcpsStringBuilder);
			}
		}
		
		if (filters.size() > 0) {
			wcpsStringBuilder.append(createFilteredCollectionString("$c1"));
		}
		// TODO define return type from process tree
		wcpsStringBuilder.append(", \"" + this.outputFormat + "\" )");
	}
	
	private void build() {
		log.debug(processGraph.toString());
		parseOpenEOProcessGraph();
		for (int c = 1; c <= collectionIDs.size(); c++) {
			wcpsStringBuilder.append("$c" + c);
			if (c > 1) {
				wcpsStringBuilder.append(", ");
			}
		}
		wcpsStringBuilder.append(" in ( ");
		for (int c = 1; c <= collectionIDs.size(); c++) {
			wcpsStringBuilder.append(collectionIDs.get(c - 1).getName() + " ");
		}
		wcpsStringBuilder.append(") return encode ( ");

        JSONArray nodesArray = new JSONArray();
		String saveNode = getSaveNode();
		nodesArray = sortNodes(saveNode, nodesArray);
		
		StringBuilder wcpsPayLoad = new StringBuilder("");
		wcpsPayLoad.append(createFilteredCollectionString("$c1"));
		
		wcpsStringBuilder.append(wcpsPayLoad.toString());
		
		boolean containsNDVIProcess = false;
		boolean containsTempAggProcess = false;
		boolean containsLinearScale = false;
		boolean containsApplyProcess = false;
		boolean containsResampleProcess = false;
		
		for(int j = nodesArray.length()-1; j > 0; j--) {
			int i = j-1;
			String nodeKeyOfCurrentProcess = nodesArray.getString(i);
			JSONObject currentProcess = processGraph.getJSONObject(nodeKeyOfCurrentProcess);
			String currentProcessID = currentProcess.getString("process_id");
			JSONObject currentProcessArguments = currentProcess.getJSONObject("arguments");
			String currentProcessFromNodeKey = getFromNodeOfCurrentKey(nodeKeyOfCurrentProcess);
			log.debug("Building WCPS Query for : " + nodesArray.getString(i));
			log.debug("currently working on: " + currentProcessID);
			
			if (currentProcessID.equals("ndvi")) {
				containsNDVIProcess = true;
				StringBuilder wcpsNDVIpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderNDVI = basicWCPSStringBuilder();
				for (int a = 0; a < aggregates.size(); a++) {
					if (aggregates.get(a).getOperator().equals("NDVI")) {
						wcpsNDVIpayLoad.append(createNDVIWCPSString(wcpsPayLoad.toString(), "$c1", aggregates.get(a)));
						wcpsPayLoad=wcpsNDVIpayLoad;
						wcpsStringBuilder=wcpsStringBuilderNDVI.append(wcpsNDVIpayLoad.toString());
						log.debug("Aggregate NDVI " + aggregates.get(a).getOperator());
						log.debug("NDVI WCPS added " + wcpsStringBuilderNDVI);
					}
				}
			}
			
			if (currentProcessID.contains("_time")) {
				containsTempAggProcess = true;
				StringBuilder wcpsTempAggpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderTempAgg = basicWCPSStringBuilder();
				for (int a = 0; a < aggregates.size(); a++) {
					if (aggregates.get(a).getAxis().equals("DATE")) {
						wcpsTempAggpayLoad.append(createTempAggWCPSString("$c1", aggregates.get(a)));
						//String replaceDate = Pattern.compile("[DATE\\(.*?\\)]").matcher(wcpsAggBuilder).replaceAll("\\$pm");
						String replaceDate = wcpsPayLoad.toString().replaceAll("DATE\\(.*?\\)", "DATE\\(\\$pm\\)");
						StringBuilder wcpsAggBuilderMod = new StringBuilder("");
						wcpsAggBuilderMod.append(replaceDate);
						wcpsTempAggpayLoad.append(wcpsAggBuilderMod);
						wcpsPayLoad=wcpsTempAggpayLoad;
						wcpsStringBuilder=wcpsStringBuilderTempAgg.append(wcpsTempAggpayLoad.toString());
						log.debug("Aggregate Temp " + aggregates.get(a).getAxis());
						log.debug("temp Agg Payload " + wcpsPayLoad);
						log.debug("Temp WCPS added " + wcpsStringBuilderTempAgg);
					}
				}
			}
			
			if (currentProcessID.equals("linear_scale_cube")) {
				containsLinearScale = true;
				StringBuilder wcpsScalepayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderScale = basicWCPSStringBuilder();
				wcpsScalepayLoad.append(createLinearScaleCubeWCPSString(nodeKeyOfCurrentProcess, wcpsPayLoad.toString()));				
				wcpsPayLoad=wcpsScalepayLoad;
				wcpsStringBuilder = wcpsStringBuilderScale.append(wcpsScalepayLoad.toString());
			}
			if (currentProcessID.equals("apply")) {
				containsApplyProcess = true;
				StringBuilder wcpsApplypayLoad = new StringBuilder("");
 				StringBuilder wcpsStringBuilderApply = basicWCPSStringBuilder();				
 				wcpsApplypayLoad.append(createApplyWCPSString(nodeKeyOfCurrentProcess, wcpsPayLoad.toString()));
				wcpsPayLoad=wcpsApplypayLoad;
				wcpsStringBuilder = wcpsStringBuilderApply.append(wcpsApplypayLoad.toString());
			}
			if (currentProcessID.equals("resample_spatial")) {
				containsResampleProcess = true;
				StringBuilder wcpsResamplepayLoad = new StringBuilder("");
 				StringBuilder wcpsStringBuilderResample = basicWCPSStringBuilder();
 				wcpsResamplepayLoad.append(createResampleWCPSString(nodeKeyOfCurrentProcess, wcpsPayLoad.toString()));
				wcpsPayLoad=wcpsResamplepayLoad;
				wcpsStringBuilder = wcpsStringBuilderResample.append(wcpsResamplepayLoad.toString());
			}
			if (currentProcessID.equals("save_result")) {
				String savePayload = wcpsStringBuilder.toString();
				StringBuilder wcpsStringBuilderSaveResult = new StringBuilder("");				
				wcpsStringBuilderSaveResult.append(createReturnResultWCPSString(nodeKeyOfCurrentProcess, savePayload));
				wcpsStringBuilder = wcpsStringBuilderSaveResult;							
			}
		}

//		boolean containsResampleProcess = false;
//		String resampleNodeKey = "";
//		for (String keyNode : processGraph.keySet()) {
//			String name = processGraph.getJSONObject(keyNode).getString("process_id");
//			
//			if (name.contains("resample_spatial")) {
//				containsResampleProcess = true;
//				resampleNodeKey = keyNode;
//				break;
//			}
//		}
//		
//		if (containsResampleProcess) {
//			wcpsStringBuilder = new StringBuilder(createResampleWCPSString(resampleNodeKey));
//		}
	}
	
	private String createReturnResultWCPSString(String returnResultNodeKey, String payload) {
		//String outputFormat = processGraph.getJSONObject(returnResultNodeKey).getJSONObject("arguments").getString("format");
		
		StringBuilder resultBuilder = new StringBuilder("");
		resultBuilder.append(payload);		
		resultBuilder.append(", \"" + this.outputFormat + "\" )");
		log.debug("Save payload: " + resultBuilder);
		return resultBuilder.toString();
	}
	
	//TODO extend this to the full functionality of the openEO process
	private String createResampleWCPSString(String resampleNodeKey, String payload) {
		int projectionEPSGCode = 0;
		try {
			projectionEPSGCode = processGraph.getJSONObject(resampleNodeKey).getJSONObject("arguments").getInt("projection");
		}catch(JSONException e) {
			log.error("no epsg code was detected!");
		}
		if(projectionEPSGCode == 0) {
			return "";
		}
		StringBuilder resampleBuilder = new StringBuilder("crsTransform(" );

		//TODO read the name of the spatial coordinate axis from describeCoverage or filter elements in order to correctly apply (E,N), (lat,lon) or X,Y depending on coordinate system
		resampleBuilder.append(payload);

		resampleBuilder.append(" ,{"
				+ "E:\"http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\","
				+ "N:\"http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\""
				+ "}, {})");
		log.debug("current payload: " + payload);
		log.debug("resample wcps query: " + resampleBuilder.toString());
		return resampleBuilder.toString();
	}
	
	//TODO extend this to the full functionality of the openEO process
	private String createResampleWCPSString(String resampleNodeKey) {
		String projectionEPSGCode = processGraph.getJSONObject(resampleNodeKey).getJSONObject("arguments").getString("projection");
        
		String currentWCPSQuery = wcpsStringBuilder.toString();
		int beginIndex = currentWCPSQuery.indexOf("return encode (") + 15;
		int endIndex = currentWCPSQuery.indexOf(", \"");
		log.debug("payload range: " + beginIndex + " " + endIndex);
		StringBuilder resampleBuilder = new StringBuilder(currentWCPSQuery.substring(0, beginIndex));
		String currentPayload = currentWCPSQuery.substring(beginIndex, endIndex);
		
		//TODO read the name of the spatial coordinate axis from describeCoverage or filter elements in order to correctly apply (E,N), (lat,lon) or X,Y depending on coordinate system
		resampleBuilder.append("crsTransform(" + currentPayload + ",{"
				+ "E:\"http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\","
				+ "N:\"http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\""
				+ "}, {})");
		resampleBuilder.append(currentWCPSQuery.substring(endIndex));
		log.debug("current payload: " + currentPayload);
		log.debug("resample wcps query: " + resampleBuilder.toString());
		return resampleBuilder.toString();
	}
	
    private String createApplyWCPSString(String applyNodeKey, String payLoad) {
		String applyBuilderExtend = null;
		JSONObject applyProcesses = processGraph.getJSONObject(applyNodeKey).getJSONObject("arguments").getJSONObject("process").getJSONObject("callback");
		
		for (String nodeKey : applyProcesses.keySet()) {
			String name = applyProcesses.getJSONObject(nodeKey).getString("process_id");			
			if (name.contains("linear_scale_range")) {
				applyBuilderExtend = createLinearScaleRangeWCPSString(nodeKey, payLoad, applyProcesses);
			}
		}		
		return applyBuilderExtend;
	}

    private String createLinearScaleRangeWCPSString(String linearScaleNodeKey, String payLoad, JSONObject process) {
	
	JSONObject scaleArgumets = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments");
	
	double inputMin = 0;
	double inputMax = 0;
	double outputMin = 0;
	double outputMax = 1;

	inputMin = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("inputMin");
	inputMax = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("inputMax");
	
	for (String outputMinMax : scaleArgumets.keySet()) {	
		if (outputMinMax.contentEquals("outputMin")) {
	        outputMin = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("outputMin");	        
		}
		else if (outputMinMax.contentEquals("outputMax")) {
			outputMax = process.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("outputMax");
		}	
	}
	
	StringBuilder stretchBuilder = new StringBuilder("(");
	stretchBuilder.append(payLoad + ")");
	String stretchString = stretchBuilder.toString();

	StringBuilder stretchBuilderExtend = new StringBuilder("(unsigned char)(");

	stretchBuilderExtend.append("(" + stretchString + " + " + (-inputMin) + ")");
	stretchBuilderExtend.append("*("+ outputMax + "/" + (inputMax - inputMin) + ")");
	stretchBuilderExtend.append(" + " + outputMin + ")");

	return stretchBuilderExtend.toString();
    }
    
    private String createLinearScaleCubeWCPSString(String linearScaleNodeKey, String payLoad) {
		
		String name = processGraph.getJSONObject(linearScaleNodeKey).getString("process_id");
		JSONObject scaleArgumets = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments");
		log.debug("currently working on: " + name);
		double inputMin = 0;
		double inputMax = 0;
		double outputMin = 0;
		double outputMax = 1;

		inputMin = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("inputMin");
		inputMax = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("inputMax");
		
		for (String outputMinMax : scaleArgumets.keySet()) {			
			if (outputMinMax.contentEquals("outputMin")) {
		        outputMin = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("outputMin");
		       
			}
			else if (outputMinMax.contentEquals("outputMax")) {
				outputMax = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("outputMax");
			}		
		}	

		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(payLoad + ")");
		String stretchString = stretchBuilder.toString();

		StringBuilder stretchBuilderExtend = new StringBuilder("(unsigned char)(");

		stretchBuilderExtend.append("(" + stretchString + " + " + (-inputMin) + ")");
		stretchBuilderExtend.append("*("+ outputMax + "/" + (inputMax - inputMin) + ")");
		stretchBuilderExtend.append(" + " + outputMin + ")");

		return stretchBuilderExtend.toString();
	}

	/**
	 * Helper Method to create a string describing an arbitrary filtering as defined
	 * from the process graph
	 * 
	 * @param collectionName
	 * @return
	 */
	private String createFilteredCollectionString(String collectionName) {
		StringBuilder stringBuilder = new StringBuilder(collectionName);
		stringBuilder.append("[");
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();
			String low = filter.getLowerBound();
			String high = filter.getUpperBound();
			stringBuilder.append(axis + "(");
			if (axis.contains("DATE") && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			stringBuilder.append(low);
			if (axis.contains("DATE") && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			if (high != null && !(high.equals(low))) {
				stringBuilder.append(":");
				if (axis.contains("DATE")) {
					stringBuilder.append("\"");
				}

				stringBuilder.append(high);
				if (axis.contains("DATE")) {
					stringBuilder.append("\"");
				}
			}
			stringBuilder.append(")");
			if (f < filters.size() - 1) {
				stringBuilder.append(",");
			}
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	/**
	 * Helper Method to create a string describing a single dimension filter as
	 * defined from the process graph
	 * 
	 * @param collectionName
	 * @return
	 */
	private String createFilteredCollectionString(String collectionName, Filter filter) {
		try {
			StringBuilder stringBuilder = new StringBuilder(collectionName);
			stringBuilder.append("[");
			String axis = filter.getAxis();
			String low = filter.getLowerBound();
			String high = filter.getUpperBound();
			stringBuilder.append(axis + "(");
			if (axis.contains("DATE") && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			stringBuilder.append(low);
			if (axis.contains("DATE") && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			if (high != null && !(high.equals(low))) {
				stringBuilder.append(":");
				if (axis.contains("DATE")) {
					stringBuilder.append("\"");
				}

				stringBuilder.append(high);
				if (axis.contains("DATE")) {
					stringBuilder.append("\"");
				}
			}
			stringBuilder.append(")");
			stringBuilder.append("]");
			return stringBuilder.toString();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return "";
		}
	}

	private String createNDVIWCPSString(String filterString, String collectionName, Aggregate ndviAggregate) {
		String redBandName = ndviAggregate.getParams().get(0);
		String nirBandName = ndviAggregate.getParams().get(1);
		
		filterString = filterString.substring(collectionName.length());
		String red = createBandSubsetString(collectionName, redBandName, filterString);
		String nir = createBandSubsetString(collectionName, nirBandName, filterString);
		StringBuilder stringBuilder = new StringBuilder("((double)");
		stringBuilder.append(nir + " - " + red);
		stringBuilder.append(") / ((double)");
		stringBuilder.append(nir + " + " + red);
		stringBuilder.append(")");
		//filters.removeAllElements();
		return stringBuilder.toString();
	}

	private String createTempAggWCPSString(String collectionName, Aggregate tempAggregate) {
		String axis = tempAggregate.getAxis();
		String operator = tempAggregate.getOperator();
		Filter tempFilter = null;
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals("DATE")) {
				tempFilter = filter;
			}
		}
		log.debug("Filters " + filters);
		log.debug("Temp filter " + tempFilter);
		if (tempFilter != null) {
			StringBuilder stringBuilder = new StringBuilder("condense ");			
			stringBuilder.append(operator + " over $pm t (imageCrsDomain(");
			stringBuilder.append(createFilteredCollectionString(collectionName, tempFilter) + ",");
			stringBuilder.append(axis + ")) using ");
			
			this.filters.remove(tempFilter);
			this.filters.add(new Filter(axis, "$pm"));
			
			return stringBuilder.toString();
		} else {
			for (Filter filter : this.filters) {
				System.err.println(filter.getAxis());
			}
			// TODO this error needs to be communicated to end user
			// meaning no appropriate filter found for running the condense operator in
			// temporal axis.
			return "";
		}		
	}

	private String createBandSubsetString(String collectionName, String bandName, String subsetString) {
		StringBuilder stringBuilder = new StringBuilder(collectionName);
		stringBuilder.append(".");
		stringBuilder.append(bandName);
		stringBuilder.append(subsetString);
		return stringBuilder.toString();
	}

	/**
	 * returns constructed query as String object
	 * 
	 * @return String WCPS query
	 */
	public String getWCPSString() {
		log.debug("The following WCPS query was requested: ");
		log.debug(wcpsStringBuilder.toString());
		return wcpsStringBuilder.toString();
	}
	
	private String getSaveNode() {		
		for (String processNodeKey : processGraph.keySet()) {			
			JSONObject processNode = processGraph.getJSONObject(processNodeKey);
			String processID = processNode.getString("process_id");
			if (processID.equals("save_result")) {
				log.debug("Found save result node: " + processNode.getString("process_id"));
				log.debug("Save Result node key found is: " + processNodeKey);
				String format = getFormatFromSaveResultNode(processNode);
				try {					
					//this.outputFormat = ConvenienceHelper.getMimeTypeFromOutput(format);
					this.outputFormat = ConvenienceHelper.getRasTypeFromOutput(format);
				} catch (JSONException | IOException e) {
					log.error("Error while parsing outputformat from process graph: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for( StackTraceElement element: e.getStackTrace()) {
						builder.append(element.toString()+"\n");
					}
					log.error(builder.toString());
				}
				return processNodeKey;
			}
		}
		return null;
	}
		
	private String getFromNodeOfCurrentKey(String currentNodeKey){		
		JSONObject currentNodeData = processGraph.getJSONObject(currentNodeKey).getJSONObject("arguments");
		for (String argumentsKey : currentNodeData.keySet()) {
		    if (argumentsKey.contentEquals("data")) {
		      currentNodeKey = currentNodeData.getJSONObject("data").getString("from_node");
		   }
		}
		return currentNodeKey;
	}
	
	private String getFormatFromSaveResultNode(JSONObject saveResultNode) {
		JSONObject saveResultArguments = saveResultNode.getJSONObject("arguments");
		String format = saveResultArguments.getString("format");
		return format;
	}

	private JSONArray sortNodes(String saveNode, JSONArray nodesArray) {		
		String nextNode = saveNode;
		for (String nodeKey : processGraph.keySet()) {
		          nodesArray.put(nextNode);
		          nextNode = getFromNodeOfCurrentKey(nextNode);
		}
		log.debug("Order of the nodes is: " + nodesArray);
		return nodesArray;
	}
	
	/**
	 * 
	 * @param processParent
	 * @return
	 */	
	private JSONObject parseOpenEOProcessGraph() {
		JSONObject result = null;
		JSONArray nodesArray = new JSONArray();
		
		String saveNode = getSaveNode();		
		nodesArray = sortNodes(saveNode, nodesArray);
		
		for(int a = 0; a < nodesArray.length(); a++) {		
		log.debug("Executing Process : " + nodesArray.getString(a));
		String nodeKeyOfCurrentProcess = nodesArray.getString(a);
		JSONObject currentProcess = processGraph.getJSONObject(nodeKeyOfCurrentProcess);
		String currentProcessID = currentProcess.getString("process_id");
		
		executeProcesses(currentProcessID, nodeKeyOfCurrentProcess);		
		}
		return result;
    }   
	
	private void executeProcesses(String processID, String processNodeKey) {		
		JSONObject processNode = processGraph.getJSONObject(processNodeKey);
		
		if (processID.equals("load_collection")) {			
			String collection = null;
			JSONObject loadCollectionNode = processGraph.getJSONObject(processNodeKey);
			JSONObject loadCollectionNodeArguments = loadCollectionNode.getJSONObject("arguments");			
			collection = (String) loadCollectionNodeArguments.get("id");
			collectionIDs.add(new Collection(collection));
			log.debug("found actual dataset: " + collection);
			
			JSONObject collectionSTACMetdata = null;
			try {
				collectionSTACMetdata = readJsonFromUrl(
						ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collection);
			} catch (JSONException e) {
				log.error("An error occured while parsing json from STAC metadata endpoint: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for( StackTraceElement element: e.getStackTrace()) {
					builder.append(element.toString()+"\n");
				}
				log.error(builder.toString());
			} catch (IOException e) {
				log.error("An error occured while receiving data from STAC metadata endpoint: " + e.getMessage());
				StringBuilder builder = new StringBuilder();
				for( StackTraceElement element: e.getStackTrace()) {
					builder.append(element.toString()+"\n");
				}
				log.error(builder.toString());
			}

			int srs = 0;			
			srs = ((JSONObject) collectionSTACMetdata.get("properties")).getInt("eo:epsg");
			log.debug("srs is: " + srs);
			
			for (String argumentKey : loadCollectionNodeArguments.keySet()) {
				if (!loadCollectionNodeArguments.isNull(argumentKey) && argumentKey.equals("spatial_extent")) {
					JSONObject spatialExtentNode = loadCollectionNodeArguments.getJSONObject("spatial_extent");
					log.debug("currently working on spatial extent: " + spatialExtentNode.toString(4));
					createBoundingBoxFilterFromArgs(loadCollectionNodeArguments, srs, collection);
				}
				if (!loadCollectionNodeArguments.isNull(argumentKey) && argumentKey.equals("temporal_extent")) {
					JSONArray processDatacubeTempExt = (JSONArray) loadCollectionNodeArguments.get("temporal_extent");
					log.debug("currently working on temporal extent: " + processDatacubeTempExt.toString(4));
					createDateRangeFilterFromArgs(processDatacubeTempExt, collection);
				}
			}
		}
		
		else if (processID.contains("_time")) {
			log.debug("Found Time node: " + processNode.getString("process_id"));
			    createTemporalAggregate(processID);
			    log.debug("Filters are: " + filters);
			}
			
		else if (processID.equals("ndvi")) {
			log.debug("Found NDVI node: " + processNode.getString("process_id"));
			    JSONObject processAggregate = processGraph.getJSONObject(processNodeKey);			    
			    String collectionNode = getFilterCollectionNode(processNodeKey);
			    String collection = processGraph.getJSONObject(collectionNode).getJSONObject("arguments").getString("id");
			    log.debug("Collection found: " + collection);
			    createNDVIAggregateFromProcess(processAggregate, collection);
			    log.debug("Filters are: " + filters);
		    }		
		
   // else if (processID.equals("filter_temporal")) {
		
   // 	String filterCollectionNodeKey = null;
   // 	String filterTempNodeKey = processNodeKey;
   // 	JSONObject filterTempDataNode = argumentsNode.getJSONObject("data");
   //		String filterTempfromNode = filterTempDataNode.getString("from_node");
    	
   //		filterCollectionNodeKey = getFilterCollectionNode(processParent, filterTempfromNode);
   //		log.debug("Key Temp is : " + filterCollectionNodeKey);
   //		JSONObject loadCollectionNode = processParent.getJSONObject(filterCollectionNodeKey);
		
   //		JSONObject loadCollectionNodeArguments = loadCollectionNode.getJSONObject("arguments");
		
   //		String coll1 = (String) loadCollectionNodeArguments.get("id");
		
   //		JSONObject processFilter = processParent.getJSONObject(processNodeKey);
   //		JSONObject processFilterArguments = processFilter.getJSONObject("arguments");
		
   //		JSONArray extentArray = (JSONArray) processFilterArguments.get("extent");
   //		createDateRangeFilterFromArgs(extentArray, coll1);
   //	}
	
   // else if (processID.equals("filter_bbox")) {
		
   // 	String filterCollectionNodeKey = null;
   // 	String filterBboxNodeKey = processNodeKey;
   // 	JSONObject filterBboxDataNode = argumentsNode.getJSONObject("data");
   //		String filterBboxfromNode = filterBboxDataNode.getString("from_node");
    	
   //		filterCollectionNodeKey = getFilterCollectionNode(processParent, filterBboxfromNode);
   //		log.debug("Key Bbox is : " + filterCollectionNodeKey);
   //		JSONObject loadCollectionNode = processParent.getJSONObject(filterCollectionNodeKey);
   //		JSONObject loadCollectionNodeArguments = loadCollectionNode.getJSONObject("arguments");
		
   //		String coll2 = (String) loadCollectionNodeArguments.get("id");
		
   //		JSONObject processFilter = processParent.getJSONObject(processNodeKey);
   //		JSONObject processFilterArguments = processFilter.getJSONObject("arguments");
		
   //     int srs = 0;
		
   //		JSONObject jsonresp = null;
   //		try {
   //			jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + coll2);
   //		} catch (JSONException e) {
   //			log.error("An error occured: " + e.getMessage());
   //			StringBuilder builder = new StringBuilder();
   //			for (StackTraceElement element : e.getStackTrace()) {
   //				builder.append(element.toString() + "\n");
   //			}
   //			log.error(builder.toString());
   //		} catch (IOException e) {
   //			log.error("An error occured: " + e.getMessage());
   //			StringBuilder builder = new StringBuilder();
   //			for (StackTraceElement element : e.getStackTrace()) {
   //				builder.append(element.toString() + "\n");
   //			}
   //			log.error(builder.toString());
   //		}
					
   //		srs = ((JSONObject) jsonresp.get("properties")).getInt("eo:epsg");
   //		log.debug("srs is: " + srs);
   //		if (srs > 0) {
   //			createBoundingBoxFilterFromArgs(processFilterArguments, srs, coll2);
   //		}
   //	}
		
	}
	
	private String getFilterCollectionNode(String fromNode) {		
		String filterCollectionNodeKey = null;			
		JSONObject loadCollectionNodeKeyArguments = processGraph.getJSONObject(fromNode).getJSONObject("arguments");
		
		for (String argumentsKey : loadCollectionNodeKeyArguments.keySet()) {
			if (argumentsKey.contentEquals("id")) {
				filterCollectionNodeKey = fromNode;			  
			}
			else if (argumentsKey.contentEquals("data")) {			  
				String filterfromNode = loadCollectionNodeKeyArguments.getJSONObject("data").getString("from_node");			  
				filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
			}
		}		
		return filterCollectionNodeKey;
	}
	
	private String filter_geometry(String collectionName) {
		return collectionName;
	}

	private String collectionName(JSONObject processParent) {
	String coll = null;
	
	for (Object keydc : processParent.keySet()) {		
		String keyStrdc = (String) keydc;				
		JSONObject processNode1 = processParent.getJSONObject(keyStrdc);
		String processName1 = processNode1.getString("process_id");		
		if (processName1.equals("load_collection")) {			
			JSONObject processDatacube = processParent.getJSONObject(keyStrdc);
			JSONObject processDatacubeArguments = processDatacube.getJSONObject("arguments");			
			coll = (String) processDatacubeArguments.get("id");			
		}}
		return coll;
	}
	
	/**
	 * 
	 * @param process
	 */	
    
	private void createFilterFromProcess(JSONObject process, String coll) {		
		for (Object key : process.keySet()) {
			String keyStr = (String) key;
			log.debug("" + keyStr);
			if (!keyStr.contains("extent")) {
				log.debug("no spatial or temporal extent defined in filter");
			}			
			// check if filter contains spatial information
			else if (process.get("process_id").toString().contains("bbox")) {
				log.debug("spatial extent defined in filter");
				int srs = 0;				
				JSONObject jsonresp = null;
				try {
					jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + coll);
				} catch (JSONException e) {
					log.error("An error occured: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				} catch (IOException e) {
					log.error("An error occured: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				}
							
				srs = ((JSONObject) jsonresp.get("properties")).getInt("eo:epsg");
				log.debug("srs is: " + srs);
				if (srs > 0) {
					createBoundingBoxFilterFromArgs(process, srs, coll);
				}
			}
			// check if filter contains temporal information
			else if (process.get("process_id").toString().contains("temporal")) {
				log.debug("temporal extent defined in filter");				
				JSONArray extentArray = (JSONArray) process.get(keyStr);
				createDateRangeFilterFromArgs(extentArray, coll);
			}
		}
	}

	private void createDateRangeFilterFromArgs(JSONArray extentArray, String coll) {
		String fromDate = null;
		String toDate = null;
		JSONObject extent;
		String extentlower = extentArray.get(0).toString();
		String extentupper = extentArray.get(1).toString();

		JSONObject jsonresp = null;
		try {
			jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + coll);
		} catch (JSONException e) {
			log.error("An error occured: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (IOException e) {
			log.error("An error occured: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		
		extent = jsonresp.getJSONObject("extent");
		JSONArray temporal = extent.getJSONArray("temporal");
		String templower = temporal.get(0).toString();
		String tempupper = temporal.get(1).toString();
				
		log.debug("temporal extent is: " + temporal);
		if (extentlower.compareTo(templower) < 0) {
			fromDate = temporal.get(0).toString();
		}
		else {
			fromDate = extentArray.get(0).toString();
		}
        if ( extentupper.compareTo(tempupper) > 0) {
        	toDate = temporal.get(1).toString();
		}
        else {
        	toDate = extentArray.get(1).toString();
		}
		if (fromDate != null && toDate != null) {
			log.debug("Temporal extent is: |" + fromDate + "|:|" + toDate + "|");
			if(LocalDateTime.parse(fromDate.replace("Z", "")).equals(LocalDateTime.parse(toDate.replace("Z", "")))) {
				toDate = null;
				log.debug("Dates are identical. To date is set to null!");
			}
			log.debug("Temporal extent is: " + fromDate + ":" + toDate);
			this.filters.add(new Filter("DATE", fromDate, toDate));
			log.debug("Temporal filter is: " + filters);
		}
	}

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		log.debug("Trying to read JSON from the following URL: " + url);
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private void createBoundingBoxFilterFromArgs(JSONObject argsObject, int srs, String coll) {
		String left = null;
		String right = null;
		String top = null;
		String bottom = null;
		log.debug("creating spatial extent filter from process");
		for (Object argsKey : argsObject.keySet()) {
			String argsKeyStr = (String) argsKey;
			if (argsKeyStr.equals("extent") || argsKeyStr.equals("spatial_extent")) {				
				JSONObject extentObject = (JSONObject) argsObject.get(argsKeyStr);
				
				for (Object extentKey : extentObject.keySet()) {
					String extentKeyStr = extentKey.toString();
					JSONObject extent;
					JSONObject jsonresp = null;
					try {
						jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + coll);
					} catch (JSONException e) {
						log.error("An error occured: " + e.getMessage());
						StringBuilder builder = new StringBuilder();
						for (StackTraceElement element : e.getStackTrace()) {
							builder.append(element.toString() + "\n");
						}
						log.error(builder.toString());
					} catch (IOException e) {
						log.error("An error occured: " + e.getMessage());
						StringBuilder builder = new StringBuilder();
						for (StackTraceElement element : e.getStackTrace()) {
							builder.append(element.toString() + "\n");
						}
						log.error(builder.toString());
					}
					extent = jsonresp.getJSONObject("extent");
					JSONArray spatial = extent.getJSONArray("spatial");
					double westlower = spatial.getDouble(0);
					double eastupper = spatial.getDouble(2);
					
					double southlower = spatial.getDouble(1);
					double northupper = spatial.getDouble(3);
					
					log.debug("spatial extent is: " + spatial);
					double leftlower = 0;
					double rightupper = 0;
					double topupper = 0;
					double bottomlower = 0;
					
					if (extentKeyStr.equals("west")) {
						left = "" + extentObject.get(extentKeyStr).toString();
						leftlower = Double.parseDouble(left);
						if (leftlower < westlower) {							
							left = Double.toString(westlower);
						}
						
					} else if (extentKeyStr.equals("east")) {
						right = "" + extentObject.get(extentKeyStr).toString();
						rightupper = Double.parseDouble(right);
						if (rightupper > eastupper) {							
							right = Double.toString(eastupper);
						}
						
					} else if (extentKeyStr.equals("north")) {
						top = "" + extentObject.get(extentKeyStr).toString();
						topupper = Double.parseDouble(top);
						if (topupper > northupper) {							
				        	top = Double.toString(northupper);
						}
						
					} else if (extentKeyStr.equals("south")) {
						bottom = "" + extentObject.get(extentKeyStr);
						bottomlower = Double.parseDouble(bottom);
						if (bottomlower < southlower) {							
				        	bottom = Double.toString(southlower).toString();
						}
					}
				}
				SpatialReference src = new SpatialReference();
				src.ImportFromEPSG(4326);
				SpatialReference dst = new SpatialReference();
				dst.ImportFromEPSG(srs);
				log.debug(srs);

				CoordinateTransformation tx = new CoordinateTransformation(src, dst);
				double[] c1 = null;
				double[] c2 = null;
				c1 = tx.TransformPoint(Double.parseDouble(left), Double.parseDouble(bottom));
				c2 = tx.TransformPoint(Double.parseDouble(right), Double.parseDouble(top));
				left = Double.toString(c1[0]);
				bottom = Double.toString(c1[1]);
				right = Double.toString(c2[0]);
				top = Double.toString(c2[1]);
								
				log.debug("WEST: "+left);
				log.debug("SOUTH: "+bottom);
				log.debug("EAST: "+right);
				log.debug("NORTH: "+top);				
			}
		}
		if (left != null && right != null && top != null && bottom != null) {
			this.filters.add(new Filter("E", left, right));
			this.filters.add(new Filter("N", bottom, top));
			log.debug("Spatial filter is: " + filters);
		} else {
			log.error("no spatial information could be found in process!");
		}
	}

	/**
	 * 
	 * @param process
	 */
	
//	private void createAggregateFromProcess(JSONObject process) {
//		boolean isTemporalAggregate = false;
//		boolean isNDVIAggregate = false;
//		String processName = null;
//		for (Object key : process.keySet()) {
//			String keyStr = (String) key;
//			if (keyStr.equals("process_id")) {
//				processName = (String) process.get(keyStr);
//				log.debug("currently working on: " + processName);
//				if (processName.contains("temporal") || processName.contains("time")) {
//					isTemporalAggregate = true;
//					createTemporalAggregate(processName);
//				} else if (processName.contains("ndvi")) {
//					isNDVIAggregate = true;
//					createNDVIAggregateFromProcess(process);
//				}
//			}
//		}
//	}

	private void createTemporalAggregate(String processName) {
		String aggregateType = processName.split("_")[0];
		Vector<String> params = new Vector<String>();
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals("DATE")) {
				params.add(filter.getLowerBound());
				params.add(filter.getUpperBound());
			}
		}
		log.debug("temporal aggregate added!");
		aggregates.add(new Aggregate(new String("DATE"), aggregateType, params));
	}

	private void createNDVIAggregateFromProcess(JSONObject argsObject, String collection) {
		String red = null;
		String nir = null;
		
		JSONObject collectionSTACMetdata = null;
		try {
			collectionSTACMetdata = readJsonFromUrl(
					ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collection);
		} catch (JSONException e) {
			log.error("An error occured while parsing json from STAC metadata endpoint: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		} catch (IOException e) {
			log.error("An error occured while receiving data from STAC metadata endpoint: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		}

		JSONArray bandsArray = ((JSONObject) collectionSTACMetdata.get("properties")).getJSONArray("eo:bands");
		
		for(int c = 0; c < bandsArray.length(); c++) {
			String bandCommon = bandsArray.getJSONObject(c).getString("common_name");
			if (bandCommon.equals("red")) {
				red = bandsArray.getJSONObject(c).getString("name");
			}
			else if (bandCommon.equals("nir")) {
				nir = bandsArray.getJSONObject(c).getString("name");
			}
		}
		
		Vector<String> params = new Vector<String>();
		params.add(red);
		params.add(nir);
		if (red != null && nir != null) {
			log.debug("feature aggregate added!");
			aggregates.add(new Aggregate(new String("feature"), new String("NDVI"), params));
		}
	}

}