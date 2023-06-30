#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

hash podman && DOCKER="sudo podman" || DOCKER=docker


set -x

check_if_backend_alive() {
    curl --silent http://127.0.0.1:8080/actuator/health | grep "UP" || {
        echo "failed to get the health of the backend server"
        curl -v http://127.0.0.1:8080/actuator/health
        echo "container logs:"
        $DOCKER logs expenses-server-dev
        return 1
    }
    echo "Your development server is up and running at http://127.0.0.1:8080"
    return 0
}

main() {
    mkdir -p .gradle
    $DOCKER build -f Dockerfile.dev -t expenses-server:dev
    $DOCKER rm -f expenses-server-dev || : 2>/dev/null
    db_host="$(hostname -i | grep -Po '192.168.1.\w*(?:$| )' || :)"
    do_tail=yes
    if [[ $db_host == "" ]]; then
        # fallback in case we run on ci
        db_host=$(hostname -i | awk '{print $1}')
    fi
    if [[ "${1:-}" == "notail" ]]; then
        do_tail=no
        shift
    fi
    $DOCKER run \
        --tty \
        --interactive \
        --user $UID \
        --volume $PWD:/src:rw,z \
        --volume $PWD/../home-lab-secrets/:/home-lab-secrets:rw,z \
        --volume $PWD/.gradle:/home/gradle/.gradle:rw,z \
        --userns=keep-id \
        --publish  8080:8080 \
        --name expenses-server-dev \
        --detach \
        expenses-server:dev \
        "/src/gradlew"\
        "bootRun" \
        "--args='--spring.datasource.url=jdbc:mysql://${db_host}:3306/expenses'" \
        "$@"


    echo "Waiting for the backend to come up..."
    local count=0
    while ! check_if_backend_alive; do
        count=$((count + 1))
        if [[ $count -ge 15 ]]; then
            echo "The server container never came up!"
            echo "You might want to try running with sudo :/"
            return 1
        fi
        echo "Checking again in 5s..."
        sleep 5
    done
    if [[ "$do_tail" == "yes" ]]; then
        $DOCKER logs -f expenses-server-dev
    fi
}

main "$@"