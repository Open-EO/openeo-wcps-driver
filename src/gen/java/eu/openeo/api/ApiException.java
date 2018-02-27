package eu.openeo.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ApiException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3711056109953996803L;
	@SuppressWarnings("unused")
	private int code;

	public ApiException(int code, String msg) {
		super(msg);
		this.code = code;
	}
}
