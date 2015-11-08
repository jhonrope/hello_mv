package models.mv.base

import models.configuracion.ConceptoFinal
import models.mv.base.fields._
import play.twirl.api.HtmlFormat

trait NoComplejoBase {
  def booleano: ElementoBooleanoBase

  def fecha: ElementoFechaBase

  def multiple: ElementoMultipleBase

  def numerico: ElementoNumericoBase

  def texto: ElementoTextoBase

  def defecto: ElementoFormularioBase

  def apply(conceptoFinal: ConceptoFinal, idPadre: String): HtmlFormat.Appendable

}