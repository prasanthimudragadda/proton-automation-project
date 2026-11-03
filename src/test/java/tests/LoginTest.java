package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    // login, logout and credential checks need a browser that is not signed in yet
    @Override
    protected boolean useCleanSession() {
        return true;
    }

    @Test(groups = {"smoke", "regression"})
    public void loginWithValidCredentials() {
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed(), "Inbox should be shown after login with valid credentials");
    }

    @Test(groups = "regression")
    public void loginWithInValidCred() {
        lp.login(username, incorrectPwd);
        Assert.assertTrue(lp.isErrorDisplayed(), "Login error should be shown for a wrong password");
        // instant URL check instead of waiting 20 s for an inbox that should never appear
        Assert.assertTrue(getDriver().getCurrentUrl().contains("account.proton.me"),
                "User should stay on the sign-in page, but URL is " + getDriver().getCurrentUrl());
    }

    @Test(groups = "regression")
    public void signOut() {
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed(), "Inbox should be shown after login");
        hp.signOut();
        Assert.assertTrue(hp.isSignInPageDisplayed(), "Sign in page should be shown after sign out");
    }

    @Test(groups = "regression")
    public void sessionStaysAfterRefresh() {
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed(), "Inbox should be shown after login");
        getDriver().navigate().refresh();
        Assert.assertTrue(hp.isInboxDisplayed(), "Inbox should still be shown after a page refresh");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("mail.proton.me"),
                "User should stay in the mailbox after refresh, but URL is " + getDriver().getCurrentUrl());
    }
}
