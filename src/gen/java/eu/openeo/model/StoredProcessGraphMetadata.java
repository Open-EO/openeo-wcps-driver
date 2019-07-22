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
 * Defines limited metadata of stored process graphs that have been submitted by users.
 */
@ApiModel(description = "Defines limited metadata of stored process graphs that have been submitted by users.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class StoredProcessGraphMetadata  implements Serializable {
  @JsonProperty("id")
  private String id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  public StoredProcessGraphMetadata id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier of a job that is generated by the back-end during job submission. MUST match the specified pattern.
   * @return id
   **/
  @JsonProperty("id")
  @ApiModelProperty(example = "cc2bab1e3b3a52aa", required = true, value = "Unique identifier of a job that is generated by the back-end during job submission. MUST match the specified pattern.")
  @NotNull  @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public StoredProcessGraphMetadata title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A short description to easily distinguish entities.
   * @return title
   **/
  @JsonProperty("title")
  @ApiModelProperty(example = "NDVI based on Sentinel 2", value = "A short description to easily distinguish entities.")
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public StoredProcessGraphMetadata description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed description to fully explain the entity.  [CommonMark 0.28](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
   **/
  @JsonProperty("description")
  @ApiModelProperty(example = "Deriving minimum NDVI measurements over pixel time series of Sentinel 2", value = "Detailed description to fully explain the entity.  [CommonMark 0.28](http://commonmark.org/) syntax MAY be used for rich text representation.")
  
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StoredProcessGraphMetadata storedProcessGraphMetadata = (StoredProcessGraphMetadata) o;
    return Objects.equals(this.id, storedProcessGraphMetadata.id) &&
        Objects.equals(this.title, storedProcessGraphMetadata.title) &&
        Objects.equals(this.description, storedProcessGraphMetadata.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, description);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StoredProcessGraphMetadata {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
