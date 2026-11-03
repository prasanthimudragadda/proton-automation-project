package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InboxActionsTest extends BaseTest {

    private static final String BODY = "Inbox actions test mail";

    // logs in and sends a mail to the own mailbox, so every test has its own fresh message in the inbox
    private String receiveNewMail() {
        loginToInbox();
        return sendMailToSelf("Inbox");
    }


    private String sendMailToSelf(String prefix) {
        String subject = prefix + System.currentTimeMillis();
        cp.sendEmail(email, subject, BODY);
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Self-sent mail '" + subject + "' should arrive in Inbox");
        return subject;
    }

    @Test(groups = "regression")
    public void markAsReadAndUnread() {
        String subject = receiveNewMail();
        Assert.assertTrue(ip.isMessageUnread(subject), "A new mail should be unread");
        ip.selectMessage(subject);
        ip.markSelectedAsRead();
        Assert.assertTrue(ip.isMessageRead(subject), "Mail should be read after 'mark as read'");
        ip.markSelectedAsUnread();
        Assert.assertTrue(ip.isMessageUnread(subject), "Mail should be unread again after 'mark as unread'");
    }

    @Test(groups = "regression")
    public void restoreFromTrash() {
        String subject = receiveNewMail();
        ip.selectMessage(subject);
        ip.trashSelected();
        Assert.assertTrue(ip.isMessageGone(subject), "Trashed mail should leave the Inbox");
        ip.openTrash();
        ip.selectMessage(subject);
        ip.restoreSelected();
        Assert.assertTrue(ip.isMessageGone(subject), "Restored mail should leave Trash");
        ip.openInbox();
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Restored mail should be back in Inbox");
    }

    @Test(groups = "regression")
    public void bulkMoveToTrash() {
        loginToInbox();
        String first = sendMailToSelf("BulkA");
        String second = sendMailToSelf("BulkB");
        ip.selectMessage(first);
        ip.selectMessage(second);
        ip.trashSelected();
        Assert.assertTrue(ip.isMessageGone(first), "First selected mail should leave the Inbox");
        Assert.assertTrue(ip.isMessageGone(second), "Second selected mail should leave the Inbox");
        ip.openTrash();
        Assert.assertTrue(ip.isMessageDisplayed(first), "First selected mail should be in Trash");
        Assert.assertTrue(ip.isMessageDisplayed(second), "Second selected mail should be in Trash");
    }

    @Test(groups = "regression")
    public void starMessage() {
        String subject = receiveNewMail();
        ip.starMessage(subject);
        ip.openStarred();
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Starred mail should be listed in Starred");
    }

    @Test(groups = "regression")
    public void archiveMessage() {
        String subject = receiveNewMail();
        ip.selectMessage(subject);
        ip.archiveSelected();
        Assert.assertTrue(ip.isMessageGone(subject), "Archived mail should leave the Inbox");
        ip.openArchive();
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Archived mail should be listed in Archive");
    }

    @Test(groups = "regression")
    public void deleteMessage() {
        String subject = receiveNewMail();
        ip.selectMessage(subject);
        ip.trashSelected();
        Assert.assertTrue(ip.isMessageGone(subject), "Trashed mail should leave the Inbox");
        ip.openTrash();
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Trashed mail should be listed in Trash");
    }

    @Test(groups = "regression")
    public void applyLabel() {
        String subject = receiveNewMail();
        ip.selectMessage(subject);
        ip.applyLabel(label);
        ip.openLabel(label);
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Labelled mail should be listed under label '" + label + "'");
    }
}
