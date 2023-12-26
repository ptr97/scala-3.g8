package $package$.domain

import $package$.domain.VersionCheck.Version

import cats.effect.Sync
import cats.syntax.all.*

trait VersionCheck[F[_]]:
  def getCurrentVersion: F[Version]

object VersionCheck:

  final case class Version(value: String)

  def make[F[_]: Sync]: F[VersionCheck[F]] = Sync[F].delay {
    new VersionCheck[F]:
      def getCurrentVersion: F[Version] = Version("<configure-version>").pure[F]
  }

//  def make[F[_]: Sync]: F[VersionCheck[F]] =
//    Sync[F]
//      .delay(Source.fromResource("app.version").getLines().toSeq.headOption)
//      .flatMap(_.pure[F].getOrRaise(RuntimeException("Unable to load version")))
//      .map { appVersion =>
//        new VersionCheck[F]:
//          def getCurrentVersion: F[Version] = Version(appVersion).pure[F]
//      }

