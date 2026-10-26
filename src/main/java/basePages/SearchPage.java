package basePages;

import Utilities.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchPage extends Waits {

    public SearchPage(WebDriver driver){
        super(driver);
    }

    private final By searchBox = By.xpath("//input[@data-testid='search-keyword']");
    private final By keywordInput = By.id("search-keyword");
    private final By moreOptions = By.xpath("//button[@data-testid='advanced-search:show-more']");
    private final By senderInput = By.xpath("//input[@data-testid='advanced-search:sender']");
    private final By searchButton = By.xpath("//button[@data-testid='advanced-search:submit']");
    private final By noResultText = By.xpath("//*[@data-testid='empty-view-placeholder--empty-title']");
    private final By clearButton = By.xpath("//button[@data-testid='clear-button']");

    private void openSearchPanel(){
        elementTobeClickable(searchBox).click();
    }

    public void searchByKeyword(String keyword){
        openSearchPanel();
        waitForElementToVisible(keywordInput).sendKeys(keyword, Keys.ENTER);
    }

    // keyword + sender together
    public void advancedSearch(String keyword, String sender){
        openSearchPanel();
        WebElement keywordField = waitForElementToVisible(keywordInput);
        keywordField.clear();
        keywordField.sendKeys(keyword);
        // the panel remembers it was expanded: "More search options" is then replaced by "Fewer search options"
        if (!isElementPresent(senderInput)) {
            elementTobeClickable(moreOptions).click();
        }
        WebElement senderField = waitForElementToVisible(senderInput);
        senderField.clear();
        senderField.sendKeys(sender);
        elementTobeClickable(searchButton).click();
    }

    public boolean isNoResultDisplayed(){
        return waitForElementToVisible(noResultText).isDisplayed();
    }

    public void clearSearch(){
        elementTobeClickable(clearButton).click();
    }

    public boolean isNoResultGone(){
        return waitForElementToDisappear(noResultText);
    }

    // the clear button empties the search box; the result list stays until the user leaves the search
    public boolean isSearchBoxEmpty(){
        return wait.until(d -> d.findElement(searchBox).getAttribute("value").isEmpty());
    }
}
