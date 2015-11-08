package models.mv.basico.fields

import models.configuracion.ConceptoFinal
import models.mv.base.fields.ElementoNumericoBase
import play.twirl.api.HtmlFormat


case object ElementoNumericoBasico extends ElementoNumericoBase {
  override def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable = views.html.mv.basico.fields.NumericoBasico(conceptoFinal, idPadre)
}
