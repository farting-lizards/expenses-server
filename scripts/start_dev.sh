#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

hash podman && DOCKER="sudo podman" || DOCKER=docker



set -x
$DOCKER build -f Dockerfile.dev -t expenses-server:dev
$DOCKER run \
    --tty \
    --interactive \
    --volume $PWD:/src:rw,idmap \
    --volume $PWD/../home-lab-secrets/:/home-lab-secrets:rw,idmap \
    --publish-all \
    --name expenses-server-dev \
    --rm \
    expenses-server:dev \
    "/src/gradlew"\
    "bootRun" \
    "$@"