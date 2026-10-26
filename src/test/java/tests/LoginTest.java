package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    // login, logout and credential checks need a browser that is not signed in yet
    @Override
    protected boolean useCleanSession(){
        return true;
    }


 @Test(groups = {"smoke", "regression"})
    public void loginWithValidCredentials(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
    }

 @Test(groups = "regression")
    public void loginWithInValidCred(){
        lp.login(username, incorrectPwd);
        Assert.assertTrue(lp.errorMessage());
    }

   @Test(groups = "regression")
    public void signOut(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        Assert.assertTrue(hp.signOut());
    }

   @Test(groups = "regression")
    public void sessionStaysAfterRefresh(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        driver.navigate().refresh();
        Assert.assertTrue(hp.isInboxDisplayed());
        Assert.assertTrue(driver.getCurrentUrl().contains("mail.proton.me"));
    }

}
