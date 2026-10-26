package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FilterTest extends BaseTest {

    String filterName;
    String keyword;
    boolean filterCreated = false;

    // fresh names for every test: Proton rejects a filter name that was used a moment ago by the previous test
    @BeforeMethod(alwaysRun = true)
    public void newFilterNames(){
        long id = System.currentTimeMillis();
        filterName = "AutoFilter" + id;
        keyword = "StarMe" + id;
    }

    @Test(groups = "regression")
    public void filterStarsMatchingMail(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());

        fp.openFilters();
        fp.createStarFilter(filterName, keyword);
        filterCreated = true;
        Assert.assertTrue(fp.isFilterDisplayed(filterName));

        // matching mail should be starred, so it shows up in Starred
        driver.get("https://account.proton.me/mail");
        String matching = keyword + " mail";
        ce.sendEmail(email, matching, "this subject has the filter keyword");
        ip.openStarred();
        Assert.assertTrue(ip.isMessageDisplayed(matching));
    }

    // negative and state checks on the same rule: a non matching mail is not starred, and once the filter is
    // switched off even a matching mail is not starred
    @Test(groups = "regression")
    public void filterIgnoresNonMatchingAndDisabled(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());

        fp.openFilters();
        fp.createStarFilter(filterName, keyword);
        filterCreated = true;
        Assert.assertTrue(fp.isFilterEnabled(filterName, true));

        driver.get("https://account.proton.me/mail");
        String nonMatching = "Plain" + System.currentTimeMillis();
        ce.sendEmail(email, nonMatching, "this subject does not have the filter keyword");
        Assert.assertTrue(ip.isMessageDisplayed(nonMatching));
        ip.openStarred();
        Assert.assertTrue(ip.isMessageGone(nonMatching));

        fp.openFilters();
        fp.toggleFilter(filterName);
        Assert.assertTrue(fp.isFilterEnabled(filterName, false));

        driver.get("https://account.proton.me/mail");
        String whileDisabled = keyword + " disabled";
        ce.sendEmail(email, whileDisabled, "this subject has the keyword but the filter is off");
        Assert.assertTrue(ip.isMessageDisplayed(whileDisabled));
        ip.openStarred();
        Assert.assertTrue(ip.isMessageGone(whileDisabled));
    }

    // free account allows only one filter, so always remove it after the test
    @AfterMethod(alwaysRun = true)
    public void deleteFilter(){
        if (filterCreated) {
            filterCreated = false;
            fp.openFilters();
            fp.deleteFilter(filterName);
            Assert.assertTrue(fp.isFilterGone(filterName));
        }
    }
}
