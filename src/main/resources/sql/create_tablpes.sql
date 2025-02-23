create type status as enum ('active', 'closed', 'suspended');
create type currency as enum ('rub', 'usd', 'eur');
create type account_type as enum ('credit', 'deposit', 'savings', 'checking');
create type payment_method as enum ('auto', 'manual');

drop table if exists client cascade;
create table client
(
    "id"         serial primary key,
    "first_name" text not null,
    "last_name"  text not null,
    "contacts"   text
);

drop table if exists bank_branch cascade;
create table bank_branch
(
    "id"      serial primary key,
    "address" text not null,
    "name"    text not null
);

drop table if exists account cascade;
create table account
(
    "account_id"      serial primary key,
    "account_no"      integer      not null,
    "client_id"       integer references client ("id"),
    "status"          status       null,
    "currency"        currency     not null,
    "current_balance" integer      not null,
    "account_type"    account_type not null,
    "bank_branch"     integer references bank_branch ("id"),
    "opening_date"    date    not null
);

drop table if exists credit_account cascade;
create table credit_account
(
    "id"                       integer references account ("account_id"),
    "max_credit"               float check ( "max_credit" >= 0),
    "current_debt"             float check ( "current_debt" >= 0 ),
    "interest_rate"            float check ( "interest_rate" >= 0),
    "repayment_restriction"    varchar(100),
    "interest_payout_interval" varchar(20)    not null,
    "payment_method"           payment_method not null
);

drop table if exists deposit_account cascade;
create table deposit_account
(
    "id"             integer references account ("account_id"),
    "interest_rate"  float check ("interest_rate" >= 0),
    "maturity_date"  date           not null,
    "payment_method" payment_method not null
);

drop table if exists savings_account cascade;
create table savings_account
(
    "id"                       integer references account ("account_id"),
    "interest_rate"            float check ( "interest_rate" >= 0),
    "interest_payout_interval" varchar(50) not null,
    "withdrawal_limit"         float check ( "withdrawal_limit" >= 0)
);

drop table if exists "checking_account" cascade;
create table checking_account
(
    "id"              integer references account ("account_id"),
    "overdraft_limit" float check ( "overdraft_limit" >= 0 )
);
