package controllers

import javax.inject.Inject

import models.mv.ConceptoFinal
import persistence.ConfiguracionPersistence
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.Json
import play.api.mvc.{Action, Codec, Controller, Result}

import scala.concurrent.ExecutionContext
import scala.io.Source
import scala.util.{Failure, Success, Try}

class NegociosController @Inject()(configuracionPersistence: ConfiguracionPersistence, val messagesApi: MessagesApi)
                                  (implicit ec: ExecutionContext) extends Controller with I18nSupport {

  import Utils._

  implicit val myCustomCharset = Codec.utf_8

  val atrNegocio = construirConceptoFinal("/estructuras/complejo.json").get


  def test() = Action { implicit request =>

    val (complejos, noComplejos) = atrNegocio.atributos.partition(_.concepto.tipo == "complejo")
    val listaOrdenada: List[ConceptoFinal] = complejos ++ noComplejos.sortBy(_.concepto.posicion)

    Ok(agregarAlIndex(views.html.mv.negocio(atrNegocio, "test")))
  }

  def index() = Action {
    Ok(agregarAlIndex(views.html.mv.menuNegocios(configuracionPersistence.listarNegocios())))
  }

  def formTest() = Action { request =>
    request.body.asFormUrlEncoded.map(println)
    Redirect(routes.NegociosController.mostrarNegocio("avanza_seguro"))
  }

  def configuracion() = Action { request =>

    val configuracionNegocio: Option[Result] = for {
      form <- request.body.asFormUrlEncoded
      dato <- form.get("nombreNegocio")
    } yield {
        Redirect(routes.NegociosController.configurarNegocio(dato.head))
      }

    configuracionNegocio.getOrElse(NotFound(views.html.mv.notFound("NOT_FOUND")))
  }

  def configurarNegocio(nombreNegocio: String) = Action { implicit request =>
    val cargarNegocio = configuracionPersistence.encontrarPorNombreNegocio(nombreNegocio).map(Success(_))
      .getOrElse(Failure(new Exception("NOT_FOUND")))


    cargarNegocio match {
      case Success(configNegocio) => render {
        case Accepts.Json() => Ok(Json.prettyPrint(Json.toJson(configNegocio.configuracionTemplate)))
        case Accepts.Html() => Ok(agregarAlIndex(views.html.mv.configuracionNegocios(nombreNegocio, true)))
      }
      case Failure(failure) => Ok(agregarAlIndex(views.html.mv.configuracionNegocios(nombreNegocio)))
    }
  }

  def descargarConfiguracionNegocio(nombreNegocio: String, file: String) = configurarNegocio(nombreNegocio)

  def negocios() = Action { request =>

    val mostrarNegocio: Option[Result] = for {
      form <- request.body.asFormUrlEncoded
      dato <- form.get("nombreNegocio")
    } yield {
        Redirect(routes.NegociosController.mostrarNegocio(dato.head))
      }

    mostrarNegocio.getOrElse(NotFound(views.html.mv.notFound("NOT_FOUND")))
  }

  def mostrarNegocio(nombreNegocio: String) = Action { implicit request =>

    val cargarNegocio = configuracionPersistence.encontrarPorNombreNegocio(nombreNegocio).map(Success(_))
      .getOrElse(Failure(new Exception("NOT_FOUND")))

    cargarNegocio match {
      case Success(negocio) => Ok(agregarAlIndex(views.html.mv.negocio(negocio.configuracionTemplate, nombreNegocio, negocio.id)))
      case Failure(failure) => NotFound(views.html.mv.notFound(failure.getMessage))
    }
  }

  def cotizar(nombreNegocio: String) = Action { request =>
    val x = request.body.asJson
    val respuesta = cargarJson(s"/estructuras/result_$nombreNegocio.json")
    x.fold(NotFound("{}"))(x => Ok(respuesta.get))
  }

  def generarControlador(nombreNegocio: String) = Action {
    Ok(views.js.mv.scripts.mainController(nombreNegocio))
  }

  def upload(nombreNegocio: String) = Action(parse.multipartFormData) { request =>
    request.body.file(nombreNegocio).map { picture =>
      import java.io.File
      val filename: String = picture.filename
      val contentType = picture.contentType
      val y: TemporaryFile = picture.ref

      val cargarNegocio: Try[ConceptoFinal] = parsearJsonAConcepto(Source.fromFile(y.file).getLines.mkString)

      cargarNegocio match {
        case Success(negocio) =>
          val configuracion = configuracionPersistence.crear(nombreNegocio, negocio)

          Redirect(routes.NegociosController.mostrarNegocio(nombreNegocio)).flashing("success" -> s"Negocio recargado con la version ${configuracion.id}.")
        case Failure(failure) => NotFound(views.html.mv.notFound(failure.getMessage))
      }
    }.getOrElse {
      Redirect(routes.NegociosController.configurarNegocio(nombreNegocio)).flashing("error" -> "Debe seleccionar un archivo.")
    }
  }

}
