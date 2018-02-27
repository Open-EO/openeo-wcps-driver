package eu.openeo.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class NotFoundException extends ApiException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5945492158311083054L;
	@SuppressWarnings("unused")
	private int code;

	public NotFoundException(int code, String msg) {
		super(code, msg);
		this.code = code;
	}
}
