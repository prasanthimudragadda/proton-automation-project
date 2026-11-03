package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FilterTest extends BaseTest {

    private String filterName;
    private String keyword;
    private boolean filterCreated = false;

    // fresh names for every test: Proton rejects a filter name that was used a moment ago by the previous test
    @BeforeMethod(alwaysRun = true)
    public void newFilterNames() {
        long id = System.currentTimeMillis();
        filterName = "AutoFilter" + id;
        keyword = "StarMe" + id;
    }

    private void loginAndCreateFilter() {
        loginToInbox();
        fp.openFilters(mailUrl);
        fp.createStarFilter(filterName, keyword);
        filterCreated = true;
    }

    @Test(groups = "regression")
    public void filterStarsMatchingMail() {
        loginAndCreateFilter();
        Assert.assertTrue(fp.isFilterDisplayed(filterName), "New filter should be listed in Settings > Filters");

        // matching mail should be starred, so it shows up in Starred
        openMailbox();
        String matching = keyword + " mail";
        cp.sendEmail(email, matching, "this subject has the filter keyword");
        ip.openStarred();
        Assert.assertTrue(ip.isMessageDisplayed(matching), "Mail with the keyword should be starred by the filter");
    }

    // negative and state checks on the same rule: a non matching mail is not starred, and once the filter is
    // switched off even a matching mail is not starred
    @Test(groups = "regression")
    public void filterIgnoresNonMatchingAndDisabled() {
        loginAndCreateFilter();
        Assert.assertTrue(fp.isFilterEnabled(filterName, true), "New filter should be enabled");

        openMailbox();
        String nonMatching = "Plain" + System.currentTimeMillis();
        cp.sendEmail(email, nonMatching, "this subject does not have the filter keyword");
        Assert.assertTrue(ip.isMessageDisplayed(nonMatching), "Non matching mail should arrive in Inbox");
        ip.openStarred();
        Assert.assertTrue(ip.isMessageGone(nonMatching), "Non matching mail must not be starred");

        fp.openFilters(mailUrl);
        fp.toggleFilter(filterName);
        Assert.assertTrue(fp.isFilterEnabled(filterName, false), "Filter should be disabled after the toggle");

        openMailbox();
        String whileDisabled = keyword + " disabled";
        cp.sendEmail(email, whileDisabled, "this subject has the keyword but the filter is off");
        Assert.assertTrue(ip.isMessageDisplayed(whileDisabled), "Mail sent while filter is off should arrive");
        ip.openStarred();
        Assert.assertTrue(ip.isMessageGone(whileDisabled), "A disabled filter must not star a matching mail");
    }

    // free account allows only one filter, so always remove it after the test (runs before BaseTest.tearDown)
    @AfterMethod(alwaysRun = true)
    public void deleteFilter() {
        if (filterCreated) {
            filterCreated = false;
            fp.openFilters(mailUrl);
            fp.deleteFilter(filterName);
            Assert.assertTrue(fp.isFilterGone(filterName), "Cleanup: filter '" + filterName + "' should be deleted");
        }
    }
}
