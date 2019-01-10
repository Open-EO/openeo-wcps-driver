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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Defines and describes a process including it&#39;s expected input arguments.
 */
@ApiModel(description = "Defines and describes a process including it's expected input arguments.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ProcessDescription implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3101125036875386069L;

	@JsonProperty("name")
	private String processId = null;

	@JsonProperty("description")
	private String description = null;
	
	@JsonProperty("summary")
	private String summary = null;

	@JsonProperty("link")
	private String link = null;

	@JsonProperty("parameters")
	private Map<String, ProcessDescriptionArgs> parameters = null;
	
	@JsonProperty("returns")
	private ProcessDescriptionReturns returns = null;
	
	@JsonProperty("min_parameters")
	private int min_parameters = 0;
	
	

	public ProcessDescription processId(String processId) {
		this.processId = processId;
		return this;
	}

	/**
	 * The unique identifier of the process.
	 * 
	 * @return processId
	 **/
	@JsonProperty("name")
	@ApiModelProperty(required = true, value = "The unique identifier of the process.")
	@NotNull
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public ProcessDescription description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * A short and concise description of what the process does and how the output
	 * looks like.
	 * 
	 * @return description
	 **/
	@JsonProperty("description")
	@ApiModelProperty(required = true, value = "A short and concise description of what the process does and how the output looks like.")
	@NotNull
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonProperty("summary")
	@ApiModelProperty(required = true, value = "A short line for the process")
	@NotNull
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public ProcessDescription link(String link) {
		this.link = link;
		return this;
	}
	
	@JsonProperty("min_parameters")
	@ApiModelProperty(required = true, value = "Minimum number of parameters required as input")
	@NotNull
	public int getMinParameters() {
		return min_parameters;
	}

	public void setMinParameters(int min_parameters) {
		this.min_parameters = min_parameters;
	}


	/**
	 * Reference to an external process definition if the process has been defined
	 * over different back ends within OpenEO
	 * 
	 * @return link
	 **/
	@JsonProperty("link")
	@ApiModelProperty(value = "Reference to an external process definition if the process has been defined over different back ends within OpenEO")
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public ProcessDescription parameters(Map<String, ProcessDescriptionArgs> parameters) {
		this.parameters = parameters;
		return this;
	}

	public ProcessDescription putParametersItem(String key, ProcessDescriptionArgs parametersItem) {
		if (this.parameters == null) {
			this.parameters = new HashMap<String, ProcessDescriptionArgs>();
		}
		this.parameters.put(key, parametersItem);
		return this;
	}

	/**
	 * Get args
	 * 
	 * @return args
	 **/
	@JsonProperty("parameters")
	@ApiModelProperty(required = true, value = "The format of the input Parameters required")
	public Map<String, ProcessDescriptionArgs> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, ProcessDescriptionArgs> parameters) {
		this.parameters = parameters;
	}
		
	
	public ProcessDescription returns(ProcessDescriptionReturns returns) {
		this.returns = returns;
		return this;
	}

	
	/**
	 * Get returns
	 * 
	 * @return returns
	 **/
	@JsonProperty("returns")
	@ApiModelProperty(required = true, value = "The format of the returned output")
	@NotNull
	public ProcessDescriptionReturns getReturns() {
		return returns;
	}

	public void setReturns(ProcessDescriptionReturns returns) {
		this.returns = returns;
	}
	
	
	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ProcessDescription processDescription = (ProcessDescription) o;
		return Objects.equals(this.processId, processDescription.processId)
				&& Objects.equals(this.description, processDescription.description)
				&& Objects.equals(this.link, processDescription.link)
				&& Objects.equals(this.parameters, processDescription.parameters)
				&& Objects.equals(this.returns, processDescription.returns)
				&& Objects.equals(this.summary, processDescription.summary)
		        && Objects.equals(this.min_parameters, processDescription.min_parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(processId, description, link, parameters, returns, summary, min_parameters);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ProcessDescription {\n");

		sb.append("    processId: ").append(toIndentedString(processId)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    link: ").append(toIndentedString(link)).append("\n");
		sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
		sb.append("    returns: ").append(toIndentedString(returns)).append("\n");
		sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
		sb.append("    min_parameters: ").append(toIndentedString(min_parameters)).append("\n");
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