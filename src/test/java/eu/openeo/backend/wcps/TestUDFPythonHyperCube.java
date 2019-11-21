package eu.openeo.backend.wcps;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class TestUDFPythonHyperCube {

	@Test
	void testHyperCubeProcessingWithPythonUDF() {
		byte[] codeBlob;
		byte[] dataBlob;
		try {
			codeBlob = Files.readAllBytes(Paths.get("src/test/resources/hypercube_ndvi.py"));
			dataBlob = Files.readAllBytes(Paths.get("src/test/resources/hypercube.json"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return;
		}
		UDFFactory udfFactory = new UDFFactory("python", new String(codeBlob, StandardCharsets.UTF_8), "EPSG:32734",
				"Test_HyperCube", new String(dataBlob, StandardCharsets.UTF_8));

		JSONObject udfDescriptor = udfFactory.getUdfDescriptor();

		byte[] udfBlob = udfDescriptor.toString(4).getBytes(StandardCharsets.UTF_8);

		System.out.println("Size of UDF blob = " + udfBlob.length);

		File file = new File("src/test/resources/udf_example_hypercube_ndvi.json");

		FileOutputStream fis;
		try {
			fis = new FileOutputStream(file);
			fis.write(udfBlob, 0, udfBlob.length);
			fis.flush();
			fis.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			fail();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return;
		}

		URL url;
		try {
			url = new URL("http://10.8.246.140:5000/udf");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			fail();
			return;
		}
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return;
		}

		try (OutputStream os = con.getOutputStream()) {
			os.write(udfBlob, 0, udfBlob.length);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
			return;
		}
		try(BufferedReader br = new BufferedReader(
				  new InputStreamReader(con.getInputStream(), "utf-8"))) {
				    StringBuilder response = new StringBuilder();
				    String responseLine = null;
				    while ((responseLine = br.readLine()) != null) {
				        response.append(responseLine.trim());
				    }
			JSONObject udfResponse = new JSONObject(response.toString());
			JSONArray hyperCubes = udfResponse.getJSONArray("hypercubes");
			JSONObject firstHyperCube = hyperCubes.getJSONObject(0);
			byte[] firstHyperCubeBlob = firstHyperCube.toString(2).getBytes(StandardCharsets.UTF_8);
			File firstHyperCubeFile = new File("src/test/resources/udf_example_hypercube_ndvi_result.json");

			FileOutputStream firstHyperCubeStream;
			try {
				firstHyperCubeStream = new FileOutputStream(firstHyperCubeFile);
				firstHyperCubeStream.write(firstHyperCubeBlob, 0, firstHyperCubeBlob.length);
				firstHyperCubeStream.flush();
				firstHyperCubeStream.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				fail();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				fail();
				return;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			try {
				System.out.println("Error stream content below this line:");
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"));
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				JSONObject responseJSON = new JSONObject(response.toString());
				System.out.println(responseJSON.toString(4));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			fail();
		}

	}

}
