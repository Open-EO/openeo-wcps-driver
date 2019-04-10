/*
 * OpenEO API
 * The OpenEO API specification for interoperable cloud-based processing of large Earth observation datasets. **This early draft version is incomplete and intended for working on a prototype and a proof of concept.** Things that are currently missing particularly include:   * Authentication and authorization with OAuth 2.0,   * how results of computations can be downloaded,   * how data is streamed into UDFs and how the output of UDFs is returned,   * how services are organized as microservices,   * how payments can be handled,  * how resources (e.g. process graphs) can be shared,  * and how OpenSearch is interfaced
 *
 * OpenAPI spec version: 0.0.2
 * Contact: marius.appel@uni-muenster.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package eu.openeo.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Defines and describes a process including it&#39;s expected input arguments.
 */
@ApiModel(description = "Links related to the list of Processes.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-02-26T14:26:50.688+01:00")
public class LinksDesc implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3101125036875386069L;

	@JsonProperty("rel")
	private String rel = null;

	@JsonProperty("href")
	private String href = null;
	
	@JsonProperty("type")
	private String type = null;

	@JsonProperty("title")
	private String title = null;
	
	

	public LinksDesc rel(String rel) {
		this.rel = rel;
		return this;
	}

	/**
	 * The unique identifier of the process.
	 * 
	 * @return processId
	 **/
	@JsonProperty("rel")
	@ApiModelProperty(required = true, value = "rel.")
	@NotNull
	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public LinksDesc href(String href) {
		this.href = href;
		return this;
	}

	/**
	 * A short and concise description of what the process does and how the output
	 * looks like.
	 * 
	 * @return description
	 **/
	@JsonProperty("href")
	@ApiModelProperty(required = true, value = "href.")
	@NotNull
	public String getHref() {
		return href;
	}

	public void sethref(String href) {
		this.href = href;
	}
	
	@JsonProperty("type")
	@ApiModelProperty(required = true, value = "type")
	@NotNull
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LinksDesc title(String title) {
		this.title = title;
		return this;
	}
	
	
	/**
	 * Reference to an external process definition if the process has been defined
	 * over different back ends within OpenEO
	 * 
	 * @return link
	 **/
	@JsonProperty("title")
	@ApiModelProperty(value = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	
	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LinksDesc linksDesc = (LinksDesc) o;
		return Objects.equals(this.rel, linksDesc.rel)
				&& Objects.equals(this.href, linksDesc.href)
				&& Objects.equals(this.type, linksDesc.type)
				&& Objects.equals(this.title, linksDesc.title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rel, href, type, title);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class LinksDesc {\n");

		sb.append("    rel: ").append(toIndentedString(rel)).append("\n");
		sb.append("    href: ").append(toIndentedString(href)).append("\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    title: ").append(toIndentedString(title)).append("\n");
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