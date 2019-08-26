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
					
			URL url;
			JSONObject processGraphJSON = (JSONObject) job.getProcessGraph();
			WCPSQueryFactory wcpsFactory = new WCPSQueryFactory(processGraphJSON);
			job.setUpdated(new Date());
			jobDao.update(job);
			
			url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1" + "&REQUEST=ProcessCoverages" + "&QUERY="
					+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));

			JSONObject linkProcessGraph = new JSONObject();
			linkProcessGraph.put("job_id", job.getId());
			linkProcessGraph.put("updated", job.getUpdated());
			
			String filePath = ConvenienceHelper.readProperties("temp-dir");
			String fileName = job.getId() + "." + wcpsFactory.getOutputFormat();
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
			jobDao.update(job);
			log.debug("The following job was set to status finished: \n" + job.toString());
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

}
