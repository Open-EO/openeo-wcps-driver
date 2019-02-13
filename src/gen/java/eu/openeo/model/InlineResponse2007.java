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
import eu.openeo.model.InlineResponse2007ProcessGraphs;
import eu.openeo.model.Link;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * InlineResponse2007
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-02-12T13:52:55.621+01:00[Europe/Rome]")
public class InlineResponse2007  implements Serializable {
  @JsonProperty("process_graphs")
  private List<InlineResponse2007ProcessGraphs> processGraphs = new ArrayList<InlineResponse2007ProcessGraphs>();

  @JsonProperty("links")
  private List<Link> links = new ArrayList<Link>();

  public InlineResponse2007 processGraphs(List<InlineResponse2007ProcessGraphs> processGraphs) {
    this.processGraphs = processGraphs;
    return this;
  }

  public InlineResponse2007 addProcessGraphsItem(InlineResponse2007ProcessGraphs processGraphsItem) {
    this.processGraphs.add(processGraphsItem);
    return this;
  }

  /**
   * Array of stored process graphs
   * @return processGraphs
   **/
  @JsonProperty("process_graphs")
  @ApiModelProperty(required = true, value = "Array of stored process graphs")
    @NotNull
@Valid
  public List<InlineResponse2007ProcessGraphs> getProcessGraphs() {
    return processGraphs;
  }

  public void setProcessGraphs(List<InlineResponse2007ProcessGraphs> processGraphs) {
    this.processGraphs = processGraphs;
  }

  public InlineResponse2007 links(List<Link> links) {
    this.links = links;
    return this;
  }

  public InlineResponse2007 addLinksItem(Link linksItem) {
    this.links.add(linksItem);
    return this;
  }

  /**
   * Additional links related to this list of resources. Could reference to alternative formats such as a rendered HTML version.  The links could also be used for pagination using the [rel types](https://www.iana.org/assignments/link-relations/link-relations.xhtml) &#x60;first&#x60;, &#x60;prev&#x60;, &#x60;next&#x60; and &#x60;last&#x60;. Pagination is currently OPTIONAL and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless.
   * @return links
   **/
  @JsonProperty("links")
  @ApiModelProperty(required = true, value = "Additional links related to this list of resources. Could reference to alternative formats such as a rendered HTML version.  The links could also be used for pagination using the [rel types](https://www.iana.org/assignments/link-relations/link-relations.xhtml) `first`, `prev`, `next` and `last`. Pagination is currently OPTIONAL and clients may not support it. Therefore it MUST be implemented in a way that clients not supporting pagination get all resources regardless.")
    @NotNull
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
    InlineResponse2007 inlineResponse2007 = (InlineResponse2007) o;
    return Objects.equals(this.processGraphs, inlineResponse2007.processGraphs) &&
        Objects.equals(this.links, inlineResponse2007.links);
  }

  @Override
  public int hashCode() {
    return Objects.hash(processGraphs, links);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse2007 {\n");
    
    sb.append("    processGraphs: ").append(toIndentedString(processGraphs)).append("\n");
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

