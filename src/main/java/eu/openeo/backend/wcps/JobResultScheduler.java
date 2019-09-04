package eu.openeo.backend.wcps;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class JobResultScheduler
 */
public class JobResultScheduler extends GenericServlet {
	private static final long serialVersionUID = 1L;
	
	Logger log = Logger.getLogger(this.getClass());
	private ScheduledExecutorService scheduler;
       
    /**
     * @see GenericServlet#GenericServlet()
     */
    public JobResultScheduler() {
        super();
    }

	/**
	 * @see Servlet#service(ServletRequest request, ServletResponse response)
	 */
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
	}

	@Override
	public void init() {
		try {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			Runnable command = new JobResultDeletion();	
			
			//Official schedule time get from the property files
			TimeUnit unit = TimeUnit.MINUTES;
			long period = Integer.parseInt(ConvenienceHelper.readProperties("servlet-remove-file-expiry"));
			
			//Test schedul time		
			//TimeUnit unit = TimeUnit.SECONDS;
			//long period = 30; // Put here the value in the properties file
			
			log.debug("Run the scheduled servlet to delete outdated files...");
			scheduler.scheduleAtFixedRate(command, 0, period, unit);
		} catch (IOException ioe) {
			log.error("An IO error occured");
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : ioe.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
	}

	@Override
	public void destroy() {
		scheduler.shutdownNow();		
	}

}
