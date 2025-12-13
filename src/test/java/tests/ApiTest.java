package tests;


import models.ApiRequests;
import models.WithLogin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.UserPage;

import static io.qameta.allure.Allure.step;


@Tag("RestAssured")
public class ApiTest extends TestBase {


    private static final UserPage USER_PAGE = new UserPage();
    private static final String USERNAME = "Clint";
    private static final String BOOK_TITLE1 = "Designing Evolvable Web APIs with ASP.NET";
    private static final String BOOK_TITLE2 = "Understanding ECMAScript 6";
    private static final int INDEX = 1;
    private static final ApiRequests REQUESTS = new ApiRequests();
    private static final String RANDOM_ISBN = "9781449337711";
    private static final String RANDOM_ISBN2 = "9781593277574";

    /**
     * Метод для авторизации
     * Можно просто @WithLogin, если дефолтные данные устраивают
     */
    @Test
    @WithLogin(username = "Clint", password = "Clint123456@")
    @DisplayName("authorization")
    public void authorization() {
        step("проверка авторизации", () -> {
            USER_PAGE.checkUsername(USERNAME);
        });
    }


    @Test
    @WithLogin(username = "Clint", password = "Clint123456@")
    public void addABookToBasket() {
        step("очищаем корзину", REQUESTS::clearBasket);

        step("Добавляем книги в корзину", () ->
                REQUESTS.addBookToBasketByISBN(RANDOM_ISBN,RANDOM_ISBN2));

        USER_PAGE.openBrowserAuthorized();

        step("проверка отображения  пользователя", () -> {
            USER_PAGE.checkUsername(USERNAME);
        });


        step("проверка присуствия книг", () -> {
            USER_PAGE.checkBookPresence(BOOK_TITLE1);
            USER_PAGE.checkBookPresence(BOOK_TITLE2);
        });


    }


    @Test
    @WithLogin(username = "Clint", password = "Clint123456@")
    public void deleteABookFromBasket() {

        step("удаление книги " + BOOK_TITLE2, () -> {
            USER_PAGE.bookRemovalByIndex(INDEX);
        });

        step("проверка отсуствия книги " + BOOK_TITLE2, () -> {
            USER_PAGE.checkBookAbsenceByTitle(BOOK_TITLE2);
        });
    }


}
