/*
 * openEO API
 * The openEO API specification for interoperable cloud-based processing of large Earth observation datasets.   **Make sure to take account of several global API specifications**, which are not (fully) covered in this specification:  * [Cross-Origin Resource Sharing (CORS) support](https://open-eo.github.io/openeo-api/v/0.4.0/cors/index.html) to allow browser-based access to the API.  * [Error handling](https://open-eo.github.io/openeo-api/v/0.4.0/errors/index.html)  * Unless otherwise stated the API works *case sensitive*.
 *
 * OpenAPI spec version: 0.4.0
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
import eu.openeo.model.CollectionEoEobands;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * CollectionEo
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class CollectionEo  implements Serializable {
  @JsonProperty("eo:gsd")
  private BigDecimal eoColonGsd;

  @JsonProperty("eo:platform")
  private String eoColonPlatform;

  @JsonProperty("eo:constellation")
  private String eoColonConstellation;

  @JsonProperty("eo:instrument")
  private String eoColonInstrument;

  @JsonProperty("eo:epsg")
  private BigDecimal eoColonEpsg;

  @JsonProperty("eo:bands")
  private List<CollectionEoEobands> eoColonBands = new ArrayList<CollectionEoEobands>();

  public CollectionEo eoColonGsd(BigDecimal eoColonGsd) {
    this.eoColonGsd = eoColonGsd;
    return this;
  }

  /**
   * The nominal Ground Sample Distance for the data, as measured in meters on the ground. Since GSD can vary across a scene depending on projection, this should be the average or most commonly used GSD in the center of the image. If the data includes multiple bands with different GSD values, this should be the value for the greatest number or most common bands. For instance, Landsat optical and short-wave IR bands are all 30 meters, but the panchromatic band is 15 meters. The eo:gsd should be 30 meters in this case since those are the bands most commonly used.
   * @return eoColonGsd
   **/
  @JsonProperty("eo:gsd")
  @ApiModelProperty(required = true, value = "The nominal Ground Sample Distance for the data, as measured in meters on the ground. Since GSD can vary across a scene depending on projection, this should be the average or most commonly used GSD in the center of the image. If the data includes multiple bands with different GSD values, this should be the value for the greatest number or most common bands. For instance, Landsat optical and short-wave IR bands are all 30 meters, but the panchromatic band is 15 meters. The eo:gsd should be 30 meters in this case since those are the bands most commonly used.")
    @NotNull
