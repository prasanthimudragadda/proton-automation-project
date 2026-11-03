package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

// mailbox: folder navigation, message list rows and toolbar actions
public class InboxPage extends BasePage {

    public InboxPage(WebDriver driver) {
        super(driver);
    }

    private final By inboxFolder = By.xpath("//a[@data-testid='navigation-link:inbox']");
    private final By sentFolder = By.xpath("//a[@data-testid='navigation-link:all-sent']");
    private final By starredFolder = By.xpath("//a[@data-testid='navigation-link:starred']");
    private final By archiveFolder = By.xpath("//a[@data-testid='navigation-link:archive']");
    private final By trashFolder = By.xpath("//a[@data-testid='navigation-link:trash']");
    private final By scheduledFolder = By.xpath("//a[@data-testid='navigation-link:scheduled']");
    private final By moreFolders = By.xpath("//nav[@aria-label='Main']//h3[normalize-space()='More']/..//button");
    private final By archiveButton = By.xpath("//button[@data-testid='toolbar:movetoarchive']");
    private final By trashButton = By.xpath("//button[@data-testid='toolbar:movetotrash']");
    private final By labelAsButton = By.xpath("//button[@data-testid='toolbar:labelas']");
    private final By applyLabelButton = By.xpath("//button[@data-testid='label-dropdown:apply']");
    private final By markReadButton = By.xpath("//button[@data-testid='toolbar:read']");
    private final By markUnreadButton = By.xpath("//button[@data-testid='toolbar:unread']");
    private final By moveToInboxButton = By.xpath("//button[@data-testid='toolbar:movetoinbox']");
    private final By scheduleBanner = By.xpath("//*[@data-testid='message:schedule-banner']");
    private final By scheduleEditButton = By.xpath("//button[@data-testid='message:schedule-banner-edit-button']");
    private final By editDraftButton = By.xpath("//button[normalize-space()='Edit draft']");
    private final By onboardingNotNow = By.xpath("//button[normalize-space()='Not now']");

    private By messageRow(String subject) {
        return By.xpath("//div[@data-testid='message-item:" + subject + "']");
    }

    // an unread row carries the css class "unread"
    private By unreadRow(String subject) {
        return By.xpath("//div[@data-testid='message-item:" + subject + "' and contains(concat(' ', normalize-space(@class), ' '), ' unread ')]");
    }

    // ---------- folders ----------

    // Archive and Trash are hidden under "More" until it is expanded
    private void openFolder(By folder) {
        if (!isPresent(folder)) {
            click(moreFolders);
        }
        click(folder);
    }

    public void openInbox() {
        openFolder(inboxFolder);
    }

    public void openSent() {
        openFolder(sentFolder);
    }

    public void openStarred() {
        openFolder(starredFolder);
    }

    public void openArchive() {
        openFolder(archiveFolder);
    }

    public void openTrash() {
        openFolder(trashFolder);
    }

    // the Scheduled link appears a few seconds after scheduling and disappears when nothing is scheduled
    public void openScheduled() {
        click(scheduledFolder);
    }

    public void openLabel(String labelName) {
        click(By.xpath("//li[@data-testid='sidebar-label:" + labelName + "']//a"));
    }

    // ---------- message rows ----------

    // long timeout: a freshly sent mail appears only after Proton's next list refresh
    public boolean isMessageDisplayed(String subject) {
        return isVisible(messageRow(subject), MAIL_DELIVERY_TIMEOUT);
    }

    public boolean isMessageGone(String subject) {
        return isGone(messageRow(subject));
    }

    public boolean hasAttachmentIcon(String subject) {
        return isVisible(By.xpath("//div[@data-testid='message-item:" + subject + "']//*[@data-testid='item-attachment-icon-paper-clip']"));
    }

    public boolean isMessageUnread(String subject) {
        return isVisible(unreadRow(subject));
    }

    public boolean isMessageRead(String subject) {
        return isVisible(messageRow(subject)) && isGone(unreadRow(subject));
    }

    public void openMessage(String subject) {
        click(messageRow(subject));
    }

    // a one-time "Make your inbox your own" popover can cover the list and swallow clicks
    public void dismissOnboardingPopup() {
        if (isPresent(onboardingNotNow)) {
            click(onboardingNotNow);
            isGone(onboardingNotNow);
        }
    }

    public void selectMessage(String subject) {
        // the real input has opacity 0 (Selenium sees it as not visible), the label around it is what the user clicks
        By checkbox = By.xpath("//div[@data-testid='message-item:" + subject + "']//label[contains(@class,'item-checkbox-label')]");
        waitForVisible(messageRow(subject));
        dismissOnboardingPopup();
        click(checkbox);
    }

    // the star icon shows only when the mouse is on the row
    public void starMessage(String subject) {
        dismissOnboardingPopup();
        By star = By.xpath("//div[@data-testid='message-item:" + subject + "']//button[@data-testid='item-star-false']");
        new Actions(driver).moveToElement(waitForVisible(messageRow(subject))).perform();
        click(star);
    }

    // ---------- toolbar actions on the selected messages ----------

    public void archiveSelected() {
        click(archiveButton);
    }

    public void trashSelected() {
        click(trashButton);
    }

    public void restoreSelected() {
        click(moveToInboxButton);
    }

    // the toolbar shows "mark as read" for unread messages and "mark as unread" for read ones
    public void markSelectedAsRead() {
        click(markReadButton);
    }

    public void markSelectedAsUnread() {
        click(markUnreadButton);
    }

    public void applyLabel(String labelName) {
        click(labelAsButton);
        click(By.xpath("//label[@data-testid='label-dropdown:label-" + labelName + "']"));
        // current Proton versions apply a ticked label to the selected message at once and close the dropdown;
        // older ones kept it open with an "Apply" button
        isGone(applyLabelButton);
    }

    // ---------- scheduled send ----------

    public String getScheduleBannerText() {
        return waitForVisible(scheduleBanner).getText();
    }

    // "Edit" on the banner cancels the schedule: the message moves to Drafts and the composer opens
    public void cancelScheduledSend() {
        click(scheduleEditButton);
        click(editDraftButton);
    }

    public boolean isScheduledFolderGone() {
        return isGone(scheduledFolder);
    }
}
