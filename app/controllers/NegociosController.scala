package controllers

import javax.inject.Inject
import models.configuracion.ConceptoFinal
import persistence.{ConfiguracionPersistenceTrait, ConfiguracionPersistence}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.Files.TemporaryFile
import play.api.libs.json._
import play.api.mvc.{Action, Codec, Controller, Result}

import scala.concurrent.ExecutionContext
import scala.io.Source
import scala.util.{Failure, Success, Try}

class NegociosController @Inject()(override val configuracionPersistence: ConfiguracionPersistence, override val messagesApi: MessagesApi)
                                  (implicit override val ec: ExecutionContext) extends Controller with I18nSupport with NegociosControllerTrait {
}

trait NegociosControllerTrait {
  self: Controller with I18nSupport =>


  import Utils._

  implicit val myCustomCharset = Codec.utf_8

  def configuracionPersistence: ConfiguracionPersistenceTrait

  def messagesApi: MessagesApi

  implicit def ec: ExecutionContext


  val atrNegocio = construirConceptoFinal("/estructuras/complejo.json").get


  def test() = Action { implicit request =>

    val (complejos, noComplejos) = atrNegocio.atributos.partition(_.concepto.tipo == "complejo")
    val listaOrdenada: List[ConceptoFinal] = complejos ++ noComplejos.sortBy(_.concepto.posicion)

    Ok(agregarAlIndex(negocioDefault(atrNegocio, "test", 1)))
  }

  def index() = Action {
    Ok(agregarAlIndex(views.html.mv.menuNegocios(configuracionPersistence.listarNegocios())))
  }

  def concatenar(jspath: JsPath, string: String): JsPath = jspath \ string

  def formTest() = Action { request =>
    request.body.asFormUrlEncoded.map { valor =>
      val wer: List[Reads[JsObject]] = valor.map { case (llave, valor) =>

        val sinRoot: List[String] = llave.split("-").toList

        val matching = "(.*)<(.*)>".r
        val pair: (String, String) = sinRoot.last match {
          case matching(c, y) => (c, y)
          case _ => ("", "")
        }
        val z: JsPath = sinRoot.init.:+(pair._1).foldLeft[JsPath](JsPath)(concatenar)

        val tipoValor: JsValue = (pair._2, valor.mkString) match {
          case ("numerico", "") => JsNumber(0)
          case ("numerico", v) => JsNumber(v.toDouble)
          case ("booleano", "") => JsBoolean(false)
          case ("booleano", v) => JsBoolean(v.toBoolean)
          case (_, "") => JsNull
          case (_, v) => JsString(v)
        }
        println(z)
        __.json.update(z.json.put(tipoValor))
      }.toList

      val superReads = wer.reduce(_ andThen _)
      println(wer)
      val werty = superReads.reads(JsObject(Map[String, JsValue]()))

      println(werty)
      werty
    }




    Redirect(routes.NegociosController.test())
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
      case Success(negocio) => Ok(agregarAlIndex(negocioDefault(negocio.configuracionTemplate, nombreNegocio, negocio.id)))
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
