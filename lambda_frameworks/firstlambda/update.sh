#!/bin/bash


zip -9 lambda1.zip lambda1.py

aws lambda update-function-code --function-name lambda1 --zip-file fileb://lambda1.zip

