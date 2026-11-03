package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ScheduledSendTest extends BaseTest {
    @Test(groups = "regression")
    public void scheduleAndCancelSend() {
        String subject = "Scheduled" + System.currentTimeMillis();
        loginToInbox();

        cp.openComposer();
        cp.setRecipient(email);
        cp.setSubject(subject);
        cp.setBody("this mail is scheduled for tomorrow");
        cp.scheduleForTomorrow();

        ip.openScheduled();
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Scheduled mail should be listed in Scheduled");
        ip.openMessage(subject);
        String banner = ip.getScheduleBannerText();
        Assert.assertTrue(banner.contains("will be sent"), "Schedule banner should say 'will be sent', but was: " + banner);

        ip.cancelScheduledSend();
        Assert.assertTrue(cp.hasSubject(subject), "Cancelled mail should open as a draft with the same subject");
        Assert.assertTrue(ip.isScheduledFolderGone(), "Scheduled folder should disappear when nothing is scheduled");

        cp.deleteDraft();
        Assert.assertTrue(ip.isMessageGone(subject), "Cleanup: draft should be deleted");
    }
}
