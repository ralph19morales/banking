#!/usr/bin/env bash

set -ex

cd "$(dirname "$0")"

docker build \
    -t banking:latest \
    ./..

docker rm -f banking || true

docker run \
    -it \
    --network=host \
    --name banking \
    -e DB_HOST='127.0.0.1:3306' \
    -e DB_USERNAME='root' \
    -e DB_PASSWORD='admin12345' \
    -e DB_NAME='banking' \
    -d \
	banking:latest