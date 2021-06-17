#!/bin/bash

if [[ $1 =~ "docker" ]]; then
    docker build -t queue:v0.1 -f ./Docker/serverDeploy .
    docker build -t python_client:v0.1 -f ./Docker/pythonDockerFile .
    cd Docker
    docker-compose up
else 
    echo "False"
fi

exit 0