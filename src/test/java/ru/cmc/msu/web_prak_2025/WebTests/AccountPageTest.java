package ru.cmc.msu.web_prak_2025.WebTests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AccountPageTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAccountListPage() {
        driver.get(BASE_URL + "/accounts");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(normalize-space(), 'Список счетов')]")));
        Assertions.assertTrue(title.isDisplayed());

        WebElement filterCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".filter-card")));
        Assertions.assertTrue(filterCard.isDisplayed());

        WebElement homeButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(normalize-space(), 'На главную')]")));
        Assertions.assertTrue(homeButton.isDisplayed());

        Assertions.assertTrue(homeButton.isEnabled());
    }
    @Test
    public void testAccountFiltering() {
        driver.get(BASE_URL + "/accounts");

        WebElement clientIdInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("clientId")));
        clientIdInput.clear();
        clientIdInput.sendKeys("1");

        WebElement accountTypeSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountType")));
        Select typeSelect = new Select(accountTypeSelect);
        typeSelect.selectByValue("CHECKING");

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Поиск')]")));
        searchButton.click();

        wait.until(ExpectedConditions.urlContains("clientId=1"));
        wait.until(ExpectedConditions.urlContains("accountType=CHECKING"));

        try {
            WebElement accountsContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".row-cols-1.row-cols-md-2.g-4")));
            Assertions.assertTrue(accountsContainer.isDisplayed());

            List<WebElement> accountCards = driver.findElements(By.cssSelector(".account-card"));
            Assertions.assertFalse(accountCards.isEmpty());
        } catch (TimeoutException e) {
            WebElement noAccountsMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h4[contains(normalize-space(), 'Счета не найдены')]")));
            Assertions.assertTrue(noAccountsMessage.isDisplayed());
        }
    }

    @Test
    public void testAccountDetailsPage() {
        driver.get(BASE_URL + "/accounts/1");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(normalize-space(), 'Детали счета')]")));
        Assertions.assertTrue(title.isDisplayed());

        WebElement accountNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[contains(., '№')]")));
        WebElement accountBalance = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".balance-card h2")));
        WebElement accountStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".status-badge")));

        Assertions.assertTrue(accountNumber.isDisplayed());
        Assertions.assertTrue(accountBalance.isDisplayed());
        Assertions.assertNotNull(accountBalance.getText());
        Assertions.assertTrue(accountStatus.isDisplayed());
    }

    @Test
    public void testCreateCheckingAccount() {
        driver.get(BASE_URL + "/clients/1/accounts/new?type=CHECKING");

        WebElement currencySelectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currency")));
        Select currencySelect = new Select(currencySelectElement);
        currencySelect.selectByValue("RUB");

        WebElement balanceInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("balance")));
        balanceInput.clear();
        balanceInput.sendKeys("1000.00");

        WebElement bankBranchSelectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bankBranch")));
        Select bankBranchSelect = new Select(bankBranchSelectElement);
        Assertions.assertTrue(bankBranchSelect.getOptions().size() > 1);
        bankBranchSelect.selectByIndex(1);

        WebElement overdraftLimitInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overdraftLimit")));
        overdraftLimitInput.clear();
        overdraftLimitInput.sendKeys("500.00");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Создать счет')]")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/clients/1"));
        Assertions.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).endsWith("/clients/1"));
    }

    @Test
    public void testCreateSavingsAccount() {
        driver.get(BASE_URL + "/clients/1/accounts/new?type=SAVINGS");

        WebElement currencySelectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currency")));
        Select currencySelect = new Select(currencySelectElement);
        currencySelect.selectByValue("USD");

        WebElement balanceInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("balance")));
        balanceInput.clear();
        balanceInput.sendKeys("5000.00");

        WebElement bankBranchSelectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bankBranch")));
        Select bankBranchSelect = new Select(bankBranchSelectElement);
        Assertions.assertTrue(bankBranchSelect.getOptions().size() > 1);
        bankBranchSelect.selectByIndex(1);

        WebElement interestRateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("interestRate")));
        interestRateInput.clear();
        interestRateInput.sendKeys("5.5");

        WebElement payoutIntervalSelectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("interestPayoutInterval")));
        Select payoutIntervalSelect = new Select(payoutIntervalSelectElement);
        payoutIntervalSelect.selectByVisibleText("Ежемесячно");

        WebElement withdrawalLimitInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("withdrawalLimit")));
        withdrawalLimitInput.clear();
        withdrawalLimitInput.sendKeys("1000.00");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Создать счет')]")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/clients/1"));
        Assertions.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).endsWith("/clients/1"));
    }

    @Test
    public void testPerformTransaction() {
        driver.get(BASE_URL + "/accounts/1");

        WebElement statusSelectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newStatus")));
        Select statusSelect = new Select(statusSelectElement);
        statusSelect.selectByValue("ACTIVE");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Изменить статус')]")));
        submitButton.click();

        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
        amountInput.clear();
        amountInput.sendKeys("100.50");

        WebElement rateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rate")));
        rateInput.clear();
        rateInput.sendKeys("1.0");

        submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Выполнить операцию')]")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/accounts/1"));

        WebElement accountBalance = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".balance-card h2")));
        Assertions.assertNotNull(accountBalance.getText());
    }

    @Test
    public void testChangeAccountStatus() {
        driver.get(BASE_URL + "/accounts/1");

        WebElement statusSelectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newStatus")));
        Select statusSelect = new Select(statusSelectElement);
        statusSelect.selectByValue("SUSPENDED");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Изменить статус')]")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/accounts/1"));

        WebElement statusBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(normalize-space(), 'Приостановлен')]")));
        Assertions.assertTrue(statusBadge.isDisplayed());

        Assertions.assertTrue(statusBadge.getText().contains("Приостановлен"));
    }

    @Test
    public void testDeleteAccount() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get(BASE_URL + "/accounts/1");

            longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h1[contains(normalize-space(), 'Детали счета')]")));

            WebElement statusBadge = longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".status-badge")));

            if (statusBadge.getText().contains("Закрыт")) {
                Assertions.fail("Нельзя закрыть уже закрытый счет");
            }

            WebElement deleteButton = longWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(normalize-space(), 'Закрыть счет')]")));

            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", deleteButton);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            deleteButton.click();

            Alert alert = longWait.until(ExpectedConditions.alertIsPresent());
            Assertions.assertNotNull(alert, "Alert подтверждения не появился");
            alert.accept();

            longWait.until(ExpectedConditions.urlContains("/accounts"));

            driver.get(BASE_URL + "/accounts/1");
            WebElement errorMessage = longWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(), 'Произошла ошибка')]")));
            Assertions.assertTrue(errorMessage.isDisplayed());

    }
}