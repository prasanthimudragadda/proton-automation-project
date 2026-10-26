package Utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Waits {
    protected WebDriver driver;
    protected WebDriverWait wait;
    public Waits(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public WebElement waitForElementToVisible(By ele){
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ele));
    }

    // for things that really take longer, e.g. a new mail showing up in the list (Proton refreshes lists by polling)
    public WebElement waitForElementToVisible(By ele, int seconds){
        return new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(ExpectedConditions.visibilityOfElementLocated(ele));
    }

    public WebElement elementTobeClickable(By eleClick){
        return wait.until(ExpectedConditions.elementToBeClickable(eleClick));
    }

    // retries the click while another element (e.g. a "Draft saved" toast) covers the target
    public void clickWhenNotCovered(By target){
        wait.ignoring(ElementClickInterceptedException.class).until(d -> {
            d.findElement(target).click();
            return true;
        });
    }

    public void switchToFrame(By iFrame){
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iFrame));
    }

    public boolean waitForElementToDisappear(By ele){
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(ele));
    }

    public boolean isElementPresent(By ele){
        return !driver.findElements(ele).isEmpty();
    }
}
