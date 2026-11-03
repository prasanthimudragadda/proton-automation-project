package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

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
    private final By recipientMissingDialog = By.xpath("//*[normalize-space()='Please add at least one recipient.']");
    private final By gotItButton = By.xpath("//button[normalize-space()='Got it']");

    private By attachmentItem(String fileName, String action) {
        return By.xpath("//button[@data-testid='attachment-item:" + fileName + "--" + action + "']");
    }

    public void openComposer() {
        click(newMessage);
        waitForVisible(recipientInputField);
        waitForClickable(sendButton);
    }

    public void setRecipient(String email) {
        type(recipientInputField, email);
    }

    public void addTo(String email) {
        type(recipientInputField, email + Keys.ENTER);
    }

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

    public void scheduleForTomorrow() {
        click(scheduleDropdown);
        click(scheduleTomorrow);
    }

    public boolean isMessageSent() {
        return isVisible(notificationMessage);
    }

    public boolean isMessageNotSent() {
        return !isVisible(notificationMessage, Duration.ofSeconds(3));
    }

    public boolean isRecipientMissingShown() {
        return isVisible(recipientMissingDialog);
    }

    public void closeRecipientMissingDialog() {
        click(gotItButton);
        isGone(gotItButton);
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

    public boolean recipientsContain(String text) {
        return eventually(d -> {
            String content = d.findElement(draftedRecipient).getDomProperty("textContent");
            return content != null && content.contains(text);
        });
    }

    public String getToChips() {
        return waitForVisible(toChips).getText();
    }

    public String getCcChips() {
        return waitForVisible(ccChips).getText();
    }

    public String getBccChips() {
        return waitForVisible(bccChips).getText();
    }

    public void openDrafts() {
        click(drafts);
    }

    public void openDraftEmail(String subjectText) {
        click(By.xpath("//div[@data-testid='message-item:" + subjectText + "']"));
    }

    public void deleteDraft() {
        waitForClickable(deleteDraftButton);
        clickWhenNotCovered(deleteDraftButton);
        click(confirmDeleteButton);
    }

    public void attachFile(String filePath) {
        wait.until(ExpectedConditions.presenceOfElementLocated(attachmentInput)).sendKeys(filePath);
    }

    public String getAttachmentCount() {
        return waitForVisible(attachmentCount).getText();
    }

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
