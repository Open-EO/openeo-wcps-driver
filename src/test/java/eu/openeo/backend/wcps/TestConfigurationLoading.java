package eu.openeo.backend.wcps;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class TestConfigurationLoading {

	@Test
	void testReadingOfProperties() {
		String providerName;
		String providerUrl;
		String pathToDatbase;
		String openEoUrl;
		String wcpsUrl;
		String pathToTmpStore;
		Long authExpiryInMinutes = new Long(0);
		Long tmpFileExpiryInMinutes = new Long(0);
		try {
			providerName = ConvenienceHelper.readProperties("provider-name");
			providerUrl = ConvenienceHelper.readProperties("provider-url");
			pathToDatbase = ConvenienceHelper.readProperties("job-database");
			openEoUrl = ConvenienceHelper.readProperties("openeo-endpoint");
			wcpsUrl = ConvenienceHelper.readProperties("wcps-endpoint");
			pathToTmpStore = ConvenienceHelper.readProperties("temp-dir");
			authExpiryInMinutes = Long.parseLong(ConvenienceHelper.readProperties("auth-expiry"));
			tmpFileExpiryInMinutes = Long.parseLong(ConvenienceHelper.readProperties("temp-file-expiry"));
			if (providerName == null) {
				fail("failed to read provider name from properties");
			} else {
				System.out.println("Provider Name: " + providerName);
			}
			if (providerUrl == null) {
				fail("failed to read provider url from properties");
			} else {
				System.out.println("Provider Url: " + providerUrl);
			}
			if (pathToDatbase == null) {
				fail("failed to read data base path from properties");
			} else {
				System.out.println("DB-Path: " + pathToDatbase);
			}
			if (openEoUrl == null) {
				fail("failed to read openEO endpoint url from properties");
			} else {
				System.out.println("openEO endpoint: " + openEoUrl);
			}
			if (wcpsUrl == null) {
				fail("failed to read wcps endpoint url from properties");
			} else {
				System.out.println("wcps endpoint: " + wcpsUrl);
			}
			if (pathToTmpStore == null) {
				fail("failed to read temp directory path from properties");
			} else {
				System.out.println("tmp path: " + pathToTmpStore);
			}
			if (tmpFileExpiryInMinutes == 0) {
				fail("failed to read temp file expiry time from properties");
			} else {
				System.out.println("tmp file expiry in minutes: " + tmpFileExpiryInMinutes);
			}
			if (authExpiryInMinutes == 0) {
				fail("failed to read auth expiry time from properties");
			} else {
				System.out.println("auth expiry in minutes: " + authExpiryInMinutes);
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("failed to read all necessary properties from conf file with exception: " + e.getMessage());
		}

	}

}
