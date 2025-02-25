#!/bin/bash

if [ -f .env ]; then
    # shellcheck disable=SC2046
    export $(grep -v '^#' .env | xargs)
else
    echo "Error: .env file not found!"
fi

if [ -z "$API_KEY" ] || [ -z "$SDK_MODE" ]; then
    echo "Error: API_KEY or SDK_MODE is not set!"
    exit 1
fi

./gradlew test