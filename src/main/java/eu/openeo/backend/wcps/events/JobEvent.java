package eu.openeo.backend.wcps.events;

import java.util.EventObject;

import eu.openeo.model.BatchJobResponse;

public class JobEvent extends EventObject {
	
	private static final long serialVersionUID = 8813946588128115189L;
	private String jobId;

	public JobEvent(Object source, String jobId) {
		super(source);
		this.jobId = jobId;
	}

	public String getJobId() {
		return jobId;
	}

}
