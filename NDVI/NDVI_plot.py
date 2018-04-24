# Import the Python 3 print function
from __future__ import print_function
import numpy as np
# Import the "gdal" and "gdal_array" submodules from within the "osgeo" module
from osgeo import gdal
from osgeo import gdal_array
import matplotlib.pyplot as plt
# Import the NumPy module

#aim of this is to test some of the NDVI files and visualize them

# # Array of 0 - 9
# x = np.arange(10)
# # 10 random numbers, between 0 and 10
# y = np.random.randint(0, 10, size=10)

# # plot them as lines
# plt.plot(x, y)

# Open a GDAL dataset
dataset = gdal.Open('sample_NDVI1.gtif', gdal.GA_ReadOnly)

# Allocate our array using the first band's datatype
image_datatype = dataset.GetRasterBand(1).DataType

image = np.zeros((dataset.RasterYSize, dataset.RasterXSize, dataset.RasterCount),
                 dtype=gdal_array.GDALTypeCodeToNumericTypeCode(image_datatype))

# Loop over all bands in dataset
for b in range(dataset.RasterCount):
    # Remember, GDAL index is on 1, but Python is on 0 -- so we add 1 for our GDAL calls
    band = dataset.GetRasterBand(b + 1)
    
    # Read in the band's data into the third dimension of our array
    image[:, :, b] = band.ReadAsArray()

ndvi = (image[:, :, 3] - image[:, :, 2]) / \
        (image[:, :, 3] + image[:, :, 2]).astype(np.float64)
# use "imshow" for an image -- nir at first
plt.imshow(image[:, :, 3])
plt.colorbar()
plt.show()
# use "imshow" for an image -- nir in first subplot, red in second
plt.subplot(121)
plt.imshow(image[:, :, 3], cmap=plt.cm.Greys)
plt.colorbar()
# plt.show()
# Now red band in the second subplot (indicated by last of the 3 numbers)
plt.subplot(122)
plt.imshow(image[:, :, 2], cmap=plt.cm.Greys)
plt.colorbar()
plt.show()
# Extract reference to SWIR1, NIR, and Red bands
index = np.array([4, 3, 2])
colors = image[:, :, index].astype(np.float64)

max_val = 8000
min_val = 0

# Enforce maximum and minimum values
colors[colors[:, :, :] > max_val] = max_val
colors[colors[:, :, :] < min_val] = min_val

for b in range(colors.shape[2]):
    colors[:, :, b] = colors[:, :, b] * 1 / (max_val - min_val)

plt.subplot(121)
plt.imshow(colors)
# plt.show()
# Show NDVI
plt.subplot(122)
plt.imshow(ndvi, cmap=plt.cm.Greys_r)
plt.show()