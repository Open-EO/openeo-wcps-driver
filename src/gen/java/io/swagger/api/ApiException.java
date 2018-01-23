package io.swagger.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public class ApiException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2174637728997742359L;
	private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.setCode(code);
    }
	public int getCode() {
		return code;
	}
	private void setCode(int code) {
		this.code = code;
	}
}
