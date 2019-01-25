# OpenEO WCPS driver prototype for proof of concept

[![Status](https://img.shields.io/badge/Status-proof--of--concept-yellow.svg)]()

## Information
- API version: 0.3.1
- JAVA: 8

This repository serves for the development of the openEO driver for WC(P)S backends.

## Links

### OpenEO project description
[openEO.org](http://openeo.org/)
### OpenEO core API definition
[openEO core API](https://open-eo.github.io/openeo-api/)
### Public testing endpoint (hosted @ [Eurac Research](http://www.eurac.edu))
[http://saocompute.eurac.edu/openEO_0_3_0/openeo/](http://saocompute.eurac.edu/openEO_0_3_0/openeo/)

## Usage of the service

### Currently implemented features
1. Listing implemented endpoints through: [/openeo/](http://saocompute.eurac.edu/openEO_0_3_0/openeo/)
2. Listing implemented processes through: [/openeo/processes](http://saocompute.eurac.edu/openEO_0_3_0/openeo/processes)
3. Listing available datasets through:    [/openeo/collections](http://saocompute.eurac.edu/openEO_0_3_0/openeo/collections)
4. Submission of processing graphs via /openeo/jobs
5. Direct execution and result retrieval via /openeo/execute
6. Processing of raster
  * subsetting in crs projection space
  * Normalied Difference Vegetation Index
7. Download of image as 
  * tiff
  * png
  * jpeg
8. Processing of time series of raster
  * subsetting along time axis
  * Min compositing along time axis
  * Max compositing along time axis
8. Download of time series as
  * json
  * csv
  * netcdf

A note on download activities. In this domain the service is not fully compliant with the 0.3.0 API specification as a /openeo/jobs/{job-id}/results request returns directly the required file and not a list of links as specified in the API.
  
## Setup of the service

### Operating environment dependencies
- Web server capable of hosting a war archive (e.g. apache tomcat).
- WCPS compliant web service (e.g. rasdaman 9.5)

### Configuration
Configuration of the running service is done in the *config.properties* file, which should be in the class path of the war file.
- wcps-endpoint should give the location of the wcps endpoint that executes processing requests
- job-database should point to the location of the sqlite database that handles job persistence

### Compiling the WAR-archive
The current git project is setup as a runnable web archive maven project in eclipse. In order to compile the source simple run maven clean install. The project is configured to pack all necessary dependencies into the WAR so no extra libraries need to be installed on the host machine running the web server instance and one simply needs to copy the war archive into the e.g. the webapps folder of a tomcat installation. 

### Migration Plan for v0.4.0

- [ ] 
- [ ] 
- [ ] 
