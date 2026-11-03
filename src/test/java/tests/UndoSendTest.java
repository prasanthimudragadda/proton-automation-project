package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UndoSendTest extends BaseTest {
    @Test(groups = "regression")
    public void undoSend() {
        String subject = "UndoSend" + System.currentTimeMillis();
        loginToInbox();
        cp.openComposer();
        cp.setRecipient(recipient);
        cp.setSubject(subject);
        cp.setBody("this mail should not be sent");
        cp.send();
        cp.clickUndo();

        Assert.assertTrue(cp.hasSubject(subject), "Composer should reopen with the same subject after Undo");
        cp.closeComposer();
        ip.openSent();
        Assert.assertTrue(ip.isMessageGone(subject), "Undone mail must not be in Sent");
    }
}
