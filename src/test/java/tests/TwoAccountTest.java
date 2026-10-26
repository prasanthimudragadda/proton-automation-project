package tests;

import base.BaseTest;
import basePages.HomePage;
import basePages.InboxPage;
import basePages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class TwoAccountTest extends BaseTest {

    // sender sends a mail, receiver logs in with a second browser and checks the mail
    @Test(groups = "regression")
    public void senderToReceiver(){
        String receiverName = config.getProperty("proton.recipient.username");
        String receiverPassword = config.getProperty("proton.recipient.password");
        if (receiverPassword == null || receiverPassword.trim().isEmpty()) {
            throw new SkipException("proton.recipient.password is not set in config.properties");
        }

        String subject = "TwoAccount" + System.currentTimeMillis();
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.sendEmail(recipient, subject, "mail from account 1 to account 2");

        WebDriver receiverDriver = createDriver();
        try {
            receiverDriver.get("https://account.proton.me/mail");
            new LoginPage(receiverDriver).login(receiverName, receiverPassword);
            Assert.assertTrue(new HomePage(receiverDriver).isInboxDisplayed());
            Assert.assertTrue(new InboxPage(receiverDriver).isMessageDisplayed(subject));
        } finally {
            receiverDriver.quit();
        }
    }
}
