package controllers

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers, OptionValues}
import org.scalatestplus.play._
import persistence.ConfiguracionPersistenceTrait
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller, Result, Results}
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest}

import scala.concurrent.{ExecutionContext, Future}


class NegociosControllerTest extends FlatSpec with Matchers with OptionValues
with WsScalaTestClient with BeforeAndAfterEach with Results
with MockFactory with OneServerPerSuite {

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


  "NegociosController" should "devolver status code 200 al consultar index()" in {

    (configuracionPersistenceMock.listarNegocios _).when().returns(Set("negocio_test"))

    val controller = new TestController()
    val result: Future[Result] = controller.index().apply(FakeRequest())

    val statusCode: Int = status(result)
    statusCode should be(200)

    (configuracionPersistenceMock.listarNegocios _).verify().atLeastOnce()

  }

  it should "devolver el index con los negocios consultados en el index()" in {

    (configuracionPersistenceMock.listarNegocios _).when().returns(Set("negocio_test"))

    val controller = new TestController()
    val result: Future[Result] = controller.index().apply(FakeRequest())

    val bodyText: String = contentAsString(result)
    val statusCode: Int = status(result)
    statusCode should be(200)
    bodyText should be(Utils.cargarJson("/controllers/index.html").get)

    (configuracionPersistenceMock.listarNegocios _).verify().atLeastOnce()

  }





}