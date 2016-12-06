#!/bin/bash
aws lambda create-function --function-name add \ 
--handler myFunc.lambda_handler --runtime python2.7 \
--zip-file fileb://myFunc.zip --memory-size 128 \
--role arn:aws:iam::ACCOUNT_NUMBER:role/lambda_basic_execution

