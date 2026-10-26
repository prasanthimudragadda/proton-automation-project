package basePages;

import Utilities.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ComposeEmail extends Waits {

    public ComposeEmail(WebDriver driver){
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


    public void openComposer(){
        elementTobeClickable(newMessage).click();
        // the composer opens at once but stays in "Loading message" until Send is enabled
        waitForElementToVisible(recipientInputField);
        elementTobeClickable(sendButton);
    }

    public void setRecipient(String email){
        waitForElementToVisible(recipientInputField).sendKeys(email);

    }
    // same as setRecipient, but Enter turns the typed address into a recipient chip at once
    public void addTo(String email){
        waitForElementToVisible(recipientInputField).sendKeys(email + Keys.ENTER);
    }

    public void setSubject(String subject){
        waitForElementToVisible(subjectField).sendKeys(subject);

    }

    public void setBody(String body){
        switchToFrame(iFrame);
        WebElement bodyText = waitForElementToVisible(emailBody);
        bodyText.clear();
        bodyText.sendKeys(body);
        driver.switchTo().defaultContent();
    }

    public void send(){
        elementTobeClickable(sendButton).click();
    }

    public boolean messageSentNotification(){
        return waitForElementToVisible(notificationMessage).isDisplayed();

    }
    public void closeComposer() {

        elementTobeClickable(composerCloseButton).click();
    }

    public boolean draftSavedNotification(){
        return waitForElementToVisible(draftNotification).isDisplayed();
    }

    public void openDraft(){

        elementTobeClickable(drafts).click();
    }

    // the list row is identified by its subject (the subject span has no stable test id of its own)
    public void openDraftEmail(String subjectText){
        By draftRow = By.xpath("//div[@data-testid='message-item:" + subjectText + "']");
        elementTobeClickable(draftRow).click();

    }
    public String getRecipient(){
        return waitForElementToVisible(draftedRecipient).getText();

    }
    // a reopened draft fills its recipient block a moment after the composer opens, so wait for the text
    public boolean recipientsContain(String text){
        // textContent, not getText(): the reopened composer shows recipients collapsed, so not all of them are visible text
        return wait.until(d -> {
            String content = d.findElement(draftedRecipient).getDomProperty("textContent");
            return content != null && content.contains(text);
        });
    }

    public String getSubject(){
        return waitForElementToVisible(subjectField).getAttribute("value");
    }
    public String getBody() {
        switchToFrame(iFrame);
        try {
            return waitForElementToVisible(emailBody).getText();
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    // full flow used as test data setup in other tests
    public void sendEmail(String email, String subject, String body){
        openComposer();
        setRecipient(email);
        setSubject(subject);
        setBody(body);
        send();
        messageSentNotification();
    }

    // when the email is not sent, the composer stays open and no "Message sent." toast comes
    public boolean isMessageNotSent(){
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.visibilityOfElementLocated(notificationMessage));
            return false;
        } catch (TimeoutException e) {
            return true;
        }
    }

    public boolean isComposerOpen(){
        return waitForElementToVisible(subjectField).isDisplayed();
    }

    public void deleteDraft(){
        elementTobeClickable(deleteDraftButton);
        clickWhenNotCovered(deleteDraftButton);
        elementTobeClickable(confirmDeleteButton).click();
    }

    public void attachFile(String filePath){
        wait.until(ExpectedConditions.presenceOfElementLocated(attachmentInput)).sendKeys(filePath);
    }

    public String getAttachmentCount(){
        return waitForElementToVisible(attachmentCount).getText();
    }

    // attachment list is collapsed by default, so expand it first
    public boolean isAttachmentDisplayed(String fileName){
        By attachment = By.xpath("//button[@data-testid='attachment-item:" + fileName + "--primary-action']");
        if (!isElementPresent(attachment)) {
            elementTobeClickable(attachmentToggle).click();
        }
        return waitForElementToVisible(attachment).isDisplayed();
    }

    public boolean isAttachmentRemoved(String fileName){
        By attachment = By.xpath("//button[@data-testid='attachment-item:" + fileName + "--primary-action']");
        return waitForElementToDisappear(attachment);
    }

    public void removeAttachment(String fileName){
        By removeButton = By.xpath("//button[@data-testid='attachment-item:" + fileName + "--secondary-action']");
        elementTobeClickable(removeButton).click();
    }

    public void clickUndo(){
        elementTobeClickable(undoButton).click();
    }

    // CC and BCC fields are hidden until their buttons next to "To" are clicked
    public void addCc(String email){
        if (!isElementPresent(ccField)) {
            elementTobeClickable(ccButton).click();
        }
        waitForElementToVisible(ccInput).sendKeys(email + Keys.ENTER);
    }

    public void addBcc(String email){
        if (!isElementPresent(bccField)) {
            elementTobeClickable(bccButton).click();
        }
        waitForElementToVisible(bccInput).sendKeys(email + Keys.ENTER);
    }

    // text of the recipient chips shown in the To / CC / BCC field
    public String getToChips(){
        return waitForElementToVisible(toChips).getText();
    }

    public String getCcChips(){
        return waitForElementToVisible(ccChips).getText();
    }

    public String getBccChips(){
        return waitForElementToVisible(bccChips).getText();
    }

    // schedule dropdown next to Send; "Tomorrow" is available on the free plan, "Custom" is not
    public void scheduleForTomorrow(){
        elementTobeClickable(scheduleDropdown).click();
        elementTobeClickable(scheduleTomorrow).click();
    }
}


