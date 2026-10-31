package basePages;

import Utilities.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends Waits {

    public LoginPage(WebDriver driver){
        super(driver);
    }

    private final By username = By.xpath("//input[@autocomplete='username']");
    private final By password = By.xpath("//input[@autocomplete='current-password']");
    private final By signIn = By.xpath("//button[@type='submit']");
    private final By inboxHeading = By.xpath("//h2[@title='Inbox']");
    private final By errorText = By.xpath("//div[@data-testid='login:error-block']");

    // a browser profile that is reused between tests can already be signed in, then there is nothing to type
    public void login (String name, String pwd){
        wait.until(d -> isElementPresent(username) || isElementPresent(inboxHeading));
        if (isElementPresent(inboxHeading)) {
            return;
        }
        waitForElementToVisible(username).sendKeys(name);
        // newer sign-in page asks for the username first and shows the password field after "Continue"
        if (!isElementPresent(password)) {
            elementTobeClickable(signIn).click();
            wait.until(d -> isElementPresent(password) || isElementPresent(errorText));
            if (isElementPresent(errorText)) {
                return;
            }
        }
        waitForElementToVisible(password).sendKeys(pwd);
        elementTobeClickable(signIn).click();
    }

    public boolean errorMessage(){
        return waitForElementToVisible(errorText).isDisplayed();


    }
}
