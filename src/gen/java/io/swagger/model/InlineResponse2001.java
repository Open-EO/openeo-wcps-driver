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
import io.swagger.model.BandDescription;
import io.swagger.model.InlineResponse2001Extent;
import io.swagger.model.InlineResponse2001Time;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;

/**
 * InlineResponse2001
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-16T14:36:16.100+01:00")
public class InlineResponse2001   {
  @JsonProperty("product_id")
  private String productId = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("source")
  private String source = null;

  @JsonProperty("extent")
  private InlineResponse2001Extent extent = null;

  @JsonProperty("time")
  private InlineResponse2001Time time = null;

  @JsonProperty("bands")
  private List<BandDescription> bands = new ArrayList<BandDescription>();

  public InlineResponse2001 productId(String productId) {
    this.productId = productId;
    return this;
  }

  /**
   * Get productId
   * @return productId
   **/
  @JsonProperty("product_id")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public InlineResponse2001 description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   **/
  @JsonProperty("description")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public InlineResponse2001 source(String source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
   **/
  @JsonProperty("source")
  @ApiModelProperty(value = "")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public InlineResponse2001 extent(InlineResponse2001Extent extent) {
    this.extent = extent;
    return this;
  }

  /**
   * Get extent
   * @return extent
   **/
  @JsonProperty("extent")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public InlineResponse2001Extent getExtent() {
    return extent;
  }

  public void setExtent(InlineResponse2001Extent extent) {
    this.extent = extent;
  }

  public InlineResponse2001 time(InlineResponse2001Time time) {
    this.time = time;
    return this;
  }

  /**
   * Get time
   * @return time
   **/
  @JsonProperty("time")
  @ApiModelProperty(value = "")
  public InlineResponse2001Time getTime() {
    return time;
  }

  public void setTime(InlineResponse2001Time time) {
    this.time = time;
  }

  public InlineResponse2001 bands(List<BandDescription> bands) {
    this.bands = bands;
    return this;
  }

  public InlineResponse2001 addBandsItem(BandDescription bandsItem) {
    this.bands.add(bandsItem);
    return this;
  }

  /**
   * Get bands
   * @return bands
   **/
  @JsonProperty("bands")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public List<BandDescription> getBands() {
    return bands;
  }

  public void setBands(List<BandDescription> bands) {
    this.bands = bands;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineResponse2001 inlineResponse2001 = (InlineResponse2001) o;
    return Objects.equals(this.productId, inlineResponse2001.productId) &&
        Objects.equals(this.description, inlineResponse2001.description) &&
        Objects.equals(this.source, inlineResponse2001.source) &&
        Objects.equals(this.extent, inlineResponse2001.extent) &&
        Objects.equals(this.time, inlineResponse2001.time) &&
        Objects.equals(this.bands, inlineResponse2001.bands);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId, description, source, extent, time, bands);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse2001 {\n");
    
    sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    bands: ").append(toIndentedString(bands)).append("\n");
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
