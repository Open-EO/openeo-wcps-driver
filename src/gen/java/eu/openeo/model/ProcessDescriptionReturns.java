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
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * **DEFAULT VALUES FOR ARGUMENTS ARE NOT FORMALIZED IN THE SWAGGER 2.0
 * DEFINITION DUE TO MISSING SUPPORT FOR oneOf OR anyOf SCHEMA COMBINATIONS.**
 */
@ApiModel(description = "**DEFAULT VALUES FOR ARGUMENTS ARE NOT FORMALIZED IN THE SWAGGER 2.0 DEFINITION DUE TO MISSING SUPPORT FOR oneOf OR anyOf SCHEMA COMBINATIONS.**")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class ProcessDescriptionReturns implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8950925769020643959L;

	@JsonProperty("description")
	private String description = null;
	
	@JsonProperty("schema")
	private Schema schema =  null;

		
//	public ProcessDescriptionReturns description(String description) {
//		this.description = description;
//		return this;
//	}

	/**
	 * A short and concise description of the process argument.
	 * 
	 * @return description
	 **/
	@JsonProperty("description")
	@ApiModelProperty(required = true, value = "A short and concise description of the process argument.")
	@NotNull
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
//	public ProcessDescriptionReturns schema(Schema schema) {
//		this.schema = schema;
//		return this;
//	}
	
	@JsonProperty("schema")
	@ApiModelProperty(required = true, value = "Schema for input parameter")
	@NotNull
	public Schema getSchema() {
		return schema;
	}
	
	public void setSchema(Schema schema) {
		this.schema = schema;
	}
	
	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ProcessDescriptionReturns returns = (ProcessDescriptionReturns) o;
		return Objects.equals(this.description, returns.description)
			&& Objects.equals(this.schema, returns.schema);
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, schema);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ProcessDescriptionReturns {\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
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