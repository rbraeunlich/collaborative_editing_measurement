/**
 * 
 */
package fr.inria.coast.formic.json;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.inria.coast.general.CollaborativeReader;

/**
 * @author qdang
 *
 */
public class FormicJsonReader extends CollaborativeReader {

	private String stringId;

	public FormicJsonReader(int n_user, int type_spd, String docUrl, int exp_id, int textSize) {
		super(n_user, type_spd, docUrl, exp_id, textSize);
		LOG.info("Starting reader");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		LOG.info("Opening Webpage");
		driver.get(docUrl);
		createNewJsonObject();
		this.inputElement = driver.findElement(By.className("jsonObject"));
	}

	/**
	 * Because the reader is the first one that is initialized, it has to create the formic string.
	 */
	private void createNewJsonObject() {
		LOG.info("Creating new JSON object");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.id("new-json-button")));
		driver.findElement(By.id("new-json-button")).click();
		WebElement input = driver.findElement(By.className("jsonObject"));
		this.stringId = input.getAttribute("id");
		LOG.info("Created new JSON object");
	}
	
	public String getStringId() {
		return stringId;
	}

}
