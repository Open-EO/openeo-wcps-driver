# OpenEO WCPS driver prototype for proof of concept

[![Status](https://img.shields.io/badge/Status-proof--of--concept-yellow.svg)]()

## Information
- API version: 0.0.2
- JAVA: 8

This repository serves for the development of the openEO driver for WC(P)S backends.

### Public testing endpoint
[Eurac Research openEO WCPS endpoint](http://saocompute.eurac.edu/openEO_WCPS_Driver/)

### Currently implemented features
1. Listing implemented endpoints through: [/openeo/capabilities](http://saocompute.eurac.edu/openEO_WCPS_Driver/openeo/capabilities)
2. Listing implemented processes through: [/openeo/processes](http://saocompute.eurac.edu/openEO_WCPS_Driver/openeo/processes)
3. Listing available datasets through:    [/openeo/data](http://saocompute.eurac.edu/openEO_WCPS_Driver/openeo/data)
4. Submission, status info and retrieval of processing graphs via /openeo/jobs
5. Processing of raster
  * subsetting in srs projection space
  * Normalied Difference Vegetation Index
6. Download of image as 
  * tiff
  * png
  * jpeg
7. Processing of time series of raster
  * subsetting along time axis
  * Min compositing along time axis
  * Max compositing along time axis
8. Download of time series as
  * json
  * csv
  * netcdf

### Operating environment dependencies
- Web server capable of hosting a war archive (e.g. apache tomcat).
- WCPS compliant web service (e.g. rasdaman 9.5)
