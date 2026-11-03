package utils;

import base.BaseTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class TestListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = BaseTest.getDriver();
        if (driver == null) {
            return;
        }
        String name = result.getName() + "_" + System.currentTimeMillis();
        try {
            String log = "url: " + driver.getCurrentUrl() + System.lineSeparator()
                    + "error: " + result.getThrowable() + System.lineSeparator();
            File logFile = new File("reports/logs/" + name + ".txt");
            FileUtils.forceMkdirParent(logFile);
            FileUtils.writeStringToFile(logFile, log, StandardCharsets.UTF_8);

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File("reports/screenshots/" + name + ".png"));
        } catch (Exception e) {
            System.err.println("Could not save failure evidence for " + name + ": " + e);
        }
    }
}
