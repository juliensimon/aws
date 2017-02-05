#!/bin/bash

zip -9 lambda2.zip lambda2.py

aws s3 cp lambda2.zip s3://jsimon-public

aws cloudformation package \
   --template-file lambda2.yml \
   --output-template-file serverless-output.yml \
   --s3-bucket jsimon-public

aws cloudformation deploy \
--template-file serverless-output.yml \
--capabilities CAPABILITY_IAM \
--stack-name lambda2WithSam
