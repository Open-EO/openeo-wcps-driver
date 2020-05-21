package eu.openeo.backend.wcps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import eu.openeo.model.BatchJobResponse;
import eu.openeo.model.Status;

public class JobResultDeletion implements Runnable {
	
	Logger log = LogManager.getLogger();
	private ConnectionSource connection = null;
	private Dao<BatchJobResponse, String> jobDao = null;
	
	public JobResultDeletion() {
		try {
			String dbURL = "jdbc:sqlite:" + ConvenienceHelper.readProperties("job-database");
			connection = new JdbcConnectionSource(dbURL);		
			jobDao = DaoManager.createDao(connection, BatchJobResponse.class);
			log.debug("JobResultDeletion() has been called and established a connection to the openEO DB."); 
		} catch (SQLException sqle) {
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : sqle.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (IOException ioe) {
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : ioe.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		
	}

	@Override
	public void run() {
		log.debug("Start thread to call the method deleteFiles()");
		
		try {
			this.checkResultsFilesJobToDelete();
		} catch (Exception e) {
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}		
	};
	
	private void checkResultsFilesJobToDelete() {
		try {
			File f = new File(ConvenienceHelper.readProperties("temp-dir"));			
			File[] listFiles = f.listFiles();
			BatchJobResponse job = null;
			
			for (int i=0; i<listFiles.length; i++) {
				File currentFile = listFiles[i];
				if(currentFile.isFile() && !Files.isSymbolicLink(currentFile.toPath())) {
					if(differenceTimeMinutes(currentFile.lastModified()) > Integer.parseInt(ConvenienceHelper.readProperties("temp-file-expiry"))) {
						currentFile.delete();
						String jobId = currentFile.getName().substring(0, currentFile.getName().indexOf("."));
						log.debug("The current file " + currentFile.getName() + " is expired.\nIt will be deleted and the its job's status will be set to SUBMITTED.");
						job = jobDao.queryForId(jobId);
						if (job == null) {
							log.debug("A job with the specified identifier is not available.");
						} else {
							job.setStatus(Status.SUBMITTED);
							job.setUpdated(new Date());
							jobDao.update(job);
						}
					}
				}
			}		
		} catch (SQLException sqle) {
			log.error("An error occured while performing an SQL-query: " + sqle.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : sqle.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (IOException ioe) {
			log.error("An IO error occured");
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : ioe.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}
		
	private long differenceTimeMinutes(long lastModified) {
		long timestamp = System.currentTimeMillis();
		long difftime = (timestamp - lastModified)/60000;
//		log.debug("DIFF NOW - TIMEFILE " + timestamp + " - " + lastModified +
//				" = " + difftime);
		return difftime;
	}
	
	/**
	//Recursive method to delete all outdated files of al the users
	private void checkFilesToDelete(File currentFolder) {
		File f = new File(currentFolder.getPath());
		
		File[] listFiles = f.listFiles();
//		log.debug("LIST FILES " + String.join(", ", f.list()));
		
		if(listFiles.length > 0) {
			for(int i=0; i<listFiles.length; i++) {
				File currentFile = listFiles[i];
				if(currentFile.isDirectory()) {
					System.out.println(currentFile + " is a directory");
					checkFilesToDelete(currentFile);
				} else {					
					if (differenceTimeMinutes(currentFile.lastModified()) > Integer.parseInt(ConvenienceHelper.readProperties("temp-file-expiry"))) {
						if(!currentFile.isDirectory()) {
							log.debug(currentFile + " is a file out of date");
							currentFile.delete();
						}
					}  else {
						log.debug(currentFile + "is OK");
					}
				}
			}	
		}	
	}**/
}
