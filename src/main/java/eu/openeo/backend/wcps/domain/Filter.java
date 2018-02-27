package eu.openeo.backend.wcps.domain;

public class Filter {

	private String axis;
	private String lowerBound;
	private String upperBound;

	public Filter(String axis, String lowerBound, String upperBound) {
		this.axis = axis;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public Filter(String axis, String lowerBound) {
		this.axis = axis;
		this.lowerBound = lowerBound;
	}

	public String getAxis() {
		return axis;
	}

	public String getLowerBound() {
		return lowerBound;
	}

	public String getUpperBound() {
		return upperBound;
	}

}
