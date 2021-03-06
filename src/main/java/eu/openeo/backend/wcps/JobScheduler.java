package eu.openeo.backend.wcps;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.dao.DaoManager;
//import com.j256.ormlite.jdbc.JdbcConnectionSource;
//import com.j256.ormlite.support.ConnectionSource;

import eu.openeo.backend.wcps.events.JobEvent;
import eu.openeo.backend.wcps.events.JobEventListener;
import eu.openeo.backend.wcps.events.UDFEvent;
import eu.openeo.backend.wcps.events.UDFEventListener;
import eu.openeo.model.BatchJobResponse;
import eu.openeo.model.Status;

public class JobScheduler implements JobEventListener, UDFEventListener{
	
	Logger log = LogManager.getLogger();
	
	private Dao<BatchJobResponse,String> jobDao = null;
//	private ConnectionSource connection = null;
	private String wcpsEndpoint = null;
	private JSONObject processGraphJSON = new JSONObject();
	private JSONObject processGraphAfterUDF = null;
	
	public JobScheduler(Dao<BatchJobResponse,String> jobDao, String wcpsEndpoint) {
		this.jobDao = jobDao;
		this.wcpsEndpoint = wcpsEndpoint;
//		try {
////			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
////			wcpsEndpoint = ConvenienceHelper.readProperties("wcps-endpoint");
////			connection =  new JdbcConnectionSource(dbURL);
////			jobDao = DaoManager.createDao(connection, BatchJobResponse.class);
////		} catch (SQLException sqle) {
////			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
////			StringBuilder builder = new StringBuilder();
////			for( StackTraceElement element: sqle.getStackTrace()) {
////				builder.append(element.toString()+"\n");
////			}
////			log.error(builder.toString());
//		} catch (IOException ioe) {
//			log.error("An error occured while reading properties file: " + ioe.getMessage());
//			StringBuilder builder = new StringBuilder();
//			for( StackTraceElement element: ioe.getStackTrace()) {
//				builder.append(element.toString()+"\n");
//			}
//			log.error(builder.toString());
//		}
	}

