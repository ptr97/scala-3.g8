package $package$.config

import cats.effect.Async
import ciris.*

final case class DatabaseConfig(url: String, user: String, password: Secret[String], maxPoolSize: Int)

object DatabaseConfig:

  def load[F[_]: Async]: F[DatabaseConfig] =
    (for
      dbUrl         <- env("DB_URL").as[String]
      dbUser        <- env("DB_USERNAME").as[String]
      dbPassword    <- env("DB_PASSWORD").secret
      dbMaxPoolSize <- env("DB_MAX_POOL_SIZE").as[Int]
    yield DatabaseConfig(
      url = dbUrl,
      user = dbUser,
      password = dbPassword,
      maxPoolSize = dbMaxPoolSize
    )).load[F]
