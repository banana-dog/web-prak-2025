package ru.cmc.msu.web_prak_2025.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "account")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Account implements CommonEntity<Long> {
    public enum PaymentMethod {
        AUTO, MANUAL
    }

    public enum Status {
        ACTIVE, CLOSED, SUSPENDED
    }

    public enum Currency {
        RUB, USD, EUR
    }

    public enum AccountType {
        CREDIT, DEPOSIT, SAVINGS, CHECKING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "account_id")
    private Long id;


    @Column(unique = true, name = "account_no")
    private String accountNo;

    @PrePersist
    public void generateAccountNo() {
        this.accountNo = "ACC-" + Instant.now().toEpochMilli();
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude
    @NonNull
    private Client clientId;

    @Column(nullable = false, name = "status")
    @NonNull
    @Enumerated(EnumType.STRING)
    private Status accountStatus;

    @Column(nullable = false, name = "currency")
    @NonNull
    @Enumerated(EnumType.STRING)
    private Currency accountCurrency;

    @Column(nullable = false, name = "current_balance")
    @NonNull
    private Float currentBalance;

    @Column(nullable = false, name = "account_type")
    @NonNull
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "bank_branch")
    @ToString.Exclude
    @NonNull
    private BankBranch branchId;

    @NonNull
    @Column(name = "opening_date", nullable = false)
    private Date openingDate;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private CheckingAccount checkingAccount;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private SavingsAccount savingsAccount;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private CreditAccount creditAccount;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private DepositAccount depositAccount;
}