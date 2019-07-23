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
 * An error message that describes the problem during the batch job execution. May only be available if the &#x60;status&#x60; is &#x60;error&#x60;. The error MUST be cleared if the job is started again (i.e. the status changes to &#x60;queue&#x60;).
 */
@ApiModel(description = "An error message that describes the problem during the batch job execution. May only be available if the `status` is `error`. The error MUST be cleared if the job is started again (i.e. the status changes to `queue`).")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class JobError  extends Error implements Serializable {
  /**
	 * 
	 */
	private static final long serialVersionUID = 626638610214804455L;

@JsonProperty("id")
  private String id;

  @JsonProperty("code")
  private String code;

  @JsonProperty("message")
  private String message;

  @JsonProperty("links")
  private List<Link> links = null;

  public JobError id(String id) {
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

  public JobError code(String code) {
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

  public JobError message(String message) {
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

  public JobError links(List<Link> links) {
    this.links = links;
    return this;
  }

  public JobError addLinksItem(Link linksItem) {
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
    JobError jobError = (JobError) o;
    return Objects.equals(this.id, jobError.id) &&
        Objects.equals(this.code, jobError.code) &&
        Objects.equals(this.message, jobError.message) &&
        Objects.equals(this.links, jobError.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code, message, links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JobError {\n");
    
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

