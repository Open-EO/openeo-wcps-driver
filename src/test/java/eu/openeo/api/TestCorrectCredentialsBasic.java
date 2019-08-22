package eu.openeo.api;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.glassfish.jersey.internal.util.Base64;
import org.junit.jupiter.api.Test;

import eu.openeo.backend.wcps.ConvenienceHelper;

public class TestCorrectCredentialsBasic {	
	
	@Test
	void testCorrectCredentials() {
		String urlString = "";
		String userId = "guest";
		String passWd = "guest_123";
		
		String authString = userId + ":" + passWd;
	    byte[] authEncBytes = Base64.encode(authString.getBytes());
	    String authStringEnc = new String(authEncBytes);
	        
		try {
			urlString = ConvenienceHelper.readProperties("openeo-endpoint") + "/credentials/basic";
		} catch (IOException e) {
			e.printStackTrace();
			fail("Failed to read openEO endpoint url from properties");
		}
        try {
        	URL url = new URL(urlString);
        	HttpURLConnection  urlConnection = (HttpURLConnection) url.openConnection();
        	urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
        	urlConnection.connect();
        	int statusCode = urlConnection.getResponseCode();
        	String message =  urlConnection.getResponseMessage();
        	if(statusCode > 299) {
        		fail("Failed to authorize with openEO credentials basic endpoint: " + statusCode + " : " + message);
        	}else {
        		System.out.println("Success: " + statusCode + " : " + message);
        	}
		} catch (IOException e) {				
			e.printStackTrace();
			fail("failed to open url connection to openEO endpoint.");
		}
	}

}
