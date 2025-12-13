package models;

import extensions.LoginExtension;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import specs.SpecCustoms;

import java.util.List;

import static helpers.CustomApiListener.withCustomTemplates;
import static io.restassured.RestAssured.given;


public class ApiRequests {


    private static final String ENDPOINT = "/BookStore/v1/Books";

    private String token() {
        return LoginExtension.getToken();
    }


    public void clearBasket() {

        given()
                .filter(withCustomTemplates())
                .spec(SpecCustoms.requestSpecification)
                .param("UserId", LoginExtension.getUserId())
                .header("Authorization", "Bearer " + token())
                .when()
                .delete(ENDPOINT)
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(204));

    }

    public Response addBookToBasketByISBN(String ... isbn) {
        Response response = given()
                .filter(withCustomTemplates())
                .spec(SpecCustoms.requestSpecification)
                .header("Authorization", "Bearer " + token())
                .body(new Book(LoginExtension.getUserId(), List.of(new ISBN(isbn[0]), new ISBN(isbn[1]))))
                .when()
                .post(ENDPOINT)
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(201))
                .extract().response();

        Assertions.assertEquals(isbn[0], response.path("books[0].isbn"));
        Assertions.assertEquals(isbn[1], response.path("books[1].isbn"));

        return response;
    }
}
