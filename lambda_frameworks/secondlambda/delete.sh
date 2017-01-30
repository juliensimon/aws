#!/bin/bash

/bin/rm -f lambda2.zip
/bin/rm -f serverless-output.yml

aws cloudformation delete-stack --stack-name lambda2WithSam

aws cloudformation describe-stacks --stack-name lambda2WithSam

