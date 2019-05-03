package eu.openeo.backend.wcps;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;

import eu.openeo.model.Job;
import eu.openeo.model.JobFull;
import eu.openeo.model.JobStatus;

public class WCPSJobsQueueManager {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private Dao<JobFull,String> jobDao = null;
	
	public WCPSJobsQueueManager() {		
	}
	
	private void addJobToQueue(JobFull job) {
		try {
			job.setStatus(JobStatus.QUEUED);
			job.setUpdated(new Date().toGMTString());		
			jobDao.update(job);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Url url = new URL(wcpsEndpoint + "?SERVICE=WCS" + "&VERSION=2.0.1"
//			+ "&REQUEST=ProcessCoverages" + "&QUERY="
//			+ URLEncoder.encode(wcpsFactory.getWCPSString(), "UTF-8").replace("+", "%20"));
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		conn.setRequestMethod("GET");
	}
	


}
