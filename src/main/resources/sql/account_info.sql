CREATE OR REPLACE FUNCTION bank.fetch_account_data(i BIGINT, p_account_type character varying)
    RETURNS jsonb
    LANGUAGE sql AS
$$SELECT jsonb_build_object(
                 'account id', a.account_id,
                 'account number', a.account_no,
                 'client', bank.client.first_name || ' ' || bank.client.last_name,
                 'client id', bank.client.client_id,
                 'client contacts', bank.client.contacts,
                 'account status', a.status,
                 'currency', a.currency,
                 'current balance', a.current_balance,
                 'bank branch', bank.bank_branch.name,
                 'bank branch address', bank.bank_branch.address,
                 'account opening date', a.opening_date,
                 'account type', a.account_type
         ) ||
         CASE
             WHEN p_account_type = 'CREDIT' THEN jsonb_build_object(
                     'maximum credit', bank.credit_account.max_credit,
                     'current debt', bank.credit_account.current_debt,
                     'interest rate', bank.credit_account.interest_rate || '%',
                     'credit repayment restriction', bank.credit_account.repayment_restriction,
                     'interest payout interval', bank.credit_account.interest_payout_interval,
                     'payment method', bank.credit_account.payment_method
                                                 )
             WHEN p_account_type = 'CHECKING' THEN jsonb_build_object(
                     'overdraft limit', bank.checking_account.overdraft_limit
                                                   )
             WHEN p_account_type = 'SAVINGS' THEN jsonb_build_object(
                     'interest rate', bank.savings_account.interest_rate || '%',
                     'interest payout interval', bank.savings_account.interest_payout_interval,
                     'withdrawal limit', bank.savings_account.withdrawal_limit
                                                  )
             WHEN p_account_type = 'DEPOSIT' THEN jsonb_build_object(
                     'interest rate', bank.deposit_account.interest_rate || '%',
                     'payment method', bank.deposit_account.payment_method,
                     'maturity date', bank.deposit_account.maturity_date
                                                  )
             ELSE '{}'::jsonb
             END
  FROM bank.account AS a
           NATURAL JOIN bank.client
           NATURAL JOIN bank.bank_branch
           LEFT JOIN bank.credit_account ON p_account_type = 'CREDIT' AND a.account_id = bank.credit_account.account_id
           LEFT JOIN bank.checking_account ON p_account_type = 'CHECKING' AND a.account_id = bank.checking_account.account_id
           LEFT JOIN bank.savings_account ON p_account_type = 'SAVINGS' AND a.account_id = bank.savings_account.account_id
           LEFT JOIN bank.deposit_account ON p_account_type = 'DEPOSIT' AND a.account_id = bank.deposit_account.account_id
  WHERE a.account_id = i;$$;

CREATE OR REPLACE FUNCTION bank.get_account_info(i BIGINT, p_account_type character varying)
    RETURNS jsonb
    LANGUAGE plpgsql AS
$$DECLARE
    result jsonb;
BEGIN
    result := bank.fetch_account_data(i, p_account_type);

    -- Проверяем, есть ли данные
    IF result IS NULL THEN
        RAISE EXCEPTION 'No account found for id % and type %', i, p_account_type;
    END IF;

    RETURN result;
END;$$;