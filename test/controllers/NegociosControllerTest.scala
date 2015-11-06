package controllers

import org.scalamock.scalatest.MockFactory
import org.scalatest.selenium.{Chrome, HtmlUnit}
import org.scalatest.tags.ChromeBrowser
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers, OptionValues}
import org.scalatestplus.play._
import persistence.ConfiguracionPersistenceTrait
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller, Result, Results}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}


class NegociosControllerTest extends FlatSpec with Matchers with OptionValues
with WsScalaTestClient with BeforeAndAfterEach with Results
with MockFactory with OneServerPerSuite  with ChromeFactory with HtmlUnit {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      withRoutes = {
        case ("GET", "/") => Action {
          Ok(Utils.cargarJson("/controllers/index.html").get)
        }
      }
    )

  val configuracionPersistenceMock = stub[ConfiguracionPersistenceTrait]

  class TestController() extends Controller with I18nSupport with NegociosControllerTrait {

    override def configuracionPersistence: ConfiguracionPersistenceTrait = configuracionPersistenceMock

    override def messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

    override implicit def ec: ExecutionContext = app.actorSystem.dispatcher
  }


  ignore should "devolver status code 200 al consultar index()" in {

    (configuracionPersistenceMock.listarNegocios _).when().returns(Set("negocio_test"))

    val controller = new TestController()
    val result: Future[Result] = controller.index().apply(FakeRequest())

    val statusCode: Int = status(result)
    statusCode should be(200)

    (configuracionPersistenceMock.listarNegocios _).verify().atLeastOnce()

  }

  ignore should "devolver el index con los negocios consultados en el index()" in {

    (configuracionPersistenceMock.listarNegocios _).when().returns(Set("negocio_test"))

    val controller = new TestController()
    val result: Future[Result] = controller.index().apply(FakeRequest())

    val bodyText: String = contentAsString(result)
    val statusCode: Int = status(result)
    statusCode should be(200)
    bodyText should be(Utils.cargarJson("/controllers/index.html").get)

    (configuracionPersistenceMock.listarNegocios _).verify().atLeastOnce()

  }


  ignore should "responder 200 al hacer GET a la ruta /" in {
    val urlTestApp = s"http://localhost:$port/"

    (configuracionPersistenceMock.listarNegocios _).when().returns(Set("negocio_test"))
    val controller = new TestController()

    val response = await(WS.url(urlTestApp).get())

    val statusCode: Int = response.status

    statusCode should be(200)
    response.body should be(Utils.cargarJson("/controllers/index.html").get)

  }

  "NegociosController" should "mostrar los negocios en el listado y cargar la configuracion" in {
    go to (s"http://localhost:9000/")
    pageTitle shouldBe "Hello MV - Configurador"
    click on find(name("configuracion")).value

  }
}