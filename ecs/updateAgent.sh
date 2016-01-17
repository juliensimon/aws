#!/bin/bash

# Author: Julien Simon <julien@julien.org>

# Script to perform a cluster-wide agent update

if [ -z $1 ]
then
	echo "updateAgent CLUSTER_NAME"
	exit
fi

for i in `aws ecs list-container-instances --cluster $1 | grep arn | cut -b 64-99`
do
	aws ecs update-container-agent --cluster $1 --container-instance $i
done
