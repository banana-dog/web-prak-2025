CREATE OR REPLACE FUNCTION get_client_info(i BIGINT)
    RETURNS jsonb
    LANGUAGE plpgsql AS
$$
BEGIN
    RETURN (
        SELECT jsonb_build_object(
                       'name', COALESCE(bank.client.first_name || ' ' || bank.client.last_name, ''),
                       'contacts', COALESCE(bank.client.contacts, ''),
                       'type', COALESCE(bank.client.type::text, ''),
                       'accounts', COALESCE(
                               (SELECT jsonb_agg(bank.get_account_info(account_id, account_type))
                                FROM bank.account
                                WHERE client_id = i),
                               '[]'::jsonb
                                   )
               )
        FROM bank.client
        WHERE client_id = i
    );
END;
$$;