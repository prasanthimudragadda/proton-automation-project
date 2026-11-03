package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchPage extends BasePage {

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    // the box in the top bar is read-only: clicking it opens a panel with the real input (id=search-keyword)
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

    // keyword + sender together
    public void advancedSearch(String keyword, String sender) {
        openSearchPanel();
        WebElement keywordField = waitForVisible(keywordInput);
        keywordField.clear();
        keywordField.sendKeys(keyword);
        // the panel remembers it was expanded: "More search options" is then replaced by "Fewer search options"
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

    // the clear button empties the search box; the result list stays until the user leaves the search
    public boolean isSearchBoxEmpty() {
        return eventually(d -> {
            String value = d.findElement(searchBox).getDomProperty("value");
            return value == null || value.isEmpty();
        });
    }
}
