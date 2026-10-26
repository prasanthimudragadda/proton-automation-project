package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ScheduledSendTest extends BaseTest {

    // Time dependent behaviour without a long test: the mail is scheduled for "Tomorrow" (free plan option),
    // so only the scheduled STATE is checked and the schedule is cancelled again. The real delivery at 08:00
    // is left to manual testing (see docs/TestStrategy.md). The mail goes to the own mailbox.
    @Test(groups = "regression")
    public void scheduleAndCancelSend(){
        String subject = "Scheduled" + System.currentTimeMillis();
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());

        ce.openComposer();
        ce.setRecipient(email);
        ce.setSubject(subject);
        ce.setBody("this mail is scheduled for tomorrow");
        ce.scheduleForTomorrow();

        // the Scheduled folder appears after the message is scheduled and holds the mail
        ip.openScheduled();
        Assert.assertTrue(ip.isMessageDisplayed(subject));
        ip.openMessage(subject);
        Assert.assertTrue(ip.getScheduleBannerText().contains("will be sent"));

        // cancelling moves it back to Drafts, the Scheduled folder is gone because nothing is scheduled any more
        ip.cancelScheduledSend();
        Assert.assertEquals(ce.getSubject(), subject);
        Assert.assertTrue(ip.isScheduledFolderGone());

        // clean up the draft
        ce.deleteDraft();
        Assert.assertTrue(ip.isMessageGone(subject));
    }
}
