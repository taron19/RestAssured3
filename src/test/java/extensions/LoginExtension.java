package extensions;


import io.restassured.response.Response;
import lombok.Getter;
import models.UserLoginData;
import models.WithLogin;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;
import specs.SpecCustoms;


import static io.qameta.allure.Allure.step;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static helpers.CustomApiListener.withCustomTemplates;
import static io.restassured.RestAssured.given;

/**
 * Метод для авторизации(логика)
 * ищем аннотацию на уровне метода,если не нашли вернем null
 * ищем аннотацию на уровне класса,если не нашли вернем null
 * если все вернулo null значения не подставятся просто
 */

public class LoginExtension implements BeforeEachCallback {

    @Getter
    private static String userId;
    @Getter
    private static String expires;
    @Getter
    private static String token;

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {

        WithLogin withLogin = extensionContext.getRequiredTestMethod().getAnnotation(WithLogin.class);

        if (withLogin == null) {
            withLogin = extensionContext.getRequiredTestClass().getAnnotation(WithLogin.class);
        }

        String username = (withLogin != null) ? withLogin.username() : "Clint";
        String password = (withLogin != null) ? withLogin.password() : "Clint123456@";


        UserLoginData loginData = new UserLoginData(username, password);


        Response response = step("Делаем REST-запрос на авторизацию", () -> given()
                .filter(withCustomTemplates())
                .spec(SpecCustoms.requestSpecification)
                .body(loginData)
                .when()
                .post("/Account/v1/Login")
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(200))
                .extract().response());


        step(" Открываем любую легкую страницу, чтобы браузер знал домен", () ->
                open("/images/Toolsqa.jpg"));

        step("Добавляем куки в WebDriver", () -> {

            userId = response.path("userId");
            expires = response.path("expires");
            token = response.path("token");

            getWebDriver().manage().addCookie(new Cookie("userID", userId));
            getWebDriver().manage().addCookie(new Cookie("expires", expires));
            getWebDriver().manage().addCookie(new Cookie("token", token));
        });

        step("Открываем профиль уже с куки", () -> open("/profile"));


        System.out.println();
    }


}
