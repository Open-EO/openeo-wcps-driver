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

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The current status of the job.
 */
public enum JobStatus {
  
  SUBMITTED("submitted"),
  
  QUEUED("queued"),
  
  RUNNING("running"),
  
  FINISHED("finished"),
  
  PAUSED("paused"),
  
  CANCELED("canceled"),
  
  ERROR("error"),
  
  UNKNOWN("unknown");

  private String value;

  JobStatus(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static JobStatus fromValue(String text) {
    for (JobStatus b : JobStatus.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

