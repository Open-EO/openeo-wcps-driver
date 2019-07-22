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
import eu.openeo.model.Link;
import eu.openeo.model.OneOfobjectobject;
import eu.openeo.model.ProcessException;
import eu.openeo.model.ProcessParameter;
import eu.openeo.model.ProcessReturnValue;
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
 * Process
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class Process  implements Serializable {
  @JsonProperty("id")
  private String id;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("description")
  private String description;

  @JsonProperty("categories")
  private List<String> categories = null;

  @JsonProperty("parameter_order")
  private List<String> parameterOrder = null;

  @JsonProperty("parameters")
  private Map<String, ProcessParameter> parameters = new HashMap<String, ProcessParameter>();

  @JsonProperty("returns")
  private ProcessReturnValue returns = null;

  @JsonProperty("deprecated")
  private Boolean deprecated = false;

  @JsonProperty("experimental")
  private Boolean experimental = false;

  @JsonProperty("exceptions")
  private Map<String, ProcessException> exceptions = null;

  @JsonProperty("examples")
  private List<OneOfobjectobject> examples = null;

  @JsonProperty("links")
  private List<Link> links = null;

  public Process id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of the process.
   * @return id
   **/
  @JsonProperty("id")
  @ApiModelProperty(example = "ndvi", required = true, value = "Unique identifier of the process.")
  @NotNull  @Pattern(regexp="^[A-Za-z0-9_]+$")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Process summary(String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * A short summary of what the process does.
   * @return summary
   **/
  @JsonProperty("summary")
  @ApiModelProperty(value = "A short summary of what the process does.")
  
  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public Process description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed description to fully explain the entity.  [CommonMark 0.28](http://commonmark.org/) syntax MAY be used for rich text representation. In addition to the CommonMark syntax, clients can convert process IDs that are formatted as in the following example into links instead of code blocks: &#x60;&#x60;&#x60; &#x60;&#x60;process_id()&#x60;&#x60; &#x60;&#x60;&#x60;
   * @return description
   **/
  @JsonProperty("description")
  @ApiModelProperty(required = true, value = "Detailed description to fully explain the entity.  [CommonMark 0.28](http://commonmark.org/) syntax MAY be used for rich text representation. In addition to the CommonMark syntax, clients can convert process IDs that are formatted as in the following example into links instead of code blocks: ``` ``process_id()`` ```")
  @NotNull 
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Process categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  public Process addCategoriesItem(String categoriesItem) {
    if (this.categories == null) {
      this.categories = new ArrayList<String>();
    }
    this.categories.add(categoriesItem);
    return this;
  }

  /**
   * A list of categories.
   * @return categories
   **/
  @JsonProperty("categories")
  @ApiModelProperty(value = "A list of categories.")
  
  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public Process parameterOrder(List<String> parameterOrder) {
    this.parameterOrder = parameterOrder;
    return this;
  }

  public Process addParameterOrderItem(String parameterOrderItem) {
    if (this.parameterOrder == null) {
      this.parameterOrder = new ArrayList<String>();
    }
    this.parameterOrder.add(parameterOrderItem);
    return this;
  }

  /**
   * Describes the order or the parameter for any environments that don&#39;t support named parameters. This property MUST be present for all processes with two or more parameters.
   * @return parameterOrder
   **/
  @JsonProperty("parameter_order")
  @ApiModelProperty(value = "Describes the order or the parameter for any environments that don't support named parameters. This property MUST be present for all processes with two or more parameters.")
  
  public List<String> getParameterOrder() {
    return parameterOrder;
  }

  public void setParameterOrder(List<String> parameterOrder) {
    this.parameterOrder = parameterOrder;
  }

  public Process parameters(Map<String, ProcessParameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public Process putParametersItem(String key, ProcessParameter parametersItem) {
    this.parameters.put(key, parametersItem);
    return this;
  }

  /**
   * A list of parameters that are applicable for this process. The keys of the object are the names of the parameters. They keys MUST match the following pattern: &#x60;^[A-Za-z0-9_]+$&#x60;
   * @return parameters
   **/
  @JsonProperty("parameters")
  @ApiModelProperty(required = true, value = "A list of parameters that are applicable for this process. The keys of the object are the names of the parameters. They keys MUST match the following pattern: `^[A-Za-z0-9_]+$`")
  @NotNull @Valid 
  public Map<String, ProcessParameter> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, ProcessParameter> parameters) {
    this.parameters = parameters;
  }

  public Process returns(ProcessReturnValue returns) {
    this.returns = returns;
    return this;
  }

  /**
   * Get returns
   * @return returns
   **/
  @JsonProperty("returns")
  @ApiModelProperty(required = true, value = "")
  @NotNull @Valid 
  public ProcessReturnValue getReturns() {
    return returns;
  }

  public void setReturns(ProcessReturnValue returns) {
    this.returns = returns;
  }

  public Process deprecated(Boolean deprecated) {
    this.deprecated = deprecated;
    return this;
  }

  /**
   * Specifies that the process or parameter is deprecated with the potential to   be removed in any of the next versions. It should   be transitioned out of usage as soon as possible and users   should refrain from using it in new implementations.
   * @return deprecated
   **/
  @JsonProperty("deprecated")
  @ApiModelProperty(value = "Specifies that the process or parameter is deprecated with the potential to   be removed in any of the next versions. It should   be transitioned out of usage as soon as possible and users   should refrain from using it in new implementations.")
  
  public Boolean getDeprecated() {
    return deprecated;
  }

  public void setDeprecated(Boolean deprecated) {
    this.deprecated = deprecated;
  }

  public Process experimental(Boolean experimental) {
    this.experimental = experimental;
    return this;
  }

  /**
   * Declares the process or parameter to be experimental, which means that it   is likely to change or may produce unpredictable behaviour.   Users should refrain from using it in production,   but still feel encouraged to try it out and give feedback.
   * @return experimental
   **/
  @JsonProperty("experimental")
  @ApiModelProperty(value = "Declares the process or parameter to be experimental, which means that it   is likely to change or may produce unpredictable behaviour.   Users should refrain from using it in production,   but still feel encouraged to try it out and give feedback.")
  
  public Boolean getExperimental() {
    return experimental;
  }

  public void setExperimental(Boolean experimental) {
    this.experimental = experimental;
  }

  public Process exceptions(Map<String, ProcessException> exceptions) {
    this.exceptions = exceptions;
    return this;
  }

  public Process putExceptionsItem(String key, ProcessException exceptionsItem) {
    if (this.exceptions == null) {
      this.exceptions = new HashMap<String, ProcessException>();
    }
    this.exceptions.put(key, exceptionsItem);
    return this;
  }

  /**
   * Declares any exceptions (errors) that might occur during execution of this process. MUST be used only for exceptions that stop the execution of a process and are therefore not to be used for warnings, or notices or debugging messages.  The keys define the error code and MUST match the following pattern: &#x60;^[A-Za-z0-9_]+$&#x60;  This schema follows the schema of the general openEO error list (see errors.json).
   * @return exceptions
   **/
  @JsonProperty("exceptions")
  @ApiModelProperty(value = "Declares any exceptions (errors) that might occur during execution of this process. MUST be used only for exceptions that stop the execution of a process and are therefore not to be used for warnings, or notices or debugging messages.  The keys define the error code and MUST match the following pattern: `^[A-Za-z0-9_]+$`  This schema follows the schema of the general openEO error list (see errors.json).")
  @Valid 
  public Map<String, ProcessException> getExceptions() {
    return exceptions;
  }

  public void setExceptions(Map<String, ProcessException> exceptions) {
    this.exceptions = exceptions;
  }

  public Process examples(List<OneOfobjectobject> examples) {
    this.examples = examples;
    return this;
  }

  public Process addExamplesItem(OneOfobjectobject examplesItem) {
    if (this.examples == null) {
      this.examples = new ArrayList<OneOfobjectobject>();
    }
    this.examples.add(examplesItem);
    return this;
  }

  /**
   * Examples, may be used for tests. Either &#x60;process_graph&#x60; or &#x60;arguments&#x60; must be set, never both.
   * @return examples
   **/
  @JsonProperty("examples")
  @ApiModelProperty(value = "Examples, may be used for tests. Either `process_graph` or `arguments` must be set, never both.")
  @Valid 
  public List<OneOfobjectobject> getExamples() {
    return examples;
  }

  public void setExamples(List<OneOfobjectobject> examples) {
    this.examples = examples;
  }

  public Process links(List<Link> links) {
    this.links = links;
    return this;
  }

  public Process addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<Link>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Related links, e.g. additional external documentation for this process.
   * @return links
   **/
  @JsonProperty("links")
  @ApiModelProperty(value = "Related links, e.g. additional external documentation for this process.")
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
    Process process = (Process) o;
    return Objects.equals(this.id, process.id) &&
        Objects.equals(this.summary, process.summary) &&
        Objects.equals(this.description, process.description) &&
        Objects.equals(this.categories, process.categories) &&
        Objects.equals(this.parameterOrder, process.parameterOrder) &&
        Objects.equals(this.parameters, process.parameters) &&
        Objects.equals(this.returns, process.returns) &&
        Objects.equals(this.deprecated, process.deprecated) &&
        Objects.equals(this.experimental, process.experimental) &&
        Objects.equals(this.exceptions, process.exceptions) &&
        Objects.equals(this.examples, process.examples) &&
        Objects.equals(this.links, process.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, summary, description, categories, parameterOrder, parameters, returns, deprecated, experimental, exceptions, examples, links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Process {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    categories: ").append(toIndentedString(categories)).append("\n");
    sb.append("    parameterOrder: ").append(toIndentedString(parameterOrder)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    returns: ").append(toIndentedString(returns)).append("\n");
    sb.append("    deprecated: ").append(toIndentedString(deprecated)).append("\n");
    sb.append("    experimental: ").append(toIndentedString(experimental)).append("\n");
    sb.append("    exceptions: ").append(toIndentedString(exceptions)).append("\n");
    sb.append("    examples: ").append(toIndentedString(examples)).append("\n");
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
