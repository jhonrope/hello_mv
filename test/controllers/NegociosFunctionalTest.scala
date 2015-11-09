package controllers

import javax.inject.{Provider, Inject}

import models.configuracion.ConfiguracionNegocio
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.selenium.{Chrome, WebBrowser}
import org.scalatest.time.{Seconds, Millis, Span}
import persistence.ConfiguracionPersistenceTrait
import play.api.inject.{RoutesProvider, Injector, bind}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{RequestHeader, Handler, Action, Results}
import play.api.routing.Router
import play.api.test._
import play.api._

import scala.runtime.AbstractPartialFunction

class NegociosFunctionalTest extends FlatSpec with Matchers with OptionValues
with BeforeAndAfterEach with BeforeAndAfterAll with Results
with MockFactory with Chrome with Eventually {

  implicit val defaultPatienceConfig = PatienceConfig(Span(2, Seconds), Span(15, Millis))

  val configuracionPersistenceMock: ConfiguracionPersistenceTrait = stub[ConfiguracionPersistenceTrait]

  val app: Application = new GuiceApplicationBuilder()
    .in(Environment(new java.io.File("."), classOf[FakeApplication].getClassLoader, Mode.Test))
    .global(None.orNull)
    .configure(Map[String, Any]())
    .bindings(
      bind[FakePluginsConfig] to FakePluginsConfig(Nil, Nil),
      bind[FakeRouterConfig] to FakeRouterConfig(PartialFunction.empty))
    .overrides(
      bind[ConfiguracionPersistenceTrait].toInstance(configuracionPersistenceMock)
    )
    .build

  val aplicacion = FakeApplication

  val port = 19001

  val testServer = TestServer(port, app)

  override def beforeAll() = {
    testServer.start()
  }

  override def afterAll() = {
    testServer.stop()
    close()
  }

  val host = s"http://localhost:$port"
  "Index" should s"mostrar los negocios en el listado y cargar la configuracion" in {
    val x = ConfiguracionNegocio(1, "avanza_seguro", Utils.construirConceptoFinal(s"/estructuras/detalleNegocio_avanza_seguro.json").get)
    (configuracionPersistenceMock.listarNegocios _).when().returns(Set("avanza_seguro"))
    (configuracionPersistenceMock.encontrarPorNombreNegocio _).when("avanza_seguro").returns(Some(x))

    go to s"$host/"
    pageTitle shouldBe "Hello MV - Configurador"
    click on find(name("configuracion")).value

    eventually(currentUrl shouldBe s"$host/negocios/avanza_seguro/configuracion")
  }

    it should s"cargar un negocio" in {
      val x = ConfiguracionNegocio(1, "avanza_seguro", Utils.construirConceptoFinal(s"/estructuras/detalleNegocio_avanza_seguro.json").get)
      (configuracionPersistenceMock.listarNegocios _).when().returns(Set("avanza_seguro"))
      (configuracionPersistenceMock.encontrarPorNombreNegocio _).when("avanza_seguro").returns(Some(x))
      go to s"$host/"
      pageTitle shouldBe "Hello MV - Configurador"
      singleSel("nombreNegocio").value shouldBe "avanza_seguro"
      click on find(name("negocios")).value

      eventually(currentUrl shouldBe s"$host/negocios/avanza_seguro")
    }

}

private case class FakePluginsConfig(additionalPlugins: Seq[String], withoutPlugins: Seq[String])

private class FakePluginsProvider @Inject()(config: FakePluginsConfig, environment: Environment, injector: Injector) extends Provider[Plugins] {
  lazy val get: Plugins = {
    val pluginClasses = config.additionalPlugins ++ Plugins.loadPluginClassNames(environment).diff(config.withoutPlugins)
    new Plugins(Plugins.loadPlugins(pluginClasses, environment, injector).toIndexedSeq)
  }
}

private class FakeRoutes(
                          injected: PartialFunction[(String, String), Handler], fallback: Router) extends Router {
  def documentation = fallback.documentation

  // Use withRoutes first, then delegate to the parentRoutes if no route is defined
  val routes = new AbstractPartialFunction[RequestHeader, Handler] {
    override def applyOrElse[A <: RequestHeader, B >: Handler](rh: A, default: A => B) =
      injected.applyOrElse((rh.method, rh.path), (_: (String, String)) => default(rh))

    def isDefinedAt(rh: RequestHeader) = injected.isDefinedAt((rh.method, rh.path))
  } orElse new AbstractPartialFunction[RequestHeader, Handler] {
    override def applyOrElse[A <: RequestHeader, B >: Handler](rh: A, default: A => B) =
      fallback.routes.applyOrElse(rh, default)

    def isDefinedAt(x: RequestHeader) = fallback.routes.isDefinedAt(x)
  }

  def withPrefix(prefix: String) = {
    new FakeRoutes(injected, fallback.withPrefix(prefix))
  }
}

private case class FakeRouterConfig(withRoutes: PartialFunction[(String, String), Handler])

private class FakeRouterProvider @Inject()(config: FakeRouterConfig, parent: RoutesProvider) extends Provider[Router] {
  lazy val get: Router = new FakeRoutes(config.withRoutes, parent.get)
}
