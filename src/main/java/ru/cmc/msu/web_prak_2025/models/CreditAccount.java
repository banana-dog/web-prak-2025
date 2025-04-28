package ru.cmc.msu.web_prak_2025.models;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "credit_account", schema = "bank")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class CreditAccount implements CommonEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    @NonNull
    private Account account;

    @Column(nullable = false, name = "max_credit")
    @NonNull
    private Float maxCredit;

    @Column(nullable = false, name = "current_debt")
    @NonNull
    private Float currentDebt;

    @Column(nullable = false, name = "interest_rate")
    @NonNull
    private Float interestRate;

    @NonNull
    @Column(name = "repayment_restriction")
    private String repaymentRestriction;

    @Column(nullable = false, name = "interest_payout_interval")
    @NonNull
    private String interestPayoutInterval;

    @Column(nullable = false, name = "payment_method")
    @NonNull
    @Enumerated(EnumType.STRING)
    private Account.PaymentMethod paymentMethod;
}