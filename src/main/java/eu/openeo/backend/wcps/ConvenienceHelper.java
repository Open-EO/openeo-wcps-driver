package eu.openeo.backend.wcps;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class ConvenienceHelper {

	private static Logger log = LogManager.getLogger();

	public static String readProperties(String key) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("config.properties");

		Properties properties = new Properties();
		properties.load(stream);

		String value = properties.getProperty(key);
		stream.close();

		return value;
	}

	public static String getMimeTypeFromOutput(String output) throws IOException, JSONException {
		output = output.toUpperCase();
		if (output.equals("NETCDF")) {
			output = "netCDF";
		}
		if (output.equals("GTIFF")) {
			output = "GTiff";
		}
		log.debug("assigning output type: " + output);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("output_formats.json");
		JSONObject outputFormats = new JSONObject(IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
		stream.close();
		JSONObject currentFormat = outputFormats.getJSONObject(output);
		
		if (currentFormat != null) {
			return currentFormat.getString("mime-type");
		}
		log.error("gdal type could not be matched to mime type: " + output);
		return MediaType.WILDCARD;
	}
	
	public static String getRasTypeFromOutput(String output) throws IOException, JSONException {
		output = output.toUpperCase();
		if (output.equals("NETCDF")) {
			output = "netCDF";
		}
		if (output.equals("GTIFF")) {
			output = "GTiff";
		}
		log.debug("assigning output type: " + output);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("output_formats.json");
		JSONObject outputFormats = new JSONObject(IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
		stream.close();
		JSONObject currentFormat = outputFormats.getJSONObject(output);
		if (currentFormat != null) {
			return currentFormat.getString("rasdaman_name");
		}
		log.error("gdal type could not be matched to mime type: " + output);
		return MediaType.WILDCARD;
	}

	public static String getRasNameFromMimeType(String mimeTypeName) throws IOException, JSONException {
		log.debug("Mime type request: " + mimeTypeName);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("output_formats.json");
		JSONObject outputFormats = new JSONObject(IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
		stream.close();
		for (String formatName : outputFormats.keySet()) {
			JSONObject currentObject = outputFormats.getJSONObject(formatName);
			if (currentObject.getString("mime-type").equals(mimeTypeName)) {
				return currentObject.getString("rasdaman_name");
			}
		}
		log.error("mime type could not be matched to rasdaman type: " + mimeTypeName);
		return MediaType.WILDCARD;
	}

	public static String getMimeTypeFromRasName(String rasName) throws IOException, JSONException {
		log.debug("Ras type request: " + rasName);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("output_formats.json");
		JSONObject outputFormats = new JSONObject(IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
		stream.close();
		for (String formatName : outputFormats.keySet()) {
			JSONObject currentObject = outputFormats.getJSONObject(formatName);
			if (currentObject.getString("rasdaman_name").equals(rasName)) {
				return currentObject.getString("mime-type");
			}
		}
		log.error("rasdman type could not be matched to mime type: " + rasName);
		return MediaType.WILDCARD;
	}

}
