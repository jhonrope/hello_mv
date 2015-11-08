package models.mv.basico.fields

import models.configuracion.ConceptoFinal
import models.mv.base.fields.ElementoFechaBase
import play.twirl.api.HtmlFormat

case object ElementoFechaBasico extends ElementoFechaBase{
  override def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable = views.html.mv.basico.fields.FechaBasico(conceptoFinal, idPadre)
}
