CREATE OR REPLACE FUNCTION update_clients_count()
    RETURNS TRIGGER AS $$
DECLARE
    client_count INTEGER;
    old_branch_id BIGINT;
    new_branch_id BIGINT;
BEGIN
    IF TG_OP = 'INSERT' THEN
        new_branch_id := NEW.bank_branch;
        old_branch_id := NULL;
    ELSIF TG_OP = 'UPDATE' THEN
        new_branch_id := NEW.bank_branch;
        old_branch_id := OLD.bank_branch;
    ELSE
        new_branch_id := NULL;
        old_branch_id := OLD.bank_branch;
    END IF;

    IF old_branch_id IS NOT NULL AND (TG_OP = 'DELETE' OR OLD.bank_branch != NEW.bank_branch) THEN
        SELECT COUNT(*) INTO client_count
        FROM account
        WHERE client_id = OLD.client_id
          AND bank_branch = old_branch_id
          AND account_id != CASE WHEN TG_OP = 'DELETE' THEN OLD.account_id ELSE NEW.account_id END;

        IF client_count = 0 THEN
            UPDATE bank_branch
            SET clients_number = clients_number - 1
            WHERE id = old_branch_id;
        END IF;
    END IF;

    IF new_branch_id IS NOT NULL AND (TG_OP = 'INSERT' OR OLD.bank_branch != NEW.bank_branch) THEN
        SELECT COUNT(*) INTO client_count
        FROM account
        WHERE client_id = NEW.client_id
          AND bank_branch = new_branch_id
          AND account_id != NEW.account_id;

        IF client_count = 0 THEN
            UPDATE bank_branch
            SET clients_number = clients_number + 1
            WHERE id = new_branch_id;
        END IF;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER accounts_clients_counter
    AFTER INSERT OR UPDATE OR DELETE ON account
    FOR EACH ROW EXECUTE FUNCTION update_clients_count();