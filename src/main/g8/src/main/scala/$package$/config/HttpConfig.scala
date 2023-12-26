package $package$.config

import cats.effect.Async
import cats.syntax.all.*
import ciris.env

final case class HttpConfig(url: String, port: Int)

object HttpConfig:

  def load[F[_]: Async]: F[HttpConfig] =
    for
      url  <- env("HTTP_URL").as[String].load[F]
      port <- env("HTTP_PORT").as[Int].load[F]
    yield HttpConfig(url, port)
