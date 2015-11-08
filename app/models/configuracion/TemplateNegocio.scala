package models.configuracion

import play.api.libs.json.Json

case class TemplateNegocio (templateNegocio: ConceptoFinal, idProducto: String, idPlan: String)

object TemplateNegocio {
  implicit val templateNegocioFormat = Json.format[TemplateNegocio]

}
