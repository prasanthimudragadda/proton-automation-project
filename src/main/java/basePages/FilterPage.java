package basePages;

import Utilities.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FilterPage extends Waits {

    public FilterPage(WebDriver driver){
        super(driver);
    }

    private final By addFilterButton = By.xpath("//button[normalize-space()='Add filter']");
    private final By filterName = By.xpath("//input[@data-testid='filter-modal:name-input']");
    private final By nextButton = By.xpath("//button[@data-testid='filter-modal:next-button']");
    private final By keywordInput = By.xpath("//input[@placeholder='Type text or keyword']");
    private final By insertButton = By.xpath("//button[normalize-space()='Insert']");
    private final By markAsStarred = By.xpath("//label[normalize-space()='Starred']");
    private final By saveButton = By.xpath("//button[@type='submit' and normalize-space()='Save']");
    // in the "Delete filter?" dialog this is a plain button (type="button"), not a submit button
    private final By confirmDeleteButton = By.xpath("(//dialog | //*[@role='dialog'])//button[normalize-space()='Delete']");

    public void openFilters(){
        driver.get("https://account.proton.me/mail/filters");
    }

    // filter: if subject contains the keyword -> mark as starred
    public void createStarFilter(String name, String keyword){
        elementTobeClickable(addFilterButton).click();
        waitForElementToVisible(filterName).sendKeys(name);
        elementTobeClickable(nextButton).click();
        waitForElementToVisible(keywordInput).sendKeys(keyword);
        elementTobeClickable(insertButton).click();
        elementTobeClickable(nextButton).click();
        elementTobeClickable(markAsStarred).click();
        elementTobeClickable(nextButton).click();
        elementTobeClickable(saveButton).click();
    }

    public boolean isFilterDisplayed(String name){
        return waitForElementToVisible(filterRow(name)).isDisplayed();
    }

    public boolean isFilterGone(String name){
        return waitForElementToDisappear(filterRow(name));
    }

    // last button in the row is the "..." actions menu
    public void deleteFilter(String name){
        By actions = By.xpath("(//tr[.//*[normalize-space()='" + name + "']]//button)[last()]");
        By delete = By.xpath("//button[normalize-space()='Delete']");
        elementTobeClickable(actions).click();
        elementTobeClickable(delete).click();
        elementTobeClickable(confirmDeleteButton).click();
    }

    public void toggleFilter(String name){
        By toggle = By.xpath("//tr[.//*[normalize-space()='" + name + "']]//label[@data-testid='toggle-switch']");
        elementTobeClickable(toggle).click();
    }

    // waits until the switch shows the expected state and the save request has finished
    public boolean isFilterEnabled(String name, boolean expected){
        By checkbox = By.xpath("//tr[.//*[normalize-space()='" + name + "']]//label[@data-testid='toggle-switch']//input");
        return wait.until(d -> {
            WebElement box = d.findElement(checkbox);
            return box.isSelected() == expected && !"true".equals(box.getAttribute("aria-busy"));
        });
    }

    private By filterRow(String name){
        return By.xpath("//tr[.//*[normalize-space()='" + name + "']]");
    }
}
