package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

// the composer window: recipients, subject, body (iframe), send, drafts, attachments, undo and schedule
public class ComposePage extends BasePage {

    public ComposePage(WebDriver driver) {
        super(driver);
    }

    private final By newMessage = By.xpath("//button[@data-testid='sidebar:compose']");
    private final By recipientInputField = By.cssSelector("input[data-testid='composer:to']");
    private final By subjectField = By.cssSelector("input[placeholder='Subject']");
    private final By iFrame = By.xpath("//iframe[@title='Email composer']");
    private final By emailBody = By.xpath("//div[@id='rooster-editor']");
    private final By sendButton = By.xpath("//button[@data-testid='composer:send-button']");
    private final By notificationMessage = By.xpath("//span[normalize-space()='Message sent.']");
    private final By composerCloseButton = By.xpath("//button[@data-testid='composer:close-button']");
    private final By draftNotification = By.xpath("//span[normalize-space()='Draft saved.']");
    private final By drafts = By.xpath("//a[@data-testid='navigation-link:all-drafts']");
    private final By draftedRecipient = By.cssSelector("div[data-testid='composer:address']");
    private final By deleteDraftButton = By.xpath("//button[@data-testid='composer:delete-draft-button']");
    private final By confirmDeleteButton = By.xpath("//button[@type='submit' and normalize-space()='Delete']");
    private final By attachmentInput = By.cssSelector("input[data-testid='composer-attachments-button']");
    private final By attachmentCount = By.xpath("//span[@data-testid='attachment-list:pure-attachment-number']");
    private final By attachmentToggle = By.xpath("//span[@data-testid='attachment-list:toggle']");
    private final By undoButton = By.xpath("//button[normalize-space()='Undo']");
    private final By ccButton = By.xpath("//div[@data-testid='composer:to-field']//button[@data-testid='composer:recipients:cc-button']");
    private final By bccButton = By.xpath("//div[@data-testid='composer:to-field']//button[@data-testid='composer:recipients:bcc-button']");
    private final By ccField = By.xpath("//div[@data-testid='composer:cc-field']");
    private final By bccField = By.xpath("//div[@data-testid='composer:bcc-field']");
    private final By ccInput = By.cssSelector("input[data-testid='composer:to-cc']");
    private final By bccInput = By.cssSelector("input[data-testid='composer:to-bcc']");
    private final By toChips = By.xpath("//div[@data-testid='composer:to-field']//div[@data-testid='composer-addresses-item']");
    private final By ccChips = By.xpath("//div[@data-testid='composer:cc-field']//div[@data-testid='composer-addresses-item']");
    private final By bccChips = By.xpath("//div[@data-testid='composer:bcc-field']//div[@data-testid='composer-addresses-item']");
    private final By scheduleDropdown = By.xpath("//button[@data-testid='composer:scheduled-send:open-dropdown']");
    private final By scheduleTomorrow = By.xpath("//button[@data-testid='composer:schedule-send:tomorrow']");

    private By attachmentItem(String fileName, String action) {
        return By.xpath("//button[@data-testid='attachment-item:" + fileName + "--" + action + "']");
    }

    // ---------- writing a mail ----------

    public void openComposer() {
        click(newMessage);
        // the composer opens at once but stays in "Loading message" until Send is enabled
        waitForVisible(recipientInputField);
        waitForClickable(sendButton);
    }

    public void setRecipient(String email) {
        type(recipientInputField, email);
    }

    // same as setRecipient, but Enter turns the typed address into a recipient chip at once
    public void addTo(String email) {
        type(recipientInputField, email + Keys.ENTER);
    }

    // CC and BCC fields are hidden until their buttons next to "To" are clicked
    public void addCc(String email) {
        if (!isPresent(ccField)) {
            click(ccButton);
        }
        type(ccInput, email + Keys.ENTER);
    }

    public void addBcc(String email) {
        if (!isPresent(bccField)) {
            click(bccButton);
        }
        type(bccInput, email + Keys.ENTER);
    }

    public void setSubject(String subject) {
        type(subjectField, subject);
    }

