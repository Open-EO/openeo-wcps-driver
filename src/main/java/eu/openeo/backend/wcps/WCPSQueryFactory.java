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
import java.lang.Math;

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
		String collName = "$c1";
		wcpsPayLoad.append(createFilteredCollectionString(collName));
		log.debug("Initial PayLoad WCPS is: " + wcpsPayLoad);
		wcpsStringBuilder.append(wcpsPayLoad.toString());

		boolean containsFilterBandProcess = false;
		boolean containsNDVIProcess = false;
		boolean containsTempAggProcess = false;
		boolean containsReduceProcess = false;
		boolean containsLinearStretch = false;
		boolean containsLinearScale = false;
		boolean containsApplyProcess = false;
		boolean containsResampleProcess = false;

		for(int j = nodesArray.length()-1; j > 0; j--) {
			int i = j-1;
			String nodeKeyOfCurrentProcess = nodesArray.getString(i);
			JSONObject currentProcess = processGraph.getJSONObject(nodeKeyOfCurrentProcess);
			String currentProcessID = currentProcess.getString("process_id");
			JSONObject currentProcessArguments = currentProcess.getJSONObject("arguments");
			//String currentProcessFromNodeKey = getFromNodeOfCurrentKey(nodeKeyOfCurrentProcess);
			log.debug("Building WCPS Query for : " + nodesArray.getString(i));
			log.debug("currently working on: " + currentProcessID);
			
			if (currentProcessID.equals("filter_bbox")) {
				StringBuilder wcpsFilterBboxpayLoad = wcpsPayLoad;
				StringBuilder wcpsStringBuilderFilterBboxPayload = basicWCPSStringBuilder();
				wcpsPayLoad=wcpsFilterBboxpayLoad;
				wcpsStringBuilder=wcpsStringBuilderFilterBboxPayload.append(wcpsFilterBboxpayLoad.toString());
			}
			
            if (currentProcessID.equals("filter_temporal")) {
            	StringBuilder wcpsFilterDatepayLoad = wcpsPayLoad;
				StringBuilder wcpsStringBuilderFilterDatePayload = basicWCPSStringBuilder();
				wcpsPayLoad=wcpsFilterDatepayLoad;
				wcpsStringBuilder=wcpsStringBuilderFilterDatePayload.append(wcpsFilterDatepayLoad.toString());
			}
			
			if (currentProcessID.equals("filter_bands")) {
				containsFilterBandProcess = true;
				StringBuilder wcpsFilterpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderFilterPayload = basicWCPSStringBuilder();
				String filterString = wcpsPayLoad.toString();
				filterString = filterString.substring(collName.length());
				JSONArray currentProcessBands = currentProcessArguments.getJSONArray("bands");
				String bandName = currentProcessBands.getString(0);
				wcpsFilterpayLoad.append(createBandSubsetString(collName, bandName, filterString));
				wcpsPayLoad=wcpsFilterpayLoad;
				wcpsStringBuilder=wcpsStringBuilderFilterPayload.append(wcpsFilterpayLoad.toString());
			}			
			if (currentProcessID.equals("ndvi")) {
				containsNDVIProcess = true;
				StringBuilder wcpsNDVIpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderNDVI = basicWCPSStringBuilder();
				for (int a = 0; a < aggregates.size(); a++) {
					if (aggregates.get(a).getOperator().equals("NDVI")) {
						wcpsNDVIpayLoad.append(createNDVIWCPSString(wcpsPayLoad.toString(), collName, aggregates.get(a)));
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
						wcpsTempAggpayLoad.append(createTempAggWCPSString(collName, aggregates.get(a)));
						String replaceDate = Pattern.compile("DATE\\(.*?\\)").matcher(wcpsPayLoad).replaceAll("DATE\\(\\$pm\\)");
						//String replaceDate = wcpsPayLoad.toString().replaceAll("DATE\\(.*?\\)", "DATE\\(\\$pm\\)");
						StringBuilder wcpsAggBuilderMod = new StringBuilder("");
						wcpsAggBuilderMod.append(replaceDate);
						wcpsTempAggpayLoad.append(wcpsAggBuilderMod);
						wcpsPayLoad=wcpsTempAggpayLoad;
						wcpsStringBuilder=wcpsStringBuilderTempAgg.append(wcpsTempAggpayLoad.toString());
						log.debug("Aggregate Temp " + aggregates.get(a).getAxis());
						log.debug("Temp Agg Payload " + wcpsPayLoad);
						log.debug("Temp WCPS added " + wcpsStringBuilderTempAgg);
					}
				}
			}
			if (currentProcessID.equals("reduce")) {
				containsReduceProcess = true;
				StringBuilder wcpsReducepayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderReduce = basicWCPSStringBuilder();
				String dimension = currentProcess.getJSONObject("arguments").getString("dimension");
				String filterString = wcpsPayLoad.toString();
				filterString = filterString.substring(collName.length());
				wcpsReducepayLoad.append(createReduceWCPSString(nodeKeyOfCurrentProcess, wcpsPayLoad.toString(), filterString, collName, dimension));
				wcpsPayLoad=wcpsReducepayLoad;
				wcpsStringBuilder = wcpsStringBuilderReduce.append(wcpsReducepayLoad.toString());				
			}
			if (currentProcessID.equals("linear_scale_cube")) {
				containsLinearScale = true;
				StringBuilder wcpsScalepayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderScale = basicWCPSStringBuilder();
				wcpsScalepayLoad.append(createLinearScaleCubeWCPSString(nodeKeyOfCurrentProcess, wcpsPayLoad.toString()));				
				wcpsPayLoad=wcpsScalepayLoad;
				wcpsStringBuilder = wcpsStringBuilderScale.append(wcpsScalepayLoad.toString());
			}
			if (currentProcessID.equals("linear_stretch_cube")) {
				containsLinearStretch = true;
				StringBuilder wcpsStretchpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderStretch = basicWCPSStringBuilder();
				wcpsStretchpayLoad.append(createLinearStretchCubeWCPSString(nodeKeyOfCurrentProcess, wcpsPayLoad.toString()));				
				wcpsPayLoad=wcpsStretchpayLoad;
				wcpsStringBuilder = wcpsStringBuilderStretch.append(wcpsStretchpayLoad.toString());
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
	}

	private String createReduceWCPSString(String reduceNodeKey, String payLoad, String filterString, String collName, String dimension) {
		String reduceBuilderExtend = null;
		JSONObject reduceProcesses = processGraph.getJSONObject(reduceNodeKey).getJSONObject("arguments").getJSONObject("reducer").getJSONObject("callback");
		JSONObject reducerPayLoads = new JSONObject();		
		JSONArray reduceNodesArray = new JSONArray();
		String endReducerNode = null;
		JSONArray endReducerNodeAsArray = new JSONArray();

		for (String reducerKey : reduceProcesses.keySet()) {
			JSONObject reducerProcess =  reduceProcesses.getJSONObject(reducerKey);
			for (String reducerField : reducerProcess.keySet()) {
				if (reducerField.equals("result")) {
					Boolean resultFlag = reducerProcess.getBoolean("result");
					if (resultFlag) {
						endReducerNode = reducerKey;
						endReducerNodeAsArray.put(endReducerNode);
						log.debug("End Reducer is " + endReducerNode);
					}
				}
			}
		}		
		
		JSONArray reduceNodesSortedArray = new JSONArray();
		reduceNodesArray.put(endReducerNodeAsArray);		
		for (int n = 0; n < reduceNodesArray.length(); n++) {
			for (int a = 0; a < reduceNodesArray.getJSONArray(n).length(); a++) {
				JSONArray fromNodeOfReducers = getReducerFromNodes(reduceNodesArray.getJSONArray(n).getString(a), reduceProcesses);
				if (fromNodeOfReducers.length()>0) {
				reduceNodesArray.put(fromNodeOfReducers);
				}
				else if (fromNodeOfReducers.length()==0) {
					reduceNodesSortedArray.put(reduceNodesArray.getJSONArray(n).getString(a));
				}
			}
		}
		
		for (int i = 0; i < reduceNodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < reduceNodesSortedArray.length(); j++) {
				if (reduceNodesSortedArray.get(i).equals(reduceNodesSortedArray.get(j))) {
					reduceNodesSortedArray.remove(j);
				}
			}
		}
		
		for (int i = reduceNodesArray.length()-1; i>0; i--) {
			if (reduceNodesArray.getJSONArray(i).length()>0) {				
				for (int a = 0; a < reduceNodesArray.getJSONArray(i).length(); a++) {
					reduceNodesSortedArray.put(reduceNodesArray.getJSONArray(i).getString(a));
				}
			}
		}
		reduceNodesSortedArray.put(endReducerNode);
		reduceNodesArray.remove(reduceNodesArray.length()-1);
		log.debug("Reducer's Old Graph Sequence is " + reduceNodesArray);
		
		for (int i = 0; i < reduceNodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < reduceNodesSortedArray.length(); j++) {
				if (reduceNodesSortedArray.get(i).equals(reduceNodesSortedArray.get(j))) {
					reduceNodesSortedArray.remove(j);
				}
			}
		}
		
		log.debug("Reducer's Graph Sequence is " + reduceNodesSortedArray);

		for (int r = 0; r < reduceNodesSortedArray.length(); r++) {
			String nodeKey = reduceNodesSortedArray.getString(r);
			String name = reduceProcesses.getJSONObject(nodeKey).getString("process_id");
			if (name.equals("array_element")) {
				JSONObject arrayData =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				int arrayIndex = arrayData.getInt("index");
				if ( arrayData.get("data") instanceof JSONObject) {
					reduceBuilderExtend = createBandWCPSString(arrayIndex, reduceNodeKey, filterString, collName);
				}
				else {
					reduceBuilderExtend = arrayData.getJSONArray("data").getString(arrayIndex);
				}
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("mean")) {
				String x = null;
				JSONObject meanArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (meanArguments.get("data") instanceof JSONObject) {
					String dataNode = meanArguments.getJSONObject("data").getString("from_node");
					String meanPayLoad = reducerPayLoads.getString(dataNode);
					x = meanPayLoad;
				}
				else {
					x = String.valueOf(meanArguments.getJSONArray("data"));
				}
				reduceBuilderExtend = createMeanWCPSString(x);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("min")) {
				String minPayLoad = null;
				JSONObject minArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				String dataNode = null;
				if (minArguments.get("data") instanceof JSONObject) {
					for (String fromType : minArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && minArguments.getJSONObject("data").getString("from_argument").equals("data")) {							
							minPayLoad = payLoad;
						}
						else if (fromType.equals("from_node")) {
							dataNode = minArguments.getJSONObject("data").getString("from_node");
							minPayLoad = reducerPayLoads.getString(dataNode);
						}
					}
				}
				else if (minArguments.get("data") instanceof JSONArray) {
					minPayLoad = String.valueOf(minArguments.getJSONArray("data"));
				}
				reduceBuilderExtend = createMinWCPSString(nodeKey, minPayLoad, reduceProcesses, dimension, collName);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("max")) {
				String maxPayLoad = null;
				JSONObject maxArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				String dataNode = null;
				if (maxArguments.get("data") instanceof JSONObject) {
					for (String fromType : maxArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && maxArguments.getJSONObject("data").getString("from_argument").equals("data")) {							
							maxPayLoad = payLoad;
						}
						else if (fromType.equals("from_node")) {
							dataNode = maxArguments.getJSONObject("data").getString("from_node");
							maxPayLoad = reducerPayLoads.getString(dataNode);
						}
					}
				}
				else if (maxArguments.get("data") instanceof JSONArray) {
					maxPayLoad = String.valueOf(maxArguments.getJSONArray("data"));
				}
				reduceBuilderExtend = createMaxWCPSString(nodeKey, maxPayLoad, reduceProcesses, dimension, collName);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("absolute")) {
				String x = null;
				JSONObject absArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (absArguments.get("data") instanceof JSONObject) {
					String dataNode = absArguments.getJSONObject("data").getString("from_node");
					String absPayLoad = reducerPayLoads.getString(dataNode);
					x = absPayLoad;
				}
				else {
					x = String.valueOf(absArguments.getDouble("x"));
				}
				reduceBuilderExtend = createAbsWCPSString(x);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("pi")) {
				reduceBuilderExtend = createPiWCPSString();
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("e")) {
				reduceBuilderExtend = createEulerNumWCPSString();
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("ln")) {
				String x = null;
				JSONObject logNArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (logNArguments.get("data") instanceof JSONObject) {
					String dataNode = logNArguments.getJSONObject("data").getString("from_node");
					String logNPayLoad = reducerPayLoads.getString(dataNode);
					x = logNPayLoad;
				}
				else {
					x = String.valueOf(logNArguments.getDouble("x"));
				}
				reduceBuilderExtend = createLogNWCPSString(x);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("log")) {
				String x = null;
				JSONObject logArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (logArguments.get("data") instanceof JSONObject) {
					String dataNode = logArguments.getJSONObject("data").getString("from_node");
					String logPayLoad = reducerPayLoads.getString(dataNode);
					x = logPayLoad;
				}
				else {
					x = String.valueOf(logArguments.getDouble("x"));
				}
				reduceBuilderExtend = createLogWCPSString(x);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("exp")) {
				String p = null;
				JSONObject expArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (expArguments.get("data") instanceof JSONObject) {
					String dataNode = expArguments.getJSONObject("data").getString("from_node");
					String expPayLoad = reducerPayLoads.getString(dataNode);
					p = expPayLoad;
				}
				else {
					p = String.valueOf(expArguments.getDouble("p"));
				}
				reduceBuilderExtend = createExpWCPSString(p);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("power")) {
				String base = null;
				JSONObject powArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (powArguments.get("data") instanceof JSONObject) {
					String dataNode = powArguments.getJSONObject("base").getString("from_node");
					String powPayLoad = reducerPayLoads.getString(dataNode);
					base = powPayLoad;
				}
				else {
					base = String.valueOf(powArguments.getDouble("base"));
				}
				reduceBuilderExtend = createPowWCPSString(nodeKey, base, reduceProcesses);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("sqrt")) {
				String x = null;
				JSONObject sqrtArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (sqrtArguments.get("data") instanceof JSONObject) {
					String dataNode = sqrtArguments.getJSONObject("data").getString("from_node");
					String sqrtPayLoad = reducerPayLoads.getString(dataNode);
					x = sqrtPayLoad;
				}
				else {
					x = String.valueOf(sqrtArguments.getDouble("x"));
				}
				reduceBuilderExtend = createSqrtWCPSString(x);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("not")) {
				String x = null;
				JSONObject notArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (notArguments.get("expression") instanceof JSONObject) {
					String dataNode = notArguments.getJSONObject("data").getString("from_node");
					String sqrtPayLoad = reducerPayLoads.getString(dataNode);
					x = sqrtPayLoad;
				}
				else {
					x = String.valueOf(notArguments.getBoolean("expression"));
				}
				reduceBuilderExtend = createNotWCPSString(x);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}			
			if (name.equals("eq")) {
				String x = null;
				String y = null;
				JSONObject eqArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (eqArguments.get("x")  instanceof JSONObject) {
					String dataNodeX = eqArguments.getJSONObject("x").getString("from_node");
					String eqPayLoadX = reducerPayLoads.getString(dataNodeX);
					x = eqPayLoadX;
				}
				else {
					x = String.valueOf(eqArguments.getDouble("x"));
				}
				if (eqArguments.get("y") instanceof JSONObject) {
					String dataNodeY = eqArguments.getJSONObject("y").getString("from_node");
					String eqPayLoadY = reducerPayLoads.getString(dataNodeY);
					y = eqPayLoadY;
				}
				else {
					y = String.valueOf(eqArguments.getDouble("y"));
				}
				reduceBuilderExtend = createEqWCPSString(x, y);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("neq")) {
				String x = null;
				String y = null;
				JSONObject neqArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (neqArguments.get("x") instanceof JSONObject) {
					String dataNodeX = neqArguments.getJSONObject("x").getString("from_node");
					String eqPayLoadX = reducerPayLoads.getString(dataNodeX);
					x = eqPayLoadX;
				}
				else {
					x = String.valueOf(neqArguments.getDouble("x"));
				}
				if (neqArguments.get("y") instanceof JSONObject) {
					String dataNodeY = neqArguments.getJSONObject("y").getString("from_node");
					String eqPayLoadY = reducerPayLoads.getString(dataNodeY);
					y = eqPayLoadY;
				}
				else {
					y = String.valueOf(neqArguments.getDouble("y"));
				}
				reduceBuilderExtend = createNotEqWCPSString(x, y);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("lt")) {
				String x = null;
				String y = null;
				JSONObject ltArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (ltArguments.get("x") instanceof JSONObject) {
					String dataNodeX = ltArguments.getJSONObject("x").getString("from_node");
					String eqPayLoadX = reducerPayLoads.getString(dataNodeX);
					x = eqPayLoadX;
				}
				else {
					x = String.valueOf(ltArguments.getDouble("x"));
				}
				if (ltArguments.get("y") instanceof JSONObject) {
					String dataNodeY = ltArguments.getJSONObject("y").getString("from_node");
					String eqPayLoadY = reducerPayLoads.getString(dataNodeY);
					y = eqPayLoadY;
				}
				else {
					y = String.valueOf(ltArguments.getDouble("y"));
				}
				reduceBuilderExtend = createLessThanWCPSString(x, y);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("lte")) {
				String x = null;
				String y = null;
				JSONObject lteArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (lteArguments.get("x") instanceof JSONObject) {
					String dataNodeX = lteArguments.getJSONObject("x").getString("from_node");
					String eqPayLoadX = reducerPayLoads.getString(dataNodeX);
					x = eqPayLoadX;
				}
				else {
					x = String.valueOf(lteArguments.getDouble("x"));
				}
				if (lteArguments.get("y") instanceof JSONObject) {
					String dataNodeY = lteArguments.getJSONObject("y").getString("from_node");
					String eqPayLoadY = reducerPayLoads.getString(dataNodeY);
					y = eqPayLoadY;
				}
				else {
					y = String.valueOf(lteArguments.getDouble("y"));
				}
				reduceBuilderExtend = createLessThanEqWCPSString(x, y);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("gt")) {
				String x = null;
				String y = null;
				JSONObject gtArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (gtArguments.get("x") instanceof JSONObject) {
					String dataNodeX = gtArguments.getJSONObject("x").getString("from_node");
					String eqPayLoadX = reducerPayLoads.getString(dataNodeX);
					x = eqPayLoadX;
				}
				else {
					x = String.valueOf(gtArguments.getDouble("x"));
				}
				if (gtArguments.get("y") instanceof JSONObject) {
					String dataNodeY = gtArguments.getJSONObject("y").getString("from_node");
					String eqPayLoadY = reducerPayLoads.getString(dataNodeY);
					y = eqPayLoadY;
				}
				else {
					y = String.valueOf(gtArguments.getDouble("y"));
				}
				reduceBuilderExtend = createGreatThanWCPSString(x, y);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("gte")) {
				String x = null;
				String y = null;
				JSONObject gteArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (gteArguments.get("x") instanceof JSONObject) {
					String dataNodeX = gteArguments.getJSONObject("x").getString("from_node");
					String eqPayLoadX = reducerPayLoads.getString(dataNodeX);
					x = eqPayLoadX;
				}
				else {
					x = String.valueOf(gteArguments.getDouble("x"));
				}
				if (gteArguments.get("y") instanceof JSONObject) {
					String dataNodeY = gteArguments.getJSONObject("y").getString("from_node");
					String eqPayLoadY = reducerPayLoads.getString(dataNodeY);
					y = eqPayLoadY;
				}
				else {
					y = String.valueOf(gteArguments.getDouble("y"));
				}
				reduceBuilderExtend = createGreatThanEqWCPSString(x, y);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("sin")||name.equals("cos")||name.equals("tan")||name.equals("sinh")||name.equals("cosh")||name.equals("tanh")||name.equals("arcsin")||name.equals("arccos")||name.equals("arctan")) {
				String x = null;
				JSONObject trigArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (trigArguments.get("data") instanceof JSONObject) {
					String dataNode = trigArguments.getJSONObject("data").getString("from_node");
					String trigPayLoad = reducerPayLoads.getString(dataNode);
					x = trigPayLoad;
				}
				else {
					x = String.valueOf(trigArguments.getDouble("x"));
				}
				reduceBuilderExtend = createTrigWCPSString(nodeKey, x, reduceProcesses, name);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("and")) {
				JSONArray andArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("expressions");
				JSONArray andArrayreturn = new JSONArray();
				for (int a = 0; a < andArray.length(); a++) {
					if (andArray.get(a) instanceof JSONObject) {
						String dataNode = andArray.getJSONObject(a).getString("from_node");						
						String andPayLoad = reducerPayLoads.getString(dataNode);
						andArrayreturn.put(andPayLoad);						
					}
					else {
						andArrayreturn.put(andArray.get(a));
					}
				}
				reduceBuilderExtend = createANDWCPSString(andArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("or")) {
				JSONArray orArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("expressions");
				JSONArray orArrayreturn = new JSONArray();
				for (int a = 0; a < orArray.length(); a++) {
					if (orArray.get(a) instanceof JSONObject) {
						String dataNode = orArray.getJSONObject(a).getString("from_node");						
						String orPayLoad = reducerPayLoads.getString(dataNode);
						orArrayreturn.put(orPayLoad);
					}
					else {
						orArrayreturn.put(orArray.get(a));
					}
				}
				reduceBuilderExtend = createORWCPSString(orArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("xor")) {
				JSONArray xorArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("expressions");
				JSONArray xorArrayreturn = new JSONArray();
				for (int a = 0; a < xorArray.length(); a++) {
					if (xorArray.get(a) instanceof JSONObject) {
						String dataNode = xorArray.getJSONObject(a).getString("from_node");						
						String xorPayLoad = reducerPayLoads.getString(dataNode);
						xorArrayreturn.put(xorPayLoad);						
					}
					else {
						xorArrayreturn.put(xorArray.get(a));
					}
				}
				reduceBuilderExtend = createXORWCPSString(xorArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
			}
			if (name.equals("product")) {
				JSONArray productArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("data");
				JSONArray productArrayreturn = new JSONArray();
				for (int a = 0; a < productArray.length(); a++) {
					if (productArray.get(a) instanceof JSONObject) {
						String dataNode = productArray.getJSONObject(a).getString("from_node");										
						String bandPayLoad = reducerPayLoads.getString(dataNode);
						productArrayreturn.put(bandPayLoad);
					}
					else {
						productArrayreturn.put(productArray.get(a));
					}
				}
				reduceBuilderExtend = createProductWCPSString(productArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Reducer PayLoad is " + reducerPayLoads);
			}
			if (name.equals("sum")) {
				JSONArray sumArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("data");
				JSONArray sumArrayreturn = new JSONArray();
				for (int a = 0; a < sumArray.length(); a++) {
					if (sumArray.get(a) instanceof JSONObject) {
						String dataNode = sumArray.getJSONObject(a).getString("from_node");
						String bandPayLoad = reducerPayLoads.getString(dataNode);
						sumArrayreturn.put(bandPayLoad);						
					}
					else {
						sumArrayreturn.put(sumArray.get(a));
					}
				}
				reduceBuilderExtend = createSumWCPSString(sumArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Reducer PayLoad is " + reducerPayLoads);
			}
			if (name.equals("subtract")) {
				JSONArray subtractArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("data");
				JSONArray subtractArrayreturn = new JSONArray();
				for (int a = 0; a < subtractArray.length(); a++) {
					if (subtractArray.get(a) instanceof JSONObject) {
						String dataNode = subtractArray.getJSONObject(a).getString("from_node");
						String bandPayLoad = reducerPayLoads.getString(dataNode);
						subtractArrayreturn.put(bandPayLoad);
					}
					else {
						subtractArrayreturn.put(subtractArray.get(a));
					}
				}
				reduceBuilderExtend = createSubtractWCPSString(subtractArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Reducer PayLoad is " + reducerPayLoads);
			}
			if (name.equals("divide")) {
				JSONArray divideArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("data");
				JSONArray divideArrayreturn = new JSONArray();
				for (int a = 0; a < divideArray.length(); a++) {
					if (divideArray.get(a) instanceof JSONObject) {
						String dataNode = divideArray.getJSONObject(a).getString("from_node");
						String bandPayLoad = reducerPayLoads.getString(dataNode);
						divideArrayreturn.put(bandPayLoad);
					}
					else {
						divideArrayreturn.put(divideArray.get(a));
					}
				}
				reduceBuilderExtend = createDivideWCPSString(divideArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Reducer PayLoad is " + reducerPayLoads);
			}
		}
		return reduceBuilderExtend;
	}

	private String createEqWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");				
		stretchBuilder.append(x + " = " + y +")");
		return stretchBuilder.toString();
	}

	private String createNotEqWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");				
		stretchBuilder.append(x + " != " + y +")");		
		return stretchBuilder.toString();
	}

	private String createLessThanWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");				
		stretchBuilder.append(x + " < " + y +")");		
		return stretchBuilder.toString();
	}

	private String createLessThanEqWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");				
		stretchBuilder.append(x + " <= " + y +")");		
		return stretchBuilder.toString();
	}

	private String createGreatThanWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");				
		stretchBuilder.append(x + " > " + y +")");		
		return stretchBuilder.toString();
	}

	private String createGreatThanEqWCPSString(String x, String y) {
		StringBuilder stretchBuilder = new StringBuilder("(");				
		stretchBuilder.append(x + " >= " + y +")");		
		return stretchBuilder.toString();
	}

	private String createNotWCPSString(String payLoad) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");
		stretchBuilder.append("not " + payLoad);
		stretchString = stretchBuilder.toString();

		return stretchString;
	}

	private String createLogNWCPSString(String payLoad) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");
		stretchBuilder.append("ln(" + payLoad + ")");
		stretchString = stretchBuilder.toString();

		return stretchString;
	}

	private String createLogWCPSString(String payLoad) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");
		stretchBuilder.append("log(" + payLoad + ")");
		stretchString = stretchBuilder.toString();			

		return stretchString;
	}

	private String createExpWCPSString(String payLoad) {
		String stretchString = null;
				StringBuilder stretchBuilder = new StringBuilder("");
				stretchBuilder.append("exp(" + payLoad + ")");
				stretchString = stretchBuilder.toString();			
		
		return stretchString;
	}

	private String createPowWCPSString(String powNodeKey, String payLoad, JSONObject reduceProcesses) {
		String stretchString = null;
		JSONObject powArguments = reduceProcesses.getJSONObject(powNodeKey).getJSONObject("arguments");		
		double pow = powArguments.getDouble("p");
		StringBuilder stretchBuilder = new StringBuilder("");
		stretchBuilder.append("pow(" + payLoad + "," + pow + ")");
		stretchString = stretchBuilder.toString();			

		return stretchString;
	}

	private String createSqrtWCPSString(String payLoad) {		
		String stretchString = null;		
		StringBuilder stretchBuilder = new StringBuilder("");
		stretchBuilder.append("sqrt(" + payLoad + ")");
		stretchString = stretchBuilder.toString();			

		return stretchString;
	}

	private String createAbsWCPSString(String payLoad) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");
		stretchBuilder.append("abs(" + payLoad + ")");
		stretchString = stretchBuilder.toString();			

		return stretchString;
	}

	private String createPiWCPSString() {
		return String.valueOf(Math.PI);
	}

	private String createEulerNumWCPSString() {
		return String.valueOf(Math.E);
	}

	private String createBandWCPSString(int arrayIndex, String reduceNodeKey, String filterString, String collName) {
		StringBuilder stretchBuilder = new StringBuilder("");		
		String fromNodeOfReduce = processGraph.getJSONObject(reduceNodeKey).getJSONObject("arguments").getJSONObject("data").getString("from_node");
		fromNodeOfReduce = getFilterCollectionNode(fromNodeOfReduce);
		JSONObject fromProcess = processGraph.getJSONObject(fromNodeOfReduce);
		if (fromProcess.getString("process_id").equals("load_collection")) {
			String bandName = fromProcess.getJSONObject("arguments").getJSONArray("bands").getString(arrayIndex);
			stretchBuilder.append(createBandSubsetString(collName, bandName, filterString));
		}
		return stretchBuilder.toString();
	}

	private String createANDWCPSString(JSONArray andArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("(" + andArrayreturn.get(0));
		for (int f = 1; f < andArrayreturn.length(); f++) {
			stretchBuilder.append(" and " + andArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createORWCPSString(JSONArray orArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("(" + orArrayreturn.get(0));
		for (int f = 1; f < orArrayreturn.length(); f++) {
			stretchBuilder.append(" or " + orArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createXORWCPSString(JSONArray xorArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("(" + xorArrayreturn.get(0));
		for (int f = 1; f < xorArrayreturn.length(); f++) {
			stretchBuilder.append(" xor " + xorArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createProductWCPSString(JSONArray productArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("("+productArrayreturn.get(0));
		for (int f = 1; f < productArrayreturn.length(); f++) {
			stretchBuilder.append(" * "+productArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createSumWCPSString(JSONArray sumArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("("+sumArrayreturn.get(0));
		for (int f = 1; f < sumArrayreturn.length(); f++) {
			stretchBuilder.append(" + "+sumArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createSubtractWCPSString(JSONArray subtractArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("("+subtractArrayreturn.get(0));
		for (int f = 1; f < subtractArrayreturn.length(); f++) {
			stretchBuilder.append(" - "+subtractArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createDivideWCPSString(JSONArray divideArrayreturn) {
		StringBuilder stretchBuilder = new StringBuilder("("+divideArrayreturn.get(0));
		for (int f = 1; f < divideArrayreturn.length(); f++) {
			stretchBuilder.append(" / "+divideArrayreturn.get(f));
		}
		stretchBuilder.append(")");
		return stretchBuilder.toString();
	}

	private String createMeanWCPSString(String payLoad) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("avg(");
		stretchBuilder.append(payLoad + ")");
		stretchString = stretchBuilder.toString();    	

		return stretchString;
	}

	private String createMaxWCPSString(String maxNodeKey, String payLoad, JSONObject reduceProcesses, String dimension, String collName) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");
		if (dimension.equals("spectral") || dimension.equals("bands")) {
			stretchBuilder.append("max(" + payLoad + ")");    	    
			stretchString = stretchBuilder.toString();    	
		}
		else if (dimension.equals("temporal")) {
			for (int a = 0; a < aggregates.size(); a++) {
				if (aggregates.get(a).getAxis().equals("DATE")) {
					stretchBuilder.append(createTempAggWCPSString(collName, aggregates.get(a)));
					String replaceDate = Pattern.compile("DATE\\(.*?\\)").matcher(payLoad).replaceAll("DATE\\(\\$pm\\)");
					//String replaceDate = wcpsPayLoad.toString().replaceAll("DATE\\(.*?\\)", "DATE\\(\\$pm\\)");
					StringBuilder wcpsAggBuilderMod = new StringBuilder("");
					wcpsAggBuilderMod.append(replaceDate);
					stretchBuilder.append(wcpsAggBuilderMod);
					stretchString=stretchBuilder.toString();
				}
			}
		}

		return stretchString;
	}

	private String createMinWCPSString(String minNodeKey, String payLoad, JSONObject reduceProcesses, String dimension, String collName) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");		
		if (dimension.equals("spectral") || dimension.equals("bands")) {
			stretchBuilder.append("min(" + payLoad + ")");
			stretchString = stretchBuilder.toString();
		}
		else if (dimension.equals("temporal")) {
			for (int a = 0; a < aggregates.size(); a++) {
				if (aggregates.get(a).getAxis().equals("DATE")) {
					stretchBuilder.append(createTempAggWCPSString(collName, aggregates.get(a)));
					log.debug("Min Payload: " + payLoad);
					String replaceDate = Pattern.compile("DATE\\(.*?\\)").matcher(payLoad).replaceAll("DATE\\(\\$pm\\)");
					//String replaceDate = wcpsPayLoad.toString().replaceAll("DATE\\(.*?\\)", "DATE\\(\\$pm\\)");
					StringBuilder wcpsAggBuilderMod = new StringBuilder("");
					wcpsAggBuilderMod.append(replaceDate);
					stretchBuilder.append(wcpsAggBuilderMod);
					stretchString=stretchBuilder.toString();
				}
			}
		}

		return stretchString;
	}

	private String createTrigWCPSString(String trigNodeKey, String payLoad, JSONObject reduceProcesses, String name) {
		String stretchString = null;
			StringBuilder stretchBuilder = new StringBuilder("");
			stretchBuilder.append(name + "(" + payLoad + ")");
			stretchString = stretchBuilder.toString();			
		
		return stretchString;
	}

	private String createReturnResultWCPSString(String returnResultNodeKey, String payload) {
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
	//	private String createResampleWCPSString(String resampleNodeKey) {
	//		String projectionEPSGCode = processGraph.getJSONObject(resampleNodeKey).getJSONObject("arguments").getString("projection");
	//        String currentWCPSQuery = wcpsStringBuilder.toString();
	//		int beginIndex = currentWCPSQuery.indexOf("return encode (") + 15;
	//		int endIndex = currentWCPSQuery.indexOf(", \"");
	//		log.debug("payload range: " + beginIndex + " " + endIndex);
	//		StringBuilder resampleBuilder = new StringBuilder(currentWCPSQuery.substring(0, beginIndex));
	//		String currentPayload = currentWCPSQuery.substring(beginIndex, endIndex);		
	//		//TODO read the name of the spatial coordinate axis from describeCoverage or filter elements in order to correctly apply (E,N), (lat,lon) or X,Y depending on coordinate system
	//		resampleBuilder.append("crsTransform(" + currentPayload + ",{"
	//				+ "E:\"http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\","
	//				+ "N:\"http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\""
	//				+ "}, {})");
	//		resampleBuilder.append(currentWCPSQuery.substring(endIndex));
	//		log.debug("current payload: " + currentPayload);
	//		log.debug("resample wcps query: " + resampleBuilder.toString());
	//		return resampleBuilder.toString();
	//	}

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
		JSONObject scaleArgumets = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments");
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

	private String createLinearStretchCubeWCPSString(String linearScaleNodeKey, String payLoad) {
		double min = 0;
		double max = 1;
		JSONObject scaleArgumets = processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments");

		for (String outputMinMax : scaleArgumets.keySet()) {
			if (outputMinMax.contentEquals("min")) {
				min = (double) processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("min");		       
			}
			else if (outputMinMax.contentEquals("max")) {
				max = (double) processGraph.getJSONObject(linearScaleNodeKey).getJSONObject("arguments").getDouble("max");
			}
		}

		StringBuilder stretchBuilder = new StringBuilder("(");
		stretchBuilder.append(payLoad + ")");
		String stretchString = stretchBuilder.toString();
		String stretch1 = stretchString.replace("$pm", "$pm1");
		String stretch2 = stretchString.replace("$pm", "$pm2");
		String stretch3 = stretchString.replace("$pm", "$pm3");
		String stretch4 = stretchString.replace("$pm", "$pm4");
		StringBuilder stretchBuilderExtend = new StringBuilder("(unsigned char)(");
		stretchBuilderExtend.append(stretch1 + " - " + "min" + stretch2 + ")*((" + max + "-" + min + ")" + "/(max" + stretch3 + "-min" + stretch4 + ")) + 0");

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
			//this.filters.remove(tempFilter);
			//this.filters.add(new Filter(axis, "$pm"));
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
	
	private String filter_polygon(String collectionName) {
		return collectionName;
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

	private JSONArray getReducerFromNodes(String nextNode, JSONObject reduceProcesses) {
		JSONObject nextNodeName = new JSONObject();
		JSONArray fromNodes = new JSONArray();
		String nextFromNode = null;
		JSONObject reducerProcessArguments =  reduceProcesses.getJSONObject(nextNode).getJSONObject("arguments");
		for (String argumentsKey : reducerProcessArguments.keySet()) {
			if (argumentsKey.contentEquals("data")) {
				if (reducerProcessArguments.get("data") instanceof JSONObject) {
					for (String fromKey : reducerProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = reducerProcessArguments.getJSONObject("data").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				else if (reducerProcessArguments.get("data") instanceof JSONArray) {
					JSONArray reduceData = reducerProcessArguments.getJSONArray("data");
					for(int a = 0; a < reduceData.length(); a++) {
						if (reduceData.get(a) instanceof JSONObject) {
							for (String fromKey : reduceData.getJSONObject(a).keySet()) {
								if (fromKey.contentEquals("from_node")) {
									nextFromNode = reduceData.getJSONObject(a).getString("from_node");
									fromNodes.put(nextFromNode);
								}
							}
						}
					}
				}
				nextNodeName.put(nextNode, fromNodes);				
			}
			if (argumentsKey.contentEquals("expressions")) {
				if (reducerProcessArguments.get("expressions") instanceof JSONObject) {
					for (String fromKey : reducerProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = reducerProcessArguments.getJSONObject("expressions").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				else if (reducerProcessArguments.get("expressions") instanceof JSONObject) {
					JSONArray reduceData = reducerProcessArguments.getJSONArray("expressions");
					for(int a = 0; a < reduceData.length(); a++) {
						if (reduceData.get(a) instanceof JSONObject) {
							for (String fromKey : reduceData.getJSONObject(a).keySet()) {
								if (fromKey.contentEquals("from_node")) {
									nextFromNode = reduceData.getJSONObject(a).getString("from_node");
									fromNodes.put(nextFromNode);
								}
							}
						}
					}
				}
				nextNodeName.put(nextNode, fromNodes);
			}
			if (argumentsKey.contentEquals("x")) {
				if (reducerProcessArguments.get("x") instanceof JSONObject) {
					for (String fromKey : reducerProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = reducerProcessArguments.getJSONObject("x").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(nextNode, fromNodes);
			}
			if (argumentsKey.contentEquals("y")) {
				if (reducerProcessArguments.get("y") instanceof JSONObject) {
					for (String fromKey : reducerProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = reducerProcessArguments.getJSONObject("y").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(nextNode, fromNodes);
			}
		}
		log.debug("Order of the nodes is: " + nextNodeName);
		return fromNodes;
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

		for(int a = nodesArray.length()-1; a>0; a--) {
			log.debug("Executing Process : " + nodesArray.getString(a));
			String nodeKeyOfCurrentProcess = nodesArray.getString(a);
			String currentProcessID = processGraph.getJSONObject(nodeKeyOfCurrentProcess).getString("process_id");
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
			
			JSONArray processDataCubeTempExt = new JSONArray();
			JSONObject spatialExtentNode = new JSONObject();
			createDateRangeFilterFromArgs(processDataCubeTempExt, collection, true);
			createBoundingBoxFilterFromArgs(loadCollectionNodeArguments, srs, collection, true);
			
			for (String argumentKey : loadCollectionNodeArguments.keySet()) {
				if (argumentKey.equals("spatial_extent")) {
					if (!loadCollectionNodeArguments.isNull(argumentKey)) {
						spatialExtentNode = loadCollectionNodeArguments.getJSONObject("spatial_extent");
						log.debug("currently working on spatial extent: " + spatialExtentNode.toString(4));
						createBoundingBoxFilterFromArgs(loadCollectionNodeArguments, srs, collection, false);
					}
				}
				if (argumentKey.equals("temporal_extent")) {
					if (!loadCollectionNodeArguments.isNull(argumentKey)) {						
						processDataCubeTempExt = (JSONArray) loadCollectionNodeArguments.get("temporal_extent");					
						log.debug("currently working on temporal extent: " + processDataCubeTempExt.toString(4));					
						createDateRangeFilterFromArgs(processDataCubeTempExt, collection, false);
					}
				}
			}
		}

		else if (processID.contains("_time")) {
			log.debug("Found Time node: " + processNode.getString("process_id"));
			createTemporalAggregate(processID);
			log.debug("Filters are: " + filters);
		}

		else if (processID.contains("reduce")) {
			log.debug("Found Time node: " + processNode.getString("process_id"));
			String dimension = processNode.getJSONObject("arguments").getString("dimension");
			if (dimension.equals("temporal")) {
				JSONObject reducer = processNode.getJSONObject("arguments").getJSONObject("reducer").getJSONObject("callback");
				for (String nodeKey : reducer.keySet()) {
					String name = reducer.getJSONObject(nodeKey).getString("process_id");
					createReduceTemporalAggregate(name);
				}
			}			
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

		else if (processID.equals("filter_temporal")) {
			String filterCollectionNodeKey = null;
			String filterTempNodeKey = processNodeKey;
			String filterTempfromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");			
			log.debug("Key Temp is : " + filterTempNodeKey);
			filterCollectionNodeKey = getFilterCollectionNode(filterTempfromNode);
			JSONObject loadCollectionNode = processGraph.getJSONObject(filterCollectionNodeKey).getJSONObject("arguments");				
			String coll = (String) loadCollectionNode.get("id");
			JSONObject processFilter = processGraph.getJSONObject(filterTempNodeKey);
			JSONObject processFilterArguments = processFilter.getJSONObject("arguments");
			JSONArray extentArray = new JSONArray();			
			extentArray = (JSONArray) processFilterArguments.get("extent");			
			log.debug("Temp New Extent : " + extentArray);
			createDateRangeFilterFromArgs(extentArray, coll, false);
		}

		else if (processID.equals("filter_bbox")) {
			String filterCollectionNodeKey = null;
			String filterBboxNodeKey = processNodeKey;
			String filterBboxfromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");			
			filterCollectionNodeKey = getFilterCollectionNode(filterBboxfromNode);
			log.debug("Key Bbox is : " + filterCollectionNodeKey);
			JSONObject loadCollectionNode = processGraph.getJSONObject(filterCollectionNodeKey).getJSONObject("arguments");			
			String coll = (String) loadCollectionNode.get("id");
			JSONObject processFilter = processGraph.getJSONObject(filterBboxNodeKey);
			JSONObject processFilterArguments = processFilter.getJSONObject("arguments");

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
				log.debug("Spat New Extent : " + processFilterArguments);
				createBoundingBoxFilterFromArgs(processFilterArguments, srs, coll, false);
			}
		}		
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

	private void createDateRangeFilterFromArgs(JSONArray extentArray, String coll, Boolean tempNull) {
		String fromDate = null;
		String toDate = null;
		JSONObject extent;
		
		if (tempNull) {
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

			log.debug("Temporal extent is: " + temporal);

			if (templower != null && tempupper != null) {
				log.debug("Temporal extent is: |" + templower + "|:|" + tempupper + "|");
				if(LocalDateTime.parse(templower.replace("Z", "")).equals(LocalDateTime.parse(tempupper.replace("Z", "")))) {
					tempupper = null;
					log.debug("Dates are identical. To date is set to null!");
				}			
				Filter dateFilter = null;
				for (Filter filter : this.filters) {
					if (filter.getAxis().equals("DATE")) {
						dateFilter = filter;
					}
				}
				this.filters.remove(dateFilter);
				this.filters.add(new Filter("DATE", templower, tempupper));
			}
		}

		else {
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

			log.debug("Temporal extent is: " + temporal);
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
				Filter dateFilter = null;
				for (Filter filter : this.filters) {
					if (filter.getAxis().equals("DATE")) {
						dateFilter = filter;
					}
				}			
				this.filters.remove(dateFilter);
				this.filters.add(new Filter("DATE", fromDate, toDate));
			}
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

	private void createBoundingBoxFilterFromArgs(JSONObject argsObject, int srs, String coll, Boolean spatNull) {
		String left = null;
		String right = null;
		String top = null;
		String bottom = null;
		log.debug("Creating spatial extent filter from process");

		if (spatNull) {
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
			double westlower = spatial.getDouble(0)+0.00001;
			double eastupper = spatial.getDouble(2)-0.00001;
			double southlower = spatial.getDouble(1)+0.00001;
			double northupper = spatial.getDouble(3)-0.00001;
			log.debug("Spatial extent is: " + spatial);
			left = Double.toString(westlower);
			right = Double.toString(eastupper);
			top = Double.toString(northupper);
			bottom = Double.toString(southlower).toString();

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

			if (left != null && right != null && top != null && bottom != null) {
				Filter eastFilter = null;
				Filter westFilter = null;
				for (Filter filter : this.filters) {
					if (filter.getAxis().equals("E")) {
						eastFilter = filter;
					}
					else if (filter.getAxis().equals("N")) {
						westFilter = filter;
					}
				}
				this.filters.remove(eastFilter);
				this.filters.remove(westFilter);
				this.filters.add(new Filter("E", left, right));
				this.filters.add(new Filter("N", bottom, top));			
			} else {
				log.error("No spatial information could be found in process!");
			}
		}

		else {
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

						log.debug("Spatial extent is: " + spatial);
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
				Filter eastFilter = null;
				Filter westFilter = null;
				for (Filter filter : this.filters) {
					if (filter.getAxis().equals("E")) {
						eastFilter = filter;
					}
					else if (filter.getAxis().equals("N")) {
						westFilter = filter;
					}
				}
				this.filters.remove(eastFilter);
				this.filters.remove(westFilter);
				this.filters.add(new Filter("E", left, right));
				this.filters.add(new Filter("N", bottom, top));			
			} else {
				log.error("No spatial information could be found in process!");
			}			
		}
	}

	private void createReduceTemporalAggregate(String processName) {
		String aggregateType = processName;
		Vector<String> params = new Vector<String>();
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals("DATE")) {
				params.add(filter.getLowerBound());
				params.add(filter.getUpperBound());
			}
		}
		log.debug("Temporal aggregate added!");
		aggregates.add(new Aggregate(new String("DATE"), aggregateType, params));
	}

	private void createTemporalAggregate(String processName) {
		String aggregateType = processName.split("_")[0];
		Vector<String> params = new Vector<String>();
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals("DATE")) {
				params.add(filter.getLowerBound());
				params.add(filter.getUpperBound());
			}
		}
		log.debug("Temporal aggregate added!");
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