	@Override
	public void jobQueued(JobEvent jobEvent) {
		BatchJobResponse job = null;
		try {
			job = jobDao.queryForId(jobEvent.getJobId());
			if(job == null) {
				log.error("A job with the specified identifier is not available.");
			}
			log.debug("The following job was retrieved: \n" + job.toString());
						
			processGraphJSON = (JSONObject) job.getProcessGraph();
			JSONArray nodesSortedArray = getProcessesNodesSequence();
			JSONArray processesSequence = new JSONArray();
			
			for (int i = 0; i < nodesSortedArray.length(); i++) {
				processesSequence.put(processGraphJSON.getJSONObject(nodesSortedArray.getString(i)).getString("process_id"));
			}
						
			if (processesSequence.toString().contains("run_udf")) {
				log.info("Found process_graph containing udf");
				String udfNodeKey = getUDFNode();
				int udfNodeIndex = 0;

				for(int j = 0; j < nodesSortedArray.length(); j++) {
					if (nodesSortedArray.getString(j).equals(udfNodeKey)) {
						udfNodeIndex=j;
					}
				}
				
				JSONObject udfNode = processGraphJSON.getJSONObject(nodesSortedArray.getString(udfNodeIndex));
				
				String runtime = udfNode.getJSONObject("arguments").getString("runtime");
				String version = "";
				try {
					version = udfNode.getJSONObject("arguments").getString("version");
				}catch(JSONException e) {
					log.warn("no version for udf specified. Will fall back on default version of specified environment.");
				}
				String udfCode = udfNode.getJSONObject("arguments").getString("udf");
				log.debug("runtime: " + runtime);
				log.debug("udf: " + udfCode);
				
				String udfCubeCoverageID = "udfCube_"; // Get ID from Rasdaman where UDF generated Cube is stored
				JSONObject loadUDFCube = new JSONObject();
				JSONObject loadUDFCubearguments = new JSONObject();

				loadUDFCubearguments.put("id", udfCubeCoverageID + job.getId().replace('-', '_'));
				udfCubeCoverageID = loadUDFCubearguments.getString("id");
				loadUDFCubearguments.put("spatial_extent", JSONObject.NULL);
				loadUDFCubearguments.put("temporal_extent", JSONObject.NULL);

				loadUDFCube.put("process_id", "load_collection");
				loadUDFCube.put("arguments", loadUDFCubearguments);
				
				processGraphAfterUDF = new JSONObject();

				processGraphAfterUDF.put(udfNodeKey, loadUDFCube);

				for(int k = udfNodeIndex+1; k < nodesSortedArray.length(); k++) {
					processGraphAfterUDF.put(nodesSortedArray.getString(k), processGraphJSON.getJSONObject(nodesSortedArray.getString(k)));
				}		
				
				JSONObject udfDescriptor = null;
				
				try {					
					//Get code block for UDF
					InputStream codeStream = null;					
					if(udfCode.startsWith("http")) {
						String relativeFileLocation = udfCode.substring(udfCode.indexOf("files")+6);
						String fileToDownloadPath = ConvenienceHelper.readProperties("temp-dir") + relativeFileLocation;
		    			log.debug("Grepping code from udf object from uploaded file resource: " + fileToDownloadPath);		    			
		    			codeStream = new FileInputStream(fileToDownloadPath);		    			
					}else {
						log.debug("Grepping code from udf object as byte array.");
						codeStream = new ByteArrayInputStream(udfCode.getBytes(StandardCharsets.UTF_8));
					}
					//Get data block for UDF
					WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON);
					wcpsFactory.setOutputFormat("gml");
					
					URL url = new URL(ConvenienceHelper.readProperties("wcps-endpoint") + "?SERVICE=WCS" + "&VERSION=2.0.1"
							+ "&REQUEST=ProcessCoverages" + "&QUERY="
							+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					//Compile code and data into one common JSON object.
					UDFFactory udfFactory = new UDFFactory(runtime, codeStream, conn.getInputStream()); 
					udfDescriptor = udfFactory.getUdfDescriptor();
					log.trace("UDF JSON: " + udfDescriptor.toString(2));
				}catch (IOException e) {
					log.error("An error occured when streaming in input to UDF: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				}
				
				String service_url = null;
				// Select correct parameters for execution environment of UDF
				if(runtime.toLowerCase().equals("python")&&version.toLowerCase().equals("openeo")) {
					runtime = "python";
					service_url = ConvenienceHelper.readProperties("python-udf-endpoint");
				}
				else if(runtime.toLowerCase().equals("python")&&version.toLowerCase().equals("candela")) {
					runtime = "python";
					service_url = ConvenienceHelper.readProperties("candela-python-udf-endpoint");
				}
				else if(runtime.toLowerCase().equals("r")) {
					runtime = "r";
					service_url = ConvenienceHelper.readProperties("r-udf-endpoint");
				}else {
					log.error("The requested runtime is not available!");
				}
				
				// Find correct udf engine endpoint based on selected execution environment
				URL udfServiceEndpoint = null;
				try {
					udfServiceEndpoint = new URL(service_url + "/udf");
				} catch (MalformedURLException e) {
					log.error("An error occured when generating udf service endpoint url: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				}
				log.info(udfServiceEndpoint);
				// open http connection to udf execution endpoint 
				HttpURLConnection con = null;
				try {
					log.info("Sending UDF to UDF endpoint.");
					con = (HttpURLConnection) udfServiceEndpoint.openConnection();
					con.setRequestMethod("POST");
					con.setRequestProperty("Content-Type", "application/json; utf-8");
					con.setRequestProperty("Accept", "application/json");
					con.setDoOutput(true);
				} catch (IOException e) {
					log.error("An error occured when connecting to udf service endpoint: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				}
				
				String inputHyperCubeDebugPath = ConvenienceHelper.readProperties("temp-dir")+"udf_result/input_" + job.getId() + ".json";
				saveHyperCubeToDisk(udfDescriptor, inputHyperCubeDebugPath);
				
				// stream UDF in form of json hypercube object to udf endpoint via http post method 
				try (OutputStream postUDFStream = con.getOutputStream()) {
					byte[] udfBlob = udfDescriptor.toString().getBytes(StandardCharsets.UTF_8);
					//log.debug(new String(udfBlob, StandardCharsets. UTF_8));
					postUDFStream.write(udfBlob, 0, udfBlob.length);
					postUDFStream.close();
					log.info("Posting UDF to UDF Service endpoint.");
				} catch (IOException e) {
					log.error("\"An error occured when posting to udf service endpoint: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				}
				
				// get result from udf endpoint in form of a json hypercube document.
				try(BufferedReader udfResultReader = new BufferedReader(
						  new InputStreamReader(con.getInputStream(), "utf-8"))) {
						    StringBuilder response = new StringBuilder();
						    String responseLine = null;
						    while ((responseLine = udfResultReader.readLine()) != null) {
						        response.append(responseLine.trim());
						    }
						    udfResultReader.close();
				    log.info("Received result from UDF endpoint.");
					JSONObject udfResponse = new JSONObject(response.toString());
					JSONArray hyperCubes = udfResponse.getJSONArray("hypercubes");

					JSONObject firstHyperCube = hyperCubes.getJSONObject(0);

					String outputHyperCubeDebugPath = ConvenienceHelper.readProperties("temp-dir")+"udf_result/output_" + job.getId() + ".json";
					saveHyperCubeToDisk(firstHyperCube, outputHyperCubeDebugPath);
					// convert hypercube json object into netcdf file and save to tempory disk
					String netCDFPath = ConvenienceHelper.readProperties("temp-dir")+"udf_result/" + job.getId() + ".nc";
					new HyperCubeFactory().writeHyperCubeToNetCDFBandAsVariable(firstHyperCube, udfResponse.getString("proj"), netCDFPath);
					JSONArray dimensionsArray = firstHyperCube.getJSONArray("dimensions");
					Iterator iterator = dimensionsArray.iterator();
					Boolean containsMultiBands = false;
					
					// Re-import result from UDF in rasdaman using wcst_import tool
					try {
						ProcessBuilder importProcessBuilder = new ProcessBuilder();
						importProcessBuilder.command("bash", "-c", "/tmp/openeo/udf_result/import_udf_multi.sh " + netCDFPath);
						log.debug(netCDFPath);
						Process importProcess = importProcessBuilder.start();
						StringBuilder importProcessLogger = new StringBuilder();

						BufferedReader importProcessLogReader = new BufferedReader(
								new InputStreamReader(importProcess.getInputStream()));

						String outLine;
						while ((outLine = importProcessLogReader.readLine()) != null) {
							importProcessLogger.append(outLine + "\n");
						}
						
						BufferedReader importProcessLogErrorReader = new BufferedReader(
								new InputStreamReader(importProcess.getErrorStream()));

						String line;
						while ((line = importProcessLogErrorReader.readLine()) != null) {
							importProcessLogger.append(line + "\n");
						}

						int exitValue = importProcess.waitFor();
						if (exitValue == 0) {
							log.info("Import to rasdaman succeeded!");
							log.debug(importProcessLogger.toString());
						} else {
							log.error("Import to rasdaman failed!");
							log.error(importProcessLogger.toString());
						}
						importProcessLogReader.close();
						importProcessLogErrorReader.close();
					}catch(IOException e) {
						log.error("\"An io error occured when launching import to cube: " + e.getMessage());
						StringBuilder builder = new StringBuilder();
						for (StackTraceElement element : e.getStackTrace()) {
							builder.append(element.toString() + "\n");
						}
						log.error(builder.toString());
					}catch(InterruptedException e) {
						log.error("\"An error occured when launching import to cube: " + e.getMessage());
						StringBuilder builder = new StringBuilder();
						for (StackTraceElement element : e.getStackTrace()) {
							builder.append(element.toString() + "\n");
						}
						log.error(builder.toString());
					}
					// continue processing of process_graph after the UDF
					log.debug(processGraphAfterUDF);
					WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphAfterUDF);
					URL urlUDF = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages" + "&QUERY="
							+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
					executeWCPS(urlUDF, job, wcpsFactory);

				} catch (UnsupportedEncodingException e) {
					log.error("An error occured when encoding response of udf service endpoint " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				} catch (IOException e) {
					log.error("An error occured during execution of UDF: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
					logErrorStream(con.getErrorStream());
				}
			}
			
			else {
				WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON);
				URL url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages" + "&QUERY="
						+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
				executeWCPS(url, job, wcpsFactory);
			}
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: sqle.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void jobExecuted(JobEvent jobEvent) {
	//TODO check if this is still needed. Currently this event chain is unused...	
	}
	
	private void executeWCPS(URL url, BatchJobResponse job, WCPSQueryFactory wcpsQuery) {
					
		job.setUpdated(new Date());
		try {
			jobDao.update(job);
		} catch (SQLException e) {
			log.error("An error occured when updating job in database: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}

		JSONObject linkProcessGraph = new JSONObject();
		linkProcessGraph.put("job_id", job.getId());
		linkProcessGraph.put("updated", job.getUpdated());

		String filePath = null;
		try {
			filePath = ConvenienceHelper.readProperties("temp-dir");
		} catch (IOException e) {
			log.error("An error occured when reading properties file " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		String fileName = job.getId() + "." + wcpsQuery.getOutputFormat();
		log.debug("The output file will be saved here: \n" + (filePath + fileName).toString());

		try (BufferedInputStream in = new BufferedInputStream(url.openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(filePath + fileName)) {
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
			log.debug("File saved correctly");
			in.close();
			fileOutputStream.close();
		} catch (IOException e) {
			log.error("An error occured when downloading the file of the current job: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}

		job.setStatus(Status.FINISHED);
		job.setUpdated(new Date());
		try {
			jobDao.update(job);
		} catch (SQLException e) {
			log.error("\"An error occured when updating job in database: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		log.debug("The following job was set to status finished: \n" + job.toString());
	}
	
	private JSONArray getProcessesNodesSequence() {
		JSONArray nodesArray = new JSONArray();
		JSONArray nodesSortedArray = new JSONArray();
		
		String saveNode = getSaveNode();
		JSONArray saveNodeAsArray = new JSONArray();
		saveNodeAsArray.put(saveNode);
		nodesArray.put(saveNodeAsArray);
		
		for (int n = 0; n < nodesArray.length(); n++) {
			for (int a = 0; a < nodesArray.getJSONArray(n).length(); a++) {
				JSONArray fromNodeOfProcess = getFromNodeOfCurrentKey(nodesArray.getJSONArray(n).getString(a));
				if (fromNodeOfProcess.length()>0) {
					nodesArray.put(fromNodeOfProcess);
				}
				else if (fromNodeOfProcess.length()==0) {
					nodesSortedArray.put(nodesArray.getJSONArray(n).getString(a));
				}
			}
		}
		
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}
		
		nodesArray.remove(nodesArray.length()-1);
		for (int i = nodesArray.length()-1; i>0; i--) {
			if (nodesArray.getJSONArray(i).length()>0) {				
				for (int a = 0; a < nodesArray.getJSONArray(i).length(); a++) {
					nodesSortedArray.put(nodesArray.getJSONArray(i).getString(a));
				}
			}
		}		
				
		nodesSortedArray.put(saveNode);
		for (int i = 0; i < nodesSortedArray.length(); i++) {
			for (int j = i + 1 ; j < nodesSortedArray.length(); j++) {
				if (nodesSortedArray.get(i).equals(nodesSortedArray.get(j))) {
					nodesSortedArray.remove(j);
				}
			}
		}		
		
		return nodesSortedArray;
	}
	
	private String getUDFNode() {
		for (String processNodeKey : processGraphJSON.keySet()) {			
			JSONObject processNode = processGraphJSON.getJSONObject(processNodeKey);
			String processID = processNode.getString("process_id");
			if (processID.equals("run_udf")) {
				log.debug("UDF Process Node key found is: " + processNodeKey);				
				return processNodeKey;
			}
		}
		return null;
	}
	
	private String getSaveNode() {
		for (String processNodeKey : processGraphJSON.keySet()) {			
			JSONObject processNode = processGraphJSON.getJSONObject(processNodeKey);
			String processID = processNode.getString("process_id");
			if (processID.equals("save_result")) {
				log.debug("Save Process Node key found is: " + processNodeKey);				
				return processNodeKey;
			}
		}
		return null;
	}
	
	private JSONArray getFromNodeOfCurrentKey(String currentNode){
		JSONObject nextNodeName = new JSONObject();
		JSONArray fromNodes = new JSONArray();
		String nextFromNode = null;
		JSONObject currentNodeProcessArguments =  processGraphJSON.getJSONObject(currentNode).getJSONObject("arguments");
		for (String argumentsKey : currentNodeProcessArguments.keySet()) {
			if (argumentsKey.contentEquals("data")) {
				if (currentNodeProcessArguments.get("data") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("data").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("data").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}
				else if (currentNodeProcessArguments.get("data") instanceof JSONArray) {
					JSONArray reduceData = currentNodeProcessArguments.getJSONArray("data");
					for(int a = 0; a < reduceData.length(); a++) {
						if (reduceData.get(a) instanceof JSONObject) {
							for (String fromKey : reduceData.getJSONObject(a).keySet()) {
								if (fromKey.contentEquals("from_node")) {
									nextFromNode = reduceData.getJSONObject(a).getString("from_node");
									fromNodes.put(nextFromNode);
								}
							}
						}
					}
				}
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("band1")) {
				if (currentNodeProcessArguments.get("band1") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("band1").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("band1").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
			else if (argumentsKey.contentEquals("band2")) {
				if (currentNodeProcessArguments.get("band2") instanceof JSONObject) {
					for (String fromKey : currentNodeProcessArguments.getJSONObject("band2").keySet()) {
						if (fromKey.contentEquals("from_node")) {
							nextFromNode = currentNodeProcessArguments.getJSONObject("band2").getString("from_node");
							fromNodes.put(nextFromNode);
						}
					}
				}				
				nextNodeName.put(currentNode, fromNodes);				
			}
		}
		return fromNodes;		
	}

	@Override
	public void udfExecuted(UDFEvent jobEvent) {
		BatchJobResponse job = null;
		try {
			job = jobDao.queryForId(jobEvent.getJobId());
			if(job == null) {
				log.error("A job with the specified identifier is not available.");
			}
			log.debug("The following job was retrieved: \n" + job.toString());
			//TODO receive resulting json object from UDF container
			//TODO import resulting UDF object into rasdaman
			
			WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphAfterUDF);
			URL urlUDF = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
			executeWCPS(urlUDF, job, wcpsFactory);
		}catch (SQLException e) {
			log.error("An error occured while performing an SQL-query: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		} catch (MalformedURLException e) {
			log.error("An error occured: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		} catch (UnsupportedEncodingException e) {
			log.error("An error occured: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		}		
	}
	
	private void saveHyperCubeToDisk(JSONObject hyperCubeJSON, String filePath) {
		log.debug("Saving hypercube to disk: " + filePath);
		byte[] firstHyperCubeBlob = hyperCubeJSON.toString(2).getBytes(StandardCharsets.UTF_8);
		File firstHyperCubeFile = new File(filePath);
		FileOutputStream firstHyperCubeStream;
		// store json hypercube as file on disk
		try {
			firstHyperCubeStream = new FileOutputStream(firstHyperCubeFile);
			firstHyperCubeStream.write(firstHyperCubeBlob, 0, firstHyperCubeBlob.length);
			firstHyperCubeStream.flush();
			firstHyperCubeStream.close();
			//TODO fire udf finished event here!
		} catch (FileNotFoundException e1) {
			log.error("File not found: " + e1.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e1.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		} catch (IOException e) {
			log.error("An error occured during writing of file: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: e.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		}
	}
	
	private void logErrorStream(InputStream errorStream) {
		try {
			log.debug("Error stream content below this line:");
			BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, "utf-8"));
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			br.close();
			JSONObject responseJSON = new JSONObject(response.toString());
			log.debug(responseJSON.toString(4));
		} catch (UnsupportedEncodingException e) {
			log.error("An error occured when encoding response of udf service endpoint " + e.getMessage());
			StringBuilder builderNested = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builderNested.append(element.toString() + "\n");
			}
			log.error(builderNested.toString());
		} catch (IOException e) {
			log.error("An error occured when receiving error stream from udf service endpoint " + e.getMessage());
			StringBuilder builderNested = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builderNested.append(element.toString() + "\n");
			}
			log.error(builderNested.toString());
		}
	}

}
