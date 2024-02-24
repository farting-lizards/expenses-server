#!/bin/bash

CURFILE="$(realpath $0)"
PROJECTDIR="${CURFILE%/*/*}"
PLATFORM="${PLATFORM:-linux/arm64}"
rm -rf "$PROJECTDIR/.gradle/caches"
podman run \
    --rm \
    -ti \
    --user $UID \
    --entrypoint /src/gradlew  \
    --userns=keep-id \
    --env "GRADLE_USER_HOME=/src/.gradle" \
    --volume "$PROJECTDIR:/src:rw,z" \
    --platform="$PLATFORM" \
    --workdir /src \
    docker.io/library/gradle:jdk11 \
    build -x test
