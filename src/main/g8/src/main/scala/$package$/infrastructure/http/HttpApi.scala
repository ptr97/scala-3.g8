package $package$.infrastructure.http

import $package$.domain.*
import $package$.domain.VersionCheck.*

import cats.effect.Async
import org.typelevel.log4cats.slf4j.*
import sttp.tapir.{EndpointInput, Tapir}
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.SwaggerUIOptions
import sttp.tapir.swagger.bundle.SwaggerInterpreter

final case class HttpApi[F[_]: Async](
  store: HealthCheck[F],
  versionCheck: VersionCheck[F]
) extends BaseHttp[F]
    with Tapir:

  private val interpreter      = Http4sServerInterpreter[F]()
  private val metricsEndpoints = HealthCheckRoutes[F](store, versionCheck).endpoints

  private lazy val mainEndpoints =
    metricsEndpoints.map(se => se.prependSecurityIn(apiContextPath.foldLeft(emptyInput: EndpointInput[Unit])(_ / _)))

  private lazy val docsEndpoints =
    SwaggerInterpreter(swaggerUIOptions = SwaggerUIOptions.default)
      .fromServerEndpoints(mainEndpoints, "$name$ endpoints", apiContextPath.last)

  def routes = interpreter.toRoutes(mainEndpoints ++ docsEndpoints)
