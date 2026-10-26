package basePages;

import Utilities.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class InboxPage extends Waits {

    public InboxPage(WebDriver driver){
        super(driver);
    }

    private final By inboxFolder = By.xpath("//a[@data-testid='navigation-link:inbox']");
    private final By sentFolder = By.xpath("//a[@data-testid='navigation-link:all-sent']");
    private final By starredFolder = By.xpath("//a[@data-testid='navigation-link:starred']");
    private final By archiveFolder = By.xpath("//a[@data-testid='navigation-link:archive']");
    private final By trashFolder = By.xpath("//a[@data-testid='navigation-link:trash']");
    private final By moreFolders = By.xpath("//nav[@aria-label='Main']//h3[normalize-space()='More']/..//button");
    private final By archiveButton = By.xpath("//button[@data-testid='toolbar:movetoarchive']");
    private final By trashButton = By.xpath("//button[@data-testid='toolbar:movetotrash']");
    private final By labelAsButton = By.xpath("//button[@data-testid='toolbar:labelas']");
    private final By applyLabelButton = By.xpath("//button[@data-testid='label-dropdown:apply']");
    private final By markReadButton = By.xpath("//button[@data-testid='toolbar:read']");
    private final By markUnreadButton = By.xpath("//button[@data-testid='toolbar:unread']");
    private final By moveToInboxButton = By.xpath("//button[@data-testid='toolbar:movetoinbox']");
    private final By scheduledFolder = By.xpath("//a[@data-testid='navigation-link:scheduled']");
    private final By scheduleBanner = By.xpath("//*[@data-testid='message:schedule-banner']");
    private final By scheduleEditButton = By.xpath("//button[@data-testid='message:schedule-banner-edit-button']");
    private final By editDraftButton = By.xpath("//button[normalize-space()='Edit draft']");

    // Archive and Trash are hidden under "More" until it is expanded
    private void openFolder(By folder){
        if (!isElementPresent(folder)) {
            elementTobeClickable(moreFolders).click();
        }
        elementTobeClickable(folder).click();
    }

    private By messageRow(String subject){
        return By.xpath("//div[@data-testid='message-item:" + subject + "']");
    }

    public void openInbox(){
        openFolder(inboxFolder);
    }

    public void openSent(){
        openFolder(sentFolder);
    }

    public void openStarred(){
        openFolder(starredFolder);
    }

    public void openArchive(){
        openFolder(archiveFolder);
    }

    public void openTrash(){
        openFolder(trashFolder);
    }

    public void openLabel(String labelName){
        By label = By.xpath("//li[@data-testid='sidebar-label:" + labelName + "']//a");
        elementTobeClickable(label).click();
    }

    public boolean isMessageDisplayed(String subject){
        return waitForElementToVisible(messageRow(subject), 60).isDisplayed();
    }

    public boolean isMessageGone(String subject){
        return waitForElementToDisappear(messageRow(subject));
    }

    public boolean hasAttachmentIcon(String subject){
        By icon = By.xpath("//div[@data-testid='message-item:" + subject + "']//*[@data-testid='item-attachment-icon-paper-clip']");
        return waitForElementToVisible(icon).isDisplayed();
    }

    public void selectMessage(String subject){
        // the real input has opacity 0 (Selenium sees it as not visible), the label around it is what the user clicks
        By checkbox = By.xpath("//div[@data-testid='message-item:" + subject + "']//label[contains(@class,'item-checkbox-label')]");
        waitForElementToVisible(messageRow(subject));
        dismissOnboardingPopup();
        elementTobeClickable(checkbox).click();
    }

    // the star icon shows only when the mouse is on the row
    // a one-time "Make your inbox your own" popover can cover the list and swallow clicks
    public void dismissOnboardingPopup(){
        By notNow = By.xpath("//button[normalize-space()='Not now']");
        if (isElementPresent(notNow)) {
            elementTobeClickable(notNow).click();
            waitForElementToDisappear(notNow);
        }
    }

    public void starMessage(String subject){
        dismissOnboardingPopup();
        By star = By.xpath("//div[@data-testid='message-item:" + subject + "']//button[@data-testid='item-star-false']");
        new Actions(driver).moveToElement(waitForElementToVisible(messageRow(subject))).perform();
        elementTobeClickable(star).click();
    }

    public void archiveSelected(){
        elementTobeClickable(archiveButton).click();
    }

    public void trashSelected(){
        elementTobeClickable(trashButton).click();
    }

    // an unread row carries the css class "unread"
    private By unreadRow(String subject){
        return By.xpath("//div[@data-testid='message-item:" + subject + "' and contains(concat(' ', normalize-space(@class), ' '), ' unread ')]");
    }

    public boolean isMessageUnread(String subject){
        return waitForElementToVisible(unreadRow(subject)).isDisplayed();
    }

    public boolean isMessageRead(String subject){
        waitForElementToVisible(messageRow(subject));
        return waitForElementToDisappear(unreadRow(subject));
    }

    // the toolbar shows "mark as read" for unread messages and "mark as unread" for read ones
    public void markSelectedAsRead(){
        elementTobeClickable(markReadButton).click();
    }

    public void markSelectedAsUnread(){
        elementTobeClickable(markUnreadButton).click();
    }

    public void restoreSelected(){
        elementTobeClickable(moveToInboxButton).click();
    }

    // the Scheduled link appears a few seconds after scheduling and disappears when nothing is scheduled
    public void openScheduled(){
        elementTobeClickable(scheduledFolder).click();
    }

    public boolean isScheduledFolderGone(){
        return waitForElementToDisappear(scheduledFolder);
    }

    public void openMessage(String subject){
        elementTobeClickable(messageRow(subject)).click();
    }

    public String getScheduleBannerText(){
        return waitForElementToVisible(scheduleBanner).getText();
    }

    // "Edit" on the banner cancels the schedule: the message moves to Drafts and the composer opens
    public void cancelScheduledSend(){
        elementTobeClickable(scheduleEditButton).click();
        elementTobeClickable(editDraftButton).click();
    }

    public void applyLabel(String labelName){
        By label = By.xpath("//label[@data-testid='label-dropdown:label-" + labelName + "']");
        elementTobeClickable(labelAsButton).click();
        elementTobeClickable(label).click();
        // current Proton versions apply a ticked label to the selected message at once and close the dropdown;
        // older ones kept it open with an "Apply" button
        waitForElementToDisappear(applyLabelButton);
    }
}
