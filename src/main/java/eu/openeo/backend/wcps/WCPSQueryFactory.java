package eu.openeo.backend.wcps;

import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import eu.openeo.backend.wcps.domain.Aggregate;
import eu.openeo.backend.wcps.domain.Collection;
import eu.openeo.backend.wcps.domain.Filter;

public class WCPSQueryFactory {
	
	private StringBuilder wcpsStringBuilder;
	private Vector<Collection> collectionIDs;
	private Vector<Filter> filters;
	private Vector<Aggregate> aggregates;
	
	/**
	 * Creates WCPS query from openEO process Graph
	 * @param openEOGraph
	 */
	public WCPSQueryFactory(JSONObject openEOGraph) {
		collectionIDs = new Vector<Collection>();
		aggregates =  new Vector<Aggregate>();
		filters = new Vector<Filter>();
		wcpsStringBuilder = new StringBuilder("for ");
		if(openEOGraph.containsKey(new String("process_graph"))) {
			parseOpenEOProcessGraph((JSONObject) openEOGraph.get(new String("process_graph")));
			
		}
		for (int c = 1 ; c <= collectionIDs.size(); c++){
			wcpsStringBuilder.append("$c" + c);
			if(c > 1) {
				wcpsStringBuilder.append(", ");
			}
		}
		wcpsStringBuilder.append(" in ( ");
		for (int c = 1 ; c <= collectionIDs.size(); c++){
			wcpsStringBuilder.append(collectionIDs.get(c-1).getName() + " ");
		}
		wcpsStringBuilder.append(") return encode ( ");
		for(int a = 0; a < aggregates.size(); a++) {
			
		}
		wcpsStringBuilder.append("$c1");
		if(filters.size() > 0) {
			wcpsStringBuilder.append("[");
			for(int f = 0; f < filters.size(); f++) {
				Filter filter = filters.get(f);
				wcpsStringBuilder.append(filter.getAxis() + 
										 "(" + 
										 filter.getLowerBound() + 
										 ":" + 
										 filter.getUpperBound() + 
										 ")");
				if(f < filters.size() - 1) {
					wcpsStringBuilder.append(",");
				}
			}
			wcpsStringBuilder.append("]");
		}
		wcpsStringBuilder.append(", tiff )");
	}
	
	/**
	 * returns constructed query as String object
	 * @return String WCPS query
	 */
	public String getWCPSString(){
		return wcpsStringBuilder.toString();
	}
	
	/**
	 * 
	 * @param processParent
	 * @return
	 */
	private JSONObject parseOpenEOProcessGraph(JSONObject processParent) {
		JSONObject result = null;
		for (Object key : processParent.keySet()) {			
	        String keyStr = (String)key;
	        if(keyStr.equals("process_id")) {
	        	String name = (String) processParent.get(keyStr);
	        	System.out.println("currently working on: " + name);
	        	if(name.contains("filter")) {
	        		createFilterFromProcess(processParent);
	        	}
	        } else
	        if(keyStr.equals("args")) {
	        	JSONObject argsObject = (JSONObject) processParent.get(keyStr);
		        for (Object argsKey : argsObject.keySet()) {
		        	String argsKeyStr = (String)argsKey;
		        	if(argsKeyStr.equals("collections")) {
		        		JSONArray collections = (JSONArray) argsObject.get(argsKey);
		        		result = parseOpenEOProcessGraph((JSONObject) collections.get(0));
		        		if(result == null) {
		        			result = (JSONObject) collections.get(0);
		        			break;
		        		}
		        	}
		        }
	        } else 
	        if(keyStr.equals("collection_id")) {
	        	String name = (String) processParent.get(keyStr);
	        	collectionIDs.add(new Collection(name));
	        	System.out.println("found actual dataset: " + name);
	        }
	    }
		return result;
	}
	
	/**
	 * 
	 * @param process
	 */
	private void createFilterFromProcess(JSONObject process) {
		boolean isTemporalFilter = false;
		boolean isBoundBoxFilter = false;
		for (Object key : process.keySet()) {
			String keyStr = (String)key;
			if(keyStr.equals("process_id")) {
	        	String name = (String) process.get(keyStr);
	        	System.out.println("currently working on: " + name);
	        	if(name.contains("date")) {
        			isTemporalFilter = true;
        		} else
        		if(name.contains("bbox")) {
        			isBoundBoxFilter = true;
        		}
	        }
		}
		for (Object key : process.keySet()) {
			String keyStr = (String)key;
			if(keyStr.equals("args")) {
	        	JSONObject argsObject = (JSONObject) process.get(keyStr);
	        	if(isTemporalFilter) {
	        		createDateRangeFilterFromArgs(argsObject);
	        	}
	        	if(isBoundBoxFilter) {
	        		createBoundingBoxFilterFromArgs(argsObject);
	        	}
		        
	        }
		}
	}
	
	private void createDateRangeFilterFromArgs(JSONObject argsObject) {
		String fromDate = null;
		String toDate = null;
		for (Object argsKey : argsObject.keySet()) {
        	String argsKeyStr = (String)argsKey;
        	if(argsKeyStr.equals("from")) {
        		fromDate = (String) argsObject.get(argsKey);
        	} else
    		if(argsKeyStr.equals("to")) {
        		toDate = (String) argsObject.get(argsKey);
        	}
        }
		if(fromDate != null && toDate != null)
		this.filters.add(new Filter("DATE", fromDate, toDate));
	}
	
	private void createBoundingBoxFilterFromArgs(JSONObject argsObject) {
		String left = null;
		String right = null;
		String top = null;
		String bottom = null;
		for (Object argsKey : argsObject.keySet()) {
        	String argsKeyStr = (String)argsKey;
        	if(argsKeyStr.equals("left")) {
        		left = "" + argsObject.get(argsKey).toString();
        	} else
    		if(argsKeyStr.equals("right")) {
    			right = "" + argsObject.get(argsKey).toString();
        	}
        	if(argsKeyStr.equals("top")) {
        		top = "" + argsObject.get(argsKey).toString();
        	} else
    		if(argsKeyStr.equals("bottom")) {
    			bottom = "" + argsObject.get(argsKey).toString();
        	}
        }
		this.filters.add(new Filter("E", left, right));
		this.filters.add(new Filter("N", top, bottom));
	}
	
	/**
	 * 
	 * @param process
	 */
	private void createAggregateFromProcess(JSONObject process) {
		
	}

}
