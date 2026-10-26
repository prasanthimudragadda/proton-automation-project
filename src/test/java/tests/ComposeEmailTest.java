package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ComposeEmailTest extends BaseTest {

    // built when needed: "username" is only loaded in @BeforeClass, so a field initializer would put "null" in the mail
    private String body(){
        return String.join("\n",
                "Hi Team,",
                "",
                "Please find the test report summary below.",
                "All login and compose tests passed.",
                "",
                "Regards,",
                username,
                "");
    }

    String subject = "Proton Mail " + System.currentTimeMillis();

    @Test(groups = {"smoke", "regression"})
    public void composeEmail(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.openComposer();
        ce.setRecipient(recipient);
        ce.setSubject(subject);
        ce.setBody(body());
        ce.send();
        Assert.assertTrue(ce.messageSentNotification());
        ip.openSent();
        Assert.assertTrue(ip.isMessageDisplayed(subject));

    }

    @Test(groups = {"smoke", "regression"})
    public void sendWithoutRecipient(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.openComposer();
        ce.setSubject("No recipient " + System.currentTimeMillis());
        ce.setBody(body());
        ce.send();
        Assert.assertTrue(ce.isMessageNotSent());
        Assert.assertTrue(ce.isComposerOpen());
    }

    @Test(groups = "regression")
    public void sendWithInvalidRecipient(){
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.openComposer();
        ce.setRecipient("invalid-email");
        ce.setSubject("Invalid recipient " + System.currentTimeMillis());
        ce.setBody(body());
        ce.send();
        Assert.assertTrue(ce.isMessageNotSent());
        Assert.assertTrue(ce.isComposerOpen());
    }

    // nothing is sent: the three recipient fields are filled, saved as a draft and checked after reopening
    @Test(groups = "regression")
    public void ccAndBccArePreservedInDraft(){
        String ccSubject = "CcBcc" + System.currentTimeMillis();
        String bcc = "bcc.check@example.com";
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed());
        ce.openComposer();
        ce.addTo(recipient);
        ce.addCc(email);
        ce.addBcc(bcc);
        // the three fields only exist as separate containers while the recipients are being edited
        Assert.assertTrue(ce.getToChips().contains(recipient));
        Assert.assertTrue(ce.getCcChips().contains(email));
        Assert.assertTrue(ce.getBccChips().contains(bcc));
        ce.setSubject(ccSubject);
        ce.setBody(body());

        ce.closeComposer();
        Assert.assertTrue(ce.draftSavedNotification());
        ce.openDraft();
        ce.openDraftEmail(ccSubject);
        // a reopened draft shows To / CC / BCC in one block (the per-field containers only exist while typing)
        Assert.assertTrue(ce.recipientsContain(recipient));
        Assert.assertTrue(ce.recipientsContain(email));
        Assert.assertTrue(ce.recipientsContain(bcc));

        // clean up so drafts do not pile up in the mailbox
        ce.deleteDraft();
        Assert.assertTrue(ip.isMessageGone(ccSubject));
    }

}
