CREATE OR REPLACE FUNCTION get_client_info(i BIGINT)
    RETURNS jsonb
    LANGUAGE plpgsql AS
$$
BEGIN
    return(
        select jsonb_build_object(
                       'name', client.first_name || ' ' || client.last_name,
                       'contacts', client.contacts,
                       'type', client.type,
                       'accounts', (select get_account_info(account_id, account_type) from account where client_id = i)
               )
        from client
        where client_id = i);
END;
$$;
