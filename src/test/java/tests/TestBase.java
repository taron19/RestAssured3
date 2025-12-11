package tests;

import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class TestBase {

    @BeforeAll
    public static void setUp() {
        Configuration.remote = "https://user1:1234@selenoid.autotests.cloud/wd/hub";
        RestAssured.baseURI = "https://demoqa.com";
        Configuration.baseUrl="https://demoqa.com";
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";
        Configuration.browserVersion ="128.0";
        Configuration.pageLoadStrategy = "eager";

    }

}
