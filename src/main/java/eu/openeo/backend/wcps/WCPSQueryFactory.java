package eu.openeo.backend.wcps;

import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WCPSQueryFactory {
	
	private StringBuilder wcpsStringBuilder;
	private Vector<String> collectionIDs;
	
	public WCPSQueryFactory(JSONObject openEOGraph) {
		collectionIDs = new Vector<String>();
		wcpsStringBuilder = new StringBuilder("for ");
		if(openEOGraph.containsKey(new String("process_graph"))) {
			JSONObject process = getFirstProcess((JSONObject) openEOGraph.get(new String("process_graph")));
			
		}
		for (int c = 1 ; c <= collectionIDs.size(); c++){
			wcpsStringBuilder.append("$c" + c);
			if(c > 1) {
				wcpsStringBuilder.append(", ");
			}
		}
		wcpsStringBuilder.append(" in ( ");
		for (int c = 1 ; c <= collectionIDs.size(); c++){
			wcpsStringBuilder.append(collectionIDs.get(c-1) + " ");
		}
		wcpsStringBuilder.append(") ");
	}
	
	public String getWCPSString(){
		return wcpsStringBuilder.toString();
	}
	
	private JSONObject getFirstProcess(JSONObject processParent) {
		JSONObject result = null;
		for (Object key : processParent.keySet()) {			
	        String keyStr = (String)key;
	        if(keyStr.equals("process_id")) {
	        	String name = (String) processParent.get(keyStr);
	        	System.out.println("currently working on: " + name);
	        } else
	        if(keyStr.equals("args")) {
	        	JSONObject argsObject = (JSONObject) processParent.get(keyStr);
		        for (Object argsKey : argsObject.keySet()) {
		        	String argsKeyStr = (String)argsKey;
		        	if(argsKeyStr.equals("collections")) {
		        		JSONArray collections = (JSONArray) argsObject.get(argsKey);
		        		result = getFirstProcess((JSONObject) collections.get(0));
		        		if(result == null) {
		        			result = (JSONObject) collections.get(0);
		        			break;
		        		}
		        	}
		        }
	        } else 
	        if(keyStr.equals("collection_id")) {
	        	String name = (String) processParent.get(keyStr);
	        	collectionIDs.add(name);
	        	System.out.println("found actual dataset: " + name);
	        }
	    }
		return result;
	}
	
	private JSONObject getProcess(JSONObject processParent, String processName) {
		JSONObject process = (JSONObject) processParent.get(new String("process_id"));
		if(process != null) {
			if(process.containsValue(processName)) {
				return process;
			}
			process = getProcess(process, processName);
		}
		return process;
	}

}
