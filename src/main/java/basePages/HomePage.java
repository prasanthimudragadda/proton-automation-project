package basePages;

import Utilities.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends Waits {
    public HomePage(WebDriver driver){
        super(driver);
    }
    private final By inboxText = By.xpath("//h2[@title='Inbox']");
    private final By profileClick = By.xpath("//button[@data-testid='heading:userdropdown']");
    private final By signOutButton = By.xpath("//button[@data-testid='userdropdown:button:logout']");
    private final By confirmSignOutButton = By.xpath("//button[normalize-space()='Sign out']");
    private final By signInHeading = By.xpath("//h1[normalize-space()='Sign in']");

    public boolean isInboxDisplayed(){
        return waitForElementToVisible(inboxText).isDisplayed();
    }

    public boolean signOut(){
        elementTobeClickable(profileClick).click();
        elementTobeClickable(signOutButton).click();
        elementTobeClickable(confirmSignOutButton).click();
        return waitForElementToVisible(signInHeading).isDisplayed();
    }
}
