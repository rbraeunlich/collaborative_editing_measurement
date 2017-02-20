/**
 * 
 */
package fr.inria.coast.formic.json;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import fr.inria.coast.general.CollaborativeDummyWriter;

/**
 * @author qdang
 *
 */
public class FormicJsonDummyWriter extends CollaborativeDummyWriter {

	private WebElement pathElement;
	private WebElement insertButton;
	
	public FormicJsonDummyWriter(int n_user, int type_spd, String docUrl, int exp_id, String formicStringId) {
		super(n_user, type_spd, docUrl, exp_id);
		this.driver = new ChromeDriver();
		LOG.info("Dummy opening webpage");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		driver.get(docUrl);
		subscribeForJsonObject(formicStringId);
		inputElement = driver.findElement(By.className("inputJson"));
		pathElement = driver.findElement(By.className("pathJson"));
		insertButton = driver.findElement(By.className("inputButton"));
	}

	private void subscribeForJsonObject(String formicStringId) {
		WebElement subscribeInput = driver.findElement(By.id("subscribe-id"));
		subscribeInput.sendKeys(formicStringId);
		driver.findElement(By.id("subscribe-button")).click();
		LOG.info("Dummy subscribed for JSON object");
	}

	@Override
	public void run() {
		while (shouldWrite) {
			this.inputElement.sendKeys("a");
			pathElement.sendKeys(UUID.randomUUID().toString());
			insertButton.click();
			pathElement.clear();
			inputElement.clear();
			int nextStep = new Random().nextInt() / 100;
			if (nextStep % 10 == 0) {
				int j = nextStep / 20;
				while (j != 0) {
					this.inputElement.sendKeys(Keys.BACK_SPACE);
					j--;
				}
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e1) {
				// do not need to handle because interrupt means main writing
				// and reading thread finish
				System.out.println("Interruped while writing dummy text");
				return;
			}
		}
	}
}
