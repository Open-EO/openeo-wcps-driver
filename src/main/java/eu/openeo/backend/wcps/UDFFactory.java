package eu.openeo.backend.wcps;

import org.json.JSONObject;

public class UDFFactory {
	
	private String udfCode;
	private JSONObject udfData;
	private String udfLanguage;
	
	private JSONObject udfDescriptor;
	
	public UDFFactory(String udfLanString, String udfCode, String crs, String id, JSONObject udfData) {
		this.udfDescriptor = new JSONObject();
		JSONObject codeBlock = new JSONObject();
		codeBlock.put("language", udfLanString);
		codeBlock.put("source", udfCode);		
		this.udfDescriptor.put("code", codeBlock);
		
		JSONObject dataBlock = new JSONObject();
		dataBlock.put("proj", crs);
		dataBlock.put("id", id);
		dataBlock.put("hypercubes", udfData.getJSONArray("hypercubes"));
		this.udfDescriptor.put("data", dataBlock);
	}

	public String getUdfCode() {
		return udfCode;
	}

	public void setUdfCode(String udfCode) {
		this.udfCode = udfCode;
	}

	public JSONObject getUdfData() {
		return udfData;
	}

	public void setUdfData(JSONObject udfData) {
		this.udfData = udfData;
	}

	public String getUdfLanguage() {
		return udfLanguage;
	}

	public void setUdfLanguage(String udfLanguage) {
		this.udfLanguage = udfLanguage;
	}

	public JSONObject getUdfDescriptor() {
		return udfDescriptor;
	}
	
	

}
