package eu.openeo.backend.wcps;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class TestUDFR {

	@Test
	void justAnExample() {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/udfR.json"));
			URL url = new URL ("http://10.8.246.128:5555/udf");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			try(OutputStream os = con.getOutputStream()) {
			    os.write(encoded, 0, encoded.length);
			}
			try(BufferedReader br = new BufferedReader(
					  new InputStreamReader(con.getInputStream(), "utf-8"))) {
					    StringBuilder response = new StringBuilder();
					    String responseLine = null;
					    while ((responseLine = br.readLine()) != null) {
					        response.append(responseLine.trim());
					    }
					    System.out.println(response.toString());
					}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during graph parsing: " + e.getMessage());
		}
	}

}
