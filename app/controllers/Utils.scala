package controllers

import models.mv.ConceptoFinal
import play.api.libs.json.Json
import play.twirl.api.Html

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
    complejos.sortBy(_.concepto.posicion ) ++ noComplejos.sortBy(_.concepto.posicion)
  }

  def generarId(idPadre: String, idConcepto: String): (String, String) = {
    val id= s"$idPadre-$idConcepto"
    (id.replaceAll("-", "."), id)
  }


}
