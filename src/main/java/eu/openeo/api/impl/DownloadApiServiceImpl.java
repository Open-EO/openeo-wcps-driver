package eu.openeo.api.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.validation.constraints.Pattern;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import eu.openeo.api.DownloadApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.ConvenienceHelper;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class DownloadApiServiceImpl extends DownloadApiService {
	
	Logger log = Logger.getLogger(this.getClass());

	@Override
	public Response downloadFileNameGet(@Pattern(regexp = "^[A-Za-z0-9_\\-\\.~]+$") String fileName,
			SecurityContext securityContext) throws NotFoundException {
			
		//TODO read file from disk tmp directory ConvenienceHelper.... with file input stream
		byte[] response = null;
		try {
			String outputFormat = ConvenienceHelper.getMimeTypeFromRasName(fileName.substring(fileName.indexOf(".") +1));
			log.debug("File download was requested:" + fileName + " of mime type: " + outputFormat);	
			String path = ConvenienceHelper.readProperties("temp-dir");			
			File userFile = new File(path + fileName);			
			response = IOUtils.toByteArray(new FileInputStream(userFile));
			log.debug("File found and converted in bytes for download");
			return Response.ok(response).type(outputFormat).header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.error("File not found:" + fileName);
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return Response.status(404).entity(new String("The file requested not found"))
					.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("IOEXception error");
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
			return Response.serverError().entity(new String("An IOException occurred"))
					.build();
		}
		
//		return Response.ok("helloworld", "application/text")
//				.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs").build();
	}
}
