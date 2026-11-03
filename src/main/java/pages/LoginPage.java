package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    private final By username = By.xpath("//input[@autocomplete='username']");
    private final By password = By.xpath("//input[@autocomplete='current-password']");
    private final By signIn = By.xpath("//button[@type='submit']");
    private final By inboxHeading = By.xpath("//h2[@title='Inbox']");
    private final By errorText = By.xpath("//div[@data-testid='login:error-block']");

    public void login(String name, String pwd) {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(username),
                ExpectedConditions.presenceOfElementLocated(inboxHeading)));
        if (isPresent(inboxHeading)) {
            return;
        }
        type(username, name);
        if (!isPresent(password)) {
            click(signIn);
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(password),
                    ExpectedConditions.presenceOfElementLocated(errorText)));
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
