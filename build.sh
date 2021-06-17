#!/bin/bash

if [[ $1 =~ "docker" ]]; then
    docker build -t queue:v0.1 -f ./Docker/serverDeploy .
    docker build -t python_client:v0.1 -f ./Docker/pythonDockerFile .
else 
    gradle clean
    gradle build
    pip3 install -r client/requirements.txt
fi
exit 0