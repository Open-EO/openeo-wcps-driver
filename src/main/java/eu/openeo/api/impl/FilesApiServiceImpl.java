package eu.openeo.api.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;
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

    	String authUserId = securityContext.getUserPrincipal().getName();
    	if(userId.equals(authUserId)) {
    		try {    			
    			String userFolderPath = ConvenienceHelper.readProperties("temp-dir") + authUserId;
    			log.debug("Current user folder path: " + userFolderPath);
    			
    			File userFolder = new File(userFolderPath);
    			
    			File[] listFiles = userFolder.listFiles();
    			
    			log.debug("Number of files in the user folder " + userFolderPath + ": " + listFiles.length);
    			
    			//String pathToShow = "files/" + authUserId + "/";
    			JSONObject[] jsonFileList = new JSONObject[listFiles.length];    					
    			
    			for (int i=0; i < listFiles.length; i++) {
    				File currentFile = listFiles[i];   				
    			    				
    				JSONObject currentFileJson = new JSONObject()
    						.put("path", currentFile.getName())
    						.put("size", currentFile.length())
    						.put("modified", currentFile.lastModified());
    						
    				jsonFileList[i] = currentFileJson;
    				
    			}
    			
    			String jsonString = new JSONObject()
		                  .put("files", jsonFileList)
		                  .put("links", "[]").toString();
    			
    			log.debug("Files' list of the folder " + jsonString);
    			return Response.status(201).entity(jsonString).build();
    		} catch (FileNotFoundException e) {
    			StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
				
				String jsonStringError = new JSONObject()
						.put("id", userId)
						.put("code", "File not found exception")
						.put("message", e.getMessage())
						.put("links", "[]").toString();
				
    			return Response.status(404).entity(jsonStringError).build();
    		} catch (IOException e) {
    			StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
				
				String jsonStringError = new JSONObject()
						.put("id", userId)
						.put("code", "Input/Output exception")
						.put("message", e.getMessage())
						.put("links", "[]").toString();
				
    			return Response.status(500).entity(jsonStringError).build();

    		}
    	} else {
    		log.error(new String("The user " + authUserId + " is not authorized to access the path /files/" + userId));
    		return Response.status(403).entity(new String("The user " + authUserId + " is not authorized to access the path /files/" + userId)).build();
    	}
		
    }
    @Override
    public Response filesUserIdPathDelete( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, String path, SecurityContext securityContext) throws NotFoundException {

    	String authUserId = securityContext.getUserPrincipal().getName();
    	if(userId.equals(authUserId)) {
    		try {    			
    			String fileToDeletePath = ConvenienceHelper.readProperties("temp-dir") + authUserId +  "/" + path;
    			log.debug("File to delete: " + fileToDeletePath);
    			
    			File fileToDelete = new File(fileToDeletePath);
    			
    			fileToDelete.delete();
    			log.debug("File " + fileToDeletePath + " deleted successfully");
    			
    			return Response.status(204).entity(new String("File " + fileToDeletePath + " deleted successfully")).build();
    			    			
    		} catch (FileNotFoundException e) {
    			StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
				
				String jsonStringError = new JSONObject()
						.put("id", userId)
						.put("code", "File not found exception")
						.put("message", e.getMessage())
						.put("links", "[]").toString();
				
    			return Response.status(404).entity(jsonStringError).build();
    		} catch (IOException e) {
    			StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
				
				String jsonStringError = new JSONObject()
						.put("id", userId)
						.put("code", "Input/Output exception")
						.put("message", e.getMessage())
						.put("links", "[]").toString();
				
    			return Response.status(500).entity(jsonStringError).build();

    		}
    	} else {
    		log.error(new String("The user " + authUserId + " is not authorized to access the path /files/" + userId));
    		return Response.status(403).entity(new String("The user " + authUserId + " is not authorized to access the path /files/" + userId)).build();
    	}
    }
    @Override
    public Response filesUserIdPathGet( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, String path, SecurityContext securityContext) throws NotFoundException {
    	String authUserId = securityContext.getUserPrincipal().getName();
    	if(userId.equals(authUserId)) {
    		try {    			
    			String fileToDownloadPath = ConvenienceHelper.readProperties("temp-dir") + authUserId +  "/" + path;
    			log.debug("File to download: " + fileToDownloadPath);
    			
    			File file = new File(fileToDownloadPath);
				byte[] bytesArray = new byte[(int) file.length()]; 
	
				FileInputStream fis = new FileInputStream(file);
				fis.read(bytesArray);
				fis.close();
    			
    			log.debug("File " + fileToDownloadPath + " downloaded successfully");
    			
    			MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
    		    String mimeType = fileTypeMap.getContentType(file.getName());
    		    
    		    log.debug("The file " + fileToDownloadPath + "is of type " + mimeType);
    			
    			return Response.ok(bytesArray, mimeType).build();
    			    			    			
    		} catch (FileNotFoundException e) {
    			StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
				
				String jsonStringError = new JSONObject()
						.put("id", userId)
						.put("code", "File not found exception")
						.put("message", e.getMessage())
						.put("links", "[]").toString();
				
    			return Response.status(404).entity(jsonStringError).build();
    		} catch (IOException e) {
    			StringBuilder builder = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					builder.append(element.toString() + "\n");
				}
				log.error(builder.toString());
				
				String jsonStringError = new JSONObject()
						.put("id", userId)
						.put("code", "Input/Output exception")
						.put("message", e.getMessage())
						.put("links", "[]").toString();
				
    			return Response.status(500).entity(jsonStringError).build();

    		}
    	} else {
    		log.error(new String("The user " + authUserId + " is not authorized to access the path /files/" + userId));
    		return Response.status(403).entity(new String("The user " + authUserId + " is not authorized to access the path /files/" + userId)).build();
    	}    
    	
    }
    
    @Override
    public Response filesUserIdPathPut( @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")String userId, String path, File body, SecurityContext securityContext) throws NotFoundException {
    	String authUserId = securityContext.getUserPrincipal().getName();
    	if(userId.equals(authUserId)) {
	    	File file;
			try {
				
				log.debug("Path from URL " + path);
				
				String filePath = ConvenienceHelper.readProperties("temp-dir") + authUserId;
				log.debug("Path associated to the user " + authUserId + ": " + filePath);
				file = new File(filePath);
				boolean pathCreated = file.exists();
				if(!pathCreated) {
					pathCreated= file.mkdir();
				}
				if(pathCreated) {
					log.debug("The following path was successfully created: " + file.getAbsolutePath());
				}else {
					return Response.serverError().entity("The requested resource path could not be created: /files/" + authUserId + "/" + path).build();
				}
				
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
			   	
			   	String pathToShow = "files/" + authUserId;
			   	
			   	String jsonString = new JSONObject()
		                  .put("path", (pathToShow + "/" + path))
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
