/**
 * 
 */
package fr.inria.coast.formic.json;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import fr.inria.coast.general.CollaborativeAutomator;
import fr.inria.coast.general.CollaborativeWriter;

/**
 * @author qdang
 *
 */
public class FormicJsonWriter extends CollaborativeWriter {

	private static final Logger LOG = Logger.getLogger(FormicJsonWriter.class.getName());
	private WebElement pathElement;
	private WebElement insertButton;

	public FormicJsonWriter(int n_user, int type_spd, String docUrl, int exp_id, String formicStringId, int textSize) {
		super(n_user, type_spd, docUrl, exp_id, textSize);
		LOG.info("Starting writer");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		driver.get(docUrl);
		subscribeForJsonObject(formicStringId);
		inputElement = driver.findElement(By.className("inputJson"));
		pathElement = driver.findElement(By.className("pathJson"));
		insertButton = driver.findElement(By.className("insertButton"));
	}

	private void subscribeForJsonObject(String formicStringId) {
		WebElement subscribeInput = driver.findElement(By.id("subscribe-id"));
		subscribeInput.sendKeys(formicStringId);
		driver.findElement(By.id("subscribe-button")).click();
		LOG.info("Writer subscribed for JSON object");
	}

	@Override
	public void run() {
		for (counter = 0; counter < textSize; counter++) {
			writeTime[counter] = System.currentTimeMillis();
			String textToType = String.format("%03d", counter) + "x";
			pathElement.sendKeys(textToType);
			inputElement.sendKeys(textToType);
			insertButton.click();
			pathElement.clear();
			inputElement.clear();
			try {
				sleep(delay);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		try (PrintWriter out = new PrintWriter(
				new BufferedWriter(new FileWriter(CollaborativeAutomator.resultFile, true)))) {
			for (int i = 0; i < textSize; i++) {
				out.print("W ");
				out.print(n_user);
				out.print(" ");
				out.print(type_spd);
				out.print(" ");
				out.print(exp_id);
				out.print(" ");
				out.print(String.format("%03d", i));
				out.print(" ");
				out.print(writeTime[i]);
				out.print("\n");
			}
			out.println("***");
		} catch (IOException e) {
			System.out.println("Error while writing the write time");
			e.printStackTrace();
		}
	}

	@Override
	public void cancel() {
		LOG.info("Stopping writer");
		if (counter < textSize - 1) counter = textSize - 1;
		driver.quit();
		interrupt();
	}

}
