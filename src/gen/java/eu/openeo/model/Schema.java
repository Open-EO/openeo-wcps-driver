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
	/**
	 * 
	 */
	private static final long serialVersionUID = 7911304482900804094L;

	@JsonProperty("type")
	private String type = null;
	
	@JsonProperty("format")
	private String format = null;
	
	@JsonProperty("properties")
	private Map<String, SchemaProperties> properties = null;
	
	@JsonProperty("additionalProperties")
	private Boolean addProp = null;
	
	@JsonProperty("required")
	private String[] required = null;
	
	@JsonProperty("description")
	private String description = null;
	
	@JsonProperty("examples")
	private String[] examples = null;
	
	@JsonProperty("example")
	private String[] example = null;
	
	@JsonProperty("items")
	private Items items = null;
	
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
	@ApiModelProperty(value = "Examples")
	@NotNull
	public String[] getExamples() {
		return examples;
	}

	public void setExamples(String[] examples) {
		this.examples = examples;
	}
	
	
	@JsonProperty("example")
	@ApiModelProperty(value = "Example")
	@NotNull
	public String[] getExample() {
		return example;
	}

	public void setExample(String[] example) {
		this.example = example;
	}
	
	
	@JsonProperty("items")
	@ApiModelProperty(value = "")
	@NotNull
	public Items getItems() {
		return items;
	}

	public void setItems(Items items) {
		this.items = items;
	}
	
	@JsonProperty("additionalProperties")
	@ApiModelProperty(value = "")
	@NotNull
	public Boolean getAddProp() {
		return addProp;
	}

	public void setAddProp(Boolean addProp) {
		this.addProp = addProp;
	}
	
	@JsonProperty("required")
	@ApiModelProperty(value = "")
	@NotNull
	public String[] getRequired() {
		return required;
	}

	public void setRequired(String[] required) {
		this.required = required;
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
	
	public Schema putPropertiesItem(String key, SchemaProperties propertiesItem) {
		if (this.properties == null) {
			this.properties = new HashMap<String, SchemaProperties>();
		}
		this.properties.put(key, propertiesItem);
		return this;
	}

	@JsonProperty("properties")
	@ApiModelProperty(value = "Map of properties")
	@NotNull
	public Map<String, SchemaProperties> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, SchemaProperties> properties) {
		this.properties = properties;
	}

	@JsonProperty("minItems")
	@ApiModelProperty(value = "Minimum Items as Input")
	@NotNull
	public int getMinItems() {
		return minItems;
	}

	public void setMinItems(int minItems) {
		this.minItems = minItems;
	}

	
	@JsonProperty("description")
	@ApiModelProperty(value = "A short and concise description of the process argument.")
	@NotNull
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonProperty("maxItems")
	@ApiModelProperty(value = "Maximum Items as Input")
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
	        && Objects.equals(this.description, schema.description)
	        && Objects.equals(this.examples, schema.examples)
	        && Objects.equals(this.example, schema.example)
	        && Objects.equals(this.minItems, schema.minItems)
	        && Objects.equals(this.maxItems, schema.maxItems)
	        && Objects.equals(this.items, schema.items);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, format, description, examples, example, minItems, maxItems, items);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Schema {\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    format: ").append(toIndentedString(format)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    examples: ").append(toIndentedString(examples)).append("\n");
		sb.append("    example: ").append(toIndentedString(example)).append("\n");
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