package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SearchPage extends BasePage {
    public SearchPage(WebDriver driver) {
        super(driver);
    }

    private final By searchBox = By.xpath("//input[@data-testid='search-keyword']");
    private final By keywordInput = By.id("search-keyword");
    private final By moreOptions = By.xpath("//button[@data-testid='advanced-search:show-more']");
    private final By senderInput = By.xpath("//input[@data-testid='advanced-search:sender']");
    private final By searchButton = By.xpath("//button[@data-testid='advanced-search:submit']");
    private final By noResultText = By.xpath("//*[@data-testid='empty-view-placeholder--empty-title']");
    private final By clearButton = By.xpath("//button[@data-testid='clear-button']");

    private void openSearchPanel() {
        click(searchBox);
    }

    public void searchByKeyword(String keyword) {
        openSearchPanel();
        type(keywordInput, keyword, Keys.ENTER);
    }

    public void advancedSearch(String keyword, String sender) {
        openSearchPanel();
        WebElement keywordField = waitForVisible(keywordInput);
        keywordField.clear();
        keywordField.sendKeys(keyword);
        if (!isPresent(senderInput)) {
            click(moreOptions);
        }
        WebElement senderField = waitForVisible(senderInput);
        senderField.clear();
        senderField.sendKeys(sender);
        click(searchButton);
    }

    public void clearSearch() {
        click(clearButton);
    }

    public boolean isNoResultDisplayed() {
        return isVisible(noResultText);
    }

    public boolean isNoResultGone() {
        return isGone(noResultText);
    }

    public boolean isSearchBoxEmpty() {
        WebElement box = waitForVisible(searchBox);
        return waitUntil(ExpectedConditions.domPropertyToBe(box, "value", ""));
    }
}
