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

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Start and end date/time in ISO 8601 format
 */
@ApiModel(description = "Start and end date/time in ISO 8601 format")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public class ViewTimeWindow   {
  @JsonProperty("start")
  private String start = null;

  @JsonProperty("end")
  private String end = null;

  public ViewTimeWindow start(String start) {
    this.start = start;
    return this;
  }

  /**
   * Get start
   * @return start
   **/
  @JsonProperty("start")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public ViewTimeWindow end(String end) {
    this.end = end;
    return this;
  }

  /**
   * Get end
   * @return end
   **/
  @JsonProperty("end")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewTimeWindow viewTimeWindow = (ViewTimeWindow) o;
    return Objects.equals(this.start, viewTimeWindow.start) &&
        Objects.equals(this.end, viewTimeWindow.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ViewTimeWindow {\n");
    
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
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

