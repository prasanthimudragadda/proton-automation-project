package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// Parent of every page object: holds the driver and all synchronisation helpers, so no page uses Thread.sleep.
// Actions (click, type, waitFor...) fail fast with a TimeoutException that names the locator.
// Checks (isVisible, isGone, eventually) return true/false, so the test's assertion message explains the failure.
public abstract class BasePage {

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);
    // a new mail needs longer: Proton refreshes the message list by polling
    protected static final Duration MAIL_DELIVERY_TIMEOUT = Duration.ofSeconds(60);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    // ---------- actions ----------

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForVisible(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(By locator, CharSequence... text) {
        waitForVisible(locator).sendKeys(text);
    }

    // retries the click while another element (e.g. a "Draft saved" toast) covers the target.
    // A separate wait object is used so the shared one does not start ignoring this exception everywhere.
    protected void clickWhenNotCovered(By target) {
        new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .ignoring(ElementClickInterceptedException.class)
                .until(d -> {
                    d.findElement(target).click();
                    return true;
                });
    }

    protected void switchToFrame(By frame) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
    }

    // ---------- checks (return false instead of throwing) ----------

    protected boolean isVisible(By locator) {
        return isVisible(locator, DEFAULT_TIMEOUT);
    }

    protected boolean isVisible(By locator, Duration timeout) {
        try {
            waitForVisible(locator, timeout);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean isGone(By locator) {
        return eventually(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // waits until the condition is true; false if it never became true within the default timeout
    protected boolean eventually(ExpectedCondition<Boolean> condition) {
        try {
            return Boolean.TRUE.equals(wait.until(condition));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // immediate check, no waiting: used to decide between two UI states that are already rendered
    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }
}
