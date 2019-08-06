package eu.openeo.backend.wcps;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class TestWCPSQueryFactory {

	@Test
	void justAnExample() {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/test.json"));
			JSONObject openEOGraph = new JSONObject(new String(encoded, StandardCharsets.UTF_8));
			WCPSQueryFactory wcpsQueryFactory = new WCPSQueryFactory(openEOGraph);
			System.out.println(wcpsQueryFactory.getWCPSString());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error during graph parsing: " + e.getMessage());
		}
	}

}
