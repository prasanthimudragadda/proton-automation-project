package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ExcelReader;

import java.io.File;
import java.io.IOException;

public class LoginDataDrivenTest extends BaseTest {
    private static final String DATA_FILE = "src/test/resources/testdata/LoginData.xlsx";

    public static final class LoginCase {
        final String user;
        final String pwd;
        final boolean expectSuccess;
        final String description;

        LoginCase(Object[] row) {
            this.user = (String) row[0];
            this.pwd = (String) row[1];
            this.description = (String) row[3];
            String expected = (String) row[2];
            if (!"success".equalsIgnoreCase(expected) && !"error".equalsIgnoreCase(expected)) {
                throw new IllegalArgumentException("Expected column must be 'success' or 'error', but was '"
                        + expected + "' in row: " + description);
            }
            this.expectSuccess = "success".equalsIgnoreCase(expected);
        }

        @Override
        public String toString() {
            return description;
        }
    }

    @Override
    protected boolean useCleanSession() {
        return true;
    }

    @DataProvider(name = "loginData")
    public Object[][] loginData() throws IOException {
        if (!new File(DATA_FILE).exists()) {
            return new Object[0][0];
        }
        Object[][] rows = ExcelReader.getData(DATA_FILE, "Login");
        Object[][] cases = new Object[rows.length][1];
        for (int i = 0; i < rows.length; i++) {
            cases[i][0] = new LoginCase(rows[i]);
        }
        return cases;
    }

    @Test(groups = "regression", dataProvider = "loginData")
    public void loginWithExcelData(LoginCase data) {
        lp.login(data.user, data.pwd);
        if (data.expectSuccess) {
            Assert.assertTrue(hp.isInboxDisplayed(), "Inbox expected: " + data.description);
        } else {
            Assert.assertTrue(lp.isErrorDisplayed(), "Login error expected: " + data.description);
        }
    }
}
