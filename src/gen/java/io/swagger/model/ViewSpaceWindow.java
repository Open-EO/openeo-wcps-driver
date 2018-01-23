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

import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * boundaries of the spatial window as coordinates expressed in the given reference system.
 */
@ApiModel(description = "boundaries of the spatial window as coordinates expressed in the given reference system.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public class ViewSpaceWindow   {
  @JsonProperty("left")
  private BigDecimal left = null;

  @JsonProperty("top")
  private BigDecimal top = null;

  @JsonProperty("right")
  private BigDecimal right = null;

  @JsonProperty("bottom")
  private BigDecimal bottom = null;

  public ViewSpaceWindow left(BigDecimal left) {
    this.left = left;
    return this;
  }

  /**
   * Get left
   * @return left
   **/
  @JsonProperty("left")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public BigDecimal getLeft() {
    return left;
  }

  public void setLeft(BigDecimal left) {
    this.left = left;
  }

  public ViewSpaceWindow top(BigDecimal top) {
    this.top = top;
    return this;
  }

  /**
   * Get top
   * @return top
   **/
  @JsonProperty("top")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public BigDecimal getTop() {
    return top;
  }

  public void setTop(BigDecimal top) {
    this.top = top;
  }

  public ViewSpaceWindow right(BigDecimal right) {
    this.right = right;
    return this;
  }

  /**
   * Get right
   * @return right
   **/
  @JsonProperty("right")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public BigDecimal getRight() {
    return right;
  }

  public void setRight(BigDecimal right) {
    this.right = right;
  }

  public ViewSpaceWindow bottom(BigDecimal bottom) {
    this.bottom = bottom;
    return this;
  }

  /**
   * Get bottom
   * @return bottom
   **/
  @JsonProperty("bottom")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public BigDecimal getBottom() {
    return bottom;
  }

  public void setBottom(BigDecimal bottom) {
    this.bottom = bottom;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewSpaceWindow viewSpaceWindow = (ViewSpaceWindow) o;
    return Objects.equals(this.left, viewSpaceWindow.left) &&
        Objects.equals(this.top, viewSpaceWindow.top) &&
        Objects.equals(this.right, viewSpaceWindow.right) &&
        Objects.equals(this.bottom, viewSpaceWindow.bottom);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, top, right, bottom);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ViewSpaceWindow {\n");
    
    sb.append("    left: ").append(toIndentedString(left)).append("\n");
    sb.append("    top: ").append(toIndentedString(top)).append("\n");
    sb.append("    right: ").append(toIndentedString(right)).append("\n");
    sb.append("    bottom: ").append(toIndentedString(bottom)).append("\n");
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

