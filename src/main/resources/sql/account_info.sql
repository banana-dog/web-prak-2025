CREATE OR REPLACE FUNCTION fetch_account_data(i BIGINT, p_account_type character varying)
    RETURNS jsonb
    LANGUAGE sql AS
$$
SELECT jsonb_build_object(
               'account id', a.account_id,
               'account number', a.account_no,
               'client', client.first_name || ' ' || client.last_name,
               'client contacts', client.contacts,
               'account status', a.status,
               'currency', a.currency,
               'current balance', a.current_balance,
               'bank branch', bank_branch.name,
               'bank branch address', bank_branch.address,
               'account opening date', a.opening_date,
               'account type', a.account_type
       ) ||
       CASE
           WHEN p_account_type = 'CREDIT' THEN jsonb_build_object(
                   'maximum credit', credit_account.max_credit,
                   'current debt', credit_account.current_debt,
                   'interest rate', credit_account.interest_rate || '%',
                   'credit repayment restriction', credit_account.repayment_restriction,
                   'interest payout interval', credit_account.interest_payout_interval,
                   'payment method', credit_account.payment_method
                                               )
           WHEN p_account_type = 'CHECKING' THEN jsonb_build_object(
                   'overdraft limit', checking_account.overdraft_limit
                                                 )
           WHEN p_account_type = 'SAVINGS' THEN jsonb_build_object(
                   'interest rate', savings_account.interest_rate || '%',
                   'interest payout interval', savings_account.interest_payout_interval,
                   'withdrawal limit', savings_account.withdrawal_limit
                                                )
           WHEN p_account_type = 'DEPOSIT' THEN jsonb_build_object(
                   'interest rate', deposit_account.interest_rate || '%',
                   'payment method', deposit_account.payment_method,
                   'maturity date', deposit_account.maturity_date
                                                )
           ELSE '{}'::jsonb
           END
FROM account AS a
         NATURAL JOIN client
         NATURAL JOIN bank_branch
         LEFT JOIN credit_account ON p_account_type = 'CREDIT' AND a.account_id = credit_account.account_id
         LEFT JOIN checking_account ON p_account_type = 'CHECKING' AND a.account_id = checking_account.account_id
         LEFT JOIN savings_account ON p_account_type = 'SAVINGS' AND a.account_id = savings_account.account_id
         LEFT JOIN deposit_account ON p_account_type = 'DEPOSIT' AND a.account_id = deposit_account.account_id
WHERE a.account_id = i;
$$;

CREATE OR REPLACE FUNCTION get_account_info(i BIGINT, p_account_type character varying)
    RETURNS jsonb
    LANGUAGE plpgsql AS
$$
DECLARE
    result jsonb;
BEGIN
    result := fetch_account_data(i, p_account_type);

    -- Проверяем, есть ли данные
    IF result IS NULL THEN
        RAISE EXCEPTION 'No account found for id % and type %', i, p_account_type;
    END IF;

    RETURN result;
END;
$$;