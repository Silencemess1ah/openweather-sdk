#! /bin/bash

# shellcheck disable=SC2046
export $(grep -v '^#' .env | xargs)

./gradlew build -Pvar1="$API_KEY" -Pvar2="$SDK_MODE"