package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    private final By username = By.xpath("//input[@autocomplete='username']");
    private final By password = By.xpath("//input[@autocomplete='current-password']");
    private final By signIn = By.xpath("//button[@type='submit']");
    private final By inboxHeading = By.xpath("//h2[@title='Inbox']");
    private final By errorText = By.xpath("//div[@data-testid='login:error-block']");

    // a browser profile that is reused between tests can already be signed in, then there is nothing to type
    public void login(String name, String pwd) {
        wait.until(d -> isPresent(username) || isPresent(inboxHeading));
        if (isPresent(inboxHeading)) {
            return;
        }
        type(username, name);
        // newer sign-in page asks for the username first and shows the password field after "Continue"
        if (!isPresent(password)) {
            click(signIn);
            wait.until(d -> isPresent(password) || isPresent(errorText));
            if (isPresent(errorText)) {
                return;
            }
        }
        type(password, pwd);
        click(signIn);
    }

    public boolean isErrorDisplayed() {
        return isVisible(errorText);
    }
}
