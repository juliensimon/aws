#!/bin/bash

source ~/dev/aws/setAccountNumber.sh

zip -9 lambda1.zip lambda1.py

aws lambda create-function --function-name lambda1 --handler lambda1.lambda_handler \
 --runtime python2.7 --zip-file fileb://lambda1.zip --memory-size 128 \
--role arn:aws:iam::$ACCOUNT_NUMBER:role/lambda_basic_execution \
--region eu-west-1

aws lambda invoke --function-name lambda1 --payload '{"value1":5, "value2":7}' \
--invocation-type RequestResponse result.txt --region eu-west-1

cat result.txt


