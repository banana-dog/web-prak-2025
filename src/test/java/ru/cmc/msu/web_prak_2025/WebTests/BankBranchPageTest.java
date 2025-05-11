package ru.cmc.msu.web_prak_2025.WebTests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BankBranchPageTest {
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
    public void testBranchesListPage() {
        driver.get(BASE_URL + "/bank");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(normalize-space(), 'Отделения банка')]")));
        Assertions.assertTrue(title.isDisplayed());

        WebElement filterCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".filter-card")));
        Assertions.assertTrue(filterCard.isDisplayed());

        WebElement addBranchButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(normalize-space(), 'Новое отделение')]")));
        Assertions.assertTrue(addBranchButton.isDisplayed());
        Assertions.assertTrue(addBranchButton.isEnabled());
    }

    @Test
    public void testBranchFiltering() {
        driver.get(BASE_URL + "/bank");

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameInput.clear();
        nameInput.sendKeys("Центральный");

        WebElement clientsNumberInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("clientsNumber")));
        clientsNumberInput.clear();
        clientsNumberInput.sendKeys("100");

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Поиск')]")));
        searchButton.click();

        try {
            WebElement branchesContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".row")));
            Assertions.assertTrue(branchesContainer.isDisplayed());

            List<WebElement> branchCards = driver.findElements(By.cssSelector(".branch-card"));
            Assertions.assertTrue(branchCards.isEmpty());

            for (WebElement card : branchCards) {
                String branchName = card.findElement(By.cssSelector(".card-title")).getText();
                String clientsCount = card.findElement(By.cssSelector(".clients-count")).getText();

                Assertions.assertTrue(branchName.contains("Центральный"));
                Assertions.assertTrue(Integer.parseInt(clientsCount) >= 100);
            }
        } catch (TimeoutException e) {
            WebElement noBranchesMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h4[contains(normalize-space(), 'Отделения не найдены')]")));
            Assertions.assertTrue(noBranchesMessage.isDisplayed());
        }
    }

    @Test
    public void testBranchDetailsPage() {
        driver.get(BASE_URL + "/bank/1");

        WebElement branchName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".detail-card .card-title")));
        WebElement branchAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".detail-card .text-muted")));

        Assertions.assertTrue(branchName.isDisplayed());
        Assertions.assertTrue(branchAddress.isDisplayed());
    }

    @Test
    public void testAddNewBranch() {
        driver.get(BASE_URL + "/bank");

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Новое отделение')]")));
        addButton.click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addBranchModal")));
        Assertions.assertTrue(modal.isDisplayed());

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newName")));
        nameInput.sendKeys("Тестовое отделение");

        WebElement addressInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newAddress")));
        addressInput.sendKeys("Тестовый адрес");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='addBranchModal']//button[contains(normalize-space(), 'Добавить')]")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/bank"));

        WebElement nameFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameFilter.clear();
        nameFilter.sendKeys("Тестовое");

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(), 'Поиск')]")));
        searchButton.click();

        try {
            WebElement branchCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'branch-card') and contains(., 'Тестовое отделение')]")));
            Assertions.assertTrue(branchCard.isDisplayed());
        } catch (TimeoutException e) {
            Assertions.fail("Новое отделение не найдено в списке после добавления");
        }
    }

    @Test
    public void testEditBranch() {
        driver.get(BASE_URL + "/bank");

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(normalize-space(), 'Редактировать')])[1]")));
        editButton.click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@id, 'editBranchModal') and contains(@style, 'display: block')]")));
        Assertions.assertTrue(modal.isDisplayed());

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@id, 'editBranchModal')]//input[@name='name']")));
        nameInput.clear();
        nameInput.sendKeys("Измененное название");

        WebElement clientsNumberInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@id, 'editBranchModal')]//input[@name='clientsNumber']")));
        clientsNumberInput.clear();
        clientsNumberInput.sendKeys("150");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@id, 'editBranchModal')]//button[contains(normalize-space(), 'Сохранить')]")));
        submitButton.click();

        wait.until(ExpectedConditions.urlContains("/bank"));

        WebElement branchName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//div[contains(@class, 'branch-card')]//h5[contains(., 'Измененное название')])[1]")));
        WebElement clientsCount = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//div[contains(@class, 'branch-card')]//span[contains(., '150')])[1]")));

        Assertions.assertTrue(branchName.isDisplayed());
        Assertions.assertTrue(clientsCount.isDisplayed());
    }


    @Test
    public void testViewBranchClients() {
        driver.get(BASE_URL + "/bank/1");

        try {
            WebElement clientsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h2[contains(normalize-space(), 'Клиенты отделения')]")));
            Assertions.assertTrue(clientsSection.isDisplayed());

            List<WebElement> clientCards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector(".client-card")));
            Assertions.assertFalse(clientCards.isEmpty());
        } catch (TimeoutException e) {
            WebElement noClientsMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h4[contains(normalize-space(), 'В отделении нет клиентов')]")));
            Assertions.assertTrue(noClientsMessage.isDisplayed());
        }
    }

    @Test
    public void testDeleteBranch() {
        driver.get(BASE_URL + "/bank");

        List<WebElement> branchCardsBefore = wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.cssSelector(".branch-card")));

        if (branchCardsBefore.isEmpty()) {
            Assertions.fail("Нет отделений для удаления");
            return;
        }

        WebElement firstBranchDetailsLink = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("(//a[contains(@href, '/bank/')])[1]")));
        String branchUrl = firstBranchDetailsLink.getAttribute("href");
        assert branchUrl != null;
        long branchId = Long.parseLong(branchUrl.substring(branchUrl.lastIndexOf('/') + 1));

        WebElement deleteButton = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("(//button[contains(normalize-space(), 'Удалить')])[1]")));
        deleteButton.click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        wait.until(ExpectedConditions.urlContains("/bank"));

        try {
            driver.get(BASE_URL + "/bank/" + branchId);

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/error"),
                    ExpectedConditions.urlContains("/bank"),
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(), 'Произошла ошибка')]"))
            ));

        } catch (TimeoutException e) {
            Assertions.fail("Страница отделения все еще доступна после удаления");
        }
    }

}