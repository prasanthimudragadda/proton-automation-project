package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UndoSendTest extends BaseTest {

    // undo toast stays only for a few seconds, so we wait for it with explicit wait instead of sleep
    @Test(groups = "regression")
    public void undoSend(){
        String subject = "UndoSend" + System.currentTimeMillis();
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.openComposer();
        ce.setRecipient(recipient);
        ce.setSubject(subject);
        ce.setBody("this mail should not be sent");
        ce.send();
        ce.clickUndo();

        // composer opens again with the same content and the mail is not in Sent
        Assert.assertEquals(ce.getSubject(), subject);
        ce.closeComposer();
        ip.openSent();
        Assert.assertTrue(ip.isMessageGone(subject));
    }
}
