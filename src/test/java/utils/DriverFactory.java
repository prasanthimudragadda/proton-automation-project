package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

// the only place that knows how a browser is started; Selenium Manager downloads the matching chromedriver
public final class DriverFactory {

    private DriverFactory() {
    }

    // profileDir == null -> brand new, signed out browser. Run with -Dheadless=true to hide the window (CI).
    public static WebDriver createChrome(String profileDir) {
        ChromeOptions options = new ChromeOptions();
        if (profileDir != null) {
            options.addArguments("--user-data-dir=" + profileDir);
        }
        boolean headless = Boolean.getBoolean("headless");
        if (headless) {
            // headless has no screen, so the window size must be given explicitly
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        WebDriver driver = new ChromeDriver(options);
        // maximize() in headless shrinks the window back to 800x600: Proton then switches to its compact layout
        // (search box becomes an icon, toolbar buttons are hidden) and 9 tests failed in headless/CI only
        if (!headless) {
            driver.manage().window().maximize();
        }
        return driver;
    }
}
