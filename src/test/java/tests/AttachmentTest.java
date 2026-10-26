package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class AttachmentTest extends BaseTest {

    String fileName = "sample.txt";
    String filePath = new File("src/test/resources/testdata/" + fileName).getAbsolutePath();

    @Test(groups = "regression")
    public void attachAndRemoveFile(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.openComposer();
        ce.attachFile(filePath);
        Assert.assertEquals(ce.getAttachmentCount(), "1");
        Assert.assertTrue(ce.isAttachmentDisplayed(fileName));
        ce.removeAttachment(fileName);
        Assert.assertTrue(ce.isAttachmentRemoved(fileName));
    }

    @Test(groups = "regression")
    public void sendEmailWithAttachment(){
        String subject = "Attachment" + System.currentTimeMillis();
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.openComposer();
        ce.setRecipient(email);
        ce.setSubject(subject);
        ce.setBody("mail with attachment");
        ce.attachFile(filePath);
        Assert.assertTrue(ce.isAttachmentDisplayed(fileName));
        ce.send();
        Assert.assertTrue(ce.messageSentNotification());
        Assert.assertTrue(ip.isMessageDisplayed(subject));
        Assert.assertTrue(ip.hasAttachmentIcon(subject));
    }
}
