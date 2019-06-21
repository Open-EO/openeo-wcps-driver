package eu.openeo.api.factories;

import eu.openeo.api.CollectionApiService;
import eu.openeo.api.impl.CollectionApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class DataApiServiceFactory {
	private final static CollectionApiService service = new CollectionApiServiceImpl();

	public static CollectionApiService getDataApi() {
		return service;
	}
}
