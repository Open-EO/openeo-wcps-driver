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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Data that is expected to be passed to a callback from a calling process.
 */
@ApiModel(description = "Data that is expected to be passed to a callback from a calling process.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class CallbackParameter  implements Serializable {
  @JsonProperty("from_argument")
  private String fromArgument;

  public CallbackParameter fromArgument(String fromArgument) {
    this.fromArgument = fromArgument;
    return this;
  }

  /**
   * The name of the parameter that is made available to a callback by a calling process.
   * @return fromArgument
   **/
  @JsonProperty("from_argument")
  @ApiModelProperty(required = true, value = "The name of the parameter that is made available to a callback by a calling process.")
  @NotNull 
  public String getFromArgument() {
    return fromArgument;
  }

  public void setFromArgument(String fromArgument) {
    this.fromArgument = fromArgument;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CallbackParameter callbackParameter = (CallbackParameter) o;
    return Objects.equals(this.fromArgument, callbackParameter.fromArgument);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromArgument);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CallbackParameter {\n");
    
    sb.append("    fromArgument: ").append(toIndentedString(fromArgument)).append("\n");
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

