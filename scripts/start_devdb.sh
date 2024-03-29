#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail


check_if_db_alive() {
    mysql \
            --host=127.0.0.1 \
            --port=3306 \
            --protocol=tcp \
            --user=root \
            --password='dummypass' \
            expenses \
            <<< "SELECT 1" \
            > /dev/null \
            2>&1
}


start_mariadb() {
    local name="expenses_devdb"
    docker rm -f "$name" || :
    docker run \
        --name="$name" \
        --detach \
        --publish=3306:3306 \
        --rm \
        --env='MARIADB_DATABASE=expenses' \
        --env='MARIADB_USER=expenses' \
        --env='MARIADB_PASSWORD=dummypass' \
        --env='MYSQL_ROOT_PASSWORD=dummypass' \
        mariadb:latest
    echo "Waiting for the db to come up..."
    local count=0
    while ! check_if_db_alive; do
        count=$((count + 1))
        if [[ $count -ge 5 ]]; then
            echo "The db container never came up!"
            return 1
        fi
        echo "Checking againg in 5s..."
        sleep 5
    done
    return 0
}

create_db() {
    mysql \
        --host 127.0.0.1 \
        --port 3306 \
        --protocol=tcp \
        --user expenses \
        --password='dummypass' \
        expenses \
        < db/schema.sql
}



main() {
    start_mariadb
    create_db
    echo "Your development db is now ready, you can access it with:"
    echo "    mysql --host=127.0.0.1 --port=3306 --protocol=tcp --user=expenses --password=dummypass expenses"
}


main "$@"
