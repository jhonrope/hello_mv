package models.mv.base

import models.configuracion.ConceptoFinal
import play.twirl.api.HtmlFormat

trait ComplejoBase {

  def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable

  def noComplejo: NoComplejoBase
}