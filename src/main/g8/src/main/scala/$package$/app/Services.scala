package $package$.app

import cats.effect.*
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.*

final case class Services()

object Services:

  def make[F[_]: Sync](): Resource[F, Services] =
    @scala.annotation.nowarn
    given Logger[F] = LoggerFactory.getLogger[F]
    Resource.pure[F, Services](Services())
