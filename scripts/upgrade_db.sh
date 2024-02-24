#!/bin/bash


set -o nounset
set -o pipefail
set -o errexit
shopt -s nullglob

UPGRADES_TABLE=db_upgrades
THIS_DIR="$(dirname $(realpath ${0}))"
REPO_DIR="$(realpath "$THIS_DIR/..")"
UPGRADES_DIR="$REPO_DIR/db/upgrades"

MYSQL="mysql  \
    --host=127.0.0.1 \
    --port=3306 \
    --protocol=tcp \
    --user=expenses \
    --password=dummypass \
    expenses"

get_applied_upgrades() {
    $MYSQL --execute="select filename from $UPGRADES_TABLE" \
    | tail -n+2
}

get_upgrades_to_apply() {
    echo "$UPGRADES_DIR"/*.sql
}

is_in() {
    local what="$1"
    shift
    local where="$@"
    local elem

    for elem in "${where[@]}"; do
        if [[ "$what" == "$elem" ]]; then
            return 0
        fi
    done
    return 1
}

apply() {
    local upgrade="${1?}"
    local upgrade_md5=$(md5sum "$upgrade"| awk '{print $1}')
    $MYSQL <"$1"
    $MYSQL --execute="insert into $UPGRADES_TABLE(filename, md5) values ('${upgrade##*/}', '$upgrade_md5');"
}

ensure_table(){
    $MYSQL \
        --skip-column-names \
        --silent \
        --execute="create table if not exists $UPGRADES_TABLE(filename varchar(255) not null primary key, md5 varchar(255) not null)"
}

main() {
    ensure_table

    if [[ "${1:-}" == "-v" ]]; then
        shift
        set -x
    fi

    local all_to_apply=(
        $(get_upgrades_to_apply)
    )
    echo "Found file upgrades:"
    for upgrade in "${all_to_apply[@]}"; do
        echo "    $upgrade"
    done

    local all_applied=(
        $(get_applied_upgrades)
    )
    echo "Applied upgrades:"
    for upgrade in "${all_applied[@]}"; do
        echo "    $upgrade"
    done

    local to_apply

    echo "Applying new upgrades:"
    for to_apply in "${all_to_apply[@]}"; do
        if ! is_in "${to_apply##*/}" "${all_applied[@]}"; then
            echo -n "    $to_apply: "
            apply "$to_apply"
            echo "ok"
        else
            echo "    $to_apply: already applied, skipped"
        fi
    done
}


main "$@"