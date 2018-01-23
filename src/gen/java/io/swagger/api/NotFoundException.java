package io.swagger.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public class NotFoundException extends ApiException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8000903005649088467L;
	private int code;
    public NotFoundException (int code, String msg) {
        super(code, msg);
        this.setCode(code);
    }
	public int getCode() {
		return code;
	}
	private void setCode(int code) {
		this.code = code;
	}
}
