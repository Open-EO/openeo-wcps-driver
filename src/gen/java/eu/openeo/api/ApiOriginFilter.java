package eu.openeo.api;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJerseyServerCodegen", date = "2019-07-22T13:33:50.326+02:00[Europe/Rome]")
public class ApiOriginFilter implements javax.servlet.Filter {
	
	
	Logger log = Logger.getLogger(this.getClass());
	
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        String clientIp = req.getHeader("Origin");
        if(clientIp == null) {
        	clientIp = req.getHeader("X-Forwarded-For");
        	if(clientIp == null) {
	        	clientIp = request.getRemoteHost();
	        	log.debug("Got direct request from the following client: " + clientIp);
        	}else {
        		log.debug("Got proxy forwared request from the following client: " + clientIp);
        	}
        }else {
        	log.debug("Got request from the following js client: " + clientIp);
        }        
        res.addHeader("Access-Control-Allow-Origin", clientIp);
        res.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, DELETE, PUT, PATCH");
        res.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        res.addHeader("Access-Control-Allow-Credentials", "true");
        res.addHeader("Access-Control-Expose-Headers", "Location, OpenEO-Identifier, OpenEO-Costs");
        chain.doFilter(request, response);
    }

    public void destroy() {}

    public void init(FilterConfig filterConfig) throws ServletException {}
}