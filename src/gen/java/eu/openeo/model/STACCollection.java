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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * STACCollection
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class STACCollection  implements Serializable {
  @JsonProperty("stac_version")
  private String stacVersion;

  @JsonProperty("id")
  private String id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("keywords")
  private List<String> keywords = null;

  @JsonProperty("version")
  private String version;

  @JsonProperty("license")
  private String license;

  @JsonProperty("providers")
  private List<Object> providers = null;

  @JsonProperty("extent")
  private CollectionExtent extent = null;

  @JsonProperty("links")
  private List<Link> links = new ArrayList<Link>();

  @JsonProperty("other_properties")
  private Map<String, STACCollectionOtherProperties> otherProperties = new HashMap<String, STACCollectionOtherProperties>();

  @JsonProperty("properties")
  private STACCollectionProperties properties = null;

  public STACCollection stacVersion(String stacVersion) {
    this.stacVersion = stacVersion;
    return this;
  }

  /**
   * The STAC version the collection implements.
   * @return stacVersion
   **/
  @JsonProperty("stac_version")
  @ApiModelProperty(example = "0.6.2", required = true, value = "The STAC version the collection implements.")
  @NotNull 
  public String getStacVersion() {
    return stacVersion;
  }

  public void setStacVersion(String stacVersion) {
    this.stacVersion = stacVersion;
  }

  public STACCollection id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier for the collection that is unique across the provider.  MUST match the specified pattern.
   * @return id
   **/
  @JsonProperty("id")
  @ApiModelProperty(example = "Sentinel-2A", required = true, value = "Identifier for the collection that is unique across the provider.  MUST match the specified pattern.")
  @NotNull  @Pattern(regexp="^[A-Za-z0-9_\\-\\.~/]+$")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public STACCollection title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A short descriptive one-line title for the collection.
   * @return title
   **/
  @JsonProperty("title")
  @ApiModelProperty(value = "A short descriptive one-line title for the collection.")
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public STACCollection description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Detailed multi-line description to fully explain the collection.  [CommonMark 0.28](http://commonmark.org/) syntax MAY be used for rich text representation.
   * @return description
   **/
  @JsonProperty("description")
  @ApiModelProperty(required = true, value = "Detailed multi-line description to fully explain the collection.  [CommonMark 0.28](http://commonmark.org/) syntax MAY be used for rich text representation.")
  @NotNull 
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public STACCollection keywords(List<String> keywords) {
    this.keywords = keywords;
    return this;
  }

  public STACCollection addKeywordsItem(String keywordsItem) {
    if (this.keywords == null) {
      this.keywords = new ArrayList<String>();
    }
    this.keywords.add(keywordsItem);
    return this;
  }

  /**
   * List of keywords describing the collection.
   * @return keywords
   **/
  @JsonProperty("keywords")
  @ApiModelProperty(value = "List of keywords describing the collection.")
  
  public List<String> getKeywords() {
    return keywords;
  }

  public void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }

  public STACCollection version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Version of the collection.
   * @return version
   **/
  @JsonProperty("version")
  @ApiModelProperty(value = "Version of the collection.")
  
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public STACCollection license(String license) {
    this.license = license;
    return this;
  }

  /**
   * Collection&#39;s license(s) as a SPDX [License identifier](https://spdx.org/licenses/) or  [expression](https://spdx.org/spdx-specification-21-web-version#h.jxpfx0ykyb60), or &#x60;proprietary&#x60; if the license is not on the SPDX license list. Proprietary licensed data SHOULD add a link to the license text with the &#x60;license&#x60; relation in the links section. The license text MUST NOT be provided as a value of this field. If there is no public license URL available, it is RECOMMENDED to host the license text independently and link to it.
   * @return license
   **/
  @JsonProperty("license")
  @ApiModelProperty(example = "Apache-2.0", required = true, value = "Collection's license(s) as a SPDX [License identifier](https://spdx.org/licenses/) or  [expression](https://spdx.org/spdx-specification-21-web-version#h.jxpfx0ykyb60), or `proprietary` if the license is not on the SPDX license list. Proprietary licensed data SHOULD add a link to the license text with the `license` relation in the links section. The license text MUST NOT be provided as a value of this field. If there is no public license URL available, it is RECOMMENDED to host the license text independently and link to it.")
  @NotNull 
  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public STACCollection providers(List<Object> providers) {
    this.providers = providers;
    return this;
  }

  public STACCollection addProvidersItem(Object providersItem) {
    if (this.providers == null) {
      this.providers = new ArrayList<Object>();
    }
    this.providers.add(providersItem);
    return this;
  }

  /**
   * A list of providers, which may include all organizations capturing or processing the data or the hosting provider. Providers should be listed in chronological order with the most recent provider being the last element of the list.
   * @return providers
   **/
  @JsonProperty("providers")
  @ApiModelProperty(value = "A list of providers, which may include all organizations capturing or processing the data or the hosting provider. Providers should be listed in chronological order with the most recent provider being the last element of the list.")
  
  public List<Object> getProviders() {
    return providers;
  }

  public void setProviders(List<Object> providers) {
    this.providers = providers;
  }

  public STACCollection extent(CollectionExtent extent) {
    this.extent = extent;
    return this;
  }

  /**
   * Get extent
   * @return extent
   **/
  @JsonProperty("extent")
  @ApiModelProperty(required = true, value = "")
  @NotNull @Valid 
  public CollectionExtent getExtent() {
    return extent;
  }

  public void setExtent(CollectionExtent extent) {
    this.extent = extent;
  }

  public STACCollection links(List<Link> links) {
    this.links = links;
    return this;
  }

  public STACCollection addLinksItem(Link linksItem) {
    this.links.add(linksItem);
    return this;
  }

  /**
   * Additional links related to this collection. Could reference to other meta data formats with additional information or a preview image.
   * @return links
   **/
  @JsonProperty("links")
  @ApiModelProperty(required = true, value = "Additional links related to this collection. Could reference to other meta data formats with additional information or a preview image.")
  @NotNull @Valid 
  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }

  public STACCollection otherProperties(Map<String, STACCollectionOtherProperties> otherProperties) {
    this.otherProperties = otherProperties;
    return this;
  }

  public STACCollection putOtherPropertiesItem(String key, STACCollectionOtherProperties otherPropertiesItem) {
    this.otherProperties.put(key, otherPropertiesItem);
    return this;
  }

  /**
   * A list of all metadata properties, which don&#39;t have common values across the whole collection. Therefore it allows to specify a summary of the values as extent or set of values.
   * @return otherProperties
   **/
  @JsonProperty("other_properties")
  @ApiModelProperty(required = true, value = "A list of all metadata properties, which don't have common values across the whole collection. Therefore it allows to specify a summary of the values as extent or set of values.")
  @NotNull @Valid 
  public Map<String, STACCollectionOtherProperties> getOtherProperties() {
    return otherProperties;
  }

  public void setOtherProperties(Map<String, STACCollectionOtherProperties> otherProperties) {
    this.otherProperties = otherProperties;
  }

  public STACCollection properties(STACCollectionProperties properties) {
    this.properties = properties;
    return this;
  }

  /**
   * A list of all metadata properties, which are common across the whole collection.
   * @return properties
   **/
  @JsonProperty("properties")
  @ApiModelProperty(required = true, value = "A list of all metadata properties, which are common across the whole collection.")
  @NotNull @Valid 
  public STACCollectionProperties getProperties() {
    return properties;
  }

  public void setProperties(STACCollectionProperties properties) {
    this.properties = properties;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    STACCollection stACCollection = (STACCollection) o;
    return Objects.equals(this.stacVersion, stACCollection.stacVersion) &&
        Objects.equals(this.id, stACCollection.id) &&
        Objects.equals(this.title, stACCollection.title) &&
        Objects.equals(this.description, stACCollection.description) &&
        Objects.equals(this.keywords, stACCollection.keywords) &&
        Objects.equals(this.version, stACCollection.version) &&
        Objects.equals(this.license, stACCollection.license) &&
        Objects.equals(this.providers, stACCollection.providers) &&
        Objects.equals(this.extent, stACCollection.extent) &&
        Objects.equals(this.links, stACCollection.links) &&
        Objects.equals(this.otherProperties, stACCollection.otherProperties) &&
        Objects.equals(this.properties, stACCollection.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stacVersion, id, title, description, keywords, version, license, providers, extent, links, otherProperties, properties);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class STACCollection {\n");
    
    sb.append("    stacVersion: ").append(toIndentedString(stacVersion)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    license: ").append(toIndentedString(license)).append("\n");
    sb.append("    providers: ").append(toIndentedString(providers)).append("\n");
    sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    otherProperties: ").append(toIndentedString(otherProperties)).append("\n");
    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
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

