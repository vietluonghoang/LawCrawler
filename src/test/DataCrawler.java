package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import medical.Disease;
import utils.FFDriver;
import utils.XLSEditor;

public class DataCrawler {
	private WebDriver driver;
	private ArrayList<String> alphabets;
	private ArrayList<Disease> diseases;

	public void startWeb(String target) {
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		try {
			driver.get(target);
		} catch (Exception e) {
			System.out.println("Loading too long. Stopping.......");
			driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
		}
	}

	@BeforeSuite
	public void init() {
		// driver = new FFDriver().getDriver();
	}

	@AfterSuite
	public void cleanup() {
		// driver.quit();
	}

	@Test
	public void prepareQuery() {
		XLSEditor xlsFile = new XLSEditor();
		try {
			diseases = new ArrayList<Disease>();
			xlsFile.readXLSXFile("Homepage", diseases);
//			xlsFile.readXLSXFile("information", diseases);
			ArrayList<String> query = new ArrayList<String>();
			for (Disease dis : diseases) {
				String[] info = dis.getInfo();
				String values = "\nTestCases\n"+info[0]+"\nScenarios\n"+info[1]+"\nDetail\n"+info[2]+"\nPreconditions\n"+info[3]+"\nTestSteps\n"+info[4]+"\nExpectedResult\n"+info[5];
//				String values = "";
//				for (String str : info) {
//					values += "N'" + str.replace("'", "''").trim() + "',";
//				}
//				query.add("insert into benh values(" + values.substring(0, values.length() - 1) + ")");
				query.add(values);
			}
			int count = 0;
			System.out.println("size: "+query.size());
			while (count < query.size()) {
//				if (count % 5 == 0) {
//					Scanner reader = new Scanner(System.in);
//					System.out.println("======================================== "+count);
//					int n = reader.nextInt();
//				}
				exportFile("query.txt", query.get(count));
				count++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void exportFile(String filename, String text) throws IOException{
		Path filePath = Paths.get(System.getProperty("user.dir")+"/"+filename);
		if (!Files.exists(filePath)) {
		    Files.createFile(filePath);
		}
		Files.write(filePath, ("\n\n"+text).getBytes(), StandardOpenOption.APPEND);
	}

	// @Test
	public void runTest() {
		diseases = new ArrayList<Disease>();
		alphabets = new ArrayList<String>();
		startWeb("http://songkhoe.vn/xem-theo-van.html");
		getAlphabetList();
		for (String str : alphabets) {
			selectAlphabet(str);
			// System.out.println("Getting diseases from " + str + " list...");
			getDiseases();
		}

		System.out.println(diseases.size());
		for (Disease disease : diseases) {

			// System.out.println("---------------------");
			// System.out.println("Getting info for disease: " +
			// disease.getName() + " ......");
			getDiseaseInfo(disease);
			try {
				// System.out.println("Adding to XLSX file......");
				XLSEditor xlsFile = new XLSEditor();
				// xlsFile.readXLSXFile("aaa");
				// xlsFile.deleteAllSheets();
				xlsFile.addRowToXLSXFile("Information2", disease);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// System.out.println("---------------------");
		}

	}

	private void getAlphabetList() {
		List<WebElement> alphabet = driver
				.findElements(By.xpath("//div[@class='character detailDisease_Xemtheovanabc_2_2209']/a"));
		for (WebElement e : alphabet) {
			alphabets.add(e.getText());
		}
	}

	private void selectAlphabet(String character) {
		if (!driver.getCurrentUrl().equals("http://songkhoe.vn/xem-theo-van.html")) {
			startWeb("http://songkhoe.vn/xem-theo-van.html");
		}
		for (WebElement e : driver
				.findElements(By.xpath("//div[@class='character detailDisease_Xemtheovanabc_2_2209']/a"))) {
			if (character.equals(e.getText())) {
				e.click();
				break;
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getDiseases() {
		for (WebElement e : driver.findElements(By.xpath("//div[@id='listDiseaseABC']//li/a"))) {
			diseases.add(new Disease(e.getText(), e.getAttribute("href")));
		}
	}

	private void getDiseaseInfo(Disease disease) {
		startWeb(disease.getUrl());
		for (WebElement e : driver.findElements(By.xpath("//div[@class='box-topic box-topic-question']//a"))) {
			String url = e.getAttribute("href");
			disease.setRelatedUrls(url);
		}
		String currentWindow = driver.getWindowHandle();

		ArrayList<String> urlList = new ArrayList<String>(disease.getRelatedUrls());

		for (String url : disease.getRelatedUrls()) {

			if (url.contains("tong-quan") || url.contains("gioi-thieu-chung")) {
				urlList.remove(url);
			} else if (url.contains("trieu-chung") || url.contains("dau-hieu")) {
				urlList.remove(url);
			} else if (url.contains("dieu-tri") || url.contains("cach-chua") || url.contains("lieu-phap")) {
				urlList.remove(url);
			} else if (url.contains("dan-gian") || url.contains("meo-nho")) {
				urlList.remove(url);
			} else if (url.contains("nguyen-nhan")) {
				urlList.remove(url);
			} else if (url.contains("yeu-to") && url.contains("nguy-co")) {
				urlList.remove(url);
			} else if (url.contains("cham-soc")) {
				urlList.remove(url);
			} else if (url.contains("phong-ngua") || url.contains("phong-tranh") || url.contains("phuong-phap-phong")) {
				urlList.remove(url);
			} else if (url.contains("bien-chung")) {
				urlList.remove(url);
			} else if (url.contains("chan-doan")) {
				urlList.remove(url);
			} else {
				System.out.println(disease.getName() + "\n" + url);
			}
		}

		for (String url : urlList) {
			driver.findElement(By.tagName("body")).sendKeys(Keys.chord(Keys.COMMAND, "n"));
		}
		ArrayList<String> handlingWindows = new ArrayList<String>(driver.getWindowHandles());
		handlingWindows.remove(currentWindow);

		for (String window : handlingWindows) {
			driver.switchTo().window(window);

			startWeb(urlList.remove(0));

			String currentUrl = driver.getCurrentUrl();

			String text = driver.findElement(By.xpath("//div[@class='wtc-div-title detailDisease_TinBenh_8_9914']"))
					.getText();
			disease.setGeneral(currentUrl);
			disease.setSymptom(text);
			driver.close();
		}
		// for (String url : disease.getRelatedUrls()) {
		// driver.findElement(By.tagName("body")).sendKeys(Keys.chord(Keys.COMMAND,
		// "n"));
		// }
		// ArrayList<String> handlingWindows = new
		// ArrayList<String>(driver.getWindowHandles());
		// handlingWindows.remove(currentWindow);

		// System.out.println(disease.getName());
		// System.out.println("- url: " + urlList.size() + " * window:" +
		// handlingWindows.size());
		// for (String window : handlingWindows) {
		// driver.switchTo().window(window);
		//
		// startWeb(urlList.remove(0));
		//
		// String text =
		// driver.findElement(By.xpath("//div[@class='wtc-div-title
		// detailDisease_TinBenh_8_9914']"))
		// .getText();
		// String currentUrl = driver.getCurrentUrl();
		//// System.out.println("- " + currentUrl);
		// if (currentUrl.contains("tong-quan") ||
		// currentUrl.contains("gioi-thieu-chung")) {
		// disease.setGeneral(text);
		// } else if (currentUrl.contains("trieu-chung") ||
		// currentUrl.contains("dau-hieu")) {
		// disease.setSymptom(text);
		// } else if (currentUrl.contains("dieu-tri") ||
		// currentUrl.contains("cach-chua")
		// || currentUrl.contains("lieu-phap")) {
		// disease.setTreatment(text);
		// } else if (currentUrl.contains("dan-gian") ||
		// currentUrl.contains("meo-nho")) {
		// disease.setTraditional(text);
		// } else if (currentUrl.contains("nguyen-nhan")) {
		// disease.setCause(text);
		// } else if (currentUrl.contains("yeu-to") &&
		// currentUrl.contains("nguy-co")) {
		// disease.setRisk(text);
		// } else if (currentUrl.contains("cham-soc")) {
		// disease.setCare(text);
		// } else if (currentUrl.contains("phong-ngua") ||
		// currentUrl.contains("phong-tranh")
		// || currentUrl.contains("phuong-phap-phong")) {
		// disease.setObviate(text);
		// } else if (currentUrl.contains("bien-chung")) {
		// disease.setComplication(text);
		// } else if (currentUrl.contains("chan-doan")) {
		// disease.setDiagnose(text);
		// } else {
		// System.out.println(disease.getName() + "\n" + currentUrl + "\n" +
		// text);
		// }
		// driver.close();
		// }
		driver.switchTo().window(currentWindow);
	}
}
