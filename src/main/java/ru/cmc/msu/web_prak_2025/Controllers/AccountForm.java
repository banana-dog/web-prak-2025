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

    // Fields for different account types
    private Float overdraftLimit; // for CHECKING
    private Float interestRate; // for SAVINGS, CREDIT, DEPOSIT
    private String interestPayoutInterval; // for SAVINGS, CREDIT
    private Float withdrawalLimit; // for SAVINGS
    private Float maxCredit; // for CREDIT
    private Float currentDebt; // for CREDIT
    private String repaymentRestriction; // for CREDIT
    private Account.PaymentMethod paymentMethod; // for CREDIT, DEPOSIT
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date maturityDate; // for DEPOSIT
}