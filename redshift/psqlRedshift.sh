#!/bin/bash

# Author: Julien Simon <julien@julien.org>

# Connect to a Redshift cluster using psql

if ([ -z $1 ] || [ -z $2 ] || [ -z $3 ])
then
	echo "Usage: psqlRedshift CLUSTER_NAME DATABASE_NAME USER_NAME"
	exit
fi

psql=/usr/local/bin/psql

redshift_port=`aws redshift describe-clusters --cluster-identifier $1 --query "Clusters[0].Endpoint.Port"`
redshift_host=`aws redshift describe-clusters --cluster-identifier $1 --query "Clusters[0].Endpoint.Address" | sed -e 's/"//g'`

echo "Connecting to database "$2" on "$redshift_host":"$redshift_port

$psql -h $redshift_host -p $redshift_port -U $3 "dbname=$2 sslmode=require"

