package eu.openeo.backend.wcps;

import org.json.simple.JSONObject;

public class WCPSQueryFactory {
	
	private JSONObject openEOGraph;
	private String wcpsString;	
	
	public WCPSQueryFactory(JSONObject openEOGraph) {
		this.openEOGraph = openEOGraph;
	}
	
	public String getWCPSString(){
		return wcpsString;
	}	

}
