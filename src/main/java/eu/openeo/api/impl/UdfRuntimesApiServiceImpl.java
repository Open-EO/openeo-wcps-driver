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
    	// JSONObject udfRLibrariesV1 = new JSONObject();   	
    	// JSONObject udfRLibrariesV1_1 = new JSONObject();
    	JSONObject udfRLibrariesV2 = new JSONObject();
    	JSONObject udfRLibrariesV2_1 = new JSONObject();
    	// JSONObject udfRLibrariesV1Plumber = new JSONObject();    	
    	// JSONObject udfRLibrariesV1Remotes = new JSONObject();    	
    	// JSONArray udfRLibrariesV1PlumberLinks = new JSONArray();    	
    	// JSONArray udfRLibrariesV1RemotesLinks = new JSONArray();    	
    	// JSONObject udfRLibrariesV1PlumberLink1 = new JSONObject();    	
    	// JSONObject udfRLibrariesV1RemotesLink1 = new JSONObject();
    	JSONObject udfRLibrariesV2Plumber = new JSONObject();
    	JSONObject udfRLibrariesV2Remotes = new JSONObject();
    	JSONObject udfRLibrariesV2SF = new JSONObject();
    	JSONObject udfRLibrariesV2Lubridate = new JSONObject();
    	JSONObject udfRLibrariesV2Stars = new JSONObject();
    	JSONObject udfRLibrariesV2Zoo = new JSONObject();
    	JSONObject udfRLibrariesV2XTS = new JSONObject();
    	
    	JSONArray udfRLibrariesV2PlumberLinks = new JSONArray();
    	JSONArray udfRLibrariesV2RemotesLinks = new JSONArray();
    	JSONArray udfRLibrariesV2SFLinks = new JSONArray();
    	JSONArray udfRLibrariesV2LubridateLinks = new JSONArray();
    	JSONArray udfRLibrariesV2StarsLinks = new JSONArray();
    	JSONArray udfRLibrariesV2ZooLinks = new JSONArray();
    	JSONArray udfRLibrariesV2XTSLinks = new JSONArray();

    	JSONObject udfRLibrariesV2PlumberLink1 = new JSONObject();
    	JSONObject udfRLibrariesV2RemotesLink1 = new JSONObject();
    	JSONObject udfRLibrariesV2SFLink1 = new JSONObject();
    	JSONObject udfRLibrariesV2LubridateLink1 = new JSONObject();
    	JSONObject udfRLibrariesV2StarsLink1 = new JSONObject();
    	JSONObject udfRLibrariesV2ZooLink1 = new JSONObject();
    	JSONObject udfRLibrariesV2XTSLink1 = new JSONObject();
    	
    	// udfRLibrariesV1RcppLink1.put("href", "https://cran.r-project.org/web/packages/Rcpp/index.html");
    	// udfRLibrariesV1RcppLinks.put(udfRLibrariesV1RcppLink1);
    	// udfRLibrariesV1RmarkdownLink1.put("href", "https://cran.r-project.org/web/packages/rmarkdown/index.html");
    	// udfRLibrariesV1RmarkdownLinks.put(udfRLibrariesV1RmarkdownLink1);
    	udfRLibrariesV2PlumberLink1.put("href", "https://cran.r-project.org/web/packages/plumber/index.html");
    	udfRLibrariesV2PlumberLinks.put(udfRLibrariesV2PlumberLink1);
    	udfRLibrariesV2RemotesLink1.put("href", "https://cran.r-project.org/web/packages/remotes/index.html");
    	udfRLibrariesV2RemotesLinks.put(udfRLibrariesV2RemotesLink1);
    	
    	udfRLibrariesV2SFLink1.put("href", "https://cran.r-project.org/web/packages/sf/index.html");
    	udfRLibrariesV2SFLinks.put(udfRLibrariesV2SFLink1);
    	
    	udfRLibrariesV2LubridateLink1.put("href", "https://cran.r-project.org/web/packages/lubridate/index.html");
    	udfRLibrariesV2LubridateLinks.put(udfRLibrariesV2LubridateLink1);
    	
    	udfRLibrariesV2StarsLink1.put("href", "https://cran.r-project.org/web/packages/stars/index.html");
    	udfRLibrariesV2StarsLinks.put(udfRLibrariesV2StarsLink1);
    	
    	udfRLibrariesV2ZooLink1.put("href", "https://cran.r-project.org/web/packages/zoo/index.html");
    	udfRLibrariesV2ZooLinks.put(udfRLibrariesV2ZooLink1);
    	
    	udfRLibrariesV2XTSLink1.put("href", "https://cran.r-project.org/web/packages/xts/index.html");
    	udfRLibrariesV2XTSLinks.put(udfRLibrariesV2XTSLink1);
    	// udfRLibrariesV1Rcpp.put("version", "1.0.10");
    	// udfRLibrariesV1Rmarkdown.put("version", "1.7.0");
    	udfRLibrariesV2Plumber.put("version", "0.4.6");
    	udfRLibrariesV2Remotes.put("version", "2.1.0");
    	udfRLibrariesV2SF.put("version", "0.8-0");
    	udfRLibrariesV2Lubridate.put("version", "1.7.4");
    	udfRLibrariesV2Stars.put("version", "0.4-0");
    	udfRLibrariesV2Zoo.put("version", "1.8-7");
    	udfRLibrariesV2XTS.put("version", "0.11-2");
    	// udfRLibrariesV1Rcpp.put("links", udfRLibrariesV1RcppLinks);
    	// udfRLibrariesV1Rmarkdown.put("links", udfRLibrariesV1RmarkdownLinks);
    	udfRLibrariesV2Plumber.put("links", udfRLibrariesV2PlumberLinks);
    	udfRLibrariesV2Remotes.put("links", udfRLibrariesV2RemotesLinks);
    	udfRLibrariesV2SF.put("links", udfRLibrariesV2SFLinks);
    	udfRLibrariesV2Lubridate.put("links", udfRLibrariesV2LubridateLinks);
    	udfRLibrariesV2Stars.put("links", udfRLibrariesV2StarsLinks);
    	udfRLibrariesV2Zoo.put("links", udfRLibrariesV2ZooLinks);
    	udfRLibrariesV2XTS.put("links", udfRLibrariesV2XTSLinks);
    	// udfRLibrariesV1_1.put("Rcpp", udfRLibrariesV1Rcpp);
    	// udfRLibrariesV1_1.put("Rmarkdown", udfRLibrariesV1Rmarkdown);
    	udfRLibrariesV2_1.put("plumber", udfRLibrariesV2Plumber);
    	udfRLibrariesV2_1.put("remotes", udfRLibrariesV2Remotes);
    	udfRLibrariesV2_1.put("sf", udfRLibrariesV2SF);
    	udfRLibrariesV2_1.put("lubridate", udfRLibrariesV2Lubridate);
    	udfRLibrariesV2_1.put("stars", udfRLibrariesV2Stars);
    	udfRLibrariesV2_1.put("zoo", udfRLibrariesV2Zoo);
    	udfRLibrariesV2_1.put("xts", udfRLibrariesV2XTS);
    	// udfRLibrariesV1.put("libraries", udfRLibrariesV1_1);
    	udfRLibrariesV2.put("libraries", udfRLibrariesV2_1);
    	// udfRVersions.put("3.4.4", udfRLibrariesV1);
    	udfRVersions.put("3.6.1", udfRLibrariesV2);
    	udfR.put("description", "R programming language with latest packages of plumber, remotes, sf, lubridate, stars, zoo and xts.");
    	udfR.put("default", "3.6.1");
    	udfR.put("versions", udfRVersions);
    	
    	udfRuntimes.put("Python", udfPython);
    	udfRuntimes.put("R", udfR);
    	return Response.ok(udfRuntimes.toString(4), MediaType.APPLICATION_JSON).build();
    }
}
