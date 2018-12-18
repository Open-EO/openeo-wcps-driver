/*
 * OpenEO API
 * The OpenEO API specification for interoperable cloud-based processing of large Earth observation datasets. **This early draft version is incomplete and intended for working on a prototype and a proof of concept.** Things that are currently missing particularly include:   * Authentication and authorization with OAuth 2.0,   * how results of computations can be downloaded,   * how data is streamed into UDFs and how the output of UDFs is returned,   * how services are organized as microservices,   * how payments can be handled,  * how resources (e.g. process graphs) can be shared,  * and how OpenSearch is interfaced
 *
 * OpenAPI spec version: 0.0.2
 * Contact: marius.appel@uni-muenster.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package eu.openeo.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A process graph defines an executable process, i.e. one process or a
 * combination of chained processes including specific arguments.
 */
@ApiModel(description = "A process graph defines an executable process, i.e. one process or a combination of chained processes including specific arguments.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ProcessGraph implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 196584606548756019L;

	@JsonProperty("process_id")
	@JsonBackReference
	private String processId = null;
	
	@JsonProperty("imagery")
	private void unpackNested(Map<String,Object> args) {
        this.args = new ArgSet();
        this.args.setArgs(args);
	}
	
	private ArgSet args = null;

	public ProcessGraph processId(String processId) {
		this.processId = processId;
		return this;
	}

	/**
	 * The unique identifier of the process.
	 * 
	 * @return processId
	 **/
	@JsonProperty("process_id")
	@ApiModelProperty(required = true, value = "The unique identifier of the process.")
	@NotNull
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public ProcessGraph args(ArgSet args) {
		this.args = args;
		return this;
	}

	/**
	 * Get args
	 * 
	 * @return args
	 **/
	@JsonProperty("imagery")
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public ArgSet getArgs() {
		return args;
	}

	public void setArgs(ArgSet args) {
		this.args = args;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ProcessGraph processGraph = (ProcessGraph) o;
		return Objects.equals(this.processId, processGraph.processId) && Objects.equals(this.args, processGraph.args);
	}

	@Override
	public int hashCode() {
		return Objects.hash(processId, args);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ProcessGraph {\n");

		sb.append("    processId: ").append(toIndentedString(processId)).append("\n");
		sb.append("    imagery: ").append(toIndentedString(args)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
