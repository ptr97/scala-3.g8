package $package$.app

import $package$.config.*
import $package$.domain.VersionCheck
import $package$.infrastructure.http.HttpApi

import cats.Parallel
import cats.effect.*
import com.comcast.ip4s.*
import fs2.io.net.Network
import org.http4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.*

object Application:

  def run[F[_]: Async: Parallel: Network]: Resource[F, Unit] =
    (for
      config    <- Resource.eval(ServiceConfig.load[F])
      resources <- Resources.make(config)
    yield (config, resources)).flatMap { (config, resources) =>
      for
        log          <- Resource.eval(LoggerFactory.create[F])
        _            <- Resource.eval(log.info("Starting server"))
        versionCheck <- Resource.eval(VersionCheck.make[F])
        store       = resources.store
        sttpBackend = resources.sttpBackend

        services <- Services.make[F]()

        httpRoutes = HttpApi(store, versionCheck).routes
        _ <- httpServer(httpRoutes, config.http)
      yield ()
    }

  private def httpServer[F[_]: Async: Network](routes: HttpRoutes[F], httpConfig: HttpConfig): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(Host.fromString(httpConfig.url).getOrElse(host"0.0.0.0"))
      .withPort(Port.fromInt(httpConfig.port).getOrElse(port"8080"))
      .withHttpApp(routes.orNotFound)
      .build
