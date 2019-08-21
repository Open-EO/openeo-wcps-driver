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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModelProperty;

/**
 * UpdateBatchJobRequest
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class UpdateBatchJobRequest implements Serializable {

	private static final long serialVersionUID = -7775114394475979736L;

	@JsonProperty("title")
	private String title;

	@JsonProperty("description")
	private String description;

	@JsonProperty("process_graph")
	private Object processGraph = null;

	@JsonProperty("plan")
	private String plan;

	@JsonProperty("budget")
	private BigDecimal budget;

	public UpdateBatchJobRequest title(String title) {
		this.title = title;
		return this;
	}

	/**
	 * A short description to easily distinguish entities.
	 * 
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

	public UpdateBatchJobRequest description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Detailed description to fully explain the entity. [CommonMark
	 * 0.28](http://commonmark.org/) syntax MAY be used for rich text
	 * representation.
	 * 
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

	public UpdateBatchJobRequest processGraph(Object processGraph) {
		this.processGraph = processGraph;
		return this;
	}

	/**
	 * A process graph defines a graph-like structure as a connected set of
	 * executable processes. Each key is a unique identifier (node id) that is used
	 * to refer to the process in the graph.
	 * 
	 * @return processGraph
	 **/
	/*
	@JsonProperty("process_graph")
	@ApiModelProperty(example = "{\"dc\":{\"process_id\":\"load_collection\",\"arguments\":{\"id\":\"Sentinel-2\",\"spatial_extent\":{\"west\":16.1,\"east\":16.6,\"north\":48.6,\"south\":47.2},\"temporal_extent\":[\"2018-01-01\",\"2018-02-01\"]}},\"bands\":{\"process_id\":\"filter_bands\",\"description\":\"Filter and order the bands. The order is important for the following reduce operation.\",\"arguments\":{\"data\":{\"from_node\":\"dc\"},\"bands\":[\"B08\",\"B04\",\"B02\"]}},\"evi\":{\"process_id\":\"reduce\",\"description\":\"Compute the EVI. Formula: 2.5 * (NIR - RED) / (1 + NIR + 6*RED + -7.5*BLUE)\",\"arguments\":{\"data\":{\"from_node\":\"bands\"},\"dimension\":\"spectral\",\"reducer\":{\"callback\":{\"nir\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_argument\":\"data\"},\"index\":0}},\"red\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_argument\":\"data\"},\"index\":1}},\"blue\":{\"process_id\":\"array_element\",\"arguments\":{\"data\":{\"from_argument\":\"data\"},\"index\":2}},\"sub\":{\"process_id\":\"subtract\",\"arguments\":{\"data\":[{\"from_node\":\"nir\"},{\"from_node\":\"red\"}]}},\"p1\":{\"process_id\":\"product\",\"arguments\":{\"data\":[6,{\"from_node\":\"red\"}]}},\"p2\":{\"process_id\":\"product\",\"arguments\":{\"data\":[-7.5,{\"from_node\":\"blue\"}]}},\"sum\":{\"process_id\":\"sum\",\"arguments\":{\"data\":[1,{\"from_node\":\"nir\"},{\"from_node\":\"p1\"},{\"from_node\":\"p2\"}]}},\"div\":{\"process_id\":\"divide\",\"arguments\":{\"data\":[{\"from_node\":\"sub\"},{\"from_node\":\"sum\"}]}},\"p3\":{\"process_id\":\"product\",\"arguments\":{\"data\":[2.5,{\"from_node\":\"div\"}]},\"result\":true}}}}},\"mintime\":{\"process_id\":\"reduce\",\"description\":\"Compute a minimum time composite by reducing the temporal dimension\",\"arguments\":{\"data\":{\"from_node\":\"evi\"},\"dimension\":\"temporal\",\"reducer\":{\"callback\":{\"min\":{\"process_id\":\"min\",\"arguments\":{\"data\":{\"from_argument\":\"data\"}},\"result\":true}}}}},\"save\":{\"process_id\":\"save_result\",\"arguments\":{\"data\":{\"from_node\":\"mintime\"},\"format\":\"GTiff\"},\"result\":true}}", value = "A process graph defines a graph-like structure as a connected set of executable processes. Each key is a unique identifier (node id) that is used to refer to the process in the graph.")
	@Valid
	public Object getProcessGraph() {
		ObjectMapper mapper = new ObjectMapper();
		JSONObject processgraphLocal = null;
		try {		
			processgraphLocal = new JSONObject(mapper.writeValueAsString(this.processGraph));
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return processgraphLocal;
	}*/
	
	/**
	 * Get processGraph
	 * 
	 * @return processGraph
	 **/
	@JsonProperty("process_graph")
	@ApiModelProperty(required = true, value = "")
	@NotNull	
	public Object getProcessGraph() {		
//		log.debug("process graph object:" + this.processGraph.getClass());
//		log.debug(this.processGraph.toString());
		return this.processGraph;
	}

	public void setProcessGraph(Object processGraph) {
		this.processGraph = processGraph;
	}

	public UpdateBatchJobRequest plan(String plan) {
		this.plan = plan;
		return this;
	}

	/**
	 * The billing plan to process and charge the job with. The plans and the
	 * default plan can be retrieved by calling &#x60;GET /&#x60;. Billing plans
	 * MUST be accepted *case insensitive*. Billing plans not on the list of
	 * available plans MUST be rejected with openEO error
	 * &#x60;BillingPlanInvalid&#x60;. If no billing plan is specified by the
	 * client, the server MUST default to the default billing plan in &#x60;GET
	 * /&#x60;. If the default billing plan of the provider changes, the job or
	 * service MUST not be affected by the change, i.e. the default plan which is
	 * valid during job or service creation must be permanently assigned to the job
	 * or service until the client requests to change it.
	 * 
	 * @return plan
	 **/
	@JsonProperty("plan")
	@ApiModelProperty(example = "free", value = "The billing plan to process and charge the job with.  The plans and the default plan can be retrieved by calling `GET /`.  Billing plans MUST be accepted *case insensitive*. Billing plans not on the list of available plans MUST be rejected with openEO error `BillingPlanInvalid`.  If no billing plan is specified by the client, the server MUST default to the default billing plan in `GET /`. If the default billing plan of the provider changes, the job or service MUST not be affected by the change, i.e. the default plan which is valid during job or service creation must be permanently assigned to the job or service until the client requests to change it.")

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public UpdateBatchJobRequest budget(BigDecimal budget) {
		this.budget = budget;
		return this;
	}

	/**
	 * Maximum amount of costs the user is allowed to produce. The value MUST be
	 * specified in the currency the back-end is working with. The currency can be
	 * retrieved by calling &#x60;GET /&#x60;. If possible, back-ends SHOULD reject
	 * jobs with openEO error &#x60;PaymentRequired&#x60; if the budget is too low
	 * to process the request completely. Otherwise, when reaching the budget jobs
	 * MAY try to return partial results if possible. Otherwise the request and
	 * results are discarded. Users SHOULD be warned by clients that reaching the
	 * budget MAY discard the results and that setting this value should be
	 * well-wrought. Setting the buget to &#x60;null&#x60; means there is no
	 * specified budget.
	 * 
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
		UpdateBatchJobRequest updateBatchJobRequest = (UpdateBatchJobRequest) o;
		return Objects.equals(this.title, updateBatchJobRequest.title)
				&& Objects.equals(this.description, updateBatchJobRequest.description)
				&& Objects.equals(this.processGraph, updateBatchJobRequest.processGraph)
				&& Objects.equals(this.plan, updateBatchJobRequest.plan)
				&& Objects.equals(this.budget, updateBatchJobRequest.budget);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, description, processGraph, plan, budget);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class UpdateBatchJobRequest {\n");

		sb.append("    title: ").append(toIndentedString(title)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    processGraph: ").append(toIndentedString(processGraph)).append("\n");
		sb.append("    plan: ").append(toIndentedString(plan)).append("\n");
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
