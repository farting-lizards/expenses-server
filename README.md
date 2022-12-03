## Dev install

You'll need java 11 jdk installed, on fedora:

> sudo dnf install java-11-openjdk-devel


### Running it locally
You will need a mariadb/mysql instance running on localhost, you can run something like:

    sudo podman run \
        --name expenses_db \
        --detach \
        -p 3306:3306 \
        --rm \
        --env MARIADB_DATABASE=expenses \
        --env MARIADB_USER=expenses \
        --env 'MARIADB_PASSWORD=GMtC40W8R*9IQt^l9' \
        --env MYSQL_RANDOM_ROOT_PASSWORD=true \
        mariadb:latest

Then you can run the application with:
> ./gradlew bootRun