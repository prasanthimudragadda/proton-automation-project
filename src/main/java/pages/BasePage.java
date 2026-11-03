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

public abstract class BasePage {
    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);
    protected static final Duration MAIL_DELIVERY_TIMEOUT = Duration.ofSeconds(60);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

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

    protected void clickWithRetry(By locator) {
        WebDriverWait retryWait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        retryWait.ignoring(ElementClickInterceptedException.class);
        retryWait.until(browser -> {
            browser.findElement(locator).click();
            return true;
        });
    }

    protected void switchToFrame(By frame) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
    }

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
        return waitUntil(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected boolean waitUntil(ExpectedCondition<?> condition) {
        try {
            wait.until(condition);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }
}
