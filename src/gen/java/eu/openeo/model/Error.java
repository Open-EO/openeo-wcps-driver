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
import eu.openeo.model.Link;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * An error object declares addtional information about a client-side or server-side error. The [openEO documentation](https://open-eo.github.io/openeo-api/v/0.4.2/errors/index.html) provides additional information regarding error handling and a list of potential error codes.
 */
@ApiModel(description = "An error object declares addtional information about a client-side or server-side error. The [openEO documentation](https://open-eo.github.io/openeo-api/v/0.4.2/errors/index.html) provides additional information regarding error handling and a list of potential error codes.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class Error  implements Serializable {
  @JsonProperty("id")
  private String id;

  @JsonProperty("code")
  private String code;

  @JsonProperty("message")
  private String message;

  @JsonProperty("links")
  private List<Link> links = null;

  public Error id(String id) {
    this.id = id;
    return this;
  }

  /**
   * A back-end may add a unique identifier to the error response to be able to log and track errors with further non-disclosable details.  A client could communicate this id to a back-end provider to get further information.
   * @return id
   **/
  @JsonProperty("id")
  @ApiModelProperty(example = "550e8400-e29b-11d4-a716-446655440000", value = "A back-end may add a unique identifier to the error response to be able to log and track errors with further non-disclosable details.  A client could communicate this id to a back-end provider to get further information.")
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Error code(String code) {
    this.code = code;
    return this;
  }

  /**
   * The code is either one of the standardized error codes or a custom error code.
   * @return code
   **/
  @JsonProperty("code")
  @ApiModelProperty(example = "SampleError", required = true, value = "The code is either one of the standardized error codes or a custom error code.")
  @NotNull 
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Error message(String message) {
    this.message = message;
    return this;
  }

  /**
   * A message explaining what the client may need to change or what difficulties the server is facing. By default the message must be sent in English language. Content Negotiation is used to localize the error messages: If an Accept-Language header is sent by the client and a translation is available, the message should be translated accordingly and the Content-Language header must be present in the response.
   * @return message
   **/
  @JsonProperty("message")
  @ApiModelProperty(example = "A sample error message.", required = true, value = "A message explaining what the client may need to change or what difficulties the server is facing. By default the message must be sent in English language. Content Negotiation is used to localize the error messages: If an Accept-Language header is sent by the client and a translation is available, the message should be translated accordingly and the Content-Language header must be present in the response.")
  @NotNull 
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Error links(List<Link> links) {
    this.links = links;
    return this;
  }

  public Error addLinksItem(Link linksItem) {
    if (this.links == null) {
      this.links = new ArrayList<Link>();
    }
    this.links.add(linksItem);
    return this;
  }

  /**
   * Additional links related to this error, e.g. a resource that is explaining the error and potential solutions in-depth or a contact e-mail address.
   * @return links
   **/
  @JsonProperty("links")
  @ApiModelProperty(example = "[{\"href\":\"http://www.openeo.org/docs/errors/SampleError\",\"rel\":\"about\"}]", value = "Additional links related to this error, e.g. a resource that is explaining the error and potential solutions in-depth or a contact e-mail address.")
  @Valid 
  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Error error = (Error) o;
    return Objects.equals(this.id, error.id) &&
        Objects.equals(this.code, error.code) &&
        Objects.equals(this.message, error.message) &&
        Objects.equals(this.links, error.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code, message, links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
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
