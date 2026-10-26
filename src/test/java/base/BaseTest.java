package base;

import basePages.ComposeEmail;
import basePages.FilterPage;
import basePages.HomePage;
import basePages.InboxPage;
import basePages.LoginPage;
import basePages.SearchPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import utils.ConfigReader;
import utils.TestListener;

import java.io.File;
import java.io.IOException;

@Listeners(TestListener.class)
public class BaseTest {

    public WebDriver driver;
    protected LoginPage lp;
    protected HomePage hp;
    protected ComposeEmail ce;
    protected InboxPage ip;
    protected SearchPage sp;
    protected FilterPage fp;
    protected ConfigReader config;
    
    protected String username;
    protected String password;
    protected String email;
    protected String recipient;
    protected String label;
    protected String incorrectPwd;

    @BeforeClass(alwaysRun = true)
    public void loadCredentials() throws IOException {
        config = new ConfigReader();
        username = config.getProperty("proton.username");
        password = config.getProperty("proton.password");
        email = config.getProperty("proton.email");
        recipient = config.getProperty("proton.recipient");
        label = config.getProperty("proton.label");
        incorrectPwd = config.getProperty("proton.incorrectPwd");

    }
    @BeforeMethod(alwaysRun = true)
    public void setUp(){
        driver = useCleanSession() ? createDriver() : createDriver(SESSION_PROFILE);
        driver.get("https://account.proton.me/mail");
        lp = new LoginPage(driver);
        hp = new HomePage(driver);
        ce = new ComposeEmail(driver);
        ip = new InboxPage(driver);
        sp = new SearchPage(driver);
        fp = new FilterPage(driver);

    }

    // Proton throttles repeated logins (a login per test made the suite fail after ~12 tests). Normal tests therefore
    // share one Chrome profile: the first test signs in, the next ones start already signed in ("Keep me signed in").
    // Tests about login/logout override this and get a brand new, signed out browser.
    private static final String SESSION_PROFILE = new File("target/chrome-profile").getAbsolutePath();

    protected boolean useCleanSession(){
        return false;
    }

    protected WebDriver createDriver(){
        return createDriver(null);
    }

    // run with -Dheadless=true to run without opening the browser window
    private WebDriver createDriver(String profileDir){
        ChromeOptions options = new ChromeOptions();
        if (profileDir != null) {
            options.addArguments("--user-data-dir=" + profileDir);
        }
        if (Boolean.getBoolean("headless")) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        WebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().window().maximize();
        return webDriver;
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(){
        if (driver != null) {
            driver.quit();
        }
    }
}
