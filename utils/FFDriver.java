package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class FFDriver {
	private WebDriver driver;

	public FFDriver() {
		DesiredCapabilities caps = new DesiredCapabilities();
		this.driver = new FirefoxDriver(caps);
	}
	
	public WebDriver getDriver(){
		return driver;
	}
	
}
