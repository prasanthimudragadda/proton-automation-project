package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ComposeEmailTest extends BaseTest {

    // built when needed: "username" is only loaded in @BeforeClass, so a field initializer would put "null" in the mail
    private String body() {
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

    private void loginToInbox() {
        lp.login(username, password);
        Assert.assertTrue(hp.isInboxDisplayed(), "Inbox should be shown after login");
    }

    @Test(groups = {"smoke", "regression"})
    public void composeEmail() {
        String subject = "Proton Mail " + System.currentTimeMillis();
        loginToInbox();
        cp.openComposer();
        cp.setRecipient(recipient);
        cp.setSubject(subject);
        cp.setBody(body());
        cp.send();
        Assert.assertTrue(cp.isMessageSent(), "'Message sent.' toast should appear");
        ip.openSent();
        Assert.assertTrue(ip.isMessageDisplayed(subject), "Sent mail '" + subject + "' should be listed in Sent");
    }

    @Test(groups = {"smoke", "regression"})
    public void sendWithoutRecipient() {
        loginToInbox();
        cp.openComposer();
        cp.setSubject("No recipient " + System.currentTimeMillis());
        cp.setBody(body());
        cp.send();
        Assert.assertTrue(cp.isMessageNotSent(), "A mail without recipient must not be sent");
        Assert.assertTrue(cp.isComposerOpen(), "Composer should stay open so the user can add a recipient");
    }

    @Test(groups = "regression")
    public void sendWithInvalidRecipient() {
        loginToInbox();
        cp.openComposer();
        cp.setRecipient("invalid-email");
        cp.setSubject("Invalid recipient " + System.currentTimeMillis());
        cp.setBody(body());
        cp.send();
        Assert.assertTrue(cp.isMessageNotSent(), "A mail to an invalid address must not be sent");
        Assert.assertTrue(cp.isComposerOpen(), "Composer should stay open so the user can fix the address");
    }

    // nothing is sent: the three recipient fields are filled, saved as a draft and checked after reopening
    @Test(groups = "regression")
    public void ccAndBccArePreservedInDraft() {
        String subject = "CcBcc" + System.currentTimeMillis();
        String bcc = "bcc.check@example.com";
        loginToInbox();
        cp.openComposer();
        cp.addTo(recipient);
        cp.addCc(email);
        cp.addBcc(bcc);
        // the three fields only exist as separate containers while the recipients are being edited
        Assert.assertTrue(cp.getToChips().contains(recipient), "To field should contain " + recipient);
        Assert.assertTrue(cp.getCcChips().contains(email), "CC field should contain " + email);
        Assert.assertTrue(cp.getBccChips().contains(bcc), "BCC field should contain " + bcc);
        cp.setSubject(subject);
        cp.setBody(body());

        cp.closeComposer();
        Assert.assertTrue(cp.isDraftSaved(), "'Draft saved.' toast should appear when the composer is closed");
        cp.openDrafts();
        cp.openDraftEmail(subject);
        // a reopened draft shows To / CC / BCC in one block (the per-field containers only exist while typing)
        Assert.assertTrue(cp.recipientsContain(recipient), "Reopened draft should keep To " + recipient);
        Assert.assertTrue(cp.recipientsContain(email), "Reopened draft should keep CC " + email);
        Assert.assertTrue(cp.recipientsContain(bcc), "Reopened draft should keep BCC " + bcc);

        // clean up so drafts do not pile up in the mailbox
        cp.deleteDraft();
        Assert.assertTrue(ip.isMessageGone(subject), "Deleted draft should leave the Drafts list");
    }
}
