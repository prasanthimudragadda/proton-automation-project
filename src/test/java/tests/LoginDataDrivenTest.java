package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ExcelReader;

import java.io.IOException;

public class LoginDataDrivenTest extends BaseTest {

    // login, logout and credential checks need a browser that is not signed in yet
    @Override
    protected boolean useCleanSession(){
        return true;
    }


    // each row of LoginData.xlsx = one test run (Username, Password, Expected, Description)
    @DataProvider(name = "loginData")
    public Object[][] loginData() throws IOException {
        // the sheet holds real passwords and is not committed, so without it this test simply has no rows
        if (!new java.io.File("src/test/resources/testdata/LoginData.xlsx").exists()) {
            return new Object[0][0];
        }
        return ExcelReader.getData("src/test/resources/testdata/LoginData.xlsx", "Login");
    }

    @Test(groups = "regression", dataProvider = "loginData")
    public void loginWithExcelData(String user, String pwd, String expected, String description){
        lp.login(user, pwd);
        if (expected.equalsIgnoreCase("success")) {
            Assert.assertTrue(hp.isInboxDisplayed(), description);
        } else {
            Assert.assertTrue(lp.errorMessage(), description);
        }
    }
}
