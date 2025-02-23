insert into client("first_name", "last_name", "contacts")
values ('Иван', 'Иванов', 'ivan@example.com'),
       ('Мария', 'Петрова', 'maria@example.com'),
       ('Алексей', 'Сидоров', 'alex@example.com'),
       ('Ольга', 'Смирнова', 'olga@example.com'),
       ('Дмитрий', 'Козлов', 'dmitry@example.com')
returning "id";

insert into bank_branch("address", "name")
values ('ул.Ленина, 10, Москва', 'Центральный Банк'),
       ('пр. Победы, 25, СПб', 'Северо-Западный'),
       ('ул. Кирова, 5, Екатеринбург', 'Уральский Банк'),
       ('пр. Ломоносова, 7, Архангельск', 'Северный Банк'),
       ('ул. Карла Маркса, 15, Новосибирск', 'Сибирский Банк')
returning "id";

insert into account("account_no", "client_id", "status", "currency", "current_balance", "account_type", "bank_branch",
                    "opening_date")
values (100001, 1, 'active', 'rub', 50000, 'checking', 1, '2023-01-10'),
       (100002, 2, 'active', 'usd', 1500, 'savings', 2, '2023-03-15'),
       (100003, 3, 'closed', 'eur', 0, 'deposit', 3, '2022-06-20'),
       (100004, 4, 'suspended', 'rub', 100000, 'credit', 4, '2021-12-05'),
       (100005, 5, 'active', 'rub', 70000, 'savings', 5, '2023-07-01')
returning "account_id";

insert into credit_account
values (4, 200000, 50000, 12.5, 'Минимальный платеж 5%', 'ежемесячно', 'auto');

insert into deposit_account
values (3, 5.0, '2024-06-20', 'manual');

insert into savings_account
values (2, 3.2, 'ежеквартально', 10000),
       (5, 4.0, 'ежегодно', 20000);

insert into checking_account
values (1, 5000);