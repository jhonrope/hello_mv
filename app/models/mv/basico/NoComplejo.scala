package models.mv.basico

import models.configuracion.{Concepto, ConceptoFinal}
import models.mv.base.NoComplejoBase
import models.mv.base.fields._
import play.twirl.api.HtmlFormat


case class NoComplejo(booleano: ElementoBooleanoBase, fecha: ElementoFechaBase, multiple: ElementoMultipleBase, numerico: ElementoNumericoBase, texto: ElementoTextoBase,
                      defecto: ElementoFormularioBase) extends NoComplejoBase {

  def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable = {
    conceptoFinal.concepto match {
      case a: Concepto if a.tipo == "booleano" => booleano(conceptoFinal, idPadre)
      case a: Concepto if a.tipo == "fecha" => fecha(conceptoFinal, idPadre)
      case a: Concepto if a.tipo == "numerico" => numerico(conceptoFinal, idPadre)
      case a: Concepto if a.tipo == "cadena" && a.valor.nonEmpty => texto(conceptoFinal, idPadre)
      case a: Concepto if a.respuestas.nonEmpty => multiple(conceptoFinal, idPadre)
      case _ => defecto(conceptoFinal, idPadre)
    }
  }
}
