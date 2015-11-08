package models.mv.basico

import models.configuracion.ConceptoFinal
import models.mv.base.{ComplejoBase, NoComplejoBase}
import play.twirl.api.HtmlFormat
import views.html.mv.basico.ComplejoBasico


case class Complejo(noComplejo: NoComplejoBase) extends ComplejoBase {

  def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable = ComplejoBasico.apply(conceptoFinal, idPadre)(noComplejo)
}