package eu.openeo.backend.wcps.domain;

import java.util.Vector;

public class Aggregate {

	private String axis;
	private String operator;
	private Vector<String> params;

	public Aggregate(String axis, String operator, Vector<String> params) {
		this.axis = axis;
		this.operator = operator;
		this.params = params;
	}

	public String getAxis() {
		return axis;
	}

	public String getOperator() {
		return operator;
	}

	public Vector<String> getParams() {
		return params;
	}

}
