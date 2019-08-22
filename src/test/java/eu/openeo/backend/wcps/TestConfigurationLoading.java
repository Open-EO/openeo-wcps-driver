package eu.openeo.backend.wcps;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class TestProperties {

	@Test
	void testReadingOfProperties() {
		String url;
		try {
			url = readProperties("job-database");
			if (url == null) {
				fail("failed to read enpoint from properties");
			} else {
				System.out.println(url);
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("failed to read enpoint from properties with exception: " + e.getMessage());
		}

	}

	public String readProperties(String key) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("config.properties");

		Properties properties = new Properties();
		properties.load(stream);

		String value = properties.getProperty(key);

		return value;
	}

}
