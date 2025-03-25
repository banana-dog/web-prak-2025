package ru.cmc.msu.web_prak_2025.models;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "savings_account")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class SavingsAccount implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    @NonNull
    @ToString.Exclude
    private Account account;

    @Column(nullable = false, name = "interest_rate")
    @NonNull
    private Float interestRate;

    @Column(nullable = false, name = "interest_payout_interval")
    @NonNull
    private String interestPayoutInterval;

    @Column(nullable = false, name = "withdrawal_limit")
    @NonNull
    private Float withdrawalLimit;
}