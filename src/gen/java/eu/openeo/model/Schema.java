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
 * **DEFAULT VALUES FOR ARGUMENTS ARE NOT FORMALIZED IN THE SWAGGER 2.0
 * DEFINITION DUE TO MISSING SUPPORT FOR oneOf OR anyOf SCHEMA COMBINATIONS.**
 */
@ApiModel(description = "**DEFAULT VALUES FOR ARGUMENTS ARE NOT FORMALIZED IN THE SWAGGER 2.0 DEFINITION DUE TO MISSING SUPPORT FOR oneOf OR anyOf SCHEMA COMBINATIONS.**")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class Schema implements Serializable {
	@JsonProperty("type")
	private String type = null;
	
	@JsonProperty("format")
	private String format = null;
	
	@JsonProperty("examples")
	private String[] examples = null;
	
	@JsonProperty("items")
	private SchemaItems items = null;
	
	@JsonProperty("minItems")
	private int minItems = 0;
	
	@JsonProperty("maxItems")
	private int maxItems = 0;

	public Schema type(String type) {
		this.type = type;
		return this;
	}

	/**
	 * A short and concise description of the process argument.
	 * 
	 * @return description
	 **/
	@JsonProperty("type")
	@ApiModelProperty(required = true, value = "A short and concise description of the process argument.")
	@NotNull
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	@JsonProperty("examples")
	@ApiModelProperty(required = true, value = "Examples")
	@NotNull
	public String[] getExamples() {
		return examples;
	}

	public void setExamples(String[] examples) {
		this.examples = examples;
	}
	
	@JsonProperty("items")
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public SchemaItems getItems() {
		return items;
	}

	public void setItems(SchemaItems items) {
		this.items = items;
	}
	
	public Schema format(String format) {
		this.format = format;
		return this;
	}
	
	@JsonProperty("format")
	@ApiModelProperty(required = true, value = "Format")
	@NotNull
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}


	@JsonProperty("minItems")
	@ApiModelProperty(required = true, value = "Minimum Items as Input")
	@NotNull
	public int getMinItems() {
		return minItems;
	}

	public void setMinItems(int minItems) {
		this.minItems = minItems;
	}

	@JsonProperty("maxItems")
	@ApiModelProperty(required = true, value = "Maximum Items as Input")
	@NotNull
	public int getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(int maxItems) {
		this.maxItems = maxItems;
	}
	
	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Schema schema = (Schema) o;
		return Objects.equals(this.type, schema.type)
	        && Objects.equals(this.format, schema.format)
	        && Objects.equals(this.examples, schema.examples)
	        && Objects.equals(this.minItems, schema.minItems)
	        && Objects.equals(this.maxItems, schema.maxItems)
	        && Objects.equals(this.items, schema.items);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, format, examples, minItems, maxItems, items);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Schema {\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    format: ").append(toIndentedString(format)).append("\n");
		sb.append("    examples: ").append(toIndentedString(examples)).append("\n");
		sb.append("    minItems: ").append(toIndentedString(minItems)).append("\n");
		sb.append("    maxItems: ").append(toIndentedString(maxItems)).append("\n");
		sb.append("    items: ").append(toIndentedString(items)).append("\n");
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