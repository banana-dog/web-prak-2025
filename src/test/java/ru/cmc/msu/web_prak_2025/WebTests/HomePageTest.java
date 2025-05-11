package ru.cmc.msu.web_prak_2025.WebTests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HomePageTest {
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
    public void testHomePageLoadsSuccessfully() {
        driver.get(BASE_URL);

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(normalize-space(), 'Банковская система')]")));
        Assertions.assertTrue(title.isDisplayed());

        WebElement subtitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(normalize-space(), 'Управление клиентами, счетами и отделениями банка')]")));
        Assertions.assertTrue(subtitle.isDisplayed());
    }

    @Test
    public void testNavigationCardsAreDisplayed() {
        driver.get(BASE_URL);

        WebElement clientsCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'clients-card')]")));
        WebElement accountsCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'accounts-card')]")));
        WebElement branchesCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'branches-card')]")));

        Assertions.assertTrue(clientsCard.isDisplayed());
        Assertions.assertTrue(accountsCard.isDisplayed());
        Assertions.assertTrue(branchesCard.isDisplayed());
    }

    @Test
    public void testClientsCardContent() {
        driver.get(BASE_URL);

        WebElement clientsCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'clients-card')]")));

        WebElement icon = clientsCard.findElement(By.cssSelector(".section-icon i.bi-people"));
        Assertions.assertTrue(icon.isDisplayed());

        WebElement title = clientsCard.findElement(By.xpath(".//h3[contains(normalize-space(), 'Клиенты')]"));
        Assertions.assertTrue(title.isDisplayed());

        WebElement description = clientsCard.findElement(By.xpath(".//p[contains(normalize-space(), 'Управление клиентами банка')]"));
        Assertions.assertTrue(description.isDisplayed());

        WebElement button = clientsCard.findElement(By.xpath(".//a[contains(@href, '/clients')]"));
        Assertions.assertTrue(button.isDisplayed());
        Assertions.assertTrue(button.isEnabled());
        Assertions.assertEquals("Перейти к клиентам", button.getText().trim());
    }

    @Test
    public void testAccountsCardContent() {
        driver.get(BASE_URL);

        WebElement accountsCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'accounts-card')]")));

        WebElement icon = accountsCard.findElement(By.cssSelector(".section-icon i.bi-wallet2"));
        Assertions.assertTrue(icon.isDisplayed());

        WebElement title = accountsCard.findElement(By.xpath(".//h3[contains(normalize-space(), 'Счета')]"));
        Assertions.assertTrue(title.isDisplayed());

        WebElement description = accountsCard.findElement(By.xpath(".//p[contains(normalize-space(), 'Управление банковскими счетами')]"));
        Assertions.assertTrue(description.isDisplayed());

        WebElement button = accountsCard.findElement(By.xpath(".//a[contains(@href, '/accounts')]"));
        Assertions.assertTrue(button.isDisplayed());
        Assertions.assertTrue(button.isEnabled());
        Assertions.assertEquals("Перейти к счетам", button.getText().trim());
    }

    @Test
    public void testBranchesCardContent() {
        driver.get(BASE_URL);

        WebElement branchesCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'branches-card')]")));

        WebElement icon = branchesCard.findElement(By.cssSelector(".section-icon i.bi-building"));
        Assertions.assertTrue(icon.isDisplayed());

        WebElement title = branchesCard.findElement(By.xpath(".//h3[contains(normalize-space(), 'Отделения')]"));
        Assertions.assertTrue(title.isDisplayed());

        WebElement description = branchesCard.findElement(By.xpath(".//p[contains(normalize-space(), 'Управление филиалами банка')]"));
        Assertions.assertTrue(description.isDisplayed());

        WebElement button = branchesCard.findElement(By.xpath(".//a[contains(@href, '/bank')]"));
        Assertions.assertTrue(button.isDisplayed());
        Assertions.assertTrue(button.isEnabled());
        Assertions.assertEquals("Перейти к отделениям", button.getText().trim());
    }

    @Test
    public void testNavigationToClientsPage() {
        driver.get(BASE_URL);

        WebElement clientsButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/clients')]")));
        clientsButton.click();

        wait.until(ExpectedConditions.urlContains("/clients"));
        Assertions.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).endsWith("/clients"));
    }

    @Test
    public void testNavigationToAccountsPage() {
        driver.get(BASE_URL);

        WebElement accountsButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/accounts')]")));
        accountsButton.click();

        wait.until(ExpectedConditions.urlContains("/accounts"));
        Assertions.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).endsWith("/accounts"));
    }

    @Test
    public void testNavigationToBranchesPage() {
        driver.get(BASE_URL);

        WebElement branchesButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/bank')]")));
        branchesButton.click();

        wait.until(ExpectedConditions.urlContains("/bank"));
        Assertions.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).endsWith("/bank"));
    }

    @Test
    public void testHoverEffectsOnCards() {
        driver.get(BASE_URL);

        WebElement clientsCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'clients-card')]")));

        String initialBoxShadow = clientsCard.getCssValue("box-shadow");
        String initialTransform = clientsCard.getCssValue("transform");

        new org.openqa.selenium.interactions.Actions(driver)
                .moveToElement(clientsCard)
                .perform();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.attributeContains(clientsCard, "style", "transform: none")));

        String hoverBoxShadow = clientsCard.getCssValue("box-shadow");
        String hoverTransform = clientsCard.getCssValue("transform");

        Assertions.assertNotEquals(initialBoxShadow, hoverBoxShadow);
        Assertions.assertNotEquals(initialTransform, hoverTransform);
    }
}