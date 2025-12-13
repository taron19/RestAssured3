package pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;

public class UserPage {

    public UserPage openBrowser(){
        open("/images/Toolsqa.jpg");
        return this;
    }

    public UserPage openBrowserAuthorized(){
        open("/profile");
        return this;
    }


    public UserPage checkUsername(String username){
        $("#userName-value").shouldHave(Condition.text(username));
        return this;
    }

    public UserPage checkBookPresence(String bookTitle){
        $$(".action-buttons").findBy(Condition.textCaseSensitive(bookTitle)).should(exist);
        return this;
    }

    public UserPage bookRemovalByIndex(int index){
        $$("#delete-record-undefined").get(index).click();
        $("#closeSmallModal-ok").click();
        return this;
    }

    public UserPage checkBookAbsenceByTitle(String title){
        $$(".action-buttons").findBy(Condition.textCaseSensitive(title)).shouldNot(exist);
        return this;
    }

}
