package tests;


import com.codeborne.selenide.Condition;
import extensions.LoginExtension;
import io.restassured.response.Response;
import models.Book;
import models.ISBN;
import models.WithLogin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import specs.SpecCustoms;

import java.util.List;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;
import static helpers.CustomApiListener.withCustomTemplates;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;


public class ApiTest extends TestBase {
    private static final String randomISBN1 = "9781449337711";
    private static final String randomISBN2 = "9781593277574";


    /**
     * Метод для авторизации
     * Можно просто @WithLogin, если дефолтные данные устраивают
     */
    @Test
    @WithLogin(username = "Clint", password = "Clint123456@")
    @DisplayName("authorization")
    public void authorization() {
        step("проверка авторизации", () -> {
            $("#userName-value").shouldHave(Condition.text("Clint"));
        });
    }


    public void clearBasket() {

        given()
                .filter(withCustomTemplates())
                .spec(SpecCustoms.requestSpecification)
                .param("UserId", LoginExtension.getUserId())
                .header("Authorization", "Bearer " + LoginExtension.getToken())
                .when()
                .delete("/BookStore/v1/Books")
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(204));

    }


    @Test
    @WithLogin(username = "Clint", password = "Clint123456@")
    public void addABookToBasket() {
        step("очищаем корзину", this::clearBasket);

        Response response = step("Добавляем книги в корзину", () -> given()
                .filter(withCustomTemplates())
                .spec(SpecCustoms.requestSpecification)
                .header("Authorization", "Bearer " + LoginExtension.getToken())
                .body(new Book(LoginExtension.getUserId(), List.of(new ISBN(randomISBN1), new ISBN(randomISBN2)))))
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(201))
                .extract().response();


        Assertions.assertEquals(randomISBN1, response.path("books[0].isbn"));
        Assertions.assertEquals(randomISBN2, response.path("books[1].isbn"));

        open("/profile");

        step("проверка отображения  пользователя", () -> {
            $("#userName-value").shouldHave(Condition.text("Clint"));
        });


        step("проверка присуствия книг", () -> {
            $$(".action-buttons").findBy(Condition.textCaseSensitive("Designing Evolvable Web APIs with ASP.NET")).should(exist);
            $$(".action-buttons").findBy(Condition.textCaseSensitive("Understanding ECMAScript 6")).should(exist);
        });


    }


    @Test
    @WithLogin(username = "Clint", password = "Clint123456@")
    public void deleteABookFromBasket() {

        step("удаление книги Understanding ECMAScript 6", () -> {
            $$("#delete-record-undefined").get(1).click();
            $("#closeSmallModal-ok").click();
        });

        step("проверка отсуствия книги Understanding ECMAScript 6", () -> {
            $$(".action-buttons").findBy(Condition.textCaseSensitive("Understanding ECMAScript 6")).shouldNot(exist);
        });
    }


}
