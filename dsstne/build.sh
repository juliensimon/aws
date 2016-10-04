#!/bin/bash

# Build DSSTNE
git clone https://github.com/amznlabs/amazon-dsstne.git
cd amazon-dsstne/src/amazon/dsstne

# Branch before introduction of 'compute60' architecture, which is unsupported by CUDA 7.0
git branch stable 9f08739b62b3d3f7c742e30f83c55b65aaf7920b
git checkout stable

export PATH=/usr/local/openmpi/bin:/usr/local/cuda/bin:$PATH
make -j8
cd
