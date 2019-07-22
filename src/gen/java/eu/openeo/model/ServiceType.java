/*
 * openEO API
 * The openEO API specification for interoperable cloud-based processing of large Earth observation datasets.   **Make sure to take account of several global API specifications**, which are not (fully) covered in this specification:  * [Cross-Origin Resource Sharing (CORS) support](https://open-eo.github.io/openeo-api/v/0.4.2/cors/index.html) to allow browser-based access to the API.  * [Error handling](https://open-eo.github.io/openeo-api/v/0.4.2/errors/index.html)  * Unless otherwise stated the API works *case sensitive*.
 *
 * The version of the OpenAPI document: 0.4.2
 * Contact: openeo@list.tuwien.ac.at
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package eu.openeo.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import eu.openeo.model.Argument;
import eu.openeo.model.Link;
import eu.openeo.model.Variable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * ServiceType
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ServiceType  implements Serializable {
  @JsonProperty("parameters")
  private Map<String, Argument> parameters = null;

  @JsonProperty("attributes")
  private Map<String, Argument> attributes = null;

  @JsonProperty("variables")
  private List<Variable> variables = null;

  @JsonProperty("links")
  private List<Link> links = null;

  public ServiceType parameters(Map<String, Argument> parameters) {
    this.parameters = parameters;
    return this;
  }

  public ServiceType putParametersItem(String key, Argument parametersItem) {
    if (this.parameters == null) {
      this.parameters = new HashMap<String, Argument>();
    }
    this.parameters.put(key, parametersItem);
    return this;
  }

  /**
   * List of supported parameters for configuration.
   * @return parameters
   **/
  @JsonProperty("parameters")
  @ApiModelProperty(value = "List of supported parameters for configuration.")
  @Valid 
  public Map<String, Argument> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Argument> parameters) {
    this.parameters = parameters;
  }

  public ServiceType attributes(Map<String, Argument> attributes) {
    this.attributes = attributes;
    return this;
  }

  public ServiceType putAttributesItem(String key, Argument attributesItem) {
    if (this.attributes == null) {
      this.attributes = new HashMap<String, Argument>();
    }
    this.attributes.put(key, attributesItem);
    return this;
  }

  /**
   * List of supported attributes.
   * @return attributes
   **/
  @JsonProperty("attributes")
  @ApiModelProperty(value = "List of supported attributes.")
  @Valid 
  public Map<String, Argument> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Argument> attributes) {
    this.attributes = attributes;
  }

  public ServiceType variables(List<Variable> variables) {
    this.variables = variables;
    return this;
  }

  public ServiceType addVariablesItem(Variable variablesItem) {
    if (this.variables == null) {
      this.variables = new ArrayList<Variable>();
    }
    this.variables.add(variablesItem);
    return this;
  }

  /**
   * List of supported process graph variables.
   * @return variables
   **/
  @JsonProperty("variables")
  @ApiModelProperty(value = "List of supported process graph variables.")
  @Valid 
  public List<Variable> getVariables() {
    return variables;
  }

  public void setVariables(List<Variable> variables) {
    this.variables = variables;
  }

  public ServiceType links(List<Link> links) {
    this.links = links;
    return this;
  }

  public ServiceType addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<Link>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Additional links related to this service type, e.g. more information about the parameters, attributes or options to access the created services.
   * @return links
   **/
  @JsonProperty("links")
  @ApiModelProperty(value = "Additional links related to this service type, e.g. more information about the parameters, attributes or options to access the created services.")
  @Valid 
  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceType serviceType = (ServiceType) o;
    return Objects.equals(this.parameters, serviceType.parameters) &&
        Objects.equals(this.attributes, serviceType.attributes) &&
        Objects.equals(this.variables, serviceType.variables) &&
        Objects.equals(this.links, serviceType.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parameters, attributes, variables, links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceType {\n");
    
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    attributes: ").append(toIndentedString(attributes)).append("\n");
    sb.append("    variables: ").append(toIndentedString(variables)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
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
