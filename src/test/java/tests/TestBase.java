package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class TestBase {

    @BeforeAll
    public static void setUp() {
        Configuration.headless = true;
        Configuration.browserSize = "1920x1080";
        RestAssured.baseURI = "https://demoqa.com";
        Configuration.baseUrl="https://demoqa.com";

    }

    @AfterAll
    static void tearDown() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            getWebDriver().quit();
        }
    }
}
