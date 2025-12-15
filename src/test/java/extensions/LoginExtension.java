package extensions;


import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.Getter;
import models.UserLoginData;
import models.WithLogin;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;
import pages.UserPage;
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
    @Getter
    private static final UserPage USER_PAGE = new UserPage();
    @Getter
    private static final String ENDPOINT_LOGIN = "/Account/v1/Login";
    @Getter
    private final String login = System.getProperty("login", "Clint");
    @Getter
    private final String credentials = System.getProperty("password", "Clint123456@");

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {

        WithLogin withLogin = extensionContext.getRequiredTestMethod().getAnnotation(WithLogin.class);

        if (withLogin == null) {
            withLogin = extensionContext.getRequiredTestClass().getAnnotation(WithLogin.class);
        }

        //если аннотацию нашли(withLogin != null true) и логин не пустой(НО У НАС ПУСТОЙ СПЕЦИАЛЬНО СДЕЛАЛИ ЧТОБЫ БРАТЬ ИЗ ПЕРЕМ ОКРУЖЕНИЯ)
        //System.getProperty
        String username = withLogin != null && !withLogin.username().isEmpty()
                ? withLogin.username()
                : login;

        String password = withLogin != null && !withLogin.password().isEmpty()
                ? withLogin.password()
                : credentials;


        UserLoginData loginData = new UserLoginData(username, password);


        Response response = step("Делаем REST-запрос на авторизацию", () -> given()
                .filter(withCustomTemplates())
                .spec(SpecCustoms.requestSpecification)
                .body(loginData)
                .when()
                .post(ENDPOINT_LOGIN)
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(200))
                .extract().response());


        step(" Открываем любую легкую страницу, чтобы браузер знал домен", () -> {
            USER_PAGE.openBrowser();
        });


        step("Добавляем куки в WebDriver", () -> {

            userId = response.path("userId");
            expires = response.path("expires");
            token = response.path("token");

            getWebDriver().manage().addCookie(new Cookie("userID", userId));
            getWebDriver().manage().addCookie(new Cookie("expires", expires));
            getWebDriver().manage().addCookie(new Cookie("token", token));
        });

        step("авторизация прошла успешно,вы зашли под такими данными", () -> {
            Allure.addAttachment("Авторизационные данные", String.format("Логин: %s\nПароль: %s", username, password));
        });


        step("Открываем профиль уже с куки", USER_PAGE::openBrowserAuthorized);


    }


}
