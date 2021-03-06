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
import eu.openeo.model.SimpleSTACCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * CollectionsResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class CollectionsResponse  implements Serializable {
  @JsonProperty("collections")
  private List<SimpleSTACCollection> collections = new ArrayList<SimpleSTACCollection>();

  @JsonProperty("links")
  private List<Link> links = new ArrayList<Link>();

  public CollectionsResponse collections(List<SimpleSTACCollection> collections) {
    this.collections = collections;
    return this;
  }

  public CollectionsResponse addCollectionsItem(SimpleSTACCollection collectionsItem) {
    this.collections.add(collectionsItem);
    return this;
  }

  /**
   * Get collections
   * @return collections
   **/
  @JsonProperty("collections")
  @ApiModelProperty(required = true, value = "")
  @NotNull @Valid 
  public List<SimpleSTACCollection> getCollections() {
    return collections;
  }

  public void setCollections(List<SimpleSTACCollection> collections) {
    this.collections = collections;
  }

  public CollectionsResponse links(List<Link> links) {
    this.links = links;
    return this;
  }

  public CollectionsResponse addLinksItem(Link linksItem) {
    this.links.add(linksItem);
    return this;
  }

  /**
   * General links related to the data discovery service, for example  another service such as a OGC WCS that describes all the collections.
   * @return links
   **/
  @JsonProperty("links")
  @ApiModelProperty(required = true, value = "General links related to the data discovery service, for example  another service such as a OGC WCS that describes all the collections.")
  @NotNull @Valid 
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
    CollectionsResponse collectionsResponse = (CollectionsResponse) o;
    return Objects.equals(this.collections, collectionsResponse.collections) &&
        Objects.equals(this.links, collectionsResponse.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(collections, links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollectionsResponse {\n");
    
    sb.append("    collections: ").append(toIndentedString(collections)).append("\n");
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

