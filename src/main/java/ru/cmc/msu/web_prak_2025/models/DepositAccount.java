package ru.cmc.msu.web_prak_2025.models;

import lombok.*;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "deposit_account")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class DepositAccount implements CommonEntity<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    @NonNull
    private Account account;

    @Column(nullable = false, name = "interest_rate")
    @NonNull
    private Float interestRate;

    @NonNull
    @Column(name = "maturity_date", nullable = false)
    private Date maturityDate;

    @Column(nullable = false, name = "payment_method")
    @NonNull
    @Enumerated(EnumType.STRING)
    private Account.PaymentMethod paymentMethod;
}