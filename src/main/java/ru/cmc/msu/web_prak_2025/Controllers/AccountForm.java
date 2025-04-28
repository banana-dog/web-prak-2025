package ru.cmc.msu.web_prak_2025.Controllers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.cmc.msu.web_prak_2025.models.Account;

import java.util.Date;

@Getter
@Setter
public class AccountForm {
    private Long clientId;
    private Account.AccountType type;
    private Account.Currency currency;
    private Float balance;
    private Long bankBranchId;
    private Date openingDate;

    private Float overdraftLimit;
    private Float interestRate;
    private String interestPayoutInterval;
    private Float withdrawalLimit;
    private Float maxCredit;
    private Float currentDebt;
    private String repaymentRestriction;
    private Account.PaymentMethod paymentMethod;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date maturityDate;
}