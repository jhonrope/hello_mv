package models.mv.basico.fields

import models.configuracion.ConceptoFinal
import models.mv.base.fields.ElementoTextoBase
import play.twirl.api.HtmlFormat


case object ElementoTextoBasico extends ElementoTextoBase {
  override def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable = views.html.mv.basico.fields.TextoBasico(conceptoFinal, idPadre)
}
