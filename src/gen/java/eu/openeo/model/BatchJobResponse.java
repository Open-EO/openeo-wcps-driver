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
import eu.openeo.model.OneOfstringjobError;
import eu.openeo.model.ProcessNode;
import eu.openeo.model.Status;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Defines full metadata of batch jobs that have been submitted by users.
 */
@ApiModel(description = "Defines full metadata of batch jobs that have been submitted by users.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class BatchJobResponse  implements Serializable {
  @JsonProperty("id")
  private String id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("process_graph")
  private Map<String, ProcessNode> processGraph = new HashMap<String, ProcessNode>();

  @JsonProperty("status")
  private Status status = Status.SUBMITTED;

  @JsonProperty("progress")
  private BigDecimal progress;

  @JsonProperty("error")
  private OneOfstringjobError error = null;

  @JsonProperty("submitted")
  private Date submitted;

  @JsonProperty("updated")
  private Date updated;

  @JsonProperty("plan")
  private String plan;

  @JsonProperty("costs")
  private BigDecimal costs;

  @JsonProperty("budget")
  private BigDecimal budget;

  public BatchJobResponse id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of a job that is generated by the back-end during job submission. MUST match the specified pattern.
   * @return id
   **/
  @JsonProperty("id")
  @ApiModelProperty(example = "a3cca2b2aa1e3b5b", required = true, value = "Unique identifier of a job that is generated by the back-end during job submission. MUST match the specified pattern.")
  @NotNull  @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BatchJobResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A short description to easily distinguish entities.
   * @return title
   **/
  @JsonProperty("title")
  @ApiModelProperty(example = "NDVI based on Sentinel 2", value = "A short description to easily distinguish entities.")
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public BatchJobResponse description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed description to fully explain the entity.  [CommonMark 0.28](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
   **/
  @JsonProperty("description")
  @ApiModelProperty(example = "Deriving minimum NDVI measurements over pixel time series of Sentinel 2", value = "Detailed description to fully explain the entity.  [CommonMark 0.28](http://commonmark.org/) syntax MAY be used for rich text representation.")
  
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BatchJobResponse processGraph(Map<String, ProcessNode> processGraph) {
    this.processGraph = processGraph;
    return this;
  }

  public BatchJobResponse putProcessGraphItem(String key, ProcessNode processGraphItem) {
    this.processGraph.put(key, processGraphItem);
    return this;
  }

  /**
   * A process graph defines a graph-like structure as a connected set of executable processes. Each key is a unique identifier (node id) that is used to refer to the process in the graph.
   * @return processGraph
   **/
  @JsonProperty("process_graph")
  @ApiModelProperty(example = "{\"dc\":{\"process_id\":\"load_collection\",\"arguments\":{\"id\":\"Sentinel-2\",\"spatial_extent\":{\"west\":16.1,\"east\":16.6,\"north\":48.6,\"south\":47.2},\"temporal_extent\":[\"2018-01-01\",\"2018-02-01\"]}},\"bands\":{\"process_id\":\"filter_bands\",\"description\":\"Filter and order the bands. The order is important for the following reduce operation.\",\"arguments\":{\"data\":{\"from_node\":\"dc\"},\"bands\":[\"B08\",\"B04\",\"B02\"]}},\"evi\":{\"process_id\":\"reduce\",\"description\":\"Compute the EVI. Formula: 2.5 * (NIR - RED) / (1 + NIR + 6*RED + -7.5*BLUE)\",\"arguments\":{\"data\":{\"from_node\":\"bands\"},\"dimension\":\"spectral\",\"reducer\":{\"callback\":{\"nir\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_argument\":\"data\"},\"index\":0}},\"red\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_argument\":\"data\"},\"index\":1}},\"blue\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_argument\":\"data\"},\"index\":2}},\"sub\":{\"process_id\":\"subtract\",\"arguments\":{\"data\":[{\"from_node\":\"nir\"},{\"from_node\":\"red\"}]}},\"p1\":{\"process_id\":\"product\",\"arguments\":{\"data\":[6,{\"from_node\":\"red\"}]}},\"p2\":{\"process_id\":\"product\",\"arguments\":{\"data\":[-7.5,{\"from_node\":\"blue\"}]}},\"sum\":{\"process_id\":\"sum\",\"arguments\":{\"data\":[1,{\"from_node\":\"nir\"},{\"from_node\":\"p1\"},{\"from_node\":\"p2\"}]}},\"div\":{\"process_id\":\"divide\",\"arguments\":{\"data\":[{\"from_node\":\"sub\"},{\"from_node\":\"sum\"}]}},\"p3\":{\"process_id\":\"product\",\"arguments\":{\"data\":[2.5,{\"from_node\":\"div\"}]},\"result\":true}}}}},\"mintime\":{\"process_id\":\"reduce\",\"description\":\"Compute a minimum time composite by reducing the temporal dimension\",\"arguments\":{\"data\":{\"from_node\":\"evi\"},\"dimension\":\"temporal\",\"reducer\":{\"callback\":{\"min\":{\"process_id\":\"min\",\"arguments\":{\"data\":{\"from_argument\":\"data\"}},\"result\":true}}}}},\"save\":{\"process_id\":\"save_result\",\"arguments\":{\"data\":{\"from_node\":\"mintime\"},\"format\":\"GTiff\"},\"result\":true}}", required = true, value = "A process graph defines a graph-like structure as a connected set of executable processes. Each key is a unique identifier (node id) that is used to refer to the process in the graph.")
  @NotNull @Valid 
  public Map<String, ProcessNode> getProcessGraph() {
    return processGraph;
  }

  public void setProcessGraph(Map<String, ProcessNode> processGraph) {
    this.processGraph = processGraph;
  }

  public BatchJobResponse status(Status status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   **/
  @JsonProperty("status")
  @ApiModelProperty(required = true, value = "")
  @NotNull @Valid 
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public BatchJobResponse progress(BigDecimal progress) {
    this.progress = progress;
    return this;
  }

  /**
   * Indicates the process of a running batch job in percent. Can also be set for a job whiched errored out or was canceled by the user. In this case, the value indicates the progress at which the job stopped. Property may not be available for the status codes &#x60;submitted&#x60; and &#x60;queued&#x60;. Submitted and queued jobs only allow the value &#x60;0&#x60;, finished jobs only allow the value &#x60;100&#x60;.
   * minimum: 0
   * maximum: 100
   * @return progress
   **/
  @JsonProperty("progress")
  @ApiModelProperty(example = "75.5", value = "Indicates the process of a running batch job in percent. Can also be set for a job whiched errored out or was canceled by the user. In this case, the value indicates the progress at which the job stopped. Property may not be available for the status codes `submitted` and `queued`. Submitted and queued jobs only allow the value `0`, finished jobs only allow the value `100`.")
  @Valid  @DecimalMin("0") @DecimalMax("100")
  public BigDecimal getProgress() {
    return progress;
  }

  public void setProgress(BigDecimal progress) {
    this.progress = progress;
  }

  public BatchJobResponse error(OneOfstringjobError error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
   **/
  @JsonProperty("error")
  @ApiModelProperty(value = "")
  @Valid 
  public OneOfstringjobError getError() {
    return error;
  }

  public void setError(OneOfstringjobError error) {
    this.error = error;
  }

  public BatchJobResponse submitted(Date submitted) {
    this.submitted = submitted;
    return this;
  }

  /**
   * Date and time of creation, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.
   * @return submitted
   **/
  @JsonProperty("submitted")
  @ApiModelProperty(required = true, value = "Date and time of creation, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.")
  @NotNull 
  public Date getSubmitted() {
    return submitted;
  }

  public void setSubmitted(Date submitted) {
    this.submitted = submitted;
  }

  public BatchJobResponse updated(Date updated) {
    this.updated = updated;
    return this;
  }

  /**
   * Date and time of last status change, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.
   * @return updated
   **/
  @JsonProperty("updated")
  @ApiModelProperty(value = "Date and time of last status change, formatted as a [RFC 3339](https://www.ietf.org/rfc/rfc3339) date-time.")
  
  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public BatchJobResponse plan(String plan) {
    this.plan = plan;
    return this;
  }

  /**
   * The billing plan to process and charge the job with. The plans can be retrieved by calling &#x60;GET /&#x60;. Billing plans MUST be accepted *case insensitive*.
   * @return plan
   **/
  @JsonProperty("plan")
  @ApiModelProperty(example = "free", value = "The billing plan to process and charge the job with. The plans can be retrieved by calling `GET /`. Billing plans MUST be accepted *case insensitive*.")
  
  public String getPlan() {
    return plan;
  }

  public void setPlan(String plan) {
    this.plan = plan;
  }

  public BatchJobResponse costs(BigDecimal costs) {
    this.costs = costs;
    return this;
  }

  /**
   * An amount of money or credits. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling &#x60;GET /&#x60;.
   * @return costs
   **/
  @JsonProperty("costs")
  @ApiModelProperty(example = "12.98", value = "An amount of money or credits. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling `GET /`.")
  @Valid 
  public BigDecimal getCosts() {
    return costs;
  }

  public void setCosts(BigDecimal costs) {
    this.costs = costs;
  }

  public BatchJobResponse budget(BigDecimal budget) {
    this.budget = budget;
    return this;
  }

  /**
   * Maximum amount of costs the user is allowed to produce. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling &#x60;GET /&#x60;. If possible, back-ends SHOULD reject jobs with openEO error &#x60;PaymentRequired&#x60; if the budget is too low to process the request completely. Otherwise, when reaching the budget jobs MAY try to return partial results if possible. Otherwise the request and results are discarded. Users SHOULD be warned by clients that reaching the budget MAY discard the results and that setting this value should be well-wrought. Setting the buget to &#x60;null&#x60; means there is no specified budget.
   * @return budget
   **/
  @JsonProperty("budget")
  @ApiModelProperty(example = "100", value = "Maximum amount of costs the user is allowed to produce. The value MUST be specified in the currency the back-end is working with. The currency can be retrieved by calling `GET /`. If possible, back-ends SHOULD reject jobs with openEO error `PaymentRequired` if the budget is too low to process the request completely. Otherwise, when reaching the budget jobs MAY try to return partial results if possible. Otherwise the request and results are discarded. Users SHOULD be warned by clients that reaching the budget MAY discard the results and that setting this value should be well-wrought. Setting the buget to `null` means there is no specified budget.")
  @Valid 
  public BigDecimal getBudget() {
    return budget;
  }

  public void setBudget(BigDecimal budget) {
    this.budget = budget;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BatchJobResponse batchJobResponse = (BatchJobResponse) o;
    return Objects.equals(this.id, batchJobResponse.id) &&
        Objects.equals(this.title, batchJobResponse.title) &&
        Objects.equals(this.description, batchJobResponse.description) &&
        Objects.equals(this.processGraph, batchJobResponse.processGraph) &&
        Objects.equals(this.status, batchJobResponse.status) &&
        Objects.equals(this.progress, batchJobResponse.progress) &&
        Objects.equals(this.error, batchJobResponse.error) &&
        Objects.equals(this.submitted, batchJobResponse.submitted) &&
        Objects.equals(this.updated, batchJobResponse.updated) &&
        Objects.equals(this.plan, batchJobResponse.plan) &&
        Objects.equals(this.costs, batchJobResponse.costs) &&
        Objects.equals(this.budget, batchJobResponse.budget);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, description, processGraph, status, progress, error, submitted, updated, plan, costs, budget);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BatchJobResponse {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    processGraph: ").append(toIndentedString(processGraph)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    progress: ").append(toIndentedString(progress)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    submitted: ").append(toIndentedString(submitted)).append("\n");
    sb.append("    updated: ").append(toIndentedString(updated)).append("\n");
    sb.append("    plan: ").append(toIndentedString(plan)).append("\n");
    sb.append("    costs: ").append(toIndentedString(costs)).append("\n");
    sb.append("    budget: ").append(toIndentedString(budget)).append("\n");
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
