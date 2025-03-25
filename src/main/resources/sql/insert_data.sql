insert into client("first_name", "last_name", "contacts", "type")
values ('Иван', 'Иванов', 'ivan@example.com', 'NATURAL_PERSON'),
       ('Мария', 'Петрова', 'maria@example.com', 'LEGAL_ENTITY'),
       ('Алексей', 'Сидоров', 'alex@example.com', 'NATURAL_PERSON'),
       ('Ольга', 'Смирнова', 'olga@example.com', 'LEGAL_ENTITY'),
       ('Дмитрий', 'Козлов', 'dmitry@example.com', 'NATURAL_PERSON')
returning "client_id";

insert into bank_branch("address", "name")
values ('ул.Ленина, 10, Москва', 'Центральный Банк'),
       ('пр. Победы, 25, СПб', 'Северо-Западный'),
       ('ул. Кирова, 5, Екатеринбург', 'Уральский Банк'),
       ('пр. Ломоносова, 7, Архангельск', 'Северный Банк'),
       ('ул. Карла Маркса, 15, Новосибирск', 'Сибирский Банк')
returning "id";

insert into account("account_no", "client_id", "status", "currency", "current_balance", "account_type", "bank_branch",
                    "opening_date")
values (100001, 1, 'ACTIVE', 'RUB', 50000, 'CHECKING', 1, '2023-01-10'),
       (100002, 2, 'ACTIVE', 'USD', 1500, 'SAVINGS', 2, '2023-03-15'),
       (100003, 3, 'CLOSED', 'EUR', 0, 'DEPOSIT', 3, '2022-06-20'),
       (100004, 4, 'SUSPENDED', 'RUB', 100000, 'CREDIT', 4, '2021-12-05'),
       (100005, 5, 'ACTIVE', 'RUB', 70000, 'SAVINGS', 5, '2023-07-01')
returning "account_id";

insert into credit_account (account_id, max_credit, current_debt, interest_rate, repayment_restriction, interest_payout_interval, payment_method)
values (4, 200000, 50000, 12.5, 'Минимальный платеж 5%', 'ежемесячно', 'AUTO') returning "id";

insert into deposit_account(account_id, interest_rate, maturity_date, payment_method)
values (3, 5.0, '2024-06-20', 'MANUAL') returning "id";

insert into savings_account (account_id, interest_rate, interest_payout_interval, withdrawal_limit)
values (2, 3.2, 'ежеквартально', 10000),
       (5, 4.0, 'ежегодно', 20000) returning "id";

insert into checking_account (account_id, overdraft_limit)
values (1, 5000) returning "id";