@Valid
  public BigDecimal getEoColonGsd() {
    return eoColonGsd;
  }

  public void setEoColonGsd(BigDecimal eoColonGsd) {
    this.eoColonGsd = eoColonGsd;
  }

  public CollectionEo eoColonPlatform(String eoColonPlatform) {
    this.eoColonPlatform = eoColonPlatform;
    return this;
  }

  /**
   * Unique name of the specific platform the instrument is attached to. For satellites this would be the name of the satellite (e.g., landsat-8, sentinel-2A), whereas for drones this would be a unique name for the drone.
   * @return eoColonPlatform
   **/
  @JsonProperty("eo:platform")
  @ApiModelProperty(required = true, value = "Unique name of the specific platform the instrument is attached to. For satellites this would be the name of the satellite (e.g., landsat-8, sentinel-2A), whereas for drones this would be a unique name for the drone.")
    @NotNull

  public String getEoColonPlatform() {
    return eoColonPlatform;
  }

  public void setEoColonPlatform(String eoColonPlatform) {
    this.eoColonPlatform = eoColonPlatform;
  }

  public CollectionEo eoColonConstellation(String eoColonConstellation) {
    this.eoColonConstellation = eoColonConstellation;
    return this;
  }

  /**
   * The name of the group of satellites that have similar payloads and have their orbits arranged in a way to increase the temporal resolution of acquisitions of data with similar geometric and radiometric characteristics. Examples are the Sentinel-2 constellation, which has S2A and S2B and RapidEye. This field allows users to search for Sentinel-2 data, for example, without needing to specify which specific platform the data came from.
   * @return eoColonConstellation
   **/
  @JsonProperty("eo:constellation")
  @ApiModelProperty(value = "The name of the group of satellites that have similar payloads and have their orbits arranged in a way to increase the temporal resolution of acquisitions of data with similar geometric and radiometric characteristics. Examples are the Sentinel-2 constellation, which has S2A and S2B and RapidEye. This field allows users to search for Sentinel-2 data, for example, without needing to specify which specific platform the data came from.")
  
  public String getEoColonConstellation() {
    return eoColonConstellation;
  }

  public void setEoColonConstellation(String eoColonConstellation) {
    this.eoColonConstellation = eoColonConstellation;
  }

  public CollectionEo eoColonInstrument(String eoColonInstrument) {
    this.eoColonInstrument = eoColonInstrument;
    return this;
  }

  /**
   * The name of the sensor used, although for Items which contain data from multiple sensors this could also name multiple sensors. For example, data from the Landsat-8 platform is collected with the OLI sensor as well as the TIRS sensor, but the data is distributed together and commonly referred to as OLI_TIRS.
   * @return eoColonInstrument
   **/
  @JsonProperty("eo:instrument")
  @ApiModelProperty(required = true, value = "The name of the sensor used, although for Items which contain data from multiple sensors this could also name multiple sensors. For example, data from the Landsat-8 platform is collected with the OLI sensor as well as the TIRS sensor, but the data is distributed together and commonly referred to as OLI_TIRS.")
    @NotNull

  public String getEoColonInstrument() {
    return eoColonInstrument;
  }

  public void setEoColonInstrument(String eoColonInstrument) {
    this.eoColonInstrument = eoColonInstrument;
  }

  public CollectionEo eoColonEpsg(BigDecimal eoColonEpsg) {
    this.eoColonEpsg = eoColonEpsg;
    return this;
  }

  /**
   * EPSG code of the datasource, &#x60;null&#x60; if no EPSG code.  A Coordinate Reference System (CRS) is the native reference system (sometimes called a &#39;projection&#39;) used by the data, and can usually be referenced using an [EPSG code](http://epsg.io). If the data does not have a CRS, such as in the case of non-rectified imagery with Ground Control Points, &#x60;eo:epsg&#x60; should be set to &#x60;null&#x60;. It should also be set to &#x60;null&#x60; if a CRS exists, but for which there is no valid EPSG code.
   * @return eoColonEpsg
   **/
  @JsonProperty("eo:epsg")
  @ApiModelProperty(value = "EPSG code of the datasource, `null` if no EPSG code.  A Coordinate Reference System (CRS) is the native reference system (sometimes called a 'projection') used by the data, and can usually be referenced using an [EPSG code](http://epsg.io). If the data does not have a CRS, such as in the case of non-rectified imagery with Ground Control Points, `eo:epsg` should be set to `null`. It should also be set to `null` if a CRS exists, but for which there is no valid EPSG code.")
  @Valid
  public BigDecimal getEoColonEpsg() {
    return eoColonEpsg;
  }

  public void setEoColonEpsg(BigDecimal eoColonEpsg) {
    this.eoColonEpsg = eoColonEpsg;
  }

  public CollectionEo eoColonBands(List<CollectionEoEobands> eoColonBands) {
    this.eoColonBands = eoColonBands;
    return this;
  }

  public CollectionEo addEoColonBandsItem(CollectionEoEobands eoColonBandsItem) {
    this.eoColonBands.add(eoColonBandsItem);
    return this;
  }

  /**
   * This is a list of the available bands where each item is a Band Object.
   * @return eoColonBands
   **/
  @JsonProperty("eo:bands")
  @ApiModelProperty(required = true, value = "This is a list of the available bands where each item is a Band Object.")
    @NotNull
@Valid
  public List<CollectionEoEobands> getEoColonBands() {
    return eoColonBands;
  }

  public void setEoColonBands(List<CollectionEoEobands> eoColonBands) {
    this.eoColonBands = eoColonBands;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectionEo collectionEo = (CollectionEo) o;
    return Objects.equals(this.eoColonGsd, collectionEo.eoColonGsd) &&
        Objects.equals(this.eoColonPlatform, collectionEo.eoColonPlatform) &&
        Objects.equals(this.eoColonConstellation, collectionEo.eoColonConstellation) &&
        Objects.equals(this.eoColonInstrument, collectionEo.eoColonInstrument) &&
        Objects.equals(this.eoColonEpsg, collectionEo.eoColonEpsg) &&
        Objects.equals(this.eoColonBands, collectionEo.eoColonBands);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eoColonGsd, eoColonPlatform, eoColonConstellation, eoColonInstrument, eoColonEpsg, eoColonBands);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollectionEo {\n");
    
    sb.append("    eoColonGsd: ").append(toIndentedString(eoColonGsd)).append("\n");
    sb.append("    eoColonPlatform: ").append(toIndentedString(eoColonPlatform)).append("\n");
    sb.append("    eoColonConstellation: ").append(toIndentedString(eoColonConstellation)).append("\n");
    sb.append("    eoColonInstrument: ").append(toIndentedString(eoColonInstrument)).append("\n");
    sb.append("    eoColonEpsg: ").append(toIndentedString(eoColonEpsg)).append("\n");
    sb.append("    eoColonBands: ").append(toIndentedString(eoColonBands)).append("\n");
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

