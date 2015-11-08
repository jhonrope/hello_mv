package models.configuracion

import play.api.libs.json.Json

case class ConceptoFinal(concepto: Concepto, atributos: List[ConceptoFinal])

object ConceptoFinal {
  implicit val conceptoFinal = Json.format[ConceptoFinal]
}