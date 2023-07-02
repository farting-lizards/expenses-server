create table
if not exists
db_upgrades(
    filename varchar(255) not null primary key,
    md5 varchar(255) not null
)