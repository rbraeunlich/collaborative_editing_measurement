/**
 * 
 */
package fr.inria.coast.formic.json;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionNotFoundException;

import fr.inria.coast.general.CollaborativeRemoteDummyWriter;

/**
 * @author qdang
 *
 */
public class FormicJsonRemoteDummyWriter extends CollaborativeRemoteDummyWriter {
	
	char c = (char) ('a' + new Random ().nextInt(15));

	private WebElement pathElement;
	private WebElement insertButton;
	
	public FormicJsonRemoteDummyWriter(int n_user, int type_spd,
			String docUrl, int exp_id, String serverAddr, String formicStringId) {
		super(n_user, type_spd, docUrl, exp_id, serverAddr);
		try {
			LOG.info("Starting remote writer");
			this.remoteDriver = new RemoteWebDriver(new URL ("http://" + serverAddr + ":4444/wd/hub"), capabilities);
		} catch (MalformedURLException e) {
			System.out.println("Cannot get remoted web driver at URL: " + serverAddr);
		}
		
		remoteDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		remoteDriver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		remoteDriver.get(docUrl);
		subscribeForJsonObject(formicStringId);
		inputElement = remoteDriver.findElement(By.className("inputJson"));
		pathElement = remoteDriver.findElement(By.className("pathJson"));
		insertButton = remoteDriver.findElement(By.className("inputButton"));
	}
	
	private void subscribeForJsonObject(String formicStringId) {
		LOG.info("Remote writer subscribing for string");
		WebElement subscribeInput = remoteDriver.findElement(By.id("subscribe-id"));
		subscribeInput.sendKeys(formicStringId);
		remoteDriver.findElement(By.id("subscribe-button")).click();
		LOG.info("Remote writer subscribed for string");
	}
	
	@Override
	public void run () {
		while (shouldWrite) {
			this.inputElement.sendKeys("" + c);
			pathElement.sendKeys(UUID.randomUUID().toString());
			insertButton.click();
			pathElement.clear();
			inputElement.clear();
			int nextStep = new Random().nextInt () / 100;
			if (nextStep % 10 == 0) {
				int j = nextStep / 5;
				while (j != 0) {
					try {
						this.inputElement.sendKeys(Keys.BACK_SPACE);
						j--;
					} catch (SessionNotFoundException e1) {
						System.out.println("Remote dummy quited before");
						return;
					} catch (Exception e1) {
						System.out.println("Catch exception when delete randomly at remote writer");
						e1.printStackTrace();
					}
				}
			} 
			
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e1) {
				//do not need to handle because interrupt means main writing and reading thread finish
				System.out.println("Interruped while writing remote dummy text");
			} catch (WebDriverException e1) {
				System.out.println("Catch WebDriver exception at remote dummy");
			} catch (Exception e1) {
				System.out.println("General exception while waiting at remote dummy");
			}
		}
	}
}
