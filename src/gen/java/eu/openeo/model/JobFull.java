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
import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import eu.openeo.dao.JSONObjectPersister;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Defines full metadata of processing jobs that have been submitted by users.
 */
@ApiModel(description = "Defines full metadata of processing jobs that have been submitted by users.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
@DatabaseTable(tableName = "jobs")
public class JobFull implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8518285065901989576L;

	@JsonProperty("job_id")
	@DatabaseField(id = true)
	private String jobId = null;

	@JsonProperty("status")
	@DatabaseField(canBeNull =  false)
	private JobStatus status = null;

	@JsonProperty("process_graph")
	@DatabaseField(canBeNull =  false, persisterClass = JSONObjectPersister.class)
	private Object processGraph = null;

	@JsonProperty("output")
	@DatabaseField(persisterClass = JSONObjectPersister.class)
	private Object output = null;

	@JsonProperty("submitted")
	@DatabaseField()
	private String submitted = null;

	@JsonProperty("updated")
	@DatabaseField()
	private String updated = null;

	@JsonProperty("user_id")
	@DatabaseField()
	private String userId = null;

	@JsonProperty("consumed_credits")
	@DatabaseField()
	private BigDecimal consumedCredits = null;

	public JobFull jobId(String jobId) {
		this.jobId = jobId;
		return this;
	}

	/**
	 * Get jobId
	 * 
	 * @return jobId
	 **/
	@JsonProperty("job_id")
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public JobFull status(JobStatus status) {
		this.status = status;
		return this;
	}

	/**
	 * Get status
	 * 
	 * @return status
	 **/
	@JsonProperty("status")
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public JobFull processGraph(Object processGraph) {
		this.processGraph = processGraph;
		return this;
	}

	/**
	 * Get processGraph
	 * 
	 * @return processGraph
	 **/
	@JsonProperty("process_graph")
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public Object getProcessGraph() {
//		JSONParser parser = new JSONParser();
//		ObjectMapper mapper = new ObjectMapper();
//		JSONObject processgraphLocal = null;
//		try {
//			processgraphLocal = ((JSONObject) parser.parse(mapper.writeValueAsString(this.processGraph)));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return this.processGraph;
	}

	public void setProcessGraph(Object processGraph) {
		this.processGraph = processGraph;
	}

	public JobFull output(Object output) {
		this.output = output;
		return this;
	}

	/**
	 * Get output
	 * 
	 * @return output
	 **/
	@JsonProperty("output")
	@ApiModelProperty(value = "")
	public Object getOutput() {
//		return output;
		JSONParser parser = new JSONParser();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject outputLocal = null;
		try {
			outputLocal = ((JSONObject) parser.parse(mapper.writeValueAsString(this.output)));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputLocal;
	}

	public void setOutput(Object output) {
		this.output = output;
	}

	public JobFull submitted(String submitted) {
		this.submitted = submitted;
		return this;
	}

	/**
	 * Get submitted
	 * 
	 * @return submitted
	 **/
	@JsonProperty("submitted")
	@ApiModelProperty(value = "")
	public String getSubmitted() {
		return submitted;
	}

	public void setSubmitted(String submitted) {
		this.submitted = submitted;
	}

	public JobFull updated(String updated) {
		this.updated = updated;
		return this;
	}

	/**
	 * Get updated
	 * 
	 * @return updated
	 **/
	@JsonProperty("updated")
	@ApiModelProperty(value = "")
	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public JobFull userId(String userId) {
		this.userId = userId;
		return this;
	}

	/**
	 * Get userId
	 * 
	 * @return userId
	 **/
	@JsonProperty("user_id")
	@ApiModelProperty(value = "")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public JobFull consumedCredits(BigDecimal consumedCredits) {
		this.consumedCredits = consumedCredits;
		return this;
	}

	/**
	 * Get consumedCredits
	 * 
	 * @return consumedCredits
	 **/
	@JsonProperty("consumed_credits")
	@ApiModelProperty(value = "")
	public BigDecimal getConsumedCredits() {
		return consumedCredits;
	}

	public void setConsumedCredits(BigDecimal consumedCredits) {
		this.consumedCredits = consumedCredits;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		JobFull jobFull = (JobFull) o;
		return Objects.equals(this.jobId, jobFull.jobId) && Objects.equals(this.status, jobFull.status)
				&& Objects.equals(this.processGraph, jobFull.processGraph)
				&& Objects.equals(this.output, jobFull.output) && Objects.equals(this.submitted, jobFull.submitted)
				&& Objects.equals(this.updated, jobFull.updated) && Objects.equals(this.userId, jobFull.userId)
				&& Objects.equals(this.consumedCredits, jobFull.consumedCredits);
	}

	@Override
	public int hashCode() {
		return Objects.hash(jobId, status, processGraph, output, submitted, updated, userId, consumedCredits);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class JobFull {\n");

		sb.append("    jobId: ").append(toIndentedString(jobId)).append("\n");
		sb.append("    status: ").append(toIndentedString(status)).append("\n");
		sb.append("    processGraph: ").append(toIndentedString(processGraph)).append("\n");
		sb.append("    output: ").append(toIndentedString(output)).append("\n");
		sb.append("    submitted: ").append(toIndentedString(submitted)).append("\n");
		sb.append("    updated: ").append(toIndentedString(updated)).append("\n");
		sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
		sb.append("    consumedCredits: ").append(toIndentedString(consumedCredits)).append("\n");
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
