CREATE OR REPLACE FUNCTION bank.update_clients_count()
    RETURNS TRIGGER AS $$BEGIN
    IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
        UPDATE bank.bank_branch
        SET clients_number = (
            SELECT COUNT(DISTINCT client_id)
            FROM bank.account
            WHERE bank_branch = NEW.bank_branch
        )
        WHERE id = NEW.bank_branch;
    END IF;

    IF (TG_OP = 'DELETE' OR TG_OP = 'UPDATE') THEN
        UPDATE bank.bank_branch
        SET clients_number = (
            SELECT COUNT(DISTINCT client_id)
            FROM bank.account
            WHERE bank_branch = OLD.bank_branch
        )
        WHERE id = OLD.bank_branch;
    END IF;

    RETURN NULL;
END;$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS accounts_clients_counter ON bank.account;

CREATE TRIGGER accounts_clients_counter
    AFTER INSERT OR UPDATE OR DELETE ON bank.account
    FOR EACH ROW EXECUTE FUNCTION bank.update_clients_count();