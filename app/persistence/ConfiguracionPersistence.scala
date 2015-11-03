package persistence

import javax.inject.{Inject, Singleton}

import controllers.Utils
import models.mv.{ConceptoFinal, ConfiguracionNegocio}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

@Singleton
class ConfiguracionPersistence @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private var tablaConfiguracion: Map[String, ConfiguracionNegocio] = Map(
    ("avanza_seguro" -> ConfiguracionNegocio(1, "avanza_seguro", Utils.construirConceptoFinal(s"/estructuras/detalleNegocio_avanza_seguro.json").get)),
    ("plan_coberturas_enfermedades_graves" -> ConfiguracionNegocio(1, "plan_coberturas_enfermedades_graves", Utils.construirConceptoFinal(s"/estructuras/detalleNegocio_plan_coberturas_enfermedades_graves.json").get))
  )
  private var tablaNegocios: Set[String] = Set("avanza_seguro", "plan_coberturas_enfermedades_graves")


  def listarNegocios(): Set[String] = tablaNegocios

  def crear(nombreNegocio: String, configuracionTemplate: ConceptoFinal): ConfiguracionNegocio = {

    val id: Int = tablaConfiguracion.get(nombreNegocio).map(cn => cn.id).getOrElse(0) + 1

    val resultado = ConfiguracionNegocio(id, nombreNegocio, configuracionTemplate)
    tablaConfiguracion = tablaConfiguracion + (nombreNegocio -> resultado)

    resultado
  }

  def listar(): List[ConfiguracionNegocio] = tablaConfiguracion.values.toList

  def encontrarPorNombreNegocio(nombreNegocio: String): Option[ConfiguracionNegocio] = tablaConfiguracion.get(nombreNegocio)

}
