package models.mv.basico.fields

import models.configuracion.ConceptoFinal
import models.mv.base.fields.ElementoBooleanoBase
import play.twirl.api.HtmlFormat

case object ElementoBooleanoBasico extends ElementoBooleanoBase {
  override def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable = views.html.mv.basico.fields.BooleanoBasico(conceptoFinal, idPadre)
}
