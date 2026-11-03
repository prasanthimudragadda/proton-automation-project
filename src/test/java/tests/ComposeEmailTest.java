package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ComposeEmailTest extends BaseTest {
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
        Assert.assertTrue(cp.isRecipientMissingShown(),
                "'Recipient missing - Please add at least one recipient.' dialog should be shown");
        Assert.assertTrue(cp.isMessageNotSent(), "A mail without recipient must not be sent");
        cp.closeRecipientMissingDialog();
        Assert.assertTrue(cp.isComposerOpen(), "Composer should stay open so the user can add a recipient");
        discardDraft();
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
        discardDraft();
    }

    private void discardDraft() {
        try {
            cp.deleteDraft();
        } catch (RuntimeException e) {
            System.err.println("Cleanup: could not delete the draft of the unsent mail: " + e.getMessage());
        }
    }

    @Test(groups = "regression")
    public void ccAndBccArePreservedInDraft() {
        String subject = "CcBcc" + System.currentTimeMillis();
        String bcc = "bcc.check@example.com";
        loginToInbox();
        cp.openComposer();
        cp.addTo(recipient);
        cp.addCc(email);
        cp.addBcc(bcc);
        Assert.assertTrue(cp.getToChips().contains(recipient), "To field should contain " + recipient);
        Assert.assertTrue(cp.getCcChips().contains(email), "CC field should contain " + email);
        Assert.assertTrue(cp.getBccChips().contains(bcc), "BCC field should contain " + bcc);
        cp.setSubject(subject);
        cp.setBody(body());

        cp.closeComposer();
        Assert.assertTrue(cp.isDraftSaved(), "'Draft saved.' toast should appear when the composer is closed");
        cp.openDrafts();
        cp.openDraftEmail(subject);
        Assert.assertTrue(cp.recipientsContain(recipient), "Reopened draft should keep To " + recipient);
        Assert.assertTrue(cp.recipientsContain(email), "Reopened draft should keep CC " + email);
        Assert.assertTrue(cp.recipientsContain(bcc), "Reopened draft should keep BCC " + bcc);

        cp.deleteDraft();
        Assert.assertTrue(ip.isMessageGone(subject), "Deleted draft should leave the Drafts list");
    }
}
