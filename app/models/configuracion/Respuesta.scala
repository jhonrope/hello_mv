package models.configuracion

import play.api.libs.json._

case class Respuesta(valorRespuesta: String, nombreConceptoDependencia: Option[String], valorDependencia: Option[String], tipoConceptoDependencia: Option[String])

object Respuesta {
  implicit val respuestaFormat = Json.format[Respuesta]


}
