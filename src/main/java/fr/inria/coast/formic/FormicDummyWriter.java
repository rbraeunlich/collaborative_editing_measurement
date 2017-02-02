/**
 * 
 */
package fr.inria.coast.formic;

import java.util.Random;
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
public class FormicDummyWriter extends CollaborativeDummyWriter {
	public FormicDummyWriter(int n_user, int type_spd, String docUrl,
			int exp_id, String formicStringId) {
		super(n_user, type_spd, docUrl, exp_id);
		// TODO Auto-generated constructor stub
		this.driver = new ChromeDriver();
		while (this.inputElement == null) {
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.get(docUrl);
			subscribeForFormicString(formicStringId);
			this.inputElement = driver.findElement(By.className("stringInput"));
		}
	}
	
	private void subscribeForFormicString(String formicStringId) {
		WebElement subscribeInput = driver.findElement(By.id("subscribe-id"));
		subscribeInput.sendKeys(formicStringId);
		driver.findElement(By.id("subscribe-button")).click();
	}
	
	@Override
	public void run () {
		while (shouldWrite) {
			this.inputElement.sendKeys("a");
			int nextStep = new Random().nextInt () / 100;
			if (nextStep % 10 == 0) {
				int j = nextStep / 20;
				while (j != 0) {
					this.inputElement.sendKeys(Keys.DELETE);
					j--;
				}
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e1) {
				//do not need to handle because interrupt means main writing and reading thread finish
				//System.out.println("Interruped while writing dummy text");
				return;
			}
		}
	}
}