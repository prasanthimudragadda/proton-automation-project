package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class DraftEmailTest extends BaseTest {

    String subjectText = "Draft"+System.currentTimeMillis();
    String body = "test"+ UUID.randomUUID().toString().substring(0,8);

    @Test(groups = {"smoke", "regression"})
    public void draft(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.openComposer();
        ce.setRecipient(recipient);
        ce.setSubject(subjectText);
        ce.setBody(body);
        ce.closeComposer();
        Assert.assertTrue(ce.draftSavedNotification());
        ce.openDraft();
        ce.openDraftEmail(subjectText);
       Assert.assertTrue(ce.getRecipient().contains(recipient));
        Assert.assertEquals(ce.getSubject(), subjectText);
       Assert.assertTrue(ce.getBody().contains(body));

    }

    @Test(groups = "regression")
    public void deleteDraft(){
        String deleteSubject = "DeleteDraft" + System.currentTimeMillis();
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.openComposer();
        ce.setRecipient(recipient);
        ce.setSubject(deleteSubject);
        ce.setBody(body);
        ce.closeComposer();
        Assert.assertTrue(ce.draftSavedNotification());
        ce.openDraft();
        ce.openDraftEmail(deleteSubject);
        ce.deleteDraft();
        Assert.assertTrue(ip.isMessageGone(deleteSubject));
    }

}
