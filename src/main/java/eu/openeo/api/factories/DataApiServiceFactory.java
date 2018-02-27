package eu.openeo.api.factories;

import eu.openeo.api.DataApiService;
import eu.openeo.api.impl.DataApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class DataApiServiceFactory {
	private final static DataApiService service = new DataApiServiceImpl();

	public static DataApiService getDataApi() {
		return service;
	}
}
