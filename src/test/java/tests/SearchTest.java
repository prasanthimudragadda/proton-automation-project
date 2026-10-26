package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    public void searchByKeyword(){
        String subject = "Search" + System.currentTimeMillis();
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.sendEmail(email, subject, "search test");
        Assert.assertTrue(ip.isMessageDisplayed(subject));
        sp.searchByKeyword(subject);
        Assert.assertTrue(ip.isMessageDisplayed(subject));
    }

    @Test(groups = "regression")
    public void searchWithNoResult(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        sp.searchByKeyword("nomatch" + System.currentTimeMillis());
        Assert.assertTrue(sp.isNoResultDisplayed());
        // clear empties the search box, going back to the inbox leaves the search and shows messages again
        sp.clearSearch();
        Assert.assertTrue(sp.isSearchBoxEmpty());
        ip.openInbox();
        Assert.assertTrue(sp.isNoResultGone());
    }

    @Test(groups = "regression")
    public void advancedSearchWithKeywordAndSender(){
        String subject = "Advanced" + System.currentTimeMillis();
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.sendEmail(email, subject, "advanced search test");
        Assert.assertTrue(ip.isMessageDisplayed(subject));

        // keyword and sender both match
        sp.advancedSearch(subject, email);
        Assert.assertTrue(ip.isMessageDisplayed(subject));

        // same keyword but a different sender, nothing should match
        sp.advancedSearch(subject, "nobody@example.com");
        Assert.assertTrue(sp.isNoResultDisplayed());
    }
}
