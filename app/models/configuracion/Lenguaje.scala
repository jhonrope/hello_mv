package models.configuracion

import play.api.libs.json.Json

case class Lenguaje(nombre: String, descripcion: String, localizacion: String)

object Lenguaje {
  implicit val lenguajeFormat = Json.format[Lenguaje]

}
