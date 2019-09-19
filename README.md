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
2. Listing implemented versions: [/.well-known/openeo (GET)](https://openeo.eurac.edu/.well-known/openeo)
3. Listing implemented processes through: [/processes (GET)](https://openeo.eurac.edu/processes)
4. Listing available datasets through:    [/collections (GET)](https://openeo.eurac.edu/collections)
5. Authentication via http BASIC: [/credentials/basic (GET)](https://openeo.eurac.edu/credentials/basic)
6. Information on authenticated user: [/me (GET)](https://openeo.eurac.edu/me)
7. List of submitted jobs: [/jobs (GET)](https://openeo.eurac.edu/jobs)
8. Submission of jobs via: [/jobs (POST)](https://openeo.eurac.edu/jobs)
9. Status of specific job: [/jobs/{job-id} (GET)](https://openeo.eurac.edu/jobs/{job-id})
9. Update a specific job: [/jobs/{job-id} (PATCH)](https://openeo.eurac.edu/jobs/{job-id})
9. Delete a specific job: [/jobs/{job-id} (DELETE)](https://openeo.eurac.edu/jobs/{job-id})
10. Submit a specific job to processing queue: [/jobs/{job-id}/results (POST)](https://openeo.eurac.edu/jobs/{job-id}/results)
11. Obtain list of results of a specific job: [/jobs/{job-id}/results (GET)](https://openeo.eurac.edu/jobs/{job-id}/results)
12. Direct execution and result retrieval via [/results (POST)](https://openeo.eurac.edu/results)
13. Processing of raster
  * subsetting in crs projection space
  * Normalied Difference Vegetation Index
  * Arithemeatic, logarithmic and Trigonometric Processing
  * Boolean operations possible except on Date, Time or Strings
14. Download of image as 
  * tiff
  * png
  * jpeg
15. Processing of time series of raster
  * subsetting along time axis
  * Min compositing along time axis
  * Max compositing along time axis
16. Download of time series as
  * json
  * csv
  * netcdf
  
## Setup of the service

### Operating environment dependencies
- Web server capable of hosting a war archive (e.g. apache tomcat).
- WCPS compliant web service (e.g. rasdaman 9.8)
- A signed and trusted certificate for setup of https
- A database for user data management (e.g. in most simple case tomcat-users.xml)
- sqlite3 for management of authentication and batch job processing database
- disk storage for hosting of results temporarily from process graph execution.

### Configuration
Configuration of the running service is done in the *config.properties* file, which should be in the class path of the war file.
- wcps-endpoint should give the location of the wcps endpoint that executes processing requests
- job-database should point to the location of the sqlite database that handles job persistence

### Compiling the WAR-archive
The current git project is setup as a runnable web archive maven project in eclipse. In order to compile the source simple run maven clean install. The project is configured to pack all necessary dependencies into the WAR so no extra libraries need to be installed on the host machine running the web server instance and one simply needs to copy the war archive into the e.g. the webapps folder of a tomcat installation. 

### Migration Plan for v0.4

- file handling -> in development 
- authentication with openID connect
- process graph storage
- process graph validation
