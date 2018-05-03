#!/bin/bash

aws lambda delete-function --function-name lambda1 --region eu-west-1

/bin/rm -f lambda1.zip result.txt


