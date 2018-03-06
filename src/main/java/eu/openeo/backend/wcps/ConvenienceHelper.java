package eu.openeo.backend.wcps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import eu.openeo.model.JobStatus;

public class ConvenienceHelper {

	public static String readProperties(String key) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("config.properties");

		Properties properties = new Properties();
		properties.load(stream);

		String value = properties.getProperty(key);

		return value;
	}
	
	public static String getMimeTypeFromOutput(String output) throws IOException, JSONException{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("output_formats.json");
		JSONObject outputFormats = new JSONObject(IOUtils.toString(stream, StandardCharsets.UTF_8.name()));
		JSONObject currentFormat = outputFormats.getJSONObject("formats").getJSONObject(output);
		if(currentFormat != null) {
			return currentFormat.getString("mime-type");
		}
		return MediaType.WILDCARD;		
	}

}
