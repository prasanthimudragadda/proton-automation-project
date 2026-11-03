package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {
    @Test(groups = {"smoke", "regression"})
    public void searchByKeyword() {
        String subject = "Search" + System.currentTimeMillis();
        loginToInbox();
        cp.sendEmail(email, subject, "search test");
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Self-sent mail should arrive in Inbox");
        sp.searchByKeyword(subject);
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Search for '" + subject + "' should find the mail");
    }

    @Test(groups = "regression")
    public void searchWithNoResult() {
        loginToInbox();
        sp.searchByKeyword("nomatch" + System.currentTimeMillis());
        Assert.assertTrue(sp.isNoResultDisplayed(), "'No results' view should be shown for an unknown keyword");
        sp.clearSearch();
        Assert.assertTrue(sp.isSearchBoxEmpty(), "Clear should empty the search box");
        ip.openInbox();
        Assert.assertTrue(sp.isNoResultGone(), "'No results' view should go away after leaving the search");
    }

    @Test(groups = "regression")
    public void advancedSearchWithKeywordAndSender() {
        String subject = "Advanced" + System.currentTimeMillis();
        loginToInbox();
        cp.sendEmail(email, subject, "advanced search test");
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Self-sent mail should arrive in Inbox");

        sp.advancedSearch(subject, email);
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Keyword + matching sender should find the mail");

        sp.advancedSearch(subject, "nobody@example.com");
        Assert.assertTrue(sp.isNoResultDisplayed(), "Keyword + other sender should find nothing");
    }
}
