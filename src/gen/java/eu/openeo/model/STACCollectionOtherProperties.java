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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import eu.openeo.model.AnyOfnumberDateTime;
import eu.openeo.model.AnyOfnumberstringDateTimeboolean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * STACCollectionOtherProperties
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class STACCollectionOtherProperties  implements Serializable {
  @JsonProperty("extent")
  private List<AnyOfnumberDateTime> extent = null;

  @JsonProperty("values")
  private List<AnyOfnumberstringDateTimeboolean> values = null;

  public STACCollectionOtherProperties extent(List<AnyOfnumberDateTime> extent) {
    this.extent = extent;
    return this;
  }

  public STACCollectionOtherProperties addExtentItem(AnyOfnumberDateTime extentItem) {
    if (this.extent == null) {
      this.extent = new ArrayList<AnyOfnumberDateTime>();
    }
    this.extent.add(extentItem);
    return this;
  }

  /**
   * If the dimension consists of [ordinal](https://en.wikipedia.org/wiki/Level_of_measurement#Ordinal_scale) values, the extent (lower and upper bounds) of the values as two-dimensional array. Strings are only allowed for temporal extents, which are formatted according to [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6). Use &#x60;null&#x60; for open intervals.
   * @return extent
   **/
  @JsonProperty("extent")
  @ApiModelProperty(value = "If the dimension consists of [ordinal](https://en.wikipedia.org/wiki/Level_of_measurement#Ordinal_scale) values, the extent (lower and upper bounds) of the values as two-dimensional array. Strings are only allowed for temporal extents, which are formatted according to [RFC 3339, section 5.6](https://tools.ietf.org/html/rfc3339#section-5.6). Use `null` for open intervals.")
  @Valid  @Size(min=2,max=2)
  public List<AnyOfnumberDateTime> getExtent() {
    return extent;
  }

  public void setExtent(List<AnyOfnumberDateTime> extent) {
    this.extent = extent;
  }

  public STACCollectionOtherProperties values(List<AnyOfnumberstringDateTimeboolean> values) {
    this.values = values;
    return this;
  }

  public STACCollectionOtherProperties addValuesItem(AnyOfnumberstringDateTimeboolean valuesItem) {
    if (this.values == null) {
      this.values = new ArrayList<AnyOfnumberstringDateTimeboolean>();
    }
    this.values.add(valuesItem);
    return this;
  }

  /**
   * If the property consists of [nominal](https://en.wikipedia.org/wiki/Level_of_measurement#Nominal_level), a set of all potential values can be specified. Only primitive data types allowed.
   * @return values
   **/
  @JsonProperty("values")
  @ApiModelProperty(value = "If the property consists of [nominal](https://en.wikipedia.org/wiki/Level_of_measurement#Nominal_level), a set of all potential values can be specified. Only primitive data types allowed.")
  @Valid 
  public List<AnyOfnumberstringDateTimeboolean> getValues() {
    return values;
  }

  public void setValues(List<AnyOfnumberstringDateTimeboolean> values) {
    this.values = values;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    STACCollectionOtherProperties stACCollectionOtherProperties = (STACCollectionOtherProperties) o;
    return Objects.equals(this.extent, stACCollectionOtherProperties.extent) &&
        Objects.equals(this.values, stACCollectionOtherProperties.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extent, values);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class STACCollectionOtherProperties {\n");
    
    sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
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
