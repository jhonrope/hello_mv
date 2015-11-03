package models.mv

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Concepto(nombreRelacion: String, codigo: String, id: String, descripcion: String, tipo: String, clase: String,
                    valor: Option[String], respuestas: List[Respuesta], maximo: Option[Double], minimo: Option[Double], precision: Option[Int],
                    esBorrable: Boolean, esLista: Option[Boolean], lenguaje: List[Lenguaje], esEditable: Boolean,
                    esObligatorioCotizacion: Boolean, esObligatorioExpedicion: Boolean, posicion: Int, esSoloConfiguracion: Boolean)


object Concepto {

  implicit val conceptoFormat =
    ((__ \ "nombreRelacion").read[String] ~
      (__ \ "codigo").read[String] ~
      (__ \ "id").read[String] ~
      (__ \ "descripcion").read[String] ~
      (__ \ "tipo").read[String] ~
      (__ \ "clase").read[String] ~
      ((__ \ "valor").formatNullable[String]
        .orElse((__ \ "valor").formatNullable[Int].map(_.map(x=> x.toString)))
        .orElse((__ \ "valor").formatNullable[Boolean].map(_.map(x=> x.toString)))
        .orElse((__ \ "valor").formatNullable[Double].map(_.map(x=> x.toString)))) ~
      (__ \ "respuestas").read[List[Respuesta]] ~
      (__ \ "maximo").readNullable[Double] ~
      (__ \ "minimo").readNullable[Double] ~
      (__ \ "precision").readNullable[Int] ~
      (__ \ "esBorrable").read[Boolean] ~
      (__ \ "esLista").readNullable[Boolean] ~
      (__ \ "lenguaje").readNullable[List[Lenguaje]].map[List[Lenguaje]](_.getOrElse(List[Lenguaje]())) ~
      (__ \ "esEditable").readNullable[Boolean].map[Boolean](_.getOrElse(true)) ~
      (__ \ "esObligatorioCotizacion").readNullable[Boolean].map[Boolean](_.getOrElse(true)) ~
      (__ \ "esObligatorioExpedicion").readNullable[Boolean].map[Boolean](_.getOrElse(true)) ~
      (__ \ "posicion").readNullable[Int].map[Int](_.getOrElse(1)) ~
      (__ \ "esSoloConfiguracion").readNullable[Boolean].map[Boolean](_.getOrElse(false))
      )(Concepto.apply _)

  implicit val conceptoWrite = Json.writes[Concepto]
}