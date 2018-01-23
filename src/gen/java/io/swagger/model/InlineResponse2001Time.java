/*
 * OpenEO API
 * The OpenEO API specification for interoperable cloud-based processing of large Earth observation datasets. **This early draft version is incomplete and intended for working on a prototype and a proof of concept.** Things that are currently missing particularly include:   * Authentication and authorization with OAuth 2.0,   * how results of computations can be downloaded,   * how data is streamed into UDFs and how the output of UDFs is returned,   * how services are organized as microservices,   * how payments can be handled,   * and how OpenSearch is interfaced
 *
 * OpenAPI spec version: 0.0.1
 * Contact: marius.appel@uni-muenster.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.model;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * InlineResponse2001Time
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public class InlineResponse2001Time   {
  @JsonProperty("from")
  private Date from = null;

  @JsonProperty("to")
  private Date to = null;

  public InlineResponse2001Time from(Date from) {
    this.from = from;
    return this;
  }

  /**
   * Date/time in ISO 8601 format
   * @return from
   **/
  @JsonProperty("from")
  @ApiModelProperty(value = "Date/time in ISO 8601 format")
  public Date getFrom() {
    return from;
  }

  public void setFrom(Date from) {
    this.from = from;
  }

  public InlineResponse2001Time to(Date to) {
    this.to = to;
    return this;
  }

  /**
   * Date/time in ISO 8601 format
   * @return to
   **/
  @JsonProperty("to")
  @ApiModelProperty(value = "Date/time in ISO 8601 format")
  public Date getTo() {
    return to;
  }

  public void setTo(Date to) {
    this.to = to;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineResponse2001Time inlineResponse2001Time = (InlineResponse2001Time) o;
    return Objects.equals(this.from, inlineResponse2001Time.from) &&
        Objects.equals(this.to, inlineResponse2001Time.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse2001Time {\n");
    
    sb.append("    from: ").append(toIndentedString(from)).append("\n");
    sb.append("    to: ").append(toIndentedString(to)).append("\n");
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

