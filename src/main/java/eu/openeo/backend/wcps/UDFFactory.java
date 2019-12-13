package eu.openeo.backend.wcps;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.nimbusds.jose.util.StandardCharset;

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
	
	public UDFFactory(String udfLanString, InputStream codeStream, InputStream dataStream) throws IOException {
		this.udfDescriptor = new JSONObject();
		
		byte[] codeBlob = IOUtils.toByteArray(codeStream);
		JSONObject codeBlock = new JSONObject();
		codeBlock.put("language", udfLanString);
		codeBlock.put("source", new String(codeBlob, StandardCharset.UTF_8));	
		this.udfDescriptor.put("code", codeBlock);
		
		JSONObject hyperCube = new HyperCubeFactory().getHyperCubeFromGML(dataStream);
		this.udfDescriptor.put("data", hyperCube);
		
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
