package models.mv.basico.fields

import models.configuracion.ConceptoFinal
import models.mv.base.fields.ElementoMultipleBase
import play.twirl.api.HtmlFormat

case object ElementoSelectBasico extends ElementoMultipleBase {
  override def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable = views.html.mv.basico.fields.SelectBasico(conceptoFinal, idPadre)
}
