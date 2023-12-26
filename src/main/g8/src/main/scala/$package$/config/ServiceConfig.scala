package $package$.config

import cats.effect.Async
import cats.syntax.all.*

final case class ServiceConfig(db: DatabaseConfig, http: HttpConfig)

object ServiceConfig:

  def load[F[_]: Async]: F[ServiceConfig] =
    for
      dbConfig   <- DatabaseConfig.load[F]
      httpConfig <- HttpConfig.load[F]
    yield ServiceConfig(db = dbConfig, http = httpConfig)
