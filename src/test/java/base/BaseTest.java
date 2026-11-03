package base;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import pages.ComposePage;
import pages.FilterPage;
import pages.HomePage;
import pages.InboxPage;
import pages.LoginPage;
import pages.SearchPage;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.TestListener;

import java.io.File;
import java.io.IOException;

@Listeners(TestListener.class)
public class BaseTest {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private static final String SESSION_PROFILE = new File("target/chrome-profile").getAbsolutePath();

    protected LoginPage lp;
    protected HomePage hp;
    protected ComposePage cp;
    protected InboxPage ip;
    protected SearchPage sp;
    protected FilterPage fp;
    protected ConfigReader config;

    protected String mailUrl;
    protected String username;
    protected String password;
    protected String email;
    protected String recipient;
    protected String label;
    protected String incorrectPwd;

    @BeforeClass(alwaysRun = true)
    public void loadConfig() throws IOException {
        config = new ConfigReader();
        mailUrl = config.getProperty("proton.url", "https://account.proton.me/mail");
        username = config.getProperty("proton.username");
        password = config.getProperty("proton.password");
        email = config.getProperty("proton.email");
        recipient = config.getProperty("proton.recipient");
        label = config.getProperty("proton.label", "p");
        incorrectPwd = config.getProperty("proton.incorrectPwd", "wrong-password-123");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriver driver = DriverFactory.createChrome(useCleanSession() ? null : SESSION_PROFILE);
        DRIVER.set(driver);
        driver.get(mailUrl);
        lp = new LoginPage(driver);
        hp = new HomePage(driver);
        cp = new ComposePage(driver);
        ip = new InboxPage(driver);
        sp = new SearchPage(driver);
        fp = new FilterPage(driver);
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    protected boolean useCleanSession() {
        return false;
    }

    protected WebDriver newCleanBrowser() {
        return DriverFactory.createChrome(null);
    }

    protected void openMailbox() {
        getDriver().get(mailUrl);
    }

    protected void loginToInbox() {
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed(), "Inbox should be shown after login");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
