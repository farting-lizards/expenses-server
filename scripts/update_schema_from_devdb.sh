#!/bin/bash


main() {
    mysqldump \
    --add-drop-database \
    --no-data \
    -h 127.0.0.1 \
    --user=expenses \
    --password=dummypass \
    expenses \
    > db/schema.sql
}


main "$@"
