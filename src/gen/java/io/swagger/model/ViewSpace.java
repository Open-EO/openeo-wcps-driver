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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Defines spatial resolution, window, and resampling method used for running processes on small sub datasets
 */
@ApiModel(description = "Defines spatial resolution, window, and resampling method used for running processes on small sub datasets")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-01-23T11:10:18.550+01:00")
public class ViewSpace   {
  @JsonProperty("srs")
  private String srs = null;

  @JsonProperty("window")
  private ViewSpaceWindow window = null;

  @JsonProperty("cell_size")
  private BigDecimal cellSize = null;

  /**
   * resampling method to use (taken from [GDAL](http://www.gdal.org/gdal_translate.html))
   */
  public enum ResamplingEnum {
    NEAREST("nearest"),
    
    BILINEAR("bilinear"),
    
    CUBIC("cubic"),
    
    CUBICSPLINE("cubicspline"),
    
    LANCZOS("lanczos"),
    
    AVERAGE("average"),
    
    MODE("mode");

    private String value;

    ResamplingEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ResamplingEnum fromValue(String text) {
      for (ResamplingEnum b : ResamplingEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("resampling")
  private ResamplingEnum resampling = ResamplingEnum.NEAREST;

  public ViewSpace srs(String srs) {
    this.srs = srs;
    return this;
  }

  /**
   * Spatial reference system as proj4 string or epsg code such as &#x60;EPSG:3857&#x60;
   * @return srs
   **/
  @JsonProperty("srs")
  @ApiModelProperty(value = "Spatial reference system as proj4 string or epsg code such as `EPSG:3857`")
  public String getSrs() {
    return srs;
  }

  public void setSrs(String srs) {
    this.srs = srs;
  }

  public ViewSpace window(ViewSpaceWindow window) {
    this.window = window;
    return this;
  }

  /**
   * Get window
   * @return window
   **/
  @JsonProperty("window")
  @ApiModelProperty(value = "")
  public ViewSpaceWindow getWindow() {
    return window;
  }

  public void setWindow(ViewSpaceWindow window) {
    this.window = window;
  }

  public ViewSpace cellSize(BigDecimal cellSize) {
    this.cellSize = cellSize;
    return this;
  }

  /**
   * Get cellSize
   * @return cellSize
   **/
  @JsonProperty("cell_size")
  @ApiModelProperty(value = "")
  public BigDecimal getCellSize() {
    return cellSize;
  }

  public void setCellSize(BigDecimal cellSize) {
    this.cellSize = cellSize;
  }

  public ViewSpace resampling(ResamplingEnum resampling) {
    this.resampling = resampling;
    return this;
  }

  /**
   * resampling method to use (taken from [GDAL](http://www.gdal.org/gdal_translate.html))
   * @return resampling
   **/
  @JsonProperty("resampling")
  @ApiModelProperty(value = "resampling method to use (taken from [GDAL](http://www.gdal.org/gdal_translate.html))")
  public ResamplingEnum getResampling() {
    return resampling;
  }

  public void setResampling(ResamplingEnum resampling) {
    this.resampling = resampling;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewSpace viewSpace = (ViewSpace) o;
    return Objects.equals(this.srs, viewSpace.srs) &&
        Objects.equals(this.window, viewSpace.window) &&
        Objects.equals(this.cellSize, viewSpace.cellSize) &&
        Objects.equals(this.resampling, viewSpace.resampling);
  }

  @Override
  public int hashCode() {
    return Objects.hash(srs, window, cellSize, resampling);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ViewSpace {\n");
    
    sb.append("    srs: ").append(toIndentedString(srs)).append("\n");
    sb.append("    window: ").append(toIndentedString(window)).append("\n");
    sb.append("    cellSize: ").append(toIndentedString(cellSize)).append("\n");
    sb.append("    resampling: ").append(toIndentedString(resampling)).append("\n");
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

