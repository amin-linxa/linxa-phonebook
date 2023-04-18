create database linxa_phonebook;
create user 'linxa'@'%' identified by 'linxa';
grant all privileges on linxa_phonebook.* to 'linxa'@'%' with grant option;

create table contact
(
    id           int          not null auto_increment primary key,
    first_name   varchar(32),
    last_name    varchar(32),
    email        varchar(256) not null,
    phone_number varchar(32)  not null unique,
    country      varchar(32),
    city         varchar(32),
    street       varchar(256)
);

insert into contact (first_name, last_name, email, phone_number, country, city, street)
values ('John', 'Doe', 'john.doe@test.com', '1234567', 'Turkey', 'Istanbul', 'Bebek'),
       ('Jane', 'Smith', 'jane.smith@test.com', '9876543', 'USA', 'Texas', 'Santa Anna');