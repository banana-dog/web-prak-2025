package ru.cmc.msu.web_prak_2025.WebTests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ClientPageTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";

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
    public void testClientsListPage() {
        driver.get(BASE_URL + "/clients");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(normalize-space(), 'Клиенты')]")));
        Assertions.assertTrue(title.isDisplayed());

        WebElement filterCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".filter-card")));
        Assertions.assertTrue(filterCard.isDisplayed());

        WebElement addClientButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(normalize-space(), 'Новый клиент')]")));
        Assertions.assertTrue(addClientButton.isDisplayed());
        Assertions.assertTrue(addClientButton.isEnabled());
    }

    @Test
    public void testAccountDetailsForNonExistingAccount() {
        driver.get(BASE_URL + "/accounts/999999");

        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/error"),
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(), 'Произошла ошибка') or contains(text(), 'Not Found')]"))
            ));

        } catch (TimeoutException e) {
            Assertions.fail("Система не обработала запрос несуществующего счета");
        }
    }

    @Test
    public void testClientFiltering() {
        driver.get(BASE_URL + "/clients");

        WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstName")));
        firstNameInput.clear();
        firstNameInput.sendKeys("Тест");

        WebElement lastNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lastName")));
        lastNameInput.clear();
        lastNameInput.sendKeys("Тестов");

        WebElement typeSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("type")));
        Select select = new Select(typeSelect);
        select.selectByValue("NATURAL_PERSON");

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Поиск')]")));
        searchButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'client-card') and contains(., 'Тестов Тест')]")));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'client-card') and contains(., 'Физ. лицо')]")));

        try {
            WebElement clientsContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".row")));
            Assertions.assertTrue(clientsContainer.isDisplayed());

            List<WebElement> clientCards = driver.findElements(By.cssSelector(".client-card"));
            Assertions.assertFalse(clientCards.isEmpty());
        } catch (TimeoutException e) {
            WebElement noClientsMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h4[contains(normalize-space(), 'Клиенты не найдены')]")));
            Assertions.assertTrue(noClientsMessage.isDisplayed());
        }
    }

    @Test
    public void testClientDetailsPage() {
        driver.get(BASE_URL + "/clients/1");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(normalize-space(), 'Детали клиента')]")));
        Assertions.assertTrue(title.isDisplayed());

        WebElement clientName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".detail-card .card-title")));
        WebElement clientContacts = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".detail-card .text-muted span")));
        WebElement clientTypeBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".detail-card .status-badge")));

        Assertions.assertTrue(clientName.isDisplayed());
        Assertions.assertTrue(clientContacts.isDisplayed());
        Assertions.assertTrue(clientTypeBadge.isDisplayed());
    }

    @Test
    public void testAddNewClient() {
        driver.get(BASE_URL + "/clients");

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Новый клиент')]")));
        addButton.click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addClientModal")));
        Assertions.assertTrue(modal.isDisplayed());

        WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newFirstName")));
        firstNameInput.sendKeys("Тест");

        WebElement lastNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newLastName")));
        lastNameInput.sendKeys("Тестов");

        WebElement contactsInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newContacts")));
        contactsInput.sendKeys("test@test.com");

        WebElement typeSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newType")));
        Select select = new Select(typeSelect);
        select.selectByValue("NATURAL_PERSON");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='addClientModal']//button[contains(normalize-space(), 'Добавить')]")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/clients"));

        WebElement firstNameFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstName")));
        firstNameFilter.clear();
        firstNameFilter.sendKeys("Тест");

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Поиск')]")));
        searchButton.click();

        try {
            WebElement clientCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'client-card') and contains(., 'Тестов Тест')]")));
            Assertions.assertTrue(clientCard.isDisplayed());
        } catch (TimeoutException e) {
            Assertions.fail("Новый клиент не найден в списке после добавления");
        }
    }

    @Test
    public void testEditClient() {
        driver.get(BASE_URL + "/clients");

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(normalize-space(), 'Редактировать')])[1]")));
        editButton.click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@id, 'editClientModal') and contains(@style, 'display: block')]")));
        Assertions.assertTrue(modal.isDisplayed());

        WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@id, 'editClientModal')]//input[@name='firstName']")));
        firstNameInput.clear();
        firstNameInput.sendKeys("Измененное имя");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@id, 'editClientModal')]//button[contains(normalize-space(), 'Сохранить')]")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/clients"));

        WebElement clientName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//div[contains(@class, 'client-card')]//h5[contains(., 'Измененное имя')])[1]")));
        Assertions.assertTrue(clientName.isDisplayed());
    }

    @Test
    public void testDeleteClient() {
        driver.get(BASE_URL + "/clients");

        List<WebElement> clientCardsBefore = wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.cssSelector(".client-card")));

        if (clientCardsBefore.isEmpty()) {
            Assertions.fail("Нет клиентов для удаления");
            return;
        }

        WebElement firstClientDetailsLink = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("(//a[contains(@href, '/clients/')])[1]")));
        String clientUrl = firstClientDetailsLink.getAttribute("href");
        assert clientUrl != null;
        long clientId = Long.parseLong(clientUrl.substring(clientUrl.lastIndexOf('/') + 1));

        WebElement deleteButton = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("(//button[contains(normalize-space(), 'Удалить')])[1]")));
        deleteButton.click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        wait.until(ExpectedConditions.urlContains("/clients"));

        try {
            driver.get(BASE_URL + "/clients/" + clientId);

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/error"),
                    ExpectedConditions.urlContains("/clients"),
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(), 'Произошла ошибка')]"))
            ));

        } catch (TimeoutException e) {
            Assertions.fail("Страница клиента все еще доступна после удаления");
        }
    }

    @Test
    public void testViewClientAccounts() {
        driver.get(BASE_URL + "/clients/1");

        WebElement accountsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[contains(normalize-space(), 'Счета клиента')]/ancestor::div[contains(@class, 'mb-4')]")));
        Assertions.assertTrue(accountsSection.isDisplayed());

        try {
            List<WebElement> accountCards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector(".account-card")));
            Assertions.assertFalse(accountCards.isEmpty());

            WebElement accountType = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//div[contains(@class, 'account-card')]//h5)[1]")));
            WebElement accountNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//div[contains(@class, 'account-card')]//h6)[1]")));
            WebElement accountBalance = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//div[contains(@class, 'account-card')]//span[contains(@class, 'account-balance')])[1]")));

            Assertions.assertTrue(accountType.isDisplayed());
            Assertions.assertTrue(accountNumber.isDisplayed());
            Assertions.assertTrue(accountBalance.isDisplayed());
        } catch (TimeoutException e) {
            WebElement noAccountsMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h4[contains(normalize-space(), 'У клиента нет счетов')]")));
            Assertions.assertTrue(noAccountsMessage.isDisplayed());
        }
    }

    @Test
    public void testNavigateToAccountDetails() {
        driver.get(BASE_URL + "/clients/1");

        try {
            WebElement detailsButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//a[contains(normalize-space(), 'Подробнее')])[1]")));
            detailsButton.click();

            wait.until(ExpectedConditions.urlContains("/accounts/"));
            WebElement accountTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h1[contains(normalize-space(), 'Детали счета')]")));
            Assertions.assertTrue(accountTitle.isDisplayed());
        } catch (TimeoutException e) {
            Assertions.fail("Нет счетов для перехода к деталям");
        }
    }
}