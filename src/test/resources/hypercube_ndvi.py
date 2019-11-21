# -*- coding: utf-8 -*-

from openeo_udf.api.hypercube import HyperCube
from openeo_udf.api.udf_data import UdfData

__license__ = "Apache License, Version 2.0"
__author__ = "Alexander Jacob"
__copyright__ = "Copyright 2019, Alexander Jacob"
__maintainer__ = "Alexander Jacob"
__email__ = "alexander.jacob@eurac.edu"
__credit__= "Inspired by original version of Soeren Gebbert"


def hyper_ndvi(udf_data: UdfData):
    """Compute the NDVI based on RED and NIR hypercubes

    A 4-dimensional hypercube is required with the second dimension containing the bands "red" and "nir" are required. 
    The NDVI computation will be applied to all hypercube dimensions.

    Args:
        udf_data (UdfData): The UDF data object that contains raster and vector tiles as well as hypercubes
        and structured data.

    Returns:
        This function will not return anything, the UdfData object "udf_data" must be used to store the resulting
        data.

    """
    red = None
    nir = None
    
    hyper_cube = None

    # Check if required hyper cube is present in list of hyper cubes
    for cube in udf_data.get_hypercube_list():
        if "hypercube1" in cube.id.lower():
            hyper_cube = cube
    if hyper_cube is None:
        raise Exception("Hyper cube is missing in input")

    red = hyper_cube.get_array().loc[:,"B04",:,:]
    nir = hyper_cube.get_array().loc[:,"B08",:,:]

    ndvi = (nir - red) / (nir + red)
    ndvi.name = "NDVI"

    hc = HyperCube(array=ndvi)
    udf_data.set_hypercube_list([hc, ])


# This function call is the entry point for the UDF.
# The caller will provide all required data in the **data** object.
hyper_ndvi(data)
