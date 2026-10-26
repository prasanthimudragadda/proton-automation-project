package utils;

import base.BaseTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;

// takes a screenshot whenever a test fails, saved in reports/screenshots
public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = ((BaseTest) result.getInstance()).driver;
        if (driver == null) {
            return;
        }
        String name = result.getName() + "_" + System.currentTimeMillis();
        try {
            // page url and error text next to the screenshot help to debug CI failures
            String log = "url: " + driver.getCurrentUrl() + System.lineSeparator()
                    + "error: " + result.getThrowable() + System.lineSeparator();
            File logFile = new File("reports/logs/" + name + ".txt");
            FileUtils.forceMkdirParent(logFile);
            FileUtils.writeStringToFile(logFile, log, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(src, new File("reports/screenshots/" + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
