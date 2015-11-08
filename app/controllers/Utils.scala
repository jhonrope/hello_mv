package controllers

import models.configuracion.ConceptoFinal
import models.mv._
import models.mv.base.{ComplejoBase, NoComplejoBase}
import models.mv.basico.fields._
import models.mv.basico.{NoComplejo, Complejo}
import play.api.libs.json.Json
import play.api.mvc.Flash
import play.twirl.api.{Html, HtmlFormat}

import scala.io.Source
import scala.util.{Failure, Try}

object Utils {
  def cargarJson(ruta: String): Try[String] = Try {
    Source.fromInputStream(getClass.getResourceAsStream(ruta)).mkString
  } recoverWith {
    case f => Failure(new Exception("NOT_FOUND"))
  }

  def parsearJsonAConcepto(negocio: String): Try[ConceptoFinal] = Try {
    Json.parse(negocio).as[ConceptoFinal]
  } recoverWith {
    case f => Failure(new Exception("MALFORMED_JSON"))
  }

  def construirConceptoFinal(rutaNegocio: String) = for {
    negocio <- cargarJson(rutaNegocio)
    conceptoFinal <- parsearJsonAConcepto(negocio)
  } yield conceptoFinal

  def agregarAlIndex(content: Html) = views.html.mv.indexAngular(content)

  def ordenarConceptoFinalPorPosicion(lista: List[ConceptoFinal]) = {
    val (complejos, noComplejos) = lista.partition(_.concepto.tipo == "complejo")
    complejos.sortBy(_.concepto.posicion) ++ noComplejos.sortBy(_.concepto.posicion)
  }

  def generarId(idPadre: String, idConcepto: String): (String, String) = {
    val id = s"$idPadre-$idConcepto"
    (id.replaceAll("-", "."), id)
  }


  def negocioDefault(conceptoFinal: ConceptoFinal, idPadre: String, version: Int)(implicit flash: Flash): HtmlFormat.Appendable = {
    val nocomplejo: NoComplejoBase = NoComplejo(ElementoBooleanoBasico, ElementoFechaBasico, ElementoSelectBasico, ElementoNumericoBasico, ElementoTextoBasico, ElementoTextoBasico)

    val complejo: ComplejoBase = Complejo(nocomplejo)

    views.html.mv.basico.NegocioBasico(conceptoFinal, idPadre, version)(complejo)
  }


}
