package eu.openeo.backend.wcps;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

public class TestUDFResultImport {

	@Test
	void testHyperCubeProcessingWithRUDF() {
		try {
			String netCDFPath = "udf_5f272f8f-6ff1-41de-84d8-7cc9b4ed4cb1.nc";
			
			ProcessBuilder importProcessBuilder = new ProcessBuilder();					
			importProcessBuilder.command("bash", "-c", "/tmp/openeo/udf_result/import_udf.sh " + netCDFPath);
			
			Process importProcess = importProcessBuilder.start();

			StringBuilder importProcessLogger = new StringBuilder();

			BufferedReader outReader = new BufferedReader(
					new InputStreamReader(importProcess.getInputStream()));

			String outLine;
			while ((outLine = outReader.readLine()) != null) {
				importProcessLogger.append(outLine + "\n");
			}
			
			BufferedReader errorReader = new BufferedReader(
					new InputStreamReader(importProcess.getErrorStream()));

			String line;
			while ((line = errorReader.readLine()) != null) {
				importProcessLogger.append(line + "\n");
			}

			int exitValue = importProcess.waitFor();
			if (exitValue == 0) {
				System.out.println("Import to rasdaman succeeded!");
				System.out.println(importProcessLogger.toString());
			} else {
				System.err.println("Import to rasdaman failed!");
				System.out.println(importProcessLogger.toString());
				fail();
			}
		}catch(IOException e) {
			System.out.println("\"An io error occured when launching import to cube: " + e.getMessage());
			e.printStackTrace();
			fail();
		}catch(InterruptedException e) {
			System.out.println("\"An error occured when launching import to cube: " + e.getMessage());
			e.printStackTrace();
			fail();
		}

	}

}
