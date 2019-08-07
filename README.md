# OpenEO WCPS driver prototype for proof of concept

[![Status](https://img.shields.io/badge/Status-proof--of--concept-yellow.svg)]()

## Information
- API version: 0.4.2
- JAVA: 8

This repository serves for the development of the openEO driver for WC(P)S backends.

## Links

### OpenEO project description
[openEO.org](http://openeo.org/)
### OpenEO core API definition
[openEO core API](https://open-eo.github.io/openeo-api/)
### Public testing endpoint (hosted @ [Eurac Research](http://www.eurac.edu))
[https://openeo.eurac.edu](https://openeo.eurac.edu)

## Usage of the service

### Currently implemented features
1. Listing implemented endpoints through: [/ (GET)](https://openeo.eurac.edu/)
2. Listing implemented processes through: [/processes (GET)](https://openeo.eurac.edu/processes)
3. Listing available datasets through:    [/collections (GET)](https://openeo.eurac.edu/collections)
4. Submission of processing graphs via [/jobs (POST)](https://openeo.eurac.edu/jobs)
5. Direct execution and result retrieval via [/results (POST)](https://openeo.eurac.edu/results)
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
  
## Setup of the service

### Operating environment dependencies
- Web server capable of hosting a war archive (e.g. apache tomcat).
- WCPS compliant web service (e.g. rasdaman 9.8)

### Configuration
Configuration of the running service is done in the *config.properties* file, which should be in the class path of the war file.
- wcps-endpoint should give the location of the wcps endpoint that executes processing requests
- job-database should point to the location of the sqlite database that handles job persistence

### Compiling the WAR-archive
The current git project is setup as a runnable web archive maven project in eclipse. In order to compile the source simple run maven clean install. The project is configured to pack all necessary dependencies into the WAR so no extra libraries need to be installed on the host machine running the web server instance and one simply needs to copy the war archive into the e.g. the webapps folder of a tomcat installation. 

### Migration Plan for v0.4

- file handling -> in development 
- authentication
- process graph storage
- process graph validation
