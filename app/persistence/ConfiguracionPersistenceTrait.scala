package persistence

import models.configuracion.{ConfiguracionNegocio, ConceptoFinal}

trait ConfiguracionPersistenceTrait {

  def listarNegocios(): Set[String]

  def crear(nombreNegocio: String, configuracionTemplate: ConceptoFinal): ConfiguracionNegocio

  def listar(): List[ConfiguracionNegocio]

  def encontrarPorNombreNegocio(nombreNegocio: String): Option[ConfiguracionNegocio]
}
