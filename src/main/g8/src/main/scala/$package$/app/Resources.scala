package $package$.app

import java.util.concurrent.{Executors, ThreadFactory}
import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.ExecutionContext

import $package$.domain.*
import $package$.config.*
import $package$.infrastructure.db.*

import cats.effect.*
import doobie.*
import doobie.hikari.HikariTransactor
import sttp.client3.SttpBackend
import sttp.client3.httpclient.fs2.HttpClientFs2Backend

trait Resources[F[_]]:
  def store: TransactionStore[F, ConnectionIO] & HealthCheck[F]
  def sttpBackend: SttpBackend[F, Any]

object Resources:

  def make[F[_]: Async](config: ServiceConfig): Resource[F, Resources[F]] =

    def makeDoobieTransactor(config: DatabaseConfig, driver: String): Resource[F, Transactor[F]] =
      for
        dbPool <- Resource
          .make {
            Async[F].delay {
              Executors.newFixedThreadPool(10, createThreadFactory)
            }
          } { executorService =>
            Async[F].delay(executorService.shutdown())
          }
          .map(ExecutionContext.fromExecutorService)
        transactor <- HikariTransactor.newHikariTransactor[F](
          driver,
          config.url,
          config.user,
          config.password.value,
          dbPool
        )
      yield transactor

    for
      _                <- Resource.eval(SchemaMigration[F](config.db))
      transactor       <- makeDoobieTransactor(config.db, "org.postgresql.Driver")
      store0           <- DbStore.make[F](transactor)
      sttpBackend0     <- HttpClientFs2Backend.resource[F]()
    yield new Resources[F]:
      override def store: TransactionStore[F, ConnectionIO] & HealthCheck[F]       = store0
      override def sttpBackend: SttpBackend[F, Any]                                = sttpBackend0

  private def createThreadFactory = new ThreadFactory:
    private val counter = AtomicLong(0L)

    def newThread(r: Runnable): Thread =
      val th = Thread(r)
      th.setName("db-thread-" + counter.getAndIncrement.toString)
      th.setDaemon(true)
      th
