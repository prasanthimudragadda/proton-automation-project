package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UndoSendTest extends BaseTest {

    // async flow: the Undo toast stays only a few seconds, so it is clicked as soon as an explicit wait finds it
    // (no sleep). Then the final state is checked: composer back with the same content, and nothing in Sent.
    @Test(groups = "regression")
    public void undoSend() {
        String subject = "UndoSend" + System.currentTimeMillis();
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed(), "Inbox should be shown after login");
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
