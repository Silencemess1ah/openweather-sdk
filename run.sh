#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: ./run.sh API_KEY SDK_MODE"
    exit 1
fi

API_KEY=$1
SDK_MODE=$2

export API_KEY=${API_KEY}
export SDK_MODE=${SDK_MODE}

docker-compose -f docker-compose.yaml build
docker-compose -f docker-compose.yaml up -d