package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FilterPage extends BasePage {
    public FilterPage(WebDriver driver) {
        super(driver);
    }

    private final By addFilterButton = By.xpath("//button[normalize-space()='Add filter']");
    private final By filterName = By.xpath("//input[@data-testid='filter-modal:name-input']");
    private final By nextButton = By.xpath("//button[@data-testid='filter-modal:next-button']");
    private final By keywordInput = By.xpath("//input[@placeholder='Type text or keyword']");
    private final By insertButton = By.xpath("//button[normalize-space()='Insert']");
    private final By markAsStarred = By.xpath("//label[normalize-space()='Starred']");
    private final By saveButton = By.xpath("//button[@type='submit' and normalize-space()='Save']");
    private final By deleteMenuItem = By.xpath("//button[normalize-space()='Delete']");
    private final By confirmDeleteButton = By.xpath("(//dialog | //*[@role='dialog'])//button[normalize-space()='Delete']");

    private By filterRow(String name) {
        return By.xpath("//tr[.//*[normalize-space()='" + name + "']]");
    }

    public void openFilters(String mailUrl) {
        driver.get(mailUrl + "/filters");
    }

    public void createStarFilter(String name, String keyword) {
        click(addFilterButton);
        type(filterName, name);
        click(nextButton);
        type(keywordInput, keyword);
        click(insertButton);
        click(nextButton);
        click(markAsStarred);
        click(nextButton);
        click(saveButton);
    }

    public boolean isFilterDisplayed(String name) {
        return isVisible(filterRow(name));
    }

    public boolean isFilterGone(String name) {
        return isGone(filterRow(name));
    }

    public void deleteFilter(String name) {
        click(By.xpath("(//tr[.//*[normalize-space()='" + name + "']]//button)[last()]"));
        click(deleteMenuItem);
        click(confirmDeleteButton);
    }

    public void toggleFilter(String name) {
        click(By.xpath("//tr[.//*[normalize-space()='" + name + "']]//label[@data-testid='toggle-switch']"));
    }

    public boolean isFilterEnabled(String name, boolean expected) {
        By checkbox = By.xpath("//tr[.//*[normalize-space()='" + name + "']]//label[@data-testid='toggle-switch']//input");
        return eventually(d -> {
            WebElement box = d.findElement(checkbox);
            return box.isSelected() == expected && !"true".equals(box.getDomAttribute("aria-busy"));
        });
    }
}
