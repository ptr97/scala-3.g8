package $package$.infrastructure.http

import scala.util.chaining.*

import $package$.domain.*
import $package$.domain.VersionCheck.Version
import $package$.infrastructure.http.HealthCheckRoutes.ServiceStatus

import cats.effect.Async
import cats.syntax.all.*
import io.circe.{Encoder, Json}
import io.circe.generic.semiauto.*
import org.typelevel.log4cats.slf4j.*
import sttp.tapir.{Schema, SchemaType}
import sttp.tapir.server.ServerEndpoint

final class HealthCheckRoutes[F[_]: Async](
  store: HealthCheck[F],
  versionCheck: VersionCheck[F]
) extends BaseHttp[F]:

  private val pingPongEndpoint =
    baseEndpoint
      .in("ping")
      .get
      .out(stringBody)
      .serverLogic(_ => "Pong".pure.toOut)

  private val statusEndpoint =
    baseEndpoint
      .in("status")
      .get
      .out(jsonBody[ServiceStatus])
      .serverLogic { _ =>
        (for
          storeHealthStatus <- healthStatus(store)
          version           <- versionCheck.getCurrentVersion
        yield ServiceStatus(storeHealthStatus, version)).toOut
      }

  private def healthStatus(healthCheck: HealthCheck[F]): F[String] =
    healthCheck.healthCheck.as("healthy").handleError(_ => "Not Healthy!")

  val endpoints = List(pingPongEndpoint, statusEndpoint)

object HealthCheckRoutes:

  final case class ServiceStatus(storeHealthStatus: String, version: Version)

  given Encoder[Version]       = _.value.pipe(Json.fromString)
  given Encoder[ServiceStatus] = deriveEncoder
