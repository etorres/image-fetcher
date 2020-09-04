package es.eriktorr.image

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

final case class LocalFileSystem(baseOutputDirectory: String)

final case class ApplicationContext(localFileSystem: LocalFileSystem)

object ApplicationContext {
  def apply(): ApplicationContext = apply(ConfigFactory.load())

  def apply(config: Config): ApplicationContext = {
    val localFileSystem: LocalFileSystem = config.as[LocalFileSystem]("localFileSystem")

    ApplicationContext(localFileSystem)
  }
}
