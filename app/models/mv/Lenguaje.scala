package models.mv

import play.api.libs.json.{Json, Writes}

case class Lenguaje(nombre: String, descripcion: String, localizacion: String)

object Lenguaje {
  implicit val lenguajeFormat = Json.format[Lenguaje]

}
