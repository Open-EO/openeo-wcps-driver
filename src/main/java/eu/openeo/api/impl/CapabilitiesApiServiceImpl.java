package eu.openeo.api.impl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import eu.openeo.api.ApiResponseMessage;
import eu.openeo.api.CapabilitiesApiService;
import eu.openeo.api.NotFoundException;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class CapabilitiesApiServiceImpl extends CapabilitiesApiService {
	@Override
	public Response capabilitiesGet(SecurityContext securityContext) throws NotFoundException {
		JSONArray endpointList = new JSONArray();
		endpointList.put(new String("/capabilities"));
		endpointList.put(new String("/capabilities/output_formats"));
		endpointList.put(new String("/data"));
		endpointList.put(new String("/data/{product_id}"));
		endpointList.put(new String("/execute"));
		endpointList.put(new String("/jobs"));
		endpointList.put(new String("/jobs/{job_id}"));
		endpointList.put(new String("/jobs/{job_id}/download"));
		endpointList.put(new String("/processes"));
		endpointList.put(new String("/processes/{process_id}"));
		return Response.ok(endpointList.toString(4), MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response capabilitiesOutputFormatsGet(SecurityContext securityContext) throws NotFoundException {
		JSONObject outputFormatsShell = new JSONObject();
		outputFormatsShell.put("default", "json");
		JSONObject outputFormats = new JSONObject();
		JSONObject jsonFormat = new JSONObject();
		jsonFormat.put("gdal_name", "JSON");
		outputFormats.put("json", jsonFormat);
		JSONObject gTiffFormat = new JSONObject();
		gTiffFormat.put("gdal_name", "GTiff");
		outputFormats.put("tiff", gTiffFormat);
		JSONObject pngFormat = new JSONObject();
		pngFormat.put("gdal_name", "PNG");
		outputFormats.put("png", pngFormat);
		JSONObject netcdfFormat = new JSONObject();
		netcdfFormat.put("gdal_name", "netCDF");
		outputFormats.put("netcdf", netcdfFormat);
		JSONObject jpegFormat = new JSONObject();
		jpegFormat.put("gdal_name", "JPEG");
		outputFormats.put("jpeg", jpegFormat);
		JSONObject gifFormat = new JSONObject();
		gifFormat.put("gdal_name", "GIF");
		outputFormats.put("gif", gifFormat);
		JSONObject csvFormat = new JSONObject();
		csvFormat.put("gdal_name", "CSV");
		outputFormats.put("csv", csvFormat);		
		outputFormatsShell.put("formats", outputFormats);
		return Response.ok(outputFormatsShell.toString(4), MediaType.APPLICATION_JSON).build();
	}

	@Override
	public Response capabilitiesOutputFormatsOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().build();
	}

	@Override
	public Response capabilitiesServicesGet(SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
	}

	@Override
	public Response capabilitiesServicesOptions(SecurityContext securityContext) throws NotFoundException {
		return Response.ok().build();
	}
}
