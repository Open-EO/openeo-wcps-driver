package eu.openeo.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.validation.constraints.Pattern;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import eu.openeo.api.FilesApiService;
import eu.openeo.api.NotFoundException;
import eu.openeo.backend.wcps.ConvenienceHelper;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class FilesApiServiceImpl extends FilesApiService {
	
	Logger log = Logger.getLogger(this.getClass());
//	log.debug("Is this user part of Eurac?: " + securityContext.isUserInRole("EURAC"));
//	log.error("No information on authentication found on request for jobs!!!");
//	return Response.status(404).entity(new String("A job with the specified identifier is not available."))
//				.build();
//	return Response.status(204).entity("The job has been successfully deleted.")
//			.header("Access-Control-Expose-Headers", "OpenEO-Identifier, OpenEO-Costs")
//			.header("OpenEO-Identifier", storedBatchJob.getId()).build();
//	return Response.serverError().entity("An error occured while performing an SQL-query: " + sqle.getMessage())
//			.build();	
	
    @Override
    public Response filesUserIdGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
    	// ll
    	
//    	log.debug("Path from URL " + path);
//		
//		String filePath = ConvenienceHelper.readProperties("temp-dir") + authUserId +"/files_uploaded";
		
//		{
//			  "files": [
//			    {
//			      "path": "test.txt",
//			      "size": 182,
//			      "modified": "2015-10-20T17:22:10Z"
//			    },
//			    {
//			      "path": "test.tif",
//			      "size": 183142,
//			      "modified": "2017-01-01T09:36:18Z"
//			    },
//			    {
//			      "path": "Sentinel2/S2A_MSIL1C_20170819T082011_N0205_R121_T34KGD_20170819T084427.zip",
//			      "size": 4183353142,
//			      "modified": "2018-01-03T10:55:29Z"
//			    }
//			  ],
//			  "links": []
//			}
    	
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response filesUserIdPathDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, String path, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
    	// rm
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response filesUserIdPathGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, String path, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
    	// wget
        return Response.status(501).entity(new String("This API feature is not supported by the back-end.")).build();
    }
    @Override
    public Response filesUserIdPathPut( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, String path, File body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
    	// mkdir css/my_shape_file/shape1.shp
    	String authUserId = securityContext.getUserPrincipal().getName();
    	if(userId.equals(authUserId)) {
	    	File file;
			try {
				
				log.debug("Path from URL " + path);
				
				String filePath = ConvenienceHelper.readProperties("temp-dir") + authUserId +"/files_uploaded";
				log.debug("Path associated to the user " + authUserId + ": " + filePath);
				file = new File(filePath);
				file.mkdir();
				
				log.debug("File to write " + filePath + "/" + path);
			    InputStream is = new FileInputStream(body);
			    OutputStream os = new FileOutputStream(filePath + "/" + path);			    
			    
			    byte[] data = new byte[(int) body.length()];
			    int length;
			    while ((length = is.read(data)) > 0) {
		            os.write(data, 0, length);
		        }
			    
			   	os.close();
			   	is.close();
			   	
			   	String jsonString = new JSONObject()
		                  .put("path", (filePath + "/" + path))
		                  .put("size", body.length())
		                  .put("modified", java.time.Clock.systemUTC().instant()).toString();

				return Response.status(201).entity(jsonString).build();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				log.error("File not found:" + path);
				StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
				return Response.status(404).entity(new String("The file requested not found"))
						.build();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("An error occurred during folder's creation.");
				return Response.serverError().build();
			} 	    	
	        
    	} else {
    		log.error(new String("The user " + authUserId + " is not authorized to access the path /files/" + userId));
    		return Response.status(403).entity(new String("The user " + authUserId + " is not authorized to access the path /files/" + userId)).build();
    	}
    }
}
