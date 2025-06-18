create table Person (
    Id uuid primary key,
    Username text not null unique,
    Email text not null unique);