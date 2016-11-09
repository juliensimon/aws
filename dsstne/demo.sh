#!/bin/bash

export PATH=~/amazon-dsstne/src/amazon/dsstne/bin:/usr/local/openmpi/bin:/usr/local/cuda/bin:$PATH
export LD_LIBRARY_PATH=/usr/local/cuda-7.0:/usr/local/cuda-7.0/lib64/

# Generate Input layer and Output Layer
generateNetCDF -d gl_input -i ml20m-all -o gl_input.nc -f features_input -s samples_input -c
generateNetCDF -d gl_output -i ml20m-all -o gl_output.nc -f features_output -s samples_input -c

# Train in parallel
mpirun -np 4 train -c config.json -i gl_input.nc -o gl_output.nc -n gl.nc -b 256 -e 10

# Predict
predict -b 1024 -d gl -i features_input -o features_output -k 10 -n gl.nc -f ml20m-all -s recs -r ml20m-all

