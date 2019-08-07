package eu.openeo.backend.wcps;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

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
	
	public JobScheduler() {
		try {
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
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
			//TODO replace here with triggering of actual processing
			
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
		}
		
	}

	@Override
	public void jobExecuted(JobEvent jobEvent) {
		// TODO Auto-generated method stub
		
	}

}
