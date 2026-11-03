package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ScheduledSendTest extends BaseTest {

    // Time dependent behaviour without a long test: the mail is scheduled for "Tomorrow" (free plan option),
    // so only the scheduled STATE is checked and the schedule is cancelled again. The real delivery at 08:00
    // is left to manual testing (see docs/TestStrategy.md). The mail goes to the own mailbox.
    @Test(groups = "regression")
    public void scheduleAndCancelSend() {
        String subject = "Scheduled" + System.currentTimeMillis();
        loginToInbox();

        cp.openComposer();
        cp.setRecipient(email);
        cp.setSubject(subject);
        cp.setBody("this mail is scheduled for tomorrow");
        cp.scheduleForTomorrow();

        // the Scheduled folder appears after the message is scheduled and holds the mail
        ip.openScheduled();
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Scheduled mail should be listed in Scheduled");
        ip.openMessage(subject);
        String banner = ip.getScheduleBannerText();
        Assert.assertTrue(banner.contains("will be sent"), "Schedule banner should say 'will be sent', but was: " + banner);

        // cancelling moves it back to Drafts, the Scheduled folder is gone because nothing is scheduled any more
        ip.cancelScheduledSend();
        Assert.assertTrue(cp.hasSubject(subject), "Cancelled mail should open as a draft with the same subject");
        Assert.assertTrue(ip.isScheduledFolderGone(), "Scheduled folder should disappear when nothing is scheduled");

        // clean up the draft
        cp.deleteDraft();
        Assert.assertTrue(ip.isMessageGone(subject), "Cleanup: draft should be deleted");
    }
}
