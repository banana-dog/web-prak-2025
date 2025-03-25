package ru.cmc.msu.web_prak_2025.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Client implements CommonEntity<Long> {
    public enum ClientType {
        NATURAL_PERSON, LEGAL_ENTITY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "client_id")
    private Long id;

    @Column(nullable = false, name = "first_name")
    @NonNull
    private String firstName;

    @Column(nullable = false, name = "last_name")
    @NonNull
    private String lastName;

    @Column(nullable = false, name = "contacts")
    @NonNull
    private String contacts;

    @Column(nullable = false, name = "type")
    @NonNull
    @Enumerated(EnumType.STRING)
    private ClientType type;

    @OneToMany(mappedBy = "clientId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Account> accounts = new ArrayList<>();
}