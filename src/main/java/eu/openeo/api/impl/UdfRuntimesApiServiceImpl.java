package eu.openeo.api.impl;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONObject;
import org.json.JSONArray;
import eu.openeo.api.ApiResponseMessage;
import eu.openeo.api.NotFoundException;
import eu.openeo.api.UdfRuntimesApiService;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class UdfRuntimesApiServiceImpl extends UdfRuntimesApiService {
    @Override
    public Response udfRuntimesGet(SecurityContext securityContext) throws NotFoundException {
    	JSONObject udfRuntimes = new JSONObject();
    	JSONObject udfPython = new JSONObject();    	
    	JSONArray udfPythonTags = new JSONArray();
    	JSONArray udfPythonLinks = new JSONArray();
    	JSONObject udfPythonLink1 = new JSONObject();    	
    	
    	udfPython.put("description", "Python programming language");
    	udfPython.put("docker", "openeo/udf");
    	udfPython.put("default", "latest");
    	udfPythonLink1.put("href", "https://github.com/Open-EO/openeo-udf");
    	udfPythonLinks.put(udfPythonLink1);
    	udfPythonTags.put("latest");
    	udfPythonTags.put("3.7.1");
    	udfPython.put("tags", udfPythonTags);
    	udfPython.put("links", udfPythonLinks);
    	
    	JSONObject udfR = new JSONObject();
    	JSONObject udfRVersions = new JSONObject();
    	JSONObject udfRLibrariesV1 = new JSONObject();
    	JSONObject udfRLibrariesV2 = new JSONObject();
    	JSONObject udfRLibrariesV1Rcpp = new JSONObject();
    	JSONObject udfRLibrariesV2Rcpp = new JSONObject();
    	JSONObject udfRLibrariesV1Rmarkdown = new JSONObject();
    	JSONObject udfRLibrariesV2Rmarkdown = new JSONObject();
    	JSONObject udfRLibrariesV1_1 = new JSONObject();
    	JSONObject udfRLibrariesV2_1 = new JSONObject();
    	JSONArray udfRLibrariesV1RcppLinks = new JSONArray();
    	JSONArray udfRLibrariesV2RcppLinks = new JSONArray();
    	JSONArray udfRLibrariesV1RmarkdownLinks = new JSONArray();
    	JSONArray udfRLibrariesV2RmarkdownLinks = new JSONArray();
    	JSONObject udfRLibrariesV1RcppLink1 = new JSONObject();
    	JSONObject udfRLibrariesV2RcppLink1 = new JSONObject();
    	JSONObject udfRLibrariesV1RmarkdownLink1 = new JSONObject();
    	JSONObject udfRLibrariesV2RmarkdownLink1 = new JSONObject();
    	
    	udfRLibrariesV1RcppLink1.put("href", "https://cran.r-project.org/web/packages/Rcpp/index.html");
    	udfRLibrariesV1RcppLinks.put(udfRLibrariesV1RcppLink1);
    	udfRLibrariesV1RmarkdownLink1.put("href", "https://cran.r-project.org/web/packages/rmarkdown/index.html");
    	udfRLibrariesV1RmarkdownLinks.put(udfRLibrariesV1RmarkdownLink1);
    	udfRLibrariesV2RcppLink1.put("href", "https://cran.r-project.org/web/packages/Rcpp/index.html");
    	udfRLibrariesV2RcppLinks.put(udfRLibrariesV2RcppLink1);
    	udfRLibrariesV2RmarkdownLink1.put("href", "https://cran.r-project.org/web/packages/rmarkdown/index.html");
    	udfRLibrariesV2RmarkdownLinks.put(udfRLibrariesV2RmarkdownLink1);
    	udfRLibrariesV1Rcpp.put("version", "1.0.10");
    	udfRLibrariesV1Rmarkdown.put("version", "1.7.0");
    	udfRLibrariesV2Rcpp.put("version", "1.2.0");
    	udfRLibrariesV2Rmarkdown.put("version", "1.7.0");
    	udfRLibrariesV1Rcpp.put("links", udfRLibrariesV1RcppLinks);
    	udfRLibrariesV1Rmarkdown.put("links", udfRLibrariesV1RmarkdownLinks);
    	udfRLibrariesV2Rcpp.put("links", udfRLibrariesV2RcppLinks);
    	udfRLibrariesV2Rmarkdown.put("links", udfRLibrariesV2RmarkdownLinks);
    	udfRLibrariesV1_1.put("Rcpp", udfRLibrariesV1Rcpp);
    	udfRLibrariesV1_1.put("Rmarkdown", udfRLibrariesV1Rmarkdown);
    	udfRLibrariesV2_1.put("Rcpp", udfRLibrariesV2Rcpp);
    	udfRLibrariesV2_1.put("Rmarkdown", udfRLibrariesV2Rmarkdown);
    	udfRLibrariesV1.put("libraries", udfRLibrariesV1_1);
    	udfRLibrariesV2.put("libraries", udfRLibrariesV1_1);
    	udfRVersions.put("3.1.0", udfRLibrariesV1);
    	udfRVersions.put("3.5.2", udfRLibrariesV2);
    	udfR.put("description", "R programming language with Rcpp and Rmarkdown.");
    	udfR.put("default", "3.5.2");
    	udfR.put("versions", udfRVersions);
    	
    	udfRuntimes.put("Python", udfPython);
    	udfRuntimes.put("R", udfR);
    	return Response.ok(udfRuntimes.toString(4), MediaType.APPLICATION_JSON).build();
    }
}
