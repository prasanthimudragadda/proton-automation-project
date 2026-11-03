package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class AttachmentTest extends BaseTest {

    private static final String FILE_NAME = "sample.txt";
    private static final String FILE_PATH = new File("src/test/resources/testdata/" + FILE_NAME).getAbsolutePath();


    @Test(groups = "regression")
    public void attachAndRemoveFile() {
        loginToInbox();
        cp.openComposer();
        cp.attachFile(FILE_PATH);
        Assert.assertEquals(cp.getAttachmentCount(), "1", "Composer should show 1 attachment");
        Assert.assertTrue(cp.isAttachmentDisplayed(FILE_NAME), FILE_NAME + " should be listed in the composer");
        cp.removeAttachment(FILE_NAME);
        Assert.assertTrue(cp.isAttachmentRemoved(FILE_NAME), FILE_NAME + " should be gone after remove");
    }

    @Test(groups = "regression")
    public void sendEmailWithAttachment() {
        String subject = "Attachment" + System.currentTimeMillis();
        loginToInbox();
        cp.openComposer();
        cp.setRecipient(email);
        cp.setSubject(subject);
        cp.setBody("mail with attachment");
        cp.attachFile(FILE_PATH);
        Assert.assertTrue(cp.isAttachmentDisplayed(FILE_NAME), FILE_NAME + " should be listed in the composer");
        cp.send();
        Assert.assertTrue(cp.isMessageSent(), "'Message sent.' toast should appear");
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Mail with attachment should arrive in Inbox");
        Assert.assertTrue(ip.hasAttachmentIcon(subject), "Received mail should show the attachment (paper clip) icon");
    }
}
