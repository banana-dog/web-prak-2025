package ru.cmc.msu.web_prak_2025.DAO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.cmc.msu.web_prak_2025.models.Client;
import ru.cmc.msu.web_prak_2025.models.Client.ClientType;

import java.util.List;
import java.util.Optional;

public interface ClientDAO extends CommonDAO<Client, Long> {
    List<Client> getByFilter(Filter filter);
    Optional<String> getClientDetails(Long id);

    @AllArgsConstructor
    @Builder
    @Getter
    class Filter {
        public String firstName;
        public String lastName;
        public ClientType type;
    }

    static Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }
}