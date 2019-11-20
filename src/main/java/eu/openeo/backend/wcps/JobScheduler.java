package eu.openeo.backend.wcps;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import eu.openeo.backend.wcps.events.JobEvent;
import eu.openeo.backend.wcps.events.JobEventListener;
import eu.openeo.model.BatchJobResponse;
import eu.openeo.model.Status;

public class JobScheduler implements JobEventListener{
	
	Logger log = Logger.getLogger(this.getClass());
	
	private Dao<BatchJobResponse,String> jobDao = null;
	private ConnectionSource connection = null;
	private String wcpsEndpoint = null;
	private JSONObject processGraphJSON = new JSONObject();
	private JSONObject processGraphAfterUDF = null;
	
	public JobScheduler() {
		try {
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
			wcpsEndpoint = ConvenienceHelper.readProperties("wcps-endpoint");
			connection =  new JdbcConnectionSource(dbURL);
			jobDao = DaoManager.createDao(connection, BatchJobResponse.class);
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: sqle.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		} catch (IOException ioe) {
			log.error("An error occured while reading properties file: " + ioe.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: ioe.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		}
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
				URL url2;
				WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON);
				url2 = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages" + "&QUERY="
						+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
				executeWCPS(url2, job, wcpsFactory);
				String udfNode = getUDFNode();
				int udfNodeIndex = 0;

				for(int j = 0; j < nodesSortedArray.length(); j++) {
					if (nodesSortedArray.getString(j).equals(udfNode)) {
						udfNodeIndex=j;
					}
				}

				String udfCubeCoverageID = "udf_"; // Get ID from Rasdaman where UDF generated Cube is stored
				JSONObject loadUDFCube = new JSONObject();
				JSONObject loadUDFCubearguments = new JSONObject();

				loadUDFCubearguments.put("id", udfCubeCoverageID + job.getId());
				udfCubeCoverageID = loadUDFCubearguments.getString("id");
				loadUDFCubearguments.put("spatial_extent", JSONObject.NULL);
				loadUDFCubearguments.put("temporal_extent", JSONObject.NULL);

				loadUDFCube.put("process_id", "load_collection");
				loadUDFCube.put("arguments", loadUDFCubearguments);

				processGraphAfterUDF.put(udfNode, loadUDFCube);

				for(int k = udfNodeIndex+1; k < nodesSortedArray.length(); k++) {
					processGraphAfterUDF.put(nodesSortedArray.getString(k), processGraphJSON.getJSONObject(nodesSortedArray.getString(k)));
				}
				URL urlUDF;
				WCPSQueryFactory wcpsFactoryUDF = new WCPSQueryFactory(processGraphAfterUDF);
				urlUDF = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages" + "&QUERY="
						+ URLEncoder.encode(wcpsFactoryUDF.getWCPSString(), "UTF-8").replace("+", "%20"));
				executeWCPS(urlUDF, job, wcpsFactoryUDF);
			}
			
			else {
				URL url1;
				WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON);
				url1 = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages" + "&QUERY="
						+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
				executeWCPS(url1, job, wcpsFactory);
				}
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			StringBuilder builder = new StringBuilder();
			for( StackTraceElement element: sqle.getStackTrace()) {
				builder.append(element.toString()+"\n");
			}
			log.error(builder.toString());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void jobExecuted(JobEvent jobEvent) {
		// TODO Auto-generated method stub
		
	}
	
	private void executeWCPS(URL url, BatchJobResponse job, WCPSQueryFactory wcpsQuery) {
					
		job.setUpdated(new Date());
		try {
			jobDao.update(job);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		JSONObject linkProcessGraph = new JSONObject();
		linkProcessGraph.put("job_id", job.getId());
		linkProcessGraph.put("updated", job.getUpdated());

		String filePath = null;
		try {
			filePath = ConvenienceHelper.readProperties("temp-dir");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		} catch (IOException e) {
			log.error("\"An error occured when downloading the file of the current job: " + e.getMessage());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
