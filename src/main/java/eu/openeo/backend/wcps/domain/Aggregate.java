package eu.openeo.backend.wcps.domain;

public class Aggregate {
	
	private String axis;
	private String operator;
	
	public Aggregate(String axis, String operator) {
		this.axis = axis;
		this.operator = operator;
	}

	public String getAxis() {
		return axis;
	}

	public String getOperator() {
		return operator;
	}	
	

}
