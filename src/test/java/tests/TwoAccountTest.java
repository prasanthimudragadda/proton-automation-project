package tests;

import base.BaseTest;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.InboxPage;
import pages.LoginPage;

public class TwoAccountTest extends BaseTest {

    // end to end: account 1 sends a mail, account 2 logs in with a second browser and finds it in its inbox
    @Test(groups = "regression")
    public void senderToReceiver() {
        String receiverName = config.getProperty("proton.recipient.username");
        String receiverPassword = config.getProperty("proton.recipient.password");
        if (receiverPassword == null || receiverPassword.isEmpty()) {
            throw new SkipException("proton.recipient.password is not set in config.properties");
        }

        String subject = "TwoAccount" + System.currentTimeMillis();
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed(), "Sender inbox should be shown after login");
        cp.sendEmail(recipient, subject, "mail from account 1 to account 2");

        WebDriver receiverDriver = newCleanBrowser();
        try {
            receiverDriver.get(mailUrl);
            new LoginPage(receiverDriver).login(receiverName, receiverPassword);
            Assert.assertTrue(new HomePage(receiverDriver).isInboxDisplayed(), "Receiver inbox should be shown after login");
            Assert.assertTrue(new InboxPage(receiverDriver).isMessageDisplayed(subject),
                    "Receiver should get mail '" + subject + "' from account 1");
        } finally {
            receiverDriver.quit();
        }
    }
}
