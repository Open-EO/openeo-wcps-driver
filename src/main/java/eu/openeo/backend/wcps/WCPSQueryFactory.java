package eu.openeo.backend.wcps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private Vector<Filter> filtersPolygon;
	private Vector<Aggregate> aggregates;
	private String outputFormat = "json";
	private JSONObject processGraph;
	private boolean withUDF = false;

	Logger log = LogManager.getLogger();

	/**
	 * Creates WCPS query from openEO process Graph
	 * 
	 * @param openEOGraph
	 */
	public WCPSQueryFactory(JSONObject openEOGraph) {
		collectionIDs = new Vector<Collection>();
		aggregates = new Vector<Aggregate>();
		filters = new Vector<Filter>();
		filtersPolygon = new Vector<Filter>();
		wcpsStringBuilder = new StringBuilder("");
		this.processGraph = openEOGraph;
		this.build();
	}

	public String getOutputFormat() {
		return outputFormat;
	}
	
	public void setOutputFormat(String outputFormat) {
		this.outputFormat =  outputFormat;
	}
	
	public boolean isWithUDF() {
		return withUDF;
	}

	private StringBuilder basicWCPSStringBuilder(String varPayLoad) {
		StringBuilder basicWCPS;
		basicWCPS = new StringBuilder("for ");
		
		for (int c = 1; c <= collectionIDs.size(); c++) {
			String collectionID = processGraph.getJSONObject(collectionIDs.get(c - 1).getName()).getJSONObject("arguments").getString("id");
			basicWCPS.append("$" + collectionID + collectionIDs.get(c - 1).getName() + " in (" + collectionID + ")");
			if (c < collectionIDs.size()) {
				basicWCPS.append(", ");
			}
		}
		basicWCPS.append(" let " + varPayLoad + " $mock := 1 return encode ( ");
		return basicWCPS;
	}

	private void build() {
		StringBuilder wcpsPayLoad = new StringBuilder("");
		StringBuilder varPayLoad = new StringBuilder("");
		//String collectionVar = "$c";
		JSONArray nodesArray = new JSONArray();
		JSONArray nodesSortedArray = new JSONArray();
		JSONObject storedPayLoads = new JSONObject();
		String saveNode = getSaveNode();
		JSONArray saveNodeAsArray = new JSONArray();
		log.debug(processGraph.toString());
		parseOpenEOProcessGraph();
//		for (int c = 1; c <= collectionIDs.size(); c++) {
//			log.debug(collectionIDs.get(c - 1).getName());
//			String collectionID = processGraph.getJSONObject(collectionIDs.get(c - 1).getName()).getJSONObject("arguments").getString("id");
//			wcpsStringBuilder.append("$" + collectionID + collectionIDs.get(c - 1).getName() + " in (" + collectionID + ")");
//			if (c < collectionIDs.size()) {
//				wcpsStringBuilder.append(", ");
//			}
//		}
		wcpsStringBuilder = basicWCPSStringBuilder(varPayLoad.toString());		
		saveNodeAsArray.put(saveNode);
		nodesArray.put(saveNodeAsArray);
		
		for (int n = 0; n < nodesArray.length(); n++) {
			for (int a = 0; a < nodesArray.getJSONArray(n).length(); a++) {
				JSONArray fromNodeOfProcess = getFromNodeOfCurrentKey(nodesArray.getJSONArray(n).getString(a));
				if (fromNodeOfProcess.length()>0) {
					nodesArray.put(fromNodeOfProcess);
				}
				else if (fromNodeOfProcess.length()==0) {
					nodesSortedArray.put(nodesArray.getJSONArray(n).getString(a));
				}
			}
		}
		
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}
		
		nodesArray.remove(nodesArray.length()-1);
		for (int i = nodesArray.length()-1; i>0; i--) {
			if (nodesArray.getJSONArray(i).length()>0) {				
				for (int a = 0; a < nodesArray.getJSONArray(i).length(); a++) {
					nodesSortedArray.put(nodesArray.getJSONArray(i).getString(a));
				}
			}
		}
		
		nodesSortedArray.put(saveNode);
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}
		
		JSONArray processesSequence = new JSONArray();
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			processesSequence.put(processGraph.getJSONObject(nodesSortedArray.getString(i)).getString("process_id"));
		}
		
		log.debug("Process Graph's Nodes Sequence is : ");
		log.debug(nodesSortedArray);
		log.debug("Process Graph's Processes Sequence is : ");
		log.debug(processesSequence);	
		
		boolean containsMergeCubes = false;
		boolean containsNormDiffProcess = false;
		boolean containsFilterBandProcess = false;
		boolean containsNDVIProcess = false;
		boolean containsTempAggProcess = false;
		boolean containsReduceProcess = false;
		boolean containsLinearStretch = false;
		boolean containsLinearScale = false;
		boolean containsApplyProcess = false;
		boolean containsResampleProcess = false;
		boolean collDims2D = false;
		//int loadedCubes = 1;

		myLoop:		for(int i = 0; i < nodesSortedArray.length(); i++) {
			String nodeKeyOfCurrentProcess = nodesSortedArray.getString(i);
			JSONObject currentProcess = processGraph.getJSONObject(nodeKeyOfCurrentProcess);
			String currentProcessID = currentProcess.getString("process_id");			
			JSONObject currentProcessArguments = currentProcess.getJSONObject("arguments");
			log.debug("Building WCPS Query for : " + currentProcessID);			
			
			if (currentProcessID.equals("load_collection")) {
				String collectionID = currentProcessArguments.getString("id");
				
				JSONObject jsonresp = null;
				try {
					jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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

				JSONObject extent = jsonresp.getJSONObject("extent");
				JSONArray temporal = extent.getJSONArray("temporal");
				String templower = null;
				
				try {
					templower = temporal.get(0).toString();					
				}			
				catch (JSONException e) {
					collDims2D = true;
					log.error("An error occured: " + e.getMessage());
					
				}				
				
				wcpsPayLoad.append(createFilteredCollectionString("$"+collectionID+nodeKeyOfCurrentProcess, collectionID));
				log.debug("Initial PayLoad WCPS is: ");
				log.debug(wcpsPayLoad);
				wcpsStringBuilder.append(wcpsPayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsPayLoad.toString());
				wcpsPayLoad = new StringBuilder("");
				log.debug("Load Collection PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
				log.info("Collection Dims : " + collDims2D);
				//loadedCubes = loadedCubes+1;
			}
			if (currentProcessID.equals("merge_cubes")) {
				containsMergeCubes = true;
				StringBuilder wcpsMergepayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderMerge = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad1 = null;
				String payLoad2 = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				if (processArguments.get("cube1") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("cube1").keySet()) {
						
						if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("cube1").getString("from_node");
							payLoad1 = storedPayLoads.getString(dataNode);
						}
					}
				}
				if (processArguments.get("cube2") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("cube2").keySet()) {
						
						if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("cube2").getString("from_node");
							payLoad2 = storedPayLoads.getString(dataNode);
						}
					}
				}
				String overlapResolver =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments").getString("overlap_resolver");
				wcpsMergepayLoad.append("("+payLoad1+")"+overlapResolver+"("+payLoad2+")");
				wcpsPayLoad=wcpsMergepayLoad;
				wcpsStringBuilder = wcpsStringBuilderMerge.append(wcpsMergepayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMergepayLoad.toString());
				log.debug("Merge Cubes Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("resample_cube_spatial")) {
				containsResampleProcess = true;
				StringBuilder wcpsResamplepayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderResample = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				String collectionID = null;
				String targetCollectionID = null;
				String collectionVar = null;
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							String collectionNodeKey = getFilterCollectionNode(dataNode);
							collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
							collectionVar = "$"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
							payLoad = storedPayLoads.getString(dataNode);
						}
					}
				}
				if (processArguments.get("target") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("target").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("target").getString("from_argument").equals("target")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("target").getString("from_node");
							String collectionNodeKey = getFilterCollectionNode(dataNode);
							targetCollectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
						}
					}
				}
				String xAxis = null;
				String yAxis = null;
				String xLow = null;
				String yLow = null;
				String xHigh = null;
				String yHigh = null;
				for (int f = 0; f < filters.size(); f++) {
					Filter filter = filters.get(f);
					String axis = filter.getAxis();
					if(axis.contains(collectionID)) {
						String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
						if (axisUpperCase.equals("N") || axisUpperCase.equals("Y") || axisUpperCase.equals("LAT")) {
							yAxis = axis.replace("_"+ collectionID, "");
							yLow = filter.getLowerBound();
							yHigh = filter.getUpperBound();
						}
						if (axisUpperCase.equals("E") || axisUpperCase.equals("X") || axisUpperCase.equals("LONG")) {
							xAxis = axis.replace("_"+ collectionID, "");
							xLow = filter.getLowerBound();
							xHigh = filter.getUpperBound();
						}
					}
				}
				JSONObject collectionSTACMetdata = null;
				try {
					collectionSTACMetdata = readJsonFromUrl(
							ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
				String resSource = null;
				for(int c = 0; c < bandsArray.length(); c++) {
					resSource = bandsArray.getJSONObject(c).getString("gsd");
				}
				JSONObject targetCollectionSTACMetdata = null;
				try {
					targetCollectionSTACMetdata = readJsonFromUrl(
							ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + targetCollectionID);
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
				JSONArray targetBandsArray = ((JSONObject) targetCollectionSTACMetdata.get("properties")).getJSONArray("eo:bands");		
				String resTarget = null;
				for(int c = 0; c < targetBandsArray.length(); c++) {
					resTarget = targetBandsArray.getJSONObject(c).getString("gsd");
				}
				wcpsResamplepayLoad.append(createResampleSpatialCubeWCPSString(nodeKeyOfCurrentProcess, payLoad, resSource, resTarget, xAxis, xLow, xHigh, yAxis, yLow, yHigh));
				wcpsPayLoad=wcpsResamplepayLoad;
				wcpsStringBuilder = wcpsStringBuilderResample.append(wcpsResamplepayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsResamplepayLoad.toString());
				log.debug("Resample Spatial Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("run_udf")) {
				this.withUDF =  true;
				StringBuilder wcpsUDFpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderUDFPayload = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments = processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				
				//String udfCode = processArguments.getString("udf");
				if (processArguments.getString("runtime").toLowerCase().equals("python")) {
					if (processArguments.get("data") instanceof JSONObject) {
						for (String fromType : processArguments.getJSONObject("data").keySet()) {
							if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
								payLoad = wcpsPayLoad.toString();
							}
							else if (fromType.equals("from_node")) {
								String dataNode = processArguments.getJSONObject("data").getString("from_node");
								payLoad = storedPayLoads.getString(dataNode);
							}
						}
					}
				}
				if (processArguments.getString("runtime").toLowerCase().equals("r")) {
					if (processArguments.get("data") instanceof JSONObject) {
						for (String fromType : processArguments.getJSONObject("data").keySet()) {
							if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
								payLoad = wcpsPayLoad.toString();
							}
							else if (fromType.equals("from_node")) {
								String dataNode = processArguments.getJSONObject("data").getString("from_node");
								payLoad = storedPayLoads.getString(dataNode);
							}
						}
					}
				}
				wcpsUDFpayLoad.append(payLoad);
				wcpsPayLoad=wcpsUDFpayLoad;
				String saveUDFPayload = wcpsStringBuilderUDFPayload.append(wcpsUDFpayLoad.toString()).toString();
				StringBuilder wcpsStringBuilderSaveUDFResult = new StringBuilder("");
				wcpsStringBuilderSaveUDFResult.append(createUDFReturnResultWCPSString(saveUDFPayload));
				wcpsStringBuilder = wcpsStringBuilderSaveUDFResult;
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsUDFpayLoad.toString());
				
				log.debug("UDF Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
				break myLoop;
			}
			if (currentProcessID.equals("run_udf_externally")) {
				StringBuilder wcpsUDFpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderUDFPayload = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				
				//String udfCode = processArguments.getString("url");
					if (processArguments.getJSONObject("data") instanceof JSONObject) {
						for (String fromType : processArguments.getJSONObject("data").keySet()) {
							if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
								payLoad = wcpsPayLoad.toString();
							}
							else if (fromType.equals("from_node")) {
								String dataNode = processArguments.getJSONObject("data").getString("from_node");
								payLoad = storedPayLoads.getString(dataNode);
							}
						}
					}				
				
					if (processArguments.getJSONObject("data") instanceof JSONObject) {
						for (String fromType : processArguments.getJSONObject("data").keySet()) {
							if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
								payLoad = wcpsPayLoad.toString();
							}
							else if (fromType.equals("from_node")) {
								String dataNode = processArguments.getJSONObject("data").getString("from_node");
								payLoad = storedPayLoads.getString(dataNode);
							}
						}
					}				
				wcpsUDFpayLoad.append(payLoad);
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsUDFpayLoad.toString());
				String saveUDFPayload = wcpsStringBuilderUDFPayload.append(wcpsUDFpayLoad.toString()).toString();
				StringBuilder wcpsStringBuilderSaveUDFResult = new StringBuilder("");
				wcpsStringBuilderSaveUDFResult.append(createUDFReturnResultWCPSString(saveUDFPayload));
				wcpsStringBuilder = wcpsStringBuilderSaveUDFResult;
				
				log.debug("UDF Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
				break myLoop;
			}
			if (currentProcessID.equals("filter_bbox")) {
				StringBuilder wcpsFilterBboxpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderFilterBboxPayload = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");						
							payLoad = storedPayLoads.getString(dataNode);
						}
					}
				}
				wcpsFilterBboxpayLoad.append(payLoad);
				wcpsPayLoad=wcpsFilterBboxpayLoad;
				wcpsStringBuilder=wcpsStringBuilderFilterBboxPayload.append(wcpsFilterBboxpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsFilterBboxpayLoad.toString());
				log.debug("Filter Bounding Box Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("filter_temporal")) {
            	StringBuilder wcpsFilterDatepayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderFilterDatePayload = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");							
							payLoad = storedPayLoads.getString(dataNode);
						}
					}
				}
				wcpsFilterDatepayLoad.append(payLoad);
				wcpsPayLoad=wcpsFilterDatepayLoad;
				wcpsStringBuilder=wcpsStringBuilderFilterDatePayload.append(wcpsFilterDatepayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsFilterDatepayLoad.toString());
				log.debug("Filter Temporal Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("filter_bands")) {
				containsFilterBandProcess = true;
				StringBuilder wcpsFilterpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderFilterPayload = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				String collectionID = null;
				String collectionVar = null;
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							String collectionNodeKey = getFilterCollectionNode(dataNode);
							collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
							collectionVar = "$"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
							payLoad = storedPayLoads.getString(dataNode);
						}
					}
				}
				String filterString = payLoad;
				filterString = filterString.substring(collectionVar.length());
				JSONArray currentProcessBands = currentProcessArguments.getJSONArray("bands");
				String bandfromIndex = currentProcessBands.getString(0);
				String bandName = null;
				JSONObject collectionSTACMetdata = null;
				try {
					collectionSTACMetdata = readJsonFromUrl(
							ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
//					String bandCommon = bandsArray.getJSONObject(c).getString("name");
					if (bandCommon.equals(bandfromIndex)) {
						bandName = bandsArray.getJSONObject(c).getString("name");
						break;
					}
					else {
						bandName = bandfromIndex;
					}
			    }
				wcpsFilterpayLoad.append(createBandSubsetString(collectionVar, bandName, filterString));
				wcpsPayLoad=wcpsFilterpayLoad;
				wcpsStringBuilder=wcpsStringBuilderFilterPayload.append(wcpsFilterpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsFilterpayLoad.toString());
				log.debug("Filter Bands Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("mask_colored")) {
				StringBuilder wcpsMaskColorpayLoad = new StringBuilder("switch case ");
				StringBuilder wcpsStringBuilderMaskColorPayload = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							payLoad = storedPayLoads.getString(dataNode);
						}
					}
				}
				wcpsMaskColorpayLoad.append(processArguments.getString("lowerThreshold") + " < (" + payLoad + ") > " + processArguments.getString("upperThreshold") + " return {red:" + processArguments.get("red") + "; green:" + processArguments.get("green") + "; blue:" + processArguments.get("blue") + "} default return {red: 230; green: 240; blue: 255}");
				wcpsPayLoad=wcpsMaskColorpayLoad;
				wcpsStringBuilder=wcpsStringBuilderMaskColorPayload.append(wcpsMaskColorpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsMaskColorpayLoad.toString());
				log.debug("Mask Colored Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}			
			if (currentProcessID.equals("if_custom")) {
				StringBuilder wcpsIFpayLoad = new StringBuilder("");
				String payLoad = null;
				String acceptPayLoad = null;
				String rejectPayLoad = null;
				double accept = 0;
				double reject = 0;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				
				if (processArguments.get("value") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("value").keySet()) {
//						if (fromType.equals("from_argument") && processArguments.getJSONObject("value").getString("from_argument").equals("data")) {
//							payLoad = wcpsPayLoad.toString();
//							accept = processArguments.getDouble("accept");
//							reject = processArguments.getDouble("reject");
//						}
						if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("value").getString("from_node");
							payLoad = storedPayLoads.getString(dataNode);
							log.debug("IF Process : ");
							if (processArguments.get("accept") instanceof JSONObject) {
								String acceptDataNode = processArguments.getJSONObject("accept").getString("from_node");
								acceptPayLoad = storedPayLoads.getString(acceptDataNode);
								log.debug("Accept Payload : " + acceptPayLoad);
							}
							else {
								accept = processArguments.getDouble("accept");
								log.debug("Accept Payload : " + accept);
							}
							if (processArguments.get("reject") instanceof JSONObject) {
								String rejectDataNode = processArguments.getJSONObject("reject").getString("from_node");
								rejectPayLoad = storedPayLoads.getString(rejectDataNode);
								log.debug("Reject Payload : " + rejectPayLoad);
							}
							else {
								reject = processArguments.getDouble("reject");
								log.debug("Reject Payload : " + reject);
							}
							
						}
					}
				}
				
				if (processArguments.get("accept") instanceof JSONObject) {
					if (processArguments.get("reject") instanceof JSONObject) {
						wcpsIFpayLoad.append("("+payLoad+"*"+acceptPayLoad+"+"+"(not "+payLoad+")*"+rejectPayLoad+")");
					}
					else {
						wcpsIFpayLoad.append("("+payLoad+"*"+acceptPayLoad+"+"+"(not "+payLoad+")*"+reject+")");
					}					
				}
				else {
					if (processArguments.get("reject") instanceof JSONObject) {
						wcpsIFpayLoad.append("("+payLoad+"*"+accept+"+"+"(not "+payLoad+")*"+rejectPayLoad+")");
					}
					else {
						wcpsIFpayLoad.append("("+payLoad+"*"+accept+"+"+"(not "+payLoad+")*"+reject+")");
					}	
				}				
				
				//wcpsIFpayLoad.append("("+payLoad+"*"+accept + ")");
				wcpsPayLoad=wcpsIFpayLoad;
				StringBuilder wcpsStringBuilderMaskThresPayload = basicWCPSStringBuilder(varPayLoad.toString());
				wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsIFpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsIFpayLoad.toString());
				log.debug("IF Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}			
			if (currentProcessID.equals("mask_custom")) {
				StringBuilder wcpsArrayFilterpayLoad = new StringBuilder("");
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
							varPayLoad.append(" $payLoad"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$rm")+",");
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							payLoad = storedPayLoads.getString(dataNode);
							log.debug("Array Filterd Process : ");
							varPayLoad.append(" $payLoad"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$rm")+",");
						}
					}
				}
				
				if (processArguments.get("mask") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("mask").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("mask").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
							varPayLoad.append(" $filterArray"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$qm")+",");
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("mask").getString("from_node");
							payLoad = storedPayLoads.getString(dataNode);
							log.debug("Array Filterd Process : ");
							varPayLoad.append(" $filterArray"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$qm")+",");
						}
					}
				}
				
				double replacement = 0;
				replacement = processArguments.getDouble("replacement");
				
				wcpsArrayFilterpayLoad.append("(($filterArray"+nodeKeyOfCurrentProcess+")"+"*$payLoad"+nodeKeyOfCurrentProcess+")" + " + " + "((not($filterArray"+nodeKeyOfCurrentProcess+"))"+"*"+replacement+")");
				wcpsPayLoad=wcpsArrayFilterpayLoad;
				
				StringBuilder wcpsStringBuilderMaskThresPayload = basicWCPSStringBuilder(varPayLoad.toString());
				wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsArrayFilterpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsArrayFilterpayLoad.toString());
				log.debug("Mask Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}			
			if (currentProcessID.equals("array_filter")) {
				StringBuilder wcpsArrayFilterpayLoad = new StringBuilder("");
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
							varPayLoad.append(" $filterArray"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$qm") + " " + processArguments.getString("comparator") + " " + processArguments.getString("threshold")+",");
							varPayLoad.append(" $payLoad"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$rm")+",");
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							payLoad = storedPayLoads.getString(dataNode);
							log.debug("Array Filterd Process : ");
							varPayLoad.append(" $filterArray"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$qm") + " " + processArguments.getString("comparator") + " " + processArguments.getString("threshold")+",");
							varPayLoad.append(" $payLoad"+ nodeKeyOfCurrentProcess + " := " + payLoad.replaceAll("\\$pm", "\\$rm")+",");
						}
					}
				}
				
				wcpsArrayFilterpayLoad.append("(($filterArray"+nodeKeyOfCurrentProcess+")"+"*$payLoad"+nodeKeyOfCurrentProcess+")");
				wcpsPayLoad=wcpsArrayFilterpayLoad;
				
				StringBuilder wcpsStringBuilderMaskThresPayload = basicWCPSStringBuilder(varPayLoad.toString());
				wcpsStringBuilder=wcpsStringBuilderMaskThresPayload.append(wcpsArrayFilterpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsArrayFilterpayLoad.toString());
				log.debug("Array Filterd Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}			
			if (currentProcessID.equals("normalized_difference")) {
				containsNormDiffProcess = true;
				StringBuilder wcpsNormDiffpayLoad = new StringBuilder("((double)");
				StringBuilder wcpsStringBuilderNormDiff = basicWCPSStringBuilder(varPayLoad.toString());
				JSONObject bandArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				String band1 = null;
				String band2 = null;
				if (bandArguments.get("band1") instanceof JSONObject) {
					for (String fromType : bandArguments.getJSONObject("band1").keySet()) {
						if (fromType.equals("from_argument") && bandArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							band1 = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = bandArguments.getJSONObject("band1").getString("from_node");
							band1 = storedPayLoads.getString(dataNode);							
						}
					}
				}
				if (bandArguments.get("band2") instanceof JSONObject) {
					for (String fromType : bandArguments.getJSONObject("band2").keySet()) {
						if (fromType.equals("from_argument") && bandArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							band2 = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = bandArguments.getJSONObject("band2").getString("from_node");
							band2 = storedPayLoads.getString(dataNode);							
						}
					}
				}
				wcpsNormDiffpayLoad.append(band2 + " - " + band1 + ") / ((double)" + band2 + " + " + band1 + ")");
				wcpsPayLoad=wcpsNormDiffpayLoad;
				wcpsStringBuilder=wcpsStringBuilderNormDiff.append(wcpsNormDiffpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsNormDiffpayLoad.toString());
				log.debug("Normalized Difference Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("ndvi")) {
				containsNDVIProcess = true;
				StringBuilder wcpsNDVIpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderNDVI = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				String collectionID = null;
				String collectionVar = null;
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							String collectionNodeKey = getFilterCollectionNode(dataNode);
							collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
							collectionVar = "$"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
							payLoad = storedPayLoads.getString(dataNode);							
						}
					}
				}
				for (int a = 0; a < aggregates.size(); a++) {					
					if (aggregates.get(a).getOperator().equals("NDVI_"+collectionID)) {
						wcpsNDVIpayLoad.append(createNDVIWCPSString(payLoad, collectionVar, aggregates.get(a)));
						wcpsPayLoad=wcpsNDVIpayLoad;
						wcpsStringBuilder=wcpsStringBuilderNDVI.append(wcpsNDVIpayLoad.toString());
						storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsNDVIpayLoad.toString());
						log.debug("NDVI Process PayLoad is : ");
						log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
					}
				}
			}
			if (currentProcessID.equals("filter_polygon")) {
				StringBuilder wcpsFilterPolygonpayLoad = new StringBuilder("clip(");
				StringBuilder wcpsStringBuilderFilterPolygonPayload = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							payLoad = storedPayLoads.getString(dataNode);
						}
					}
				}
				StringBuilder stringBuilderPoly = new StringBuilder();
				stringBuilderPoly.append("POLYGON((");
				for (int f = 0; f < filtersPolygon.size(); f++) {
					Filter filter = filtersPolygon.get(f);					
					String low = filter.getLowerBound();
					String high = filter.getUpperBound();					
					stringBuilderPoly.append(low);					
					if (high != null && !(high.equals(low))) {
						stringBuilderPoly.append(" ");
						stringBuilderPoly.append(high);
					}
					if (f < filtersPolygon.size() - 1) {
						stringBuilderPoly.append(",");
					}
				}
				stringBuilderPoly.append("))");
				wcpsFilterPolygonpayLoad.append(payLoad + "," + stringBuilderPoly.toString() + ")");
				wcpsPayLoad=wcpsFilterPolygonpayLoad;
				wcpsStringBuilder=wcpsStringBuilderFilterPolygonPayload.append(wcpsFilterPolygonpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsFilterPolygonpayLoad.toString());
				log.debug("Filter Polygon Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.contains("_time")) {
				containsTempAggProcess = true;
				StringBuilder wcpsTempAggpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderTempAgg = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				String collectionID = null;
				String collectionVar = null;
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							String collectionNodeKey = getFilterCollectionNode(dataNode);
							collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
							collectionVar = "$"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
							payLoad = storedPayLoads.getString(dataNode);
						}
					}
				}
				String tempAxis = null;
				for (int f = 0; f < filters.size(); f++) {
					Filter filter = filters.get(f);
					String axis = filter.getAxis();
					if(axis.contains(collectionID)) {
						String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();				
						if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
							tempAxis = axis.replace("_"+ collectionID, "");
						}
					}
				}
				for (int a = 0; a < aggregates.size(); a++) {
					if (aggregates.get(a).getAxis().equals(tempAxis+"_"+collectionID+nodeKeyOfCurrentProcess)) {
						wcpsTempAggpayLoad.append(createTempAggWCPSString(nodeKeyOfCurrentProcess, collectionVar, collectionID, aggregates.get(a), tempAxis));
						//String replaceDate = Pattern.compile(tempAxis+"\\(.*?\\)").matcher(wcpsPayLoad).replaceAll(tempAxis+"\\(\\$pm\\)");
						String replaceDate = payLoad.replaceAll(tempAxis+"\\(.*?\\)", tempAxis+"\\(\\$pm\\"+ nodeKeyOfCurrentProcess +")");
						StringBuilder wcpsAggBuilderMod = new StringBuilder("");
						wcpsAggBuilderMod.append(replaceDate);
						wcpsTempAggpayLoad.append(wcpsAggBuilderMod);
						wcpsPayLoad=wcpsTempAggpayLoad;
						wcpsStringBuilder=wcpsStringBuilderTempAgg.append(wcpsTempAggpayLoad.toString());
						storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsTempAggpayLoad.toString());
						log.debug("Max/Min Time Process PayLoad is : ");
						log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
					}
				}
			}
			if (currentProcessID.equals("reduce")) {
				containsReduceProcess = true;
				StringBuilder wcpsReducepayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderReduce = basicWCPSStringBuilder(varPayLoad.toString());
				String dimension = currentProcess.getJSONObject("arguments").getString("dimension");
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				String collectionID = null;
				String collectionVar = null;
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							String collectionNodeKey = getFilterCollectionNode(dataNode);
							collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
							collectionVar = "$"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
							payLoad = storedPayLoads.getString(dataNode);							
						}
					}
				}
				String filterString = payLoad;
				filterString = filterString.substring(collectionVar.length());
				wcpsReducepayLoad.append(createReduceWCPSString(nodeKeyOfCurrentProcess, payLoad, filterString, collectionVar, collectionID, dimension));
				wcpsPayLoad=wcpsReducepayLoad;
				wcpsStringBuilder = wcpsStringBuilderReduce.append(wcpsReducepayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsReducepayLoad.toString());
				log.debug("Reduce Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("linear_scale_cube")) {
				containsLinearScale = true;
				StringBuilder wcpsScalepayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderScale = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							payLoad = storedPayLoads.getString(dataNode);
						}
					}
				}
				wcpsScalepayLoad.append(createLinearScaleCubeWCPSString(nodeKeyOfCurrentProcess, payLoad));
				wcpsPayLoad=wcpsScalepayLoad;
				wcpsStringBuilder = wcpsStringBuilderScale.append(wcpsScalepayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsScalepayLoad.toString());
				log.debug("Linear Scale Cube Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("linear_stretch_cube")) {
				containsLinearStretch = true;
				StringBuilder wcpsStretchpayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderStretch = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							payLoad = storedPayLoads.getString(dataNode);							
						}
					}
				}
				wcpsStretchpayLoad.append(createLinearStretchCubeWCPSString(nodeKeyOfCurrentProcess, payLoad));
				wcpsPayLoad=wcpsStretchpayLoad;
				wcpsStringBuilder = wcpsStringBuilderStretch.append(wcpsStretchpayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsStretchpayLoad.toString());
				log.debug("Linear Stretch Cube Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("apply")) {
				containsApplyProcess = true;
				StringBuilder wcpsApplypayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderApply = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							payLoad = storedPayLoads.getString(dataNode);							
						}
					}
				}
				wcpsApplypayLoad.append(createApplyWCPSString(nodeKeyOfCurrentProcess, payLoad));
				wcpsPayLoad=wcpsApplypayLoad;
				wcpsStringBuilder = wcpsStringBuilderApply.append(wcpsApplypayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsApplypayLoad.toString());
				log.debug("Apply Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("resample_spatial")) {
				containsResampleProcess = true;
				StringBuilder wcpsResamplepayLoad = new StringBuilder("");
				StringBuilder wcpsStringBuilderResample = basicWCPSStringBuilder(varPayLoad.toString());
				String payLoad = null;
				JSONObject processArguments =  processGraph.getJSONObject(nodeKeyOfCurrentProcess).getJSONObject("arguments");
				String collectionID = null;
				String collectionVar = null;
				if (processArguments.get("data") instanceof JSONObject) {
					for (String fromType : processArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && processArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							payLoad = wcpsPayLoad.toString();
						}
						else if (fromType.equals("from_node")) {
							String dataNode = processArguments.getJSONObject("data").getString("from_node");
							String collectionNodeKey = getFilterCollectionNode(dataNode);
							collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
							collectionVar = "$"+collectionID+getFilterCollectionNode(currentProcessArguments.getJSONObject("data").getString("from_node"));
							payLoad = storedPayLoads.getString(dataNode);
						}
					}
				}
				String xAxis = null;
				String yAxis = null;
				for (int f = 0; f < filters.size(); f++) {
					Filter filter = filters.get(f);
					String axis = filter.getAxis();
					if(axis.contains(collectionID)) {
						String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
						if (axisUpperCase.equals("N") || axisUpperCase.equals("Y") || axisUpperCase.equals("LAT")) {
							yAxis = axis.replace("_"+ collectionID, "");
						}
						if (axisUpperCase.equals("E") || axisUpperCase.equals("X") || axisUpperCase.equals("LONG")) {
							xAxis = axis.replace("_"+ collectionID, "");
						}
					}
				}				
				wcpsResamplepayLoad.append(createResampleSpatialWCPSString(nodeKeyOfCurrentProcess, payLoad, xAxis, yAxis));
				wcpsPayLoad=wcpsResamplepayLoad;
				wcpsStringBuilder = wcpsStringBuilderResample.append(wcpsResamplepayLoad.toString());
				storedPayLoads.put(nodeKeyOfCurrentProcess, wcpsResamplepayLoad.toString());
				log.debug("Resample Spatial Process PayLoad is : ");
				log.debug(storedPayLoads.get(nodeKeyOfCurrentProcess));
			}
			if (currentProcessID.equals("save_result")) {
				String savePayload = wcpsStringBuilder.toString();
				StringBuilder wcpsStringBuilderSaveResult = new StringBuilder("");
				log.info("Collection Dims : " + collDims2D);
				wcpsStringBuilderSaveResult.append(createReturnResultWCPSString(nodeKeyOfCurrentProcess, savePayload, collDims2D));
				wcpsStringBuilder = wcpsStringBuilderSaveResult;
			}
		}
	}
	
	private String createApplyWCPSString(String applyNodeKey, String payLoad) {
		String applyBuilderExtend = null;
		JSONObject applyProcesses = processGraph.getJSONObject(applyNodeKey).getJSONObject("arguments").getJSONObject("process").getJSONObject("callback");

		JSONObject applyPayLoads = new JSONObject();
		JSONArray applyNodesArray = new JSONArray();
		String endApplyNode = null;
		JSONArray endApplyNodeAsArray = new JSONArray();
		
		for (String applyProcessKey : applyProcesses.keySet()) {
			JSONObject applyProcess =  applyProcesses.getJSONObject(applyProcessKey);
			for (String reducerField : applyProcess.keySet()) {
				if (reducerField.equals("result")) {
					Boolean resultFlag = applyProcess.getBoolean("result");
					if (resultFlag) {
						endApplyNode = applyProcessKey;
						endApplyNodeAsArray.put(endApplyNode);
						log.debug("End Apply Process is : " + applyProcesses.getJSONObject(endApplyNode).getString("process_id"));
					}
				}
			}
		}
		
		JSONArray applyNodesSortedArray = new JSONArray();
		applyNodesArray.put(endApplyNodeAsArray);
		for (int n = 0; n < applyNodesArray.length(); n++) {
			for (int a = 0; a < applyNodesArray.getJSONArray(n).length(); a++) {
				JSONArray fromNodeOfApplyProcesses = getApplyFromNodes(applyNodesArray.getJSONArray(n).getString(a), applyProcesses);
				if (fromNodeOfApplyProcesses.length()>0) {
					applyNodesArray.put(fromNodeOfApplyProcesses);
				}
				else if (fromNodeOfApplyProcesses.length()==0) {
					applyNodesSortedArray.put(applyNodesArray.getJSONArray(n).getString(a));
				}
			}
		}
		
		for (int i = 0; i < applyNodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < applyNodesSortedArray.length(); j++) {
				if (applyNodesSortedArray.get(i).equals(applyNodesSortedArray.get(j))) {
					applyNodesSortedArray.remove(j);
				}
			}
		}
		
		applyNodesArray.remove(applyNodesArray.length()-1);
		for (int i = applyNodesArray.length()-1; i>0; i--) {
			if (applyNodesArray.getJSONArray(i).length()>0) {				
				for (int a = 0; a < applyNodesArray.getJSONArray(i).length(); a++) {
					applyNodesSortedArray.put(applyNodesArray.getJSONArray(i).getString(a));
				}
			}
		}
		
		applyNodesSortedArray.put(endApplyNode);
		for (int i = 0; i < applyNodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < applyNodesSortedArray.length(); j++) {
				if (applyNodesSortedArray.get(i).equals(applyNodesSortedArray.get(j))) {
					applyNodesSortedArray.remove(j);
				}
			}
		}
		
		JSONArray applyProcessesSequence = new JSONArray();
		for (int i = 0; i < applyNodesSortedArray.length(); i++) {
			applyProcessesSequence.put(applyProcesses.getJSONObject(applyNodesSortedArray.getString(i)).getString("process_id"));
		}
		
		log.debug("Apply's Nodes Sequence is : ");
		log.debug(applyNodesSortedArray);
		log.debug("Apply's Processes Sequence is : ");
		log.debug(applyProcessesSequence);
		
		for (int r = 0; r < applyNodesSortedArray.length(); r++) {
			String nodeKey = applyNodesSortedArray.getString(r);
			String name = applyProcesses.getJSONObject(nodeKey).getString("process_id");
			
			if (name.contains("linear_scale_range")) {
				String x = null;
				JSONObject linearScaleRangeArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : linearScaleRangeArguments.keySet()) {
					if ((argType.equals("x") || argType.equals("data")) && linearScaleRangeArguments.get(argType) instanceof JSONObject) {
						for (String fromType : linearScaleRangeArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_argument") && linearScaleRangeArguments.getJSONObject(argType).getString("from_argument").equals("x")) {
								x = payLoad;								
							}
							else if (fromType.equals("from_node")) {
								String dataNode = linearScaleRangeArguments.getJSONObject(argType).getString("from_node");
								String linearScaleRangePayLoad = applyPayLoads.getString(dataNode);
								x = linearScaleRangePayLoad;
							}
						}
					}
					else if (argType.equals("x") && linearScaleRangeArguments.get(argType) instanceof Double) {						
						x = String.valueOf(linearScaleRangeArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createLinearScaleRangeWCPSString(nodeKey, x, applyProcesses);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Linear Scale Range Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("absolute")) {
				String x = null;
				JSONObject absArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : absArguments.keySet()) {
					if ((argType.equals("x") || argType.equals("data")) && absArguments.get(argType) instanceof JSONObject ) {
						for (String fromType : absArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_argument") && absArguments.getJSONObject(argType).getString("from_argument").equals("x")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = absArguments.getJSONObject(argType).getString("from_node");
								String absPayLoad = applyPayLoads.getString(dataNode);
								x = absPayLoad;
							}						
						}
					}
					else if (argType.equals("x") && absArguments.get(argType) instanceof Double) {
						x = String.valueOf(absArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createAbsWCPSString(x);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Absolute Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("not")) {
				String x = null;
				JSONObject notArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : notArguments.keySet()) {
					if ((argType.equals("expression") || argType.equals("data")) && notArguments.get(argType) instanceof JSONObject) {
						for (String fromType : notArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_argument") && notArguments.getJSONObject(argType).getString("from_argument").equals("x")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = notArguments.getJSONObject(argType).getString("from_node");
								String notPayLoad = applyPayLoads.getString(dataNode);
								x = notPayLoad;
							}						
						}
					}

					else if (argType.equals("x") && notArguments.get(argType) instanceof Boolean) {
						x = String.valueOf(notArguments.getBoolean("expression"));
					}
					applyBuilderExtend = createNotWCPSString(x);
					applyPayLoads.put(nodeKey, applyBuilderExtend);
					log.debug("NOT Process PayLoad is : ");
					log.debug(applyPayLoads.get(nodeKey));
				}
			}
			
			if (name.equals("log")) {
				String x = null;
				JSONObject logArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : logArguments.keySet()) {
					if ((argType.equals("x") || argType.equals("data")) && logArguments.get(argType) instanceof JSONObject) {
						for (String fromType : logArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_argument") && logArguments.getJSONObject(argType).getString("from_argument").equals("x")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = logArguments.getJSONObject(argType).getString("from_node");
								String logPayLoad = applyPayLoads.getString(dataNode);
								x = logPayLoad;
							}						
						}
					}
					else if (argType.equals("x") && logArguments.get(argType) instanceof Double) {
						x = String.valueOf(logArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createLogWCPSString(x);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Log Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("ln")) {
				String x = null;
				JSONObject logNArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : logNArguments.keySet()) {
					if ((argType.equals("x") || argType.equals("data")) && logNArguments.get(argType) instanceof JSONObject) {
						for (String fromType : logNArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_argument") && logNArguments.getJSONObject(argType).getString("from_argument").equals("x")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = logNArguments.getJSONObject(argType).getString("from_node");
								String logNPayLoad = applyPayLoads.getString(dataNode);
								x = logNPayLoad;
							}						
						}
					}
					else if (argType.equals("x") && logNArguments.get(argType) instanceof Double) {
						x = String.valueOf(logNArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createLogNWCPSString(x);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Natural Log Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("sqrt")) {
				String x = null;
				JSONObject sqrtArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : sqrtArguments.keySet()) {
					if ((argType.equals("x") || argType.equals("data")) && sqrtArguments.get(argType) instanceof JSONObject) {
						for (String fromType : sqrtArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_argument") && sqrtArguments.getJSONObject(argType).getString("from_argument").equals("x")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = sqrtArguments.getJSONObject(argType).getString("from_node");
								String sqrtPayLoad = applyPayLoads.getString(dataNode);
								x = sqrtPayLoad;
							}						
						}
					}
					else if (argType.equals("x") && sqrtArguments.get(argType) instanceof Double) {
						x = String.valueOf(sqrtArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createSqrtWCPSString(x);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Square Root Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("power")) {
				String base = null;
				JSONObject powArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : powArguments.keySet()) {
					if ((argType.equals("base") || argType.equals("data")) && powArguments.get(argType) instanceof JSONObject) {
						for (String fromType : powArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_argument") && powArguments.getJSONObject(argType).getString("from_argument").equals("x")) {
								base = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = powArguments.getJSONObject(argType).getString("from_node");
								String powPayLoad = applyPayLoads.getString(dataNode);
								base = powPayLoad;
							}						
						}
					}
					else if (argType.equals("x") && powArguments.get(argType) instanceof Double) {
						base = String.valueOf(powArguments.getDouble("base"));
					}
				}
				applyBuilderExtend = createPowWCPSString(nodeKey, base, applyProcesses);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Power Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("exp")) {
				String p = null;
				JSONObject expArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : expArguments.keySet()) {
					if ((argType.equals("p") || argType.equals("data")) && expArguments.get(argType) instanceof JSONObject) {
						for (String fromType : expArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_argument") && expArguments.getJSONObject(argType).getString("from_argument").equals("x")) {
								p = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = expArguments.getJSONObject(argType).getString("from_node");
								String expPayLoad = applyPayLoads.getString(dataNode);
								p = expPayLoad;
							}
						}
					}
					else if (argType.equals("x") && expArguments.get(argType) instanceof Double) {
						p = String.valueOf(expArguments.getDouble("p"));
					}
				}
				applyBuilderExtend = createExpWCPSString(p);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Exponential Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("pi")) {
				applyBuilderExtend = createPiWCPSString();
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Pi Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			if (name.equals("e")) {
				applyBuilderExtend = createEulerNumWCPSString();
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Euler's Constant Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("sin")||name.equals("cos")||name.equals("tan")||name.equals("sinh")||name.equals("cosh")||name.equals("tanh")||name.equals("arcsin")||name.equals("arccos")||name.equals("arctan")) {
				String x = null;
				JSONObject trigArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				for (String argType : trigArguments.keySet()) {
					if ((argType.equals("x") || argType.equals("data")) && trigArguments.get(argType) instanceof JSONObject) {
						for (String fromType : trigArguments.getJSONObject(argType).keySet()) {
							if (fromType.equals("from_argument") && trigArguments.getJSONObject(argType).getString("from_argument").equals("x")) {
								x = payLoad;
							}
							else if (fromType.equals("from_node")) {
								String dataNode = trigArguments.getJSONObject(argType).getString("from_node");
								String trigPayLoad = applyPayLoads.getString(dataNode);
								x = trigPayLoad;
							}
						}
					}
					else if (argType.equals("x") && trigArguments.get(argType) instanceof Double) {
						x = String.valueOf(trigArguments.getDouble("x"));
					}
				}
				applyBuilderExtend = createTrigWCPSString(nodeKey, x, applyProcesses, name);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Trigonometric Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("gte")) {
				String x = null;
				String y = null;
				JSONObject gteArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (gteArguments.get("x") instanceof JSONObject) {
					for (String fromType : gteArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_argument") && gteArguments.getJSONObject("x").getString("from_argument").equals("x")) {
							x = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeX = gteArguments.getJSONObject("x").getString("from_node");
							String gtePayLoadX = applyPayLoads.getString(dataNodeX);
							x = gtePayLoadX;
						}						
					}
				}
				else {
					x = String.valueOf(gteArguments.getDouble("x"));
				}
				if (gteArguments.get("y") instanceof JSONObject) {
					for (String fromType : gteArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_argument") && gteArguments.getJSONObject("y").getString("from_argument").equals("x")) {
							y = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeY = gteArguments.getJSONObject("y").getString("from_node");
							String gtePayLoadY = applyPayLoads.getString(dataNodeY);
							y = gtePayLoadY;
						}						
					}
				}
				else {
					y = String.valueOf(gteArguments.getDouble("y"));
				}
				applyBuilderExtend = createGreatThanEqWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Greater Than Equal Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("gt")) {
				String x = null;
				String y = null;
				JSONObject gtArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (gtArguments.get("x") instanceof JSONObject) {
					for (String fromType : gtArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_argument") && gtArguments.getJSONObject("x").getString("from_argument").equals("x")) {
							x = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeX = gtArguments.getJSONObject("x").getString("from_node");
							String gtPayLoadX = applyPayLoads.getString(dataNodeX);
							x = gtPayLoadX;
						}						
					}
				}
				else {
					x = String.valueOf(gtArguments.getDouble("x"));
				}
				if (gtArguments.get("y") instanceof JSONObject) {
					for (String fromType : gtArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_argument") && gtArguments.getJSONObject("y").getString("from_argument").equals("x")) {
							y = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeY = gtArguments.getJSONObject("y").getString("from_node");
							String gtPayLoadY = applyPayLoads.getString(dataNodeY);
							y = gtPayLoadY;
						}						
					}
				}
				else {
					y = String.valueOf(gtArguments.getDouble("y"));
				}
				applyBuilderExtend = createGreatThanWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Greater Than Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("lte")) {
				String x = null;
				String y = null;
				JSONObject lteArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (lteArguments.get("x") instanceof JSONObject) {
					for (String fromType : lteArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_argument") && lteArguments.getJSONObject("x").getString("from_argument").equals("x")) {
							x = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeX = lteArguments.getJSONObject("x").getString("from_node");
							String ltePayLoadX = applyPayLoads.getString(dataNodeX);
							x = ltePayLoadX;
						}						
					}
				}
				else {
					x = String.valueOf(lteArguments.getDouble("x"));
				}
				if (lteArguments.get("y") instanceof JSONObject) {
					for (String fromType : lteArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_argument") && lteArguments.getJSONObject("y").getString("from_argument").equals("x")) {
							y = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeY = lteArguments.getJSONObject("y").getString("from_node");
							String ltePayLoadY = applyPayLoads.getString(dataNodeY);
							y = ltePayLoadY;
						}						
					}
				}
				else {
					y = String.valueOf(lteArguments.getDouble("y"));
				}
				applyBuilderExtend = createLessThanEqWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Less Than Equal Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("lt")) {
				String x = null;
				String y = null;
				JSONObject ltArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (ltArguments.get("x") instanceof JSONObject) {
					for (String fromType : ltArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_argument") && ltArguments.getJSONObject("x").getString("from_argument").equals("x")) {
							x = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeX = ltArguments.getJSONObject("x").getString("from_node");
							String ltPayLoadX = applyPayLoads.getString(dataNodeX);
							x = ltPayLoadX;
						}						
					}
				}
				else {
					x = String.valueOf(ltArguments.getDouble("x"));
				}
				if (ltArguments.get("y") instanceof JSONObject) {
					for (String fromType : ltArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_argument") && ltArguments.getJSONObject("y").getString("from_argument").equals("x")) {
							y = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeY = ltArguments.getJSONObject("y").getString("from_node");
							String ltPayLoadY = applyPayLoads.getString(dataNodeY);
							y = ltPayLoadY;
						}						
					}
				}
				else {
					y = String.valueOf(ltArguments.getInt("y"));
				}
				applyBuilderExtend = createLessThanWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Less Than Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("neq")) {
				String x = null;
				String y = null;
				JSONObject neqArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (neqArguments.get("x") instanceof JSONObject) {
					for (String fromType : neqArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_argument") && neqArguments.getJSONObject("x").getString("from_argument").equals("x")) {
							x = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeX = neqArguments.getJSONObject("x").getString("from_node");
							String neqPayLoadX = applyPayLoads.getString(dataNodeX);
							x = neqPayLoadX;
						}						
					}
				}
				else {
					x = String.valueOf(neqArguments.getDouble("x"));
				}
				if (neqArguments.get("y") instanceof JSONObject) {
					for (String fromType : neqArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_argument") && neqArguments.getJSONObject("y").getString("from_argument").equals("x")) {
							y = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeY = neqArguments.getJSONObject("y").getString("from_node");
							String neqPayLoadY = applyPayLoads.getString(dataNodeY);
							y = neqPayLoadY;
						}						
					}
				}
				else {
					y = String.valueOf(neqArguments.getDouble("y"));
				}
				applyBuilderExtend = createNotEqWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Not Equal Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
			}
			
			if (name.equals("eq")) {
				String x = null;
				String y = null;
				JSONObject eqArguments =  applyProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (eqArguments.get("x")  instanceof JSONObject) {
					for (String fromType : eqArguments.getJSONObject("x").keySet()) {
						if (fromType.equals("from_argument") && eqArguments.getJSONObject("x").getString("from_argument").equals("x")) {
							x = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeX = eqArguments.getJSONObject("x").getString("from_node");
							String eqPayLoadX = applyPayLoads.getString(dataNodeX);
							x = eqPayLoadX;
						}						
					}
				}
				else {
					x = String.valueOf(eqArguments.getDouble("x"));
				}
				if (eqArguments.get("y") instanceof JSONObject) {
					for (String fromType : eqArguments.getJSONObject("y").keySet()) {
						if (fromType.equals("from_argument") && eqArguments.getJSONObject("y").getString("from_argument").equals("x")) {
							y = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNodeY = eqArguments.getJSONObject("y").getString("from_node");
							String eqPayLoadY = applyPayLoads.getString(dataNodeY);
							y = eqPayLoadY;
						}						
					}
				}
				else {
					y = String.valueOf(eqArguments.getDouble("y"));
				}
				applyBuilderExtend = createEqWCPSString(x, y);
				applyPayLoads.put(nodeKey, applyBuilderExtend);
				log.debug("Equal Process PayLoad is : ");
				log.debug(applyPayLoads.get(nodeKey));
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

	private String createReduceWCPSString(String reduceNodeKey, String payLoad, String filterString, String collectionVar, String collectionID, String dimension) {
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
						log.debug("End Reducer Process is : " + reduceProcesses.getJSONObject(endReducerNode).getString("process_id"));
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
		
		reduceNodesArray.remove(reduceNodesArray.length()-1);
		for (int i = reduceNodesArray.length()-1; i>0; i--) {
			if (reduceNodesArray.getJSONArray(i).length()>0) {				
				for (int a = 0; a < reduceNodesArray.getJSONArray(i).length(); a++) {
					reduceNodesSortedArray.put(reduceNodesArray.getJSONArray(i).getString(a));
				}
			}
		}
		
		reduceNodesSortedArray.put(endReducerNode);
		for (int i = 0; i < reduceNodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < reduceNodesSortedArray.length(); j++) {
				if (reduceNodesSortedArray.get(i).equals(reduceNodesSortedArray.get(j))) {
					reduceNodesSortedArray.remove(j);
				}
			}
		}
				
		JSONArray reduceProcessesSequence = new JSONArray();
		for (int i = 0; i < reduceNodesSortedArray.length(); i++) {
			reduceProcessesSequence.put(reduceProcesses.getJSONObject(reduceNodesSortedArray.getString(i)).getString("process_id"));
		}
		
		log.debug("Reducer's Nodes Sequence is : ");
		log.debug(reduceNodesSortedArray);
		log.debug("Reducer's Processes Sequence is : ");
		log.debug(reduceProcessesSequence);

		for (int r = 0; r < reduceNodesSortedArray.length(); r++) {
			String nodeKey = reduceNodesSortedArray.getString(r);
			String name = reduceProcesses.getJSONObject(nodeKey).getString("process_id");
			
			if (name.equals("array_element")) {
				JSONObject arrayData =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				int arrayIndex = arrayData.getInt("index");
				if ( arrayData.get("data") instanceof JSONObject) {
					for (String fromType : arrayData.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && arrayData.getJSONObject("data").getString("from_argument").equals("data")) {
							String dataNode = processGraph.getJSONObject(reduceNodeKey).getJSONObject("arguments").getJSONObject("data").getString("from_node");
							String loadCollNode = getFilterCollectionNode();
							//if (dataNode.equals(loadCollNode)) {
								reduceBuilderExtend = createBandWCPSString(collectionID, arrayIndex, reduceNodeKey, filterString, collectionVar);
								
							//}
						}
						else if (fromType.equals("from_node")) {
							String dataNode = arrayData.getJSONObject("data").getString("from_node");
						}
					}
				}
				else {
					reduceBuilderExtend = arrayData.getJSONArray("data").getString(arrayIndex);
				}
				
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);				
				log.debug("Array Element Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			if (name.equals("count")) {
				String x = null;
				JSONObject countArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (countArguments.get("data") instanceof JSONObject) {
					for (String fromType : countArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && countArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							x = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNode = countArguments.getJSONObject("data").getString("from_node");
							String countPayLoad = reducerPayLoads.getString(dataNode);
							x = countPayLoad;
						}
					}
				}
				else {
					x = String.valueOf(countArguments.getJSONArray("data"));
				}
				reduceBuilderExtend = createCountWCPSString(x);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Count Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			if (name.equals("mean")) {
				String meanPayLoad = null;
				JSONObject meanArguments =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (meanArguments.get("data") instanceof JSONObject) {
					for (String fromType : meanArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && meanArguments.getJSONObject("data").getString("from_argument").equals("data")) {
							meanPayLoad = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNode = meanArguments.getJSONObject("data").getString("from_node");
							meanPayLoad = reducerPayLoads.getString(dataNode);
						}						
					}
				}
				else if (meanArguments.get("data") instanceof JSONArray) {
					meanPayLoad = String.valueOf(meanArguments.getJSONArray("data"));
				}
				reduceBuilderExtend = createMeanWCPSString(reduceNodeKey, meanPayLoad, reduceProcesses, dimension, collectionVar, collectionID);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Mean Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			if (name.equals("min")) {
				String minPayLoad = null;
				JSONObject minArguments = reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments");
				if (minArguments.get("data") instanceof JSONObject) {
					for (String fromType : minArguments.getJSONObject("data").keySet()) {
						if (fromType.equals("from_argument") && minArguments.getJSONObject("data").getString("from_argument").equals("data")) {							
							minPayLoad = payLoad;
						}
						else if (fromType.equals("from_node")) {
							String dataNode = minArguments.getJSONObject("data").getString("from_node");
							minPayLoad = reducerPayLoads.getString(dataNode);
						}
					}
				}
				else if (minArguments.get("data") instanceof JSONArray) {
					minPayLoad = String.valueOf(minArguments.getJSONArray("data"));
				}
				reduceBuilderExtend = createMinWCPSString(reduceNodeKey, minPayLoad, reduceProcesses, dimension, collectionVar, collectionID);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Min Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
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
				reduceBuilderExtend = createMaxWCPSString(reduceNodeKey, maxPayLoad, reduceProcesses, dimension, collectionVar, collectionID);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Max Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			
			if (name.equals("and")) {
				JSONArray andArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("expressions");
				JSONArray andArrayreturn = new JSONArray();
				for (int a = 0; a < andArray.length(); a++) {
					if (andArray.get(a) instanceof JSONObject) {
						for (String fromType : andArray.getJSONObject(a).keySet()) {
							if (fromType.equals("from_argument") && andArray.getJSONObject(a).getString("from_argument").equals("data")) {						
								andArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = andArray.getJSONObject(a).getString("from_node");
								String andPayLoad = reducerPayLoads.getString(dataNode);
								andArrayreturn.put(andPayLoad);
							}			
						}
					}
					else {
						andArrayreturn.put(andArray.get(a));
					}
				}
				reduceBuilderExtend = createANDWCPSString(andArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("AND Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			if (name.equals("or")) {
				JSONArray orArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("expressions");
				JSONArray orArrayreturn = new JSONArray();
				for (int a = 0; a < orArray.length(); a++) {
					if (orArray.get(a) instanceof JSONObject) {
						for (String fromType : orArray.getJSONObject(a).keySet()) {
							if (fromType.equals("from_argument") && orArray.getJSONObject(a).getString("from_argument").equals("data")) {						
								orArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = orArray.getJSONObject(a).getString("from_node");
								String orPayLoad = reducerPayLoads.getString(dataNode);
								orArrayreturn.put(orPayLoad);
							}			
						}
					}
					else {
						orArrayreturn.put(orArray.get(a));
					}
				}
				reduceBuilderExtend = createORWCPSString(orArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("OR Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			if (name.equals("xor")) {
				JSONArray xorArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("expressions");
				JSONArray xorArrayreturn = new JSONArray();
				for (int a = 0; a < xorArray.length(); a++) {
					if (xorArray.get(a) instanceof JSONObject) {
						for (String fromType : xorArray.getJSONObject(a).keySet()) {
							if (fromType.equals("from_argument") && xorArray.getJSONObject(a).getString("from_argument").equals("data")) {
								xorArrayreturn.put(payLoad);
							}
							if (fromType.equals("from_node")) {
								String dataNode = xorArray.getJSONObject(a).getString("from_node");						
								String xorPayLoad = reducerPayLoads.getString(dataNode);
								xorArrayreturn.put(xorPayLoad);
							}
						}
					}
					else {
						xorArrayreturn.put(xorArray.getBoolean(a));
					}
				}
				reduceBuilderExtend = createXORWCPSString(xorArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("XOR Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			if (name.equals("product")) {
				JSONArray productArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("data");
				JSONArray productArrayreturn = new JSONArray();
				for (int a = 0; a < productArray.length(); a++) {
					if (productArray.get(a) instanceof JSONObject) {
						for (String fromType : productArray.getJSONObject(a).keySet()) {
							if (fromType.equals("from_argument") && productArray.getJSONObject(a).getString("from_argument").equals("data")) {						
								productArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = productArray.getJSONObject(a).getString("from_node");
								String productPayLoad = reducerPayLoads.getString(dataNode);
								productArrayreturn.put(productPayLoad);
							}			
						}
					}
					else {
						productArrayreturn.put(productArray.get(a));
					}
				}
				reduceBuilderExtend = createProductWCPSString(productArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Product Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			if (name.equals("sum")) {
				JSONArray sumArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("data");
				JSONArray sumArrayreturn = new JSONArray();
				for (int a = 0; a < sumArray.length(); a++) {
					if (sumArray.get(a) instanceof JSONObject) {
						for (String fromType : sumArray.getJSONObject(a).keySet()) {
							if (fromType.equals("from_argument") && sumArray.getJSONObject(a).getString("from_argument").equals("data")) {						
								sumArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = sumArray.getJSONObject(a).getString("from_node");
								String sumPayLoad = reducerPayLoads.getString(dataNode);
								sumArrayreturn.put(sumPayLoad);
							}			
						}
					}
					else {
						sumArrayreturn.put(sumArray.get(a));
					}
				}
				reduceBuilderExtend = createSumWCPSString(sumArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Sum Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			if (name.equals("subtract")) {
				JSONArray subtractArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("data");
				JSONArray subtractArrayreturn = new JSONArray();
				for (int a = 0; a < subtractArray.length(); a++) {
					if (subtractArray.get(a) instanceof JSONObject) {
						for (String fromType : subtractArray.getJSONObject(a).keySet()) {
							if (fromType.equals("from_argument") && subtractArray.getJSONObject(a).getString("from_argument").equals("data")) {						
								subtractArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = subtractArray.getJSONObject(a).getString("from_node");
								String subtractPayLoad = reducerPayLoads.getString(dataNode);
								subtractArrayreturn.put(subtractPayLoad);
							}			
						}
					}
					else {
						subtractArrayreturn.put(subtractArray.get(a));
					}
				}
				reduceBuilderExtend = createSubtractWCPSString(subtractArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Subtract Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
			}
			if (name.equals("divide")) {
				JSONArray divideArray =  reduceProcesses.getJSONObject(nodeKey).getJSONObject("arguments").getJSONArray("data");
				JSONArray divideArrayreturn = new JSONArray();
				for (int a = 0; a < divideArray.length(); a++) {
					if (divideArray.get(a) instanceof JSONObject) {
						for (String fromType : divideArray.getJSONObject(a).keySet()) {
							if (fromType.equals("from_argument") && divideArray.getJSONObject(a).getString("from_argument").equals("data")) {						
								divideArrayreturn.put(payLoad);
							}
							else if (fromType.equals("from_node")) {
								String dataNode = divideArray.getJSONObject(a).getString("from_node");
								String dividePayLoad = reducerPayLoads.getString(dataNode);
								divideArrayreturn.put(dividePayLoad);
							}			
						}
					}
					else {
						divideArrayreturn.put(divideArray.get(a));
					}
				}
				reduceBuilderExtend = createDivideWCPSString(divideArrayreturn);
				reducerPayLoads.put(nodeKey, reduceBuilderExtend);
				log.debug("Divide Process PayLoad is : ");
				log.debug(reducerPayLoads.get(nodeKey));
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

	private String createBandWCPSString(String collectionID, int arrayIndex, String reduceNodeKey, String filterString, String collectionVar) {
		StringBuilder stretchBuilder = new StringBuilder("");
		String fromNodeOfReduce = processGraph.getJSONObject(reduceNodeKey).getJSONObject("arguments").getJSONObject("data").getString("from_node");
		fromNodeOfReduce = getFilterCollectionNode(fromNodeOfReduce);
		JSONObject fromProcess = processGraph.getJSONObject(fromNodeOfReduce);
		if (fromProcess.getString("process_id").equals("load_collection")) {
			String bandfromIndex = fromProcess.getJSONObject("arguments").getJSONArray("bands").getString(arrayIndex);
			String bandName = null;
			JSONObject collectionSTACMetdata = null;
			try {
				collectionSTACMetdata = readJsonFromUrl(
						ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
				log.debug(collectionVar);
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
				if (bandCommon.equals(bandfromIndex)) {
					bandName = bandsArray.getJSONObject(c).getString("name");
					break;
				}
				else {
					bandName = bandfromIndex;
				}
			}
			stretchBuilder.append(createBandSubsetString(collectionVar, bandName, filterString));		
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
	
	private String createCountWCPSString(String payLoad) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("count(");
		stretchBuilder.append(payLoad + ")");
		stretchString = stretchBuilder.toString();
		
		return stretchString;
	}

	private String createMeanWCPSString(String reduceNodeKey, String payLoad, JSONObject reduceProcesses, String dimension, String collectionVar, String collectionID) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");
		JSONObject jsonresp = null;
		try {
			jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
		
		String temporalAxis = null;
		for (String tempAxis1 : jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").keySet()) {
			String tempAxis1UpperCase = tempAxis1.toUpperCase();
			if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
				temporalAxis = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(tempAxis1).getString("axis");
			}
		}
		
		if (dimension.contains("spectral") || dimension.contains("bands")) {
			stretchBuilder.append("avg(" + payLoad + ")");    	    
			stretchString = stretchBuilder.toString();
		}
		else if (dimension.equals("temporal")) {
			String tempAxis = null;
			for (int f = 0; f < filters.size(); f++) {
				Filter filter = filters.get(f);
				String axis = filter.getAxis();			
				if(axis.contains(collectionID)) {
					String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();				
					if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
						tempAxis = axis.replace("_"+ collectionID, "");
					}
				}
			}
			for (int a = 0; a < aggregates.size(); a++) {
				log.debug("Aggregate is : ");
				log.debug(aggregates.get(a).getAxis() + "Operator " + aggregates.get(a).getOperator());
				log.debug("TempAxis " + tempAxis + " " + collectionID);
				if (aggregates.get(a).getAxis().equals(tempAxis+"_"+collectionID+reduceNodeKey)) {
					stretchBuilder.append(createMeanTempAggWCPSString(reduceNodeKey, collectionVar, collectionID, aggregates.get(a), payLoad, tempAxis));
//					String replaceDate = wcpsPayLoad.toString().replaceAll(tempAxis+"\\(.*?\\)", tempAxis+"\\(\\$pm\\)");
//					StringBuilder wcpsAggBuilderMod = new StringBuilder("");
//					wcpsAggBuilderMod.append(meanDateRange1);
//					stretchBuilder.append(wcpsAggBuilderMod);
					stretchString=stretchBuilder.toString();
				}
			}
		}
		return stretchString;
	}

	private String createMaxWCPSString(String reduceNodeKey, String payLoad, JSONObject reduceProcesses, String dimension, String collectionVar, String collectionID) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");
		JSONObject jsonresp = null;
		try {
			jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
		
		String temporalAxis = null;
		for (String tempAxis1 : jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").keySet()) {
			String tempAxis1UpperCase = tempAxis1.toUpperCase();
			if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
				temporalAxis = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(tempAxis1).getString("axis");
			}
		}
		
		if (dimension.contains("spectral") || dimension.contains("bands")) {
			stretchBuilder.append("max(" + payLoad + ")");    	    
			stretchString = stretchBuilder.toString();
		}
		else if (dimension.equals("temporal")) {
			String tempAxis = null;
			for (int f = 0; f < filters.size(); f++) {
				Filter filter = filters.get(f);
				String axis = filter.getAxis();			
				if(axis.contains(collectionID)) {
					String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();				
					if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
						tempAxis = axis.replace("_"+ collectionID, "");
					}
				}
			}
			for (int a = 0; a < aggregates.size(); a++) {
				log.debug("Aggregate Axis " + aggregates.get(a).getAxis());
				log.debug("Aggregate Operator " + aggregates.get(a).getOperator());
				log.debug("Reduce Node " + reduceNodeKey);
				if (aggregates.get(a).getAxis().equals(tempAxis+"_"+collectionID+reduceNodeKey)) {
					stretchBuilder.append(createTempAggWCPSString(reduceNodeKey, collectionVar, collectionID, aggregates.get(a), tempAxis));
					String replaceDate = Pattern.compile(tempAxis+"\\(.*?\\)").matcher(payLoad).replaceAll(tempAxis+"\\(\\$pm\\"+ reduceNodeKey + ")");
					//String replaceDate = wcpsPayLoad.toString().replaceAll(tempAxis+"\\(.*?\\)", tempAxis+"\\(\\$pm\\)");
					StringBuilder wcpsAggBuilderMod = new StringBuilder("");
					wcpsAggBuilderMod.append(replaceDate);
					stretchBuilder.append(wcpsAggBuilderMod);
					stretchString=stretchBuilder.toString();
				}
			}
		}
		return stretchString;
	}

	private String createMinWCPSString(String reduceNodeKey, String payLoad, JSONObject reduceProcesses, String dimension, String collectionVar, String collectionID) {
		String stretchString = null;
		StringBuilder stretchBuilder = new StringBuilder("");		
		JSONObject jsonresp = null;
		try {
			jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
		
		String temporalAxis = null;
		for (String tempAxis1 : jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").keySet()) {
			String tempAxis1UpperCase = tempAxis1.toUpperCase();
			if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
				temporalAxis = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(tempAxis1).getString("axis");
			}
		}
		
		if (dimension.contains("spectral") || dimension.contains("bands")) {
			stretchBuilder.append("min(" + payLoad + ")");
			stretchString = stretchBuilder.toString();
		}
		else if (dimension.equals("temporal")) {
			String tempAxis = null;
			for (int f = 0; f < filters.size(); f++) {
				Filter filter = filters.get(f);
				String axis = filter.getAxis();
				if(axis.contains(collectionID)) {
					String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();				
					if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
						tempAxis = axis.replace("_"+ collectionID, "");
					}
				}
			}
			for (int a = 0; a < aggregates.size(); a++) {
				log.debug("Aggregate Axis " + aggregates.get(a).getAxis());
				log.debug("Aggregate Operator " + aggregates.get(a).getOperator());
				log.debug("Reduce Node " + reduceNodeKey);
				if (aggregates.get(a).getAxis().equals(tempAxis+"_"+collectionID+reduceNodeKey)) {
					stretchBuilder.append(createTempAggWCPSString(reduceNodeKey, collectionVar, collectionID, aggregates.get(a), tempAxis));
					String replaceDate = Pattern.compile(tempAxis+"\\(.*?\\)").matcher(payLoad).replaceAll(tempAxis+"\\(\\$pm\\"+ reduceNodeKey + ")");
					//String replaceDate = wcpsPayLoad.toString().replaceAll(tempAxis+"\\(.*?\\)", tempAxis+"\\(\\$pm\\)");
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

	private String createReturnResultWCPSString(String returnResultNodeKey, String payload, Boolean collDims2D) {
		StringBuilder resultBuilder = new StringBuilder("");
		resultBuilder.append(payload);
		log.info("Collection Dims : " + collDims2D);
		if (this.outputFormat.equals("netcdf")) {
			if (collDims2D) {
				resultBuilder.append(", \"" + this.outputFormat + "\" ," + "\"{ \\\"transpose\\\": [0,1] }\"" + ")");
			}
			else if (!collDims2D) {
				resultBuilder.append(", \"" + this.outputFormat + "\" ," + "\"{ \\\"transpose\\\": [1,2] }\"" + ")");
			}			
		}
		else {
			resultBuilder.append(", \"" + this.outputFormat + "\" )");
		}
		log.debug("Save payload : ");
		log.debug(resultBuilder);
		return resultBuilder.toString();
	}
	private String createUDFReturnResultWCPSString(String payload) {
		StringBuilder resultBuilder = new StringBuilder("");
		resultBuilder.append(payload);
		resultBuilder.append(", \"" + "gml" + "\" )");
		log.debug("Save UDF payload : ");
		log.debug(resultBuilder);
		return resultBuilder.toString();
	}

	//TODO extend this to the full functionality of the openEO process
	private String createResampleSpatialWCPSString(String resampleNodeKey, String payload, String xAxis, String yAxis) {
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
				+ xAxis +":\"http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\","
				+ yAxis +":\"http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\""
				+ "}, {})");
		return resampleBuilder.toString();
	}
	
	private String createResampleSpatialCubeWCPSString(String resampleNodeKey, String payload, String resSource, String resTarget, String xAxis, String xLow, String xHigh, String yAxis, String yLow, String yHigh) {
		double res = Double.parseDouble(resSource)/Double.parseDouble(resTarget);
		log.debug(xHigh+xLow);
		log.debug(yHigh+yLow);
		double xScale = (double)((Double.parseDouble(xHigh)-Double.parseDouble(xLow))*res)+Double.parseDouble(xLow);
		double yScale = (double)((Double.parseDouble(yHigh)-Double.parseDouble(yLow))*res)+Double.parseDouble(yLow);
		int projectionEPSGCode = 0;
//		try {
//			projectionEPSGCode = processGraph.getJSONObject(resampleNodeKey).getJSONObject("arguments").getInt("projection");
//		}catch(JSONException e) {
//			log.error("no epsg code was detected!");
//		}
//		if(projectionEPSGCode == 0) {
//			return "";
//		}
		StringBuilder resampleBuilder = new StringBuilder("scale(" );
		//TODO read the name of the spatial coordinate axis from describeCoverage or filter elements in order to correctly apply (E,N), (lat,lon) or X,Y depending on coordinate system
		resampleBuilder.append(payload);
		resampleBuilder.append(" ,{"
				+ xAxis + ":" +"\"CRS:http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\"" + "(" + xLow + ":" + xScale + ")" + ","
				+ yAxis + ":" +"\"CRS:http://10.8.244.147:8080/def/crs/EPSG/0/" + projectionEPSGCode + "\"" + "(" + yLow + ":" + yScale + ")" + ""
				+ "})");
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
	private String createFilteredCollectionString(String collectionVar, String collectionID) {
		StringBuilder stringBuilder = new StringBuilder(collectionVar);
		stringBuilder.append("[");
		int noOfFilters = 0;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();			
			if(axis.contains(collectionID)) {
				noOfFilters = noOfFilters+1;
			}
		}
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();
			
			if(axis.contains(collectionID)) {
				axis = axis.replace("_"+ collectionID, "");
				String axisUpperCase = axis.toUpperCase();
				String low = filter.getLowerBound();
				String high = filter.getUpperBound();
				stringBuilder.append(axis + "(");
				if ((axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) && !low.contains("$")) {
					stringBuilder.append("\"");
				}
				stringBuilder.append(low);
				if ((axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) && !low.contains("$")) {
					stringBuilder.append("\"");
				}
				if (high != null && !(high.equals(low))) {
					stringBuilder.append(":");
					if (axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) {
						stringBuilder.append("\"");
					}
					stringBuilder.append(high);
					if (axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) {
						stringBuilder.append("\"");
					}
				}
				stringBuilder.append(")");
				noOfFilters = noOfFilters-1;
				if (noOfFilters > 0) {
					stringBuilder.append(",");
				}
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
	private String createFilteredCollectionString(String collectionVar, String collectionID, Filter filter) {
		try {
			StringBuilder stringBuilder = new StringBuilder(collectionVar);
			stringBuilder.append("[");
			String axis = filter.getAxis();
			if(axis.contains(collectionID)) {
			axis = axis.replace("_"+ collectionID, "");
			String axisUpperCase = axis.toUpperCase();
			String low = filter.getLowerBound();
			String toDate = filter.getUpperBound();
//			DateFormat toDateNewFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//			Date toDateNew;			
//			try {
//				toDateNew = toDateNewFormat.parse(toDate);
//				toDateNew.setTime(toDateNew.getTime() - 1);
//				toDate = toDateNewFormat.format(toDateNew);
//				log.debug("To Date"+toDate);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			String high = toDate;
			stringBuilder.append(axis + "(");
			if ((axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			stringBuilder.append(low);
			if ((axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			if (high != null && !(high.equals(low))) {
				stringBuilder.append(":");
				if (axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) {
					stringBuilder.append("\"");
				}
				stringBuilder.append(high);
				if (axisUpperCase.contains("DATE") || axisUpperCase.contains("TIME") || axisUpperCase.contains("ANSI") || axisUpperCase.contains("UNIX")) {
					stringBuilder.append("\"");
				}
			}
			stringBuilder.append(")");
		}
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

	private String createTempAggWCPSString(String reduceNodeKey, String collectionVar, String collectionID, Aggregate tempAggregate, String tempAxis) {
		String axis = tempAggregate.getAxis();
		String operator = tempAggregate.getOperator();
		Filter tempFilter = null;
		for (Filter filter : this.filters) {
			log.debug("Filter Axis is : ");
			log.debug(filter.getAxis());
			log.debug("Collection ID is : ");
			log.debug(collectionID);
			String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
			if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
				tempFilter = filter;
				log.debug("TempHigh"+tempFilter.getUpperBound());
				log.debug("Temporal Axis is : ");
				log.debug(tempFilter);
			}
		}
		log.debug("Filters are : ");
		log.debug(filters);
		log.debug("Temporal filter is : ");
		log.debug(tempFilter);
		if (tempFilter != null) {
			StringBuilder stringBuilder = new StringBuilder("condense ");
			stringBuilder.append(operator + " over $pm" + reduceNodeKey + " t (imageCrsDomain(");
			stringBuilder.append(createFilteredCollectionString(collectionVar, collectionID, tempFilter) + ",");
			stringBuilder.append(tempAxis + ")) using ");
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
	
	private String createMeanTempAggWCPSString(String reduceNodeKey, String collectionVar, String collectionID, Aggregate tempAggregate, String payLoad, String tempAxis) {
		String axis = tempAggregate.getAxis();
		String operator = tempAggregate.getOperator();
		Filter tempFilter = null;
		for (Filter filter : this.filters) {
			log.debug("Filter Axis is : ");
			log.debug(filter.getAxis());
			log.debug("Collection ID is : ");
			log.debug(collectionID);
			String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
			if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
				tempFilter = filter;
				log.debug("TempHigh"+tempFilter.getUpperBound());
				log.debug("Temporal Axis is : ");
				log.debug(tempFilter.getAxis());
			}
		}
		log.debug("Filters are : ");
		log.debug(filters);
		log.debug("Temporal filter is : ");
		log.debug(tempFilter);
		if (tempFilter != null) {
			StringBuilder stringBuilder = new StringBuilder("(condense + ");
			stringBuilder.append("over $pm" + reduceNodeKey + " t (imageCrsDomain(");
			stringBuilder.append(createFilteredCollectionString(collectionVar, collectionID, tempFilter) + ",");
			stringBuilder.append(tempAxis + ")) using ");
			String meanDateRange1 = Pattern.compile(tempAxis+"\\(.*?\\)").matcher(payLoad).replaceAll(tempAxis+"\\(\\$pm\\" + reduceNodeKey + ")");
			stringBuilder.append(meanDateRange1 + ")/( condense + over $pmm" + reduceNodeKey + " t (imageCrsDomain(" + payLoad + ",");
			stringBuilder.append(tempAxis + ")) using 1)");
			
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
	
	private void createPolygonFilter(JSONObject argsObject, int srs, String coll) {
		double polygonArrayLong = 0;
		double polygonArrayLat = 0;

		if (argsObject.getString("type").equals("Polygon")) {
			for (Object argsKey : argsObject.keySet()) {
				String argsKeyStr = (String) argsKey;
				if (argsKeyStr.equals("coordinates")) {
					JSONArray polygonArray = (JSONArray) argsObject.getJSONArray(argsKeyStr).getJSONArray(0);
					for (int a = 0; a < polygonArray.length(); a++) {
						polygonArrayLong = polygonArray.getJSONArray(a).getDouble(0);
						polygonArrayLat = polygonArray.getJSONArray(a).getDouble(0);
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

						if (polygonArrayLong < westlower) {
							polygonArrayLong = westlower;
						}

						if (polygonArrayLong > eastupper) {
							polygonArrayLong = eastupper;
						}

						if (polygonArrayLat > northupper) {
							polygonArrayLat = northupper;
						}

						if (polygonArrayLat < southlower) {
							polygonArrayLat = southlower;
						}

						SpatialReference src = new SpatialReference();
						src.ImportFromEPSG(4326);
						SpatialReference dst = new SpatialReference();
						dst.ImportFromEPSG(srs);				

						CoordinateTransformation tx = new CoordinateTransformation(src, dst);
						double[] c1 = null;				
						c1 = tx.TransformPoint(polygonArrayLat, polygonArrayLong);

						polygonArrayLong = c1[0];
						polygonArrayLat = c1[1];

						log.debug("Polygon Long : ");
						log.debug(polygonArrayLat);
						log.debug("Polygon Lat : ");
						log.debug(polygonArrayLat);
						this.filtersPolygon.add(new Filter("Poly"+a, Double.toString(polygonArrayLong), Double.toString(polygonArrayLat)));
					}
				}
			}
		}
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
				log.debug("Save Result Process Node key found is: " + processNodeKey);
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

	private JSONArray getFromNodeOfCurrentKey(String currentNode){
		JSONObject nextNodeName = new JSONObject();
		JSONArray fromNodes = new JSONArray();
		String nextFromNode = null;
		JSONObject currentNodeProcessArguments =  processGraph.getJSONObject(currentNode).getJSONObject("arguments");
		for (String argumentsKey : currentNodeProcessArguments.keySet()) {
			if (argumentsKey.contentEquals("data")) {
				if (currentNodeProcessArguments.get("data") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("data").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				else if (currentNodeProcessArguments.get("data") instanceof JSONArray) {
					JSONArray reduceData = currentNodeProcessArguments.getJSONArray("data");
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
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("mask")) {
				if (currentNodeProcessArguments.get("mask") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("mask").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("mask").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			
			else if (argumentsKey.contentEquals("accept")) {
				if (currentNodeProcessArguments.get("accept") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("accept").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("accept").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			
			else if (argumentsKey.contentEquals("reject")) {
				if (currentNodeProcessArguments.get("reject") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("reject").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("reject").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			
			else if (argumentsKey.contentEquals("value")) {
				if (currentNodeProcessArguments.get("value") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("value").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("value").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				else if (currentNodeProcessArguments.get("value") instanceof JSONArray) {
					JSONArray reduceData = currentNodeProcessArguments.getJSONArray("value");
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
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("band1")) {
				if (currentNodeProcessArguments.get("band1") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("band1").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("band1").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("band2")) {
				if (currentNodeProcessArguments.get("band2") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("band2").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("band2").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("cube1")) {
				if (currentNodeProcessArguments.get("cube1") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("cube1").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("cube1").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("cube2")) {
				if (currentNodeProcessArguments.get("cube2") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("cube2").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("cube2").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
		}
		return fromNodes;		
	}

	private String getFormatFromSaveResultNode(JSONObject saveResultNode) {
		JSONObject saveResultArguments = saveResultNode.getJSONObject("arguments");
		String format = saveResultArguments.getString("format");
		return format;
	}
	
	private JSONArray getApplyFromNodes(String currentNode, JSONObject applyProcesses) {
		JSONObject nextNodeName = new JSONObject();
		JSONArray fromNodes = new JSONArray();
		String nextFromNode = null;
		JSONObject applyProcessArguments =  applyProcesses.getJSONObject(currentNode).getJSONObject("arguments");
		for (String argumentsKey : applyProcessArguments.keySet()) {
			if (argumentsKey.contentEquals("data")) {
				if (applyProcessArguments.get("data") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("data").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				else if (applyProcessArguments.get("data") instanceof JSONArray) {
					JSONArray reduceData = applyProcessArguments.getJSONArray("data");
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
				nextNodeName.put(currentNode, fromNodes);				
			}
			if (argumentsKey.contentEquals("expressions")) {
				if (applyProcessArguments.get("expressions") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("expressions").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				else if (applyProcessArguments.get("expressions") instanceof JSONArray) {
					JSONArray reduceData = applyProcessArguments.getJSONArray("expressions");
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
				nextNodeName.put(currentNode, fromNodes);
			}
			if (argumentsKey.contentEquals("expression")) {
				if (applyProcessArguments.get("expression") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("expression").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);
			}
			if (argumentsKey.contentEquals("x")) {
				if (applyProcessArguments.get("x") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("x").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("x").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);
			}
			if (argumentsKey.contentEquals("y")) {
				if (applyProcessArguments.get("y") instanceof JSONObject) {
					for (String fromKey : applyProcessArguments.getJSONObject("y").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = applyProcessArguments.getJSONObject("y").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);
			}
		}
		return fromNodes;
	}
	
	private JSONArray getReducerFromNodes(String currentNode, JSONObject reduceProcesses) {
		JSONObject nextNodeName = new JSONObject();
		JSONArray fromNodes = new JSONArray();
		String nextFromNode = null;
		JSONObject reducerProcessArguments =  reduceProcesses.getJSONObject(currentNode).getJSONObject("arguments");
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
				nextNodeName.put(currentNode, fromNodes);				
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
				else if (reducerProcessArguments.get("expressions") instanceof JSONArray) {
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
				nextNodeName.put(currentNode, fromNodes);
			}
			if (argumentsKey.contentEquals("expression")) {
				if (reducerProcessArguments.get("expression") instanceof JSONObject) {
					for (String fromKey : reducerProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = reducerProcessArguments.getJSONObject("expression").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);
			}
			if (argumentsKey.contentEquals("x")) {
				if (reducerProcessArguments.get("x") instanceof JSONObject) {
					for (String fromKey : reducerProcessArguments.getJSONObject("x").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = reducerProcessArguments.getJSONObject("x").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);
			}
			if (argumentsKey.contentEquals("y")) {
				if (reducerProcessArguments.get("y") instanceof JSONObject) {
					for (String fromKey : reducerProcessArguments.getJSONObject("y").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = reducerProcessArguments.getJSONObject("y").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);
			}
		}
		return fromNodes;
	}

	/**
	 * 
	 * @param processParent
	 * @return
	 */
	private JSONObject parseOpenEOProcessGraph() {
		//TODO why do we create an object here, that we never touch again and return that empty object?
		JSONObject result = null;
		JSONArray nodesArray = new JSONArray();
		JSONArray nodesSortedArray = new JSONArray();
		String saveNode = getSaveNode();
		JSONArray saveNodeAsArray = new JSONArray();
		saveNodeAsArray.put(saveNode);
		nodesArray.put(saveNodeAsArray);

		for (int n = 0; n < nodesArray.length(); n++) {
			for (int a = 0; a < nodesArray.getJSONArray(n).length(); a++) {
				JSONArray fromNodeOfReducers = getFromNodeOfCurrentKey(nodesArray.getJSONArray(n).getString(a));
				if (fromNodeOfReducers.length()>0) {
					nodesArray.put(fromNodeOfReducers);
				}
				else if (fromNodeOfReducers.length()==0) {
					nodesSortedArray.put(nodesArray.getJSONArray(n).getString(a));
				}
			}
		}
		
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}
				
		nodesArray.remove(nodesArray.length()-1);		
		
		for (int i = nodesArray.length()-1; i>0; i--) {
			if (nodesArray.getJSONArray(i).length()>0) {				
				for (int a = 0; a < nodesArray.getJSONArray(i).length(); a++) {
					nodesSortedArray.put(nodesArray.getJSONArray(i).getString(a));
				}
			}
		}		
				
		nodesSortedArray.put(saveNode);
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}
		log.debug("Executing Processes : " + nodesSortedArray);
		for(int a = 0; a<nodesSortedArray.length()-1; a++) {
			String nodeKeyOfCurrentProcess = nodesSortedArray.getString(a);
			String currentProcessID = processGraph.getJSONObject(nodeKeyOfCurrentProcess).getString("process_id");
			log.debug("Executing Process : " + currentProcessID);
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
			collectionIDs.add(new Collection(processNodeKey));
			log.debug("Found actual dataset: " + collection);

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
						log.debug("Currently working on Spatial Extent: ");
						log.debug(spatialExtentNode.toString(4));
						createBoundingBoxFilterFromArgs(loadCollectionNodeArguments, srs, collection, false);
					}
				}
				if (argumentKey.equals("temporal_extent")) {
					if (!loadCollectionNodeArguments.isNull(argumentKey)) {
						processDataCubeTempExt = (JSONArray) loadCollectionNodeArguments.get("temporal_extent");					
						log.debug("Currently working on Temporal Extent: ");
						log.debug(processDataCubeTempExt.toString(4));
						createDateRangeFilterFromArgs(processDataCubeTempExt, collection, false);
					}
				}
			}
		}
		
		else if (processID.contains("_time")) {
			log.debug(processNode);
			String fromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");
			String collectionNodeKey = getFilterCollectionNode(fromNode);
			String collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
			createTemporalAggregate(processID, collectionID, processNodeKey);
		}

		else if (processID.contains("reduce")) {
			String dimension = processNode.getJSONObject("arguments").getString("dimension");
			String fromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");
			String collectionNodeKey = getFilterCollectionNode(fromNode);
			String collectionID = processGraph.getJSONObject(collectionNodeKey).getJSONObject("arguments").getString("id");
			JSONObject jsonresp = null;
			try {
				jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
			
			String temporalAxis = null;
			for (String tempAxis1 : jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").keySet()) {
				String tempAxis1UpperCase = tempAxis1.toUpperCase();
				if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
					temporalAxis = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(tempAxis1).getString("axis");
				}
			}
			if (dimension.equals("temporal")) {
				JSONObject reducer = processNode.getJSONObject("arguments").getJSONObject("reducer").getJSONObject("callback");
				for (String nodeKey : reducer.keySet()) {
					
					String processName = reducer.getJSONObject(nodeKey).getString("process_id");
					createReduceTemporalAggregate(processName, collectionID, processNodeKey);
				}
			}
		}

		else if (processID.equals("ndvi")) {
			JSONObject processAggregate = processGraph.getJSONObject(processNodeKey);			    
			String collectionNode = getFilterCollectionNode(processNodeKey);
			String collection = processGraph.getJSONObject(collectionNode).getJSONObject("arguments").getString("id");			
			createNDVIAggregateFromProcess(processAggregate, collection);
		}

		else if (processID.equals("filter_temporal")) {
			String filterCollectionNodeKey = null;
			String filterTempNodeKey = processNodeKey;
			String filterTempfromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");			
			filterCollectionNodeKey = getFilterCollectionNode(filterTempfromNode);
			JSONObject loadCollectionNode = processGraph.getJSONObject(filterCollectionNodeKey).getJSONObject("arguments");
			String coll = (String) loadCollectionNode.get("id");
			JSONObject processFilter = processGraph.getJSONObject(filterTempNodeKey);
			JSONObject processFilterArguments = processFilter.getJSONObject("arguments");
			JSONArray extentArray = new JSONArray();			
			extentArray = (JSONArray) processFilterArguments.get("extent");
			createDateRangeFilterFromArgs(extentArray, coll, false);
		}

		else if (processID.equals("filter_bbox")) {
			String filterCollectionNodeKey = null;
			String filterBboxNodeKey = processNodeKey;
			String filterBboxfromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");			
			filterCollectionNodeKey = getFilterCollectionNode(filterBboxfromNode);
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
				createBoundingBoxFilterFromArgs(processFilterArguments, srs, coll, false);
			}
		}
		
		else if (processID.equals("filter_polygon")) {
			String filterCollectionNodeKey = null;
			String filterPolygonNodeKey = processNodeKey;
			String filterPolygonfromNode = processNode.getJSONObject("arguments").getJSONObject("data").getString("from_node");			
			filterCollectionNodeKey = getFilterCollectionNode(filterPolygonfromNode);
			JSONObject loadCollectionNode = processGraph.getJSONObject(filterCollectionNodeKey).getJSONObject("arguments");			
			String coll = (String) loadCollectionNode.get("id");
			JSONObject processFilter = processGraph.getJSONObject(filterPolygonNodeKey);
			JSONObject processFilterArguments = processFilter.getJSONObject("arguments").getJSONObject("polygons");

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
			
			if (srs > 0) {
				log.debug("Polygon Extent is : " + processFilterArguments.getJSONArray("coordinates"));
				createPolygonFilter(processFilterArguments, srs, coll);
				log.debug("Polygon Filters are : ");
				log.debug(filtersPolygon);
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
	
	private String getFilterCollectionNode() {
		String filterCollectionNodeKey = null;
		for (String argumentsKey : processGraph.keySet()) {
			JSONObject args = processGraph.getJSONObject(argumentsKey).getJSONObject("arguments");
			for (String argsKey : args.keySet()) {
				if (argsKey.contentEquals("id")) {
					filterCollectionNodeKey = argumentsKey;
				}
				else if (argsKey.contentEquals("data")) {
					String filterfromNode = processGraph.getJSONObject(argumentsKey).getJSONObject("arguments").getJSONObject("data").getString("from_node");			  
					filterCollectionNodeKey = getFilterCollectionNode(filterfromNode);
				}
			}
		}
		return filterCollectionNodeKey;
	}

	private void createDateRangeFilterFromArgs(JSONArray extentArray, String collectionID, Boolean tempNull) {
		String fromDate = null;
		String toDate = null;
		JSONObject extent;
		
		if (tempNull) {
			JSONObject jsonresp = null;
			try {
				jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
			String templower = null;
			String tempupper = null;
			
			try {
				templower = temporal.get(0).toString();
				tempupper = temporal.get(1).toString();
			}			
			catch (JSONException e) {
				log.error("An error occured: " + e.getMessage());
				
			}
			
			log.debug("Temporal Extent is: ");
			log.debug(temporal);
			
			if (templower != null && tempupper != null) {
				log.debug("Temporal Extent is: |" + templower + "|:|" + tempupper + "|");
				if(LocalDateTime.parse(templower.replace("Z", "")).equals(LocalDateTime.parse(tempupper.replace("Z", "")))) {
					tempupper = null;
					log.debug("Dates are identical. To date is set to null!");
				}
				Filter dateFilter = null;
				for (Filter filter : this.filters) {
					String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
					if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
						dateFilter = filter;
					}
				}
				this.filters.remove(dateFilter);
				String tempAxis = null;
				for (String tempAxis1 : jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").keySet()) {
					String tempAxis1UpperCase = tempAxis1.toUpperCase();
					if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
						tempAxis = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(tempAxis1).getString("axis");
					}
				}
				this.filters.add(new Filter(tempAxis+"_"+collectionID, templower, tempupper));				
			}
		}
		
		else {
			String extentlower = extentArray.get(0).toString();
			String extentupper = extentArray.get(1).toString();
			JSONObject jsonresp = null;
			try {
				jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
			log.debug("Temporal Extent is: ");
			log.debug(temporal);
			if (extentlower.compareTo(templower) < 0) {
				fromDate = temporal.get(0).toString();
			}
			else {
				fromDate = extentArray.get(0).toString();
			}
			if (extentupper.compareTo(tempupper) > 0) {
				toDate = temporal.get(1).toString();
			}
			else {
				toDate = extentArray.get(1).toString();
//				DateFormat toDateNewFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//				Date toDateNew;			
//				try {
//					toDateNew = toDateNewFormat.parse(toDate);
//					toDateNew.setTime(toDateNew.getTime() - 1);
//					toDate = toDateNewFormat.format(toDateNew);
//					log.debug("To Date"+toDate);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			if (fromDate != null && toDate != null) {
				log.debug("Temporal Extent is: |" + fromDate + "|:|" + toDate + "|");
				if(LocalDateTime.parse(fromDate.replace("Z", "")).equals(LocalDateTime.parse(toDate.replace("Z", "")))) {
					toDate = null;
					log.debug("Dates are identical. To date is set to null!");
				}
				Filter dateFilter = null;
				for (Filter filter : this.filters) {
					String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
					if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
						dateFilter = filter;
					}
				}
				this.filters.remove(dateFilter);
				String tempAxis = null;
				for (String tempAxis1 : jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").keySet()) {
					String tempAxis1UpperCase = tempAxis1.toUpperCase();
					if (tempAxis1UpperCase.contentEquals("DATE") || tempAxis1UpperCase.contentEquals("TIME") || tempAxis1UpperCase.contentEquals("ANSI") || tempAxis1UpperCase.contentEquals("UNIX")) {
						tempAxis = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(tempAxis1).getString("axis");
					}
				}
				log.debug("To Date  "+toDate);
				this.filters.add(new Filter(tempAxis+"_"+collectionID, fromDate, toDate));
				
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
		log.debug("Trying to read JSON from the following URL : ");
		log.debug(url);
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

	private void createBoundingBoxFilterFromArgs(JSONObject argsObject, int srs, String collectionID, Boolean spatNull) {
		String left = null;
		String right = null;
		String top = null;
		String bottom = null;
		
		if (spatNull) {
			JSONObject extent;
			JSONObject jsonresp = null;
			try {
				jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
			log.debug("Spatial Extent is: ");
			log.debug(spatial);
			left = Double.toString(westlower);
			right = Double.toString(eastupper);
			top = Double.toString(northupper);
			bottom = Double.toString(southlower).toString();

			SpatialReference src = new SpatialReference();
			src.ImportFromEPSG(4326);
			SpatialReference dst = new SpatialReference();
			dst.ImportFromEPSG(srs);
			log.debug("SRS is :" + srs);
			
			CoordinateTransformation tx = new CoordinateTransformation(src, dst);
			double[] c1 = null;
			double[] c2 = null;
			c1 = tx.TransformPoint(Double.parseDouble(bottom), Double.parseDouble(left));
			c2 = tx.TransformPoint(Double.parseDouble(top), Double.parseDouble(right));
			left = Double.toString(c1[0]);
			bottom = Double.toString(c1[1]);
			right = Double.toString(c2[0]);
			top = Double.toString(c2[1]);
			log.debug("WEST: "+left);
			log.debug("SOUTH: "+bottom);
			log.debug("EAST: "+right);
			log.debug("NORTH: "+top);
			String spatAxisX = null;
			String spatAxisY = null;
			
			if (left != null && right != null && top != null && bottom != null) {
				Filter eastFilter = null;
				Filter westFilter = null;
				for (Filter filter : this.filters) {
					String axis = filter.getAxis();
					String axisUpperCase = axis.replace("_"+ collectionID, "").toUpperCase();
					if (axisUpperCase.equals("E") || axisUpperCase.equals("LONG") || axisUpperCase.equals("LON") || axisUpperCase.equals("LONGITUDE") || axisUpperCase.equals("X")) {
						eastFilter = filter;
					}
					else if (axisUpperCase.equals("N") || axisUpperCase.equals("LAT") || axisUpperCase.equals("LATITUDE") || axisUpperCase.equals("Y")) {
						westFilter = filter;
					}
				}
				this.filters.remove(eastFilter);
				this.filters.remove(westFilter);
				for (String spatAxis : jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").keySet()) {	
					String spatAxisUpperCase = spatAxis.toUpperCase();
					if (spatAxisUpperCase.contentEquals("E") || spatAxisUpperCase.contentEquals("LONG") || spatAxisUpperCase.equals("LON") || spatAxisUpperCase.equals("LONGITUDE") || spatAxisUpperCase.contentEquals("X")) {
						spatAxisX = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(spatAxis).getString("axis");
					}
					else if (spatAxisUpperCase.contentEquals("N") || spatAxisUpperCase.contentEquals("LAT") || spatAxisUpperCase.equals("LATITUDE") || spatAxisUpperCase.contentEquals("Y")) {
						spatAxisY = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(spatAxis).getString("axis");
					}
				}
				this.filters.add(new Filter(spatAxisX+"_"+collectionID, left, right));
				this.filters.add(new Filter(spatAxisY+"_"+collectionID, bottom, top));
			} else {
				log.error("No spatial information could be found in process!");
			}
		}

		else {
			JSONObject jsonresp = null;
			String spatAxisX = null;
			String spatAxisY = null;
			for (Object argsKey : argsObject.keySet()) {
				String argsKeyStr = (String) argsKey;
				if (argsKeyStr.equals("extent") || argsKeyStr.equals("spatial_extent")) {
					JSONObject extentObject = (JSONObject) argsObject.get(argsKeyStr);

					for (Object extentKey : extentObject.keySet()) {
						String extentKeyStr = extentKey.toString();
						JSONObject extent;
						
						try {
							jsonresp = readJsonFromUrl(ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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

						log.debug("Spatial Extent is: ");
						log.debug(spatial);
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
								bottom = Double.toString(southlower);
							}
						}
					}

					SpatialReference src = new SpatialReference();
					src.ImportFromEPSG(4326);
					SpatialReference dst = new SpatialReference();
					dst.ImportFromEPSG(srs);
					log.debug("SRS is : " + srs);

					CoordinateTransformation tx = new CoordinateTransformation(src, dst);
					double[] c1 = null;
					double[] c2 = null;
					c1 = tx.TransformPoint(Double.parseDouble(bottom), Double.parseDouble(left));
					c2 = tx.TransformPoint(Double.parseDouble(top), Double.parseDouble(right));
					
					if (srs==3035) {
						left = Double.toString(c1[1]);
						bottom = Double.toString(c1[0]);
						right = Double.toString(c2[1]);
						top = Double.toString(c2[0]);
					}
					else {
						left = Double.toString(c1[0]);
						bottom = Double.toString(c1[1]);
						right = Double.toString(c2[0]);
						top = Double.toString(c2[1]);
					}

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
					String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
					if (axisUpperCase.equals("E") || axisUpperCase.equals("LONG") || axisUpperCase.equals("LON") || axisUpperCase.equals("LONGITUDE") || axisUpperCase.equals("X")) {
						eastFilter = filter;
					}
					else if (axisUpperCase.equals("N") || axisUpperCase.equals("LAT") || axisUpperCase.equals("LATITUDE") || axisUpperCase.equals("Y")) {
						westFilter = filter;
					}
				}
				this.filters.remove(eastFilter);
				this.filters.remove(westFilter);
				for (String spatAxis : jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").keySet()) {	
					String spatAxisUpperCase = spatAxis.toUpperCase();
					if (spatAxisUpperCase.contentEquals("E") || spatAxisUpperCase.contentEquals("LONG") || spatAxisUpperCase.equals("LON") || spatAxisUpperCase.equals("LONGITUDE") || spatAxisUpperCase.contentEquals("X")) {
						spatAxisX = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(spatAxis).getString("axis");
					}
					else if (spatAxisUpperCase.contentEquals("N") || spatAxisUpperCase.contentEquals("LAT") || spatAxisUpperCase.equals("LATITUDE") || spatAxisUpperCase.contentEquals("Y")) {
						spatAxisY = jsonresp.getJSONObject("properties").getJSONObject("cube:dimensions").getJSONObject(spatAxis).getString("axis");
					}
				}
				this.filters.add(new Filter(spatAxisX+"_"+collectionID, left, right));
				this.filters.add(new Filter(spatAxisY+"_"+collectionID, bottom, top));
				
			} else {
				log.error("No spatial information could be found in process!");
			}			
		}
	}

	private void createReduceTemporalAggregate(String processName, String collectionID, String processNodeKey) {
		String aggregateType = processName;
		Vector<String> params = new Vector<String>();
		String tempAxis = null;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();			
			if(axis.contains(collectionID)) {
				String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();				
				if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
					tempAxis = axis.replace("_"+ collectionID, "");
				}
			}
		}
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals(tempAxis+"_"+collectionID)) {
				params.add(filter.getLowerBound());
				params.add(filter.getUpperBound());
			}
		}
		log.debug("Temporal Aggregate added!");
		aggregates.add(new Aggregate(new String(tempAxis+"_"+collectionID+processNodeKey), aggregateType, params));
	}

	private void createTemporalAggregate(String processName, String collectionID, String processNodeKey) {
		String aggregateType = processName.split("_")[0];
		Vector<String> params = new Vector<String>();
		String tempAxis = null;
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();			
			if(axis.contains(collectionID)) {
				String axisUpperCase = filter.getAxis().replace("_"+ collectionID, "").toUpperCase();
				if (axisUpperCase.equals("DATE") || axisUpperCase.equals("TIME") || axisUpperCase.equals("ANSI") || axisUpperCase.equals("UNIX")) {
					tempAxis = axis.replace("_"+ collectionID, "");
				}
			}
		}
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals(tempAxis+"_"+collectionID)) {
				params.add(filter.getLowerBound());
				params.add(filter.getUpperBound());
			}
		}
		log.debug("Temporal Aggregate added!");
		aggregates.add(new Aggregate(new String(tempAxis+"_"+collectionID+processNodeKey), aggregateType, params));
	}

	private void createNDVIAggregateFromProcess(JSONObject argsObject, String collectionID) {
		String red = null;
		String nir = null;
		JSONObject collectionSTACMetdata = null;
		try {
			collectionSTACMetdata = readJsonFromUrl(
					ConvenienceHelper.readProperties("openeo-endpoint") + "/collections/" + collectionID);
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
			log.debug("Feature Aggregate added!");
			aggregates.add(new Aggregate(new String("feature_"+collectionID), new String("NDVI_"+collectionID), params));
		}
	}
}