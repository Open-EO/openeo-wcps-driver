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
 * HTTPBasicAuthenticationResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class HTTPBasicAuthenticationResponse  implements Serializable {
  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("access_token")
  private String accessToken;

  public HTTPBasicAuthenticationResponse userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Unique identifier of the user. MUST match the specified pattern. SHOULD be a human-friendly user name instead of a randomly generated identifier.
   * @return userId
   **/
  @JsonProperty("user_id")
  @ApiModelProperty(example = "john_doe", required = true, value = "Unique identifier of the user. MUST match the specified pattern. SHOULD be a human-friendly user name instead of a randomly generated identifier.")
  @NotNull  @Pattern(regexp="^[A-Za-z0-9_\\-\\.~]+$")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public HTTPBasicAuthenticationResponse accessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  /**
   * An access token that is used for subsequent calls to the API as Bearer token in the &#x60;Authorization&#x60; header.
   * @return accessToken
   **/
  @JsonProperty("access_token")
  @ApiModelProperty(example = "b34ba2bdf9ac9ee1", required = true, value = "An access token that is used for subsequent calls to the API as Bearer token in the `Authorization` header.")
  @NotNull 
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HTTPBasicAuthenticationResponse htTPBasicAuthenticationResponse = (HTTPBasicAuthenticationResponse) o;
    return Objects.equals(this.userId, htTPBasicAuthenticationResponse.userId) &&
        Objects.equals(this.accessToken, htTPBasicAuthenticationResponse.accessToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, accessToken);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HTTPBasicAuthenticationResponse {\n");
    
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
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
