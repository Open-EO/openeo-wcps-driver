package eu.openeo.backend.wcps.events;

import java.util.EventObject;

public class UDFEvent extends EventObject {
	
	private static final long serialVersionUID = 8813946588128115189L;
	private String jobId;

	public UDFEvent(Object source, String jobId) {
		super(source);
		this.jobId = jobId;
	}

	public String getJobId() {
		return jobId;
	}

}
