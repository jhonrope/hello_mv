package models.mv.base.fields

import models.configuracion.ConceptoFinal
import play.twirl.api.HtmlFormat

trait ElementoFormularioBase {

  def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable
}

trait ElementoTextoBase extends ElementoFormularioBase

trait ElementoBooleanoBase extends ElementoFormularioBase

trait ElementoNumericoBase extends ElementoFormularioBase

trait ElementoFechaBase extends ElementoFormularioBase

trait ElementoMultipleBase extends ElementoFormularioBase