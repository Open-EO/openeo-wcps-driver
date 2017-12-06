package eu.openeo.backend.wcps;

import org.junit.jupiter.api.Test;

import eu.openeo.backend.wcps.WCPSQueryFactory;

import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TestWCPSQueryFactory {
	
	@Test
	void justAnExample() {
		JSONParser parser = new JSONParser();
		try {
			JSONObject openEOGraph = (JSONObject) parser.parse(new FileReader("src/test/resources/use_case_1_test.json"));
			WCPSQueryFactory wcpsQueryFactory =  new WCPSQueryFactory(openEOGraph);
			System.out.println(wcpsQueryFactory.getWCPSString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        System.out.println("This test method should be run");
    }

}
