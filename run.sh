#!/bin/bash

if [[ $1 =~ "docker" ]]; then
    cd Docker
    docker-compose up
else 
    java -jar test.jar &
    python3 client/client.py
fi
exit 0