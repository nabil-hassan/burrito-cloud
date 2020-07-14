-- noinspection SqlNoDataSourceInspectionForFile

-- noinspection SqlDialectInspectionForFile

-- ============================================================
-- Author:       Nabil Hassan
-- Create date:  8/6/2020
-- Description:  Sets up the application's schema.
--
--               N.B. the file must be renamed to 'schema.sql'
--               if you wish it to run automatically when the
--               application starts up.
-- ============================================================

create table if not exists ingredient (
    id   varchar(4)  not null,
    name varchar(25) not null,
    type varchar(10) not null
);

create table if not exists burrito (
    id         identity,
    name       varchar(50) not null,
    created_at timestamp   not null
);

create table if not exists burrito_ingredients (
    burrito_id    bigint     not null,
    ingredient_id varchar(4) not null
);

alter table burrito_ingredients add foreign key(burrito_id)    references burrito(id);
alter table burrito_ingredients add foreign key(ingredient_id) references ingredient(id);

create table if not exists orders (
    id            identity,
    name          varchar(50) not null,
    street        varchar(50) not null,
    town          varchar(50) not null,
    county        varchar(50) not null,
    postcode      varchar(10) not null,
    ccNo          varchar(12) not null,
    ccExpiryDate  varchar(5)  not null,
    ccCCV         varchar(3)  not null,
    placedAt      timestamp
);

create table if not exists order_burritos (
    burrito_id   bigint not null,
    order_id     bigint not null
);

alter table order_burritos add foreign key (burrito_id) references burrito(id);
alter table order_burritos add foreign key (order_id)   references orders(id);