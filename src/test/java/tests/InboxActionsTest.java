package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InboxActionsTest extends BaseTest {

    String body = "Inbox actions test mail";

    // sends a mail to own mailbox so every test has its own fresh message in the inbox
    private String receiveNewMail(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        return sendMailToSelf("Inbox");
    }

    // needs a logged in session, used directly by tests that need more than one message
    private String sendMailToSelf(String prefix){
        String subject = prefix + System.currentTimeMillis();
        ce.sendEmail(email, subject, body);
        Assert.assertTrue(ip.isMessageDisplayed(subject));
        return subject;
    }

    @Test(groups = "regression")
    public void markAsReadAndUnread(){
        String subject = receiveNewMail();
        // a new mail is unread
        Assert.assertTrue(ip.isMessageUnread(subject));
        ip.selectMessage(subject);
        ip.markSelectedAsRead();
        Assert.assertTrue(ip.isMessageRead(subject));
        ip.markSelectedAsUnread();
        Assert.assertTrue(ip.isMessageUnread(subject));
    }

    @Test(groups = "regression")
    public void restoreFromTrash(){
        String subject = receiveNewMail();
        ip.selectMessage(subject);
        ip.trashSelected();
        Assert.assertTrue(ip.isMessageGone(subject));
        ip.openTrash();
        ip.selectMessage(subject);
        ip.restoreSelected();
        Assert.assertTrue(ip.isMessageGone(subject));
        ip.openInbox();
        Assert.assertTrue(ip.isMessageDisplayed(subject));
    }

    @Test(groups = "regression")
    public void bulkMoveToTrash(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        String first = sendMailToSelf("BulkA");
        String second = sendMailToSelf("BulkB");
        ip.selectMessage(first);
        ip.selectMessage(second);
        ip.trashSelected();
        Assert.assertTrue(ip.isMessageGone(first));
        Assert.assertTrue(ip.isMessageGone(second));
        ip.openTrash();
        Assert.assertTrue(ip.isMessageDisplayed(first));
        Assert.assertTrue(ip.isMessageDisplayed(second));
    }

    @Test(groups = "regression")
    public void starMessage(){
        String subject = receiveNewMail();
        ip.starMessage(subject);
        ip.openStarred();
        Assert.assertTrue(ip.isMessageDisplayed(subject));
    }

    @Test(groups = "regression")
    public void archiveMessage(){
        String subject = receiveNewMail();
        ip.selectMessage(subject);
        ip.archiveSelected();
        Assert.assertTrue(ip.isMessageGone(subject));
        ip.openArchive();
        Assert.assertTrue(ip.isMessageDisplayed(subject));
    }

    @Test(groups = "regression")
    public void deleteMessage(){
        String subject = receiveNewMail();
        ip.selectMessage(subject);
        ip.trashSelected();
        Assert.assertTrue(ip.isMessageGone(subject));
        ip.openTrash();
        Assert.assertTrue(ip.isMessageDisplayed(subject));
    }

    @Test(groups = "regression")
    public void applyLabel(){
        String subject = receiveNewMail();
        ip.selectMessage(subject);
        ip.applyLabel(label);
        ip.openLabel(label);
        Assert.assertTrue(ip.isMessageDisplayed(subject));
    }
}
