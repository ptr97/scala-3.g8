package $package$.infrastructure.db

import scala.jdk.CollectionConverters.*
import scala.util.control.NonFatal

import $package$.config.DatabaseConfig

import cats.effect.Sync
import cats.syntax.all.*
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.slf4j.Slf4jLogger

object SchemaMigration:

  def apply[F[_]: Sync](dbConfig: DatabaseConfig, location: String = "db/migration"): F[Unit] =
    val logger = Slf4jLogger.getLogger[F]

    Sync[F]
      .blocking {
        val flyway = Flyway.configure().dataSource(dbConfig.url, dbConfig.user, dbConfig.password.value).locations(location)
        flyway.baselineOnMigrate(true).load().migrate()
      }
      .flatMap { migrationsApplied =>
        val migrations = migrationsApplied.migrations.asScala.map(_.description).mkString(", ")
        logger.info(s"Successfully applied [\$migrations] migrations to the database")
      }
      .void
      .onError { case NonFatal(t) =>
        logger.error(s"Unable to run Flyway migration with error: \${t.getMessage}")
      }
