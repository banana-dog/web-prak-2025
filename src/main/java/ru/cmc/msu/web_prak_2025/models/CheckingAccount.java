package ru.cmc.msu.web_prak_2025.models;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "checking_account")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class CheckingAccount implements CommonEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    @NonNull
    private Account account;

    @Column(nullable = false, name = "overdraft_limit")
    @NonNull
    private Float overdraftLimit;
}
