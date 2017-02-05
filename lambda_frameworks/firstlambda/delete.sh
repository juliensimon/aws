#!/bin/bash

aws lambda delete-function --function-name lambda1 >& /dev/null

/bin/rm -f lambda1.zip result.txt


