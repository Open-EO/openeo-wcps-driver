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
		this.build(openEOGraph);
	}	

	public String getOutputFormat() {
		return outputFormat;
	}

	private void build(JSONObject openEOGraph) {
		log.debug(openEOGraph.toString());
		parseOpenEOProcessGraph(openEOGraph);
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
							stretchBuilder.append(createNDVIWCPSString("$c1", aggregates.get(a)));
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
							stretchBuilder.append(createNDVIWCPSString("$c1", aggregates.get(a)));
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
				wcpsStringBuilder.append(createNDVIWCPSString("$c1", aggregates.get(a)));
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

	private String createNDVIWCPSString(String collectionName, Aggregate ndviAggregate) {
		String redBandName = ndviAggregate.getParams().get(0);
		String nirBandName = ndviAggregate.getParams().get(1);
		String filterString = createFilteredCollectionString(collectionName);
		filterString = filterString.substring(collectionName.length());
		String red = createBandSubsetString(collectionName, redBandName, filterString);
		String nir = createBandSubsetString(collectionName, nirBandName, filterString);
		StringBuilder stringBuilder = new StringBuilder("((double)");
		stringBuilder.append(nir + " - " + red);
		stringBuilder.append(") / ((double)");
		stringBuilder.append(nir + " + " + red);
		stringBuilder.append(")");
		filters.removeAllElements();

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
			
			log.debug("Aggregate Temp StringBuilder " + stringBuilder);
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

	/**
	 * 
	 * @param processParent
	 * @return
	 */
	private JSONObject parseOpenEOProcessGraph(JSONObject processParent) {
		JSONObject result = null;
		String collection = null;
		for (String processNodeKey : processParent.keySet()) {
			
			log.debug("Key found is: " + processNodeKey);
			
			JSONObject processNode = processParent.getJSONObject(processNodeKey);
			String processID = processNode.getString("process_id");
			JSONObject argumentsNode = processNode.getJSONObject("arguments");
							
				if (processID.equals("load_collection")) {
					
					String collectionNodeKey = processNodeKey;
					
					JSONObject loadCollectionNode = processParent.getJSONObject(processNodeKey);
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
            
			else if (processID.contains("time")) {
				//log.debug("Found Time node: " + processNode.getString("process_id"));
				    createTemporalAggregate(processID);
				}
				
			else if (processID.equals("ndvi")) {
				//log.debug("Found NDVI node: " + processNode.getString("process_id"));
				    JSONObject processAggregate = processParent.getJSONObject(processNodeKey);
				    createNDVIAggregateFromProcess(processAggregate);
			    }
						
			else if (processID.equals("save_result")) {
				log.debug("Found save result node: " + processNode.getString("process_id"));
				String format = getFormatFromSaveResultNode(processNode);
				try {					
					this.outputFormat = ConvenienceHelper.getMimeTypeFromOutput(format);
				} catch (JSONException | IOException e) {
					log.error("Error while parsing outputformat from process graph: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for( StackTraceElement element: e.getStackTrace()) {
						builder.append(element.toString()+"\n");
					}
					log.error(builder.toString());
				}				
			}	
		 
	  }
		return result;
   }
	
	private String getFormatFromSaveResultNode(JSONObject saveResultNode) {
		JSONObject saveResultArguments = saveResultNode.getJSONObject("arguments");
		String format = saveResultArguments.getString("format");
		return format;
	}

	private String getFilterCollectionNode(JSONObject processParent, String fromNode) {
		
		String filterCollectionNodeKey = null;
		
		JSONObject loadCollectionNodeKey = processParent.getJSONObject(fromNode);
		JSONObject loadCollectionNodeKeyArguments = loadCollectionNodeKey.getJSONObject("arguments");
		
		for (String argumentsKey : loadCollectionNodeKeyArguments.keySet()) {
		
		  if (argumentsKey.contentEquals("id")) {
			  filterCollectionNodeKey = fromNode;
		  }
		  if (argumentsKey.contentEquals("data")) {
			  JSONObject filterDataNode = loadCollectionNodeKeyArguments.getJSONObject("data");
			  String filterfromNode = filterDataNode.getString("from_node");
			  
			  filterCollectionNodeKey = getFilterCollectionNode(processParent, filterfromNode);
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
			
			String loadCollNode = keyStrdc;
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
	
    private void createFilterFromProcessNew(JSONObject processFilter, JSONObject processFilterArguments, String coll) {
		
	    for (String keyStr : processFilterArguments.keySet()) {
		//Object key = processFilterArguments.getJSONObject("extent");
		
			if (!keyStr.contains("extent")) {
				log.debug("no spatial or temporal extent defined in filter");
			}
			
			// check if filter contains spatial information
	        else if (processFilter.get("process_id").toString().contains("bbox")) {
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
					createBoundingBoxFilterFromArgs(processFilterArguments, srs, coll);
				}
			}
			// check if filter contains temporal information
			else if (processFilter.get("process_id").toString().contains("temporal")) {
				log.debug("temporal extent defined in filter");
				
				JSONArray extentArray = (JSONArray) processFilterArguments.get(keyStr);
				createDateRangeFilterFromArgs(extentArray, coll);
			}
	    }
	}
	
	
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
		} else {
			log.error("no spatial information could be found in process!");
		}
	}

	/**
	 * 
	 * @param process
	 */
	
	private void createAggregateFromProcess(JSONObject process) {
		boolean isTemporalAggregate = false;
		boolean isNDVIAggregate = false;
		String processName = null;
		for (Object key : process.keySet()) {
			String keyStr = (String) key;
			if (keyStr.equals("process_id")) {
				processName = (String) process.get(keyStr);
				log.debug("currently working on: " + processName);
				if (processName.contains("temporal") || processName.contains("time")) {
					isTemporalAggregate = true;
					createTemporalAggregate(processName);
				} else if (processName.contains("ndvi")) {
					isNDVIAggregate = true;
					createNDVIAggregateFromProcess(process);
				}
			}
		}
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
		aggregates.add(new Aggregate(new String("DATE"), aggregateType, params));
	}

	private void createNDVIAggregateFromProcess(JSONObject argsObject) {
		String red = "B04";
		String nir = "B08";
		for (Object argsKey : argsObject.keySet()) {
			String argsKeyStr = (String) argsKey;
			if (argsKeyStr.equals("red")) {
				red = "" + argsObject.getString(argsKeyStr);
			} else if (argsKeyStr.equals("nir")) {
				nir = "" + argsObject.getString(argsKeyStr);
			}
		}
		Vector<String> params = new Vector<String>();
		params.add(red);
		params.add(nir);
		if (red != null && nir != null)
			aggregates.add(new Aggregate(new String("feature"), new String("NDVI"), params));
	}

}