/*
 * OpenEO API
 * The OpenEO API specification for interoperable cloud-based processing of large Earth observation datasets. **This early draft version is incomplete and intended for working on a prototype and a proof of concept.** Things that are currently missing particularly include:   * Authentication and authorization with OAuth 2.0,   * how results of computations can be downloaded,   * how data is streamed into UDFs and how the output of UDFs is returned,   * how services are organized as microservices,   * and  how OpenSearch is interfaced
 *
 * OpenAPI spec version: 0.0.1
 * Contact: marius.appel@uni-muenster.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;

/**
 * InlineResponse2003
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class InlineResponse2003   {
  @JsonProperty("job_id")
  private String jobId = null;

  public InlineResponse2003 jobId(String jobId) {
    this.jobId = jobId;
    return this;
  }

  /**
   * Get jobId
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineResponse2003 inlineResponse2003 = (InlineResponse2003) o;
    return Objects.equals(this.jobId, inlineResponse2003.jobId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse2003 {\n");
    
    sb.append("    jobId: ").append(toIndentedString(jobId)).append("\n");
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

