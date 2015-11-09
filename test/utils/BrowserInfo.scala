package utils
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.{FirefoxProfile, FirefoxDriver}
import org.openqa.selenium.ie.InternetExplorerDriver

abstract class BrowserInfo(val name: String, val tagName: String) {
  def createWebDriver(): WebDriver
}

case object FirefoxInfo extends BrowserInfo("[Firefox]", "org.scalatest.tags.FirefoxBrowser") {
  def createWebDriver(): WebDriver = new FirefoxDriver(new FirefoxProfile())
}

case object InternetExplorerInfo extends BrowserInfo("[InternetExplorer]", "org.scalatest.tags.InternetExplorerBrowser") {
  def createWebDriver(): WebDriver = new InternetExplorerDriver
}

case object ChromeInfo extends BrowserInfo("[Chrome]", "org.scalatest.tags.ChromeBrowser") {
  def createWebDriver(): WebDriver = new ChromeDriver
}