    // the body editor lives in an iframe
    public void setBody(String body) {
        switchToFrame(iFrame);
        try {
            WebElement bodyText = waitForVisible(emailBody);
            bodyText.clear();
            bodyText.sendKeys(body);
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    public void send() {
        click(sendButton);
    }

    // full flow used as test data setup in other tests; fails loudly if the mail was not sent
    public void sendEmail(String email, String subject, String body) {
        openComposer();
        setRecipient(email);
        setSubject(subject);
        setBody(body);
        send();
        if (!isMessageSent()) {
            throw new IllegalStateException("'Message sent.' toast did not appear for mail '" + subject + "'");
        }
    }

    public void closeComposer() {
        click(composerCloseButton);
    }

    public void clickUndo() {
        click(undoButton);
    }

    // schedule dropdown next to Send; "Tomorrow" is available on the free plan, "Custom" is not
    public void scheduleForTomorrow() {
        click(scheduleDropdown);
        click(scheduleTomorrow);
    }

    // ---------- state checks ----------

    public boolean isMessageSent() {
        return isVisible(notificationMessage);
    }

    // when the email is not sent, the composer stays open and no "Message sent." toast comes.
    // A short window is enough: when sending works the toast shows within about a second.
    public boolean isMessageNotSent() {
        return !isVisible(notificationMessage, Duration.ofSeconds(3));
    }

    public boolean isComposerOpen() {
        return isVisible(subjectField);
    }

    public boolean isDraftSaved() {
        return isVisible(draftNotification);
    }

    public String getSubject() {
        return waitForVisible(subjectField).getDomProperty("value");
    }

    // true once the composer shows the expected subject (a reopened composer fills it a moment later)
    public boolean hasSubject(String expected) {
        return eventually(d -> expected.equals(d.findElement(subjectField).getDomProperty("value")));
    }

    public String getBody() {
        switchToFrame(iFrame);
        try {
            return waitForVisible(emailBody).getText();
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    public String getRecipient() {
        return waitForVisible(draftedRecipient).getText();
    }

    // textContent, not getText(): a reopened composer shows recipients collapsed, so not all of them are visible text.
    // It also fills its recipient block a moment after opening, so wait for the text.
    public boolean recipientsContain(String text) {
        return eventually(d -> {
            String content = d.findElement(draftedRecipient).getDomProperty("textContent");
            return content != null && content.contains(text);
        });
    }

    // text of the recipient chips shown in the To / CC / BCC field
    public String getToChips() {
        return waitForVisible(toChips).getText();
    }

    public String getCcChips() {
        return waitForVisible(ccChips).getText();
    }

    public String getBccChips() {
        return waitForVisible(bccChips).getText();
    }

    // ---------- drafts ----------

    public void openDrafts() {
        click(drafts);
    }

    // the list row is identified by its subject (the subject span has no stable test id of its own)
    public void openDraftEmail(String subjectText) {
        click(By.xpath("//div[@data-testid='message-item:" + subjectText + "']"));
    }

    public void deleteDraft() {
        waitForClickable(deleteDraftButton);
        clickWhenNotCovered(deleteDraftButton);
        click(confirmDeleteButton);
    }

    // ---------- attachments ----------

    // the file input is hidden, so it only has to be present to receive the file path
    public void attachFile(String filePath) {
        wait.until(ExpectedConditions.presenceOfElementLocated(attachmentInput)).sendKeys(filePath);
    }

    public String getAttachmentCount() {
        return waitForVisible(attachmentCount).getText();
    }

    // attachment list is collapsed by default, so expand it first
    public boolean isAttachmentDisplayed(String fileName) {
        By attachment = attachmentItem(fileName, "primary-action");
        if (!isPresent(attachment)) {
            click(attachmentToggle);
        }
        return isVisible(attachment);
    }

    public boolean isAttachmentRemoved(String fileName) {
        return isGone(attachmentItem(fileName, "primary-action"));
    }

    public void removeAttachment(String fileName) {
        click(attachmentItem(fileName, "secondary-action"));
    }
}
