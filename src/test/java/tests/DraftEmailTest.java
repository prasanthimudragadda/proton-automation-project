package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class DraftEmailTest extends BaseTest {
    private final String body = "test" + UUID.randomUUID().toString().substring(0, 8);

    private void createDraft(String subject) {
        loginToInbox();
        cp.openComposer();
        cp.setRecipient(recipient);
        cp.setSubject(subject);
        cp.setBody(body);
        cp.closeComposer();
        Assert.assertTrue(cp.isDraftSaved(), "'Draft saved.' toast should appear when the composer is closed");
    }

    @Test(groups = {"smoke", "regression"})
    public void draft() {
        String subject = "Draft" + System.currentTimeMillis();
        createDraft(subject);
        cp.openDrafts();
        cp.openDraftEmail(subject);
        Assert.assertTrue(cp.getRecipient().contains(recipient), "Reopened draft should keep the recipient");
        Assert.assertEquals(cp.getSubject(), subject, "Reopened draft should keep the subject");
        Assert.assertTrue(cp.getBody().contains(body), "Reopened draft should keep the body text " + body);

        cp.deleteDraft();
        Assert.assertTrue(ip.isMessageGone(subject), "Deleted draft should leave the Drafts list");
    }

    @Test(groups = "regression")
    public void deleteDraft() {
        String subject = "DeleteDraft" + System.currentTimeMillis();
        createDraft(subject);
        cp.openDrafts();
        cp.openDraftEmail(subject);
        cp.deleteDraft();
        Assert.assertTrue(ip.isMessageGone(subject), "Deleted draft should leave the Drafts list");
    }
}
