package models.infraetructura

import persistence.{ConfiguracionPersistence, ConfiguracionPersistenceTrait}
import play.api.inject.Module
import play.api.{Configuration, Environment}


class ConfiguracionPersistenceModule extends Module {
  def bindings(env: Environment, conf: Configuration) = Seq(
    bind[ConfiguracionPersistenceTrait].to[ConfiguracionPersistence]
  )
}