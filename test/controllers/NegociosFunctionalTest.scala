package controllers

import org.scalamock.scalatest.MockFactory
import org.scalatest._
import org.scalatest.concurrent.IntegrationPatience
import org.scalatestplus.play._
import play.api.mvc.Results


class NegociosFunctionalTest extends FlatSpec with Matchers with OptionValues
with WsScalaTestClient with BeforeAndAfterEach with BeforeAndAfterAll with Results
with MockFactory with OneServerPerSuite with HtmlUnitFactory with AllBrowsersPerSuite with IntegrationPatience {

  override lazy val browsers = Vector(
    ChromeInfo,
    FirefoxInfo(firefoxProfile),
    InternetExplorerInfo
  )

  override def afterAll(): Unit = {
  }

  val host = s"http://localhost:$port"

  def sharedTests(browser: BrowserInfo) = {

    "Index" should s"mostrar los negocios en el listado y cargar la configuracion ${browser.name}" in {
      go to s"$host/"
      pageTitle shouldBe "Hello MV - Configurador"
      click on find(name("configuracion")).value

      eventually(currentUrl shouldBe s"$host/negocios/avanza_seguro/configuracion")
    }

    it should s"cargar un negocio ${browser.name}" in {
      go to s"$host/"
      pageTitle shouldBe "Hello MV - Configurador"
      click on find(name("negocios")).value

      eventually(currentUrl shouldBe s"$host/negocios/avanza_seguro")
    }

  }

  override def convertToLegacyEqualizer[T](left: T): LegacyEqualizer[T] = ???

  override def convertToLegacyCheckingEqualizer[T](left: T): LegacyCheckingEqualizer[T] = ???
}
