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

import io.swagger.annotations.ApiModelProperty;

/**
 * Service1
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class Service1 implements Serializable {
	@JsonProperty("job_id")
	private String jobId = null;

	@JsonProperty("service_type")
	private String serviceType = null;

	@JsonProperty("service_args")
	private ServiceArgs serviceArgs = null;

	public Service1 jobId(String jobId) {
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

	public Service1 serviceType(String serviceType) {
		this.serviceType = serviceType;
		return this;
	}

	/**
	 * Get serviceType
	 * 
	 * @return serviceType
	 **/
	@JsonProperty("service_type")
	@ApiModelProperty(required = true, value = "")
	@NotNull
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Service1 serviceArgs(ServiceArgs serviceArgs) {
		this.serviceArgs = serviceArgs;
		return this;
	}

	/**
	 * Get serviceArgs
	 * 
	 * @return serviceArgs
	 **/
	@JsonProperty("service_args")
	@ApiModelProperty(value = "")
	public ServiceArgs getServiceArgs() {
		return serviceArgs;
	}

	public void setServiceArgs(ServiceArgs serviceArgs) {
		this.serviceArgs = serviceArgs;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Service1 service1 = (Service1) o;
		return Objects.equals(this.jobId, service1.jobId) && Objects.equals(this.serviceType, service1.serviceType)
				&& Objects.equals(this.serviceArgs, service1.serviceArgs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(jobId, serviceType, serviceArgs);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Service1 {\n");

		sb.append("    jobId: ").append(toIndentedString(jobId)).append("\n");
		sb.append("    serviceType: ").append(toIndentedString(serviceType)).append("\n");
		sb.append("    serviceArgs: ").append(toIndentedString(serviceArgs)).append("\n");
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
