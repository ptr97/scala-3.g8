package $package$.infrastructure.db

import $package$.domain.*

import cats.~>
import cats.effect.*
import cats.syntax.all.*
import doobie.*
import doobie.free.connection.ConnectionIO
import doobie.implicits.*
import fs2.Stream

object DbStore:

  def make[F[_]: Async](transactor: Transactor[F]): Resource[F, DbStore[F]] =
    WeakAsync.liftK[F, ConnectionIO].map(lift => DbStore[F](lift, transactor))

final class DbStore[F[_]: MonadCancelThrow] private (l: F ~> ConnectionIO, tx: Transactor[F])
  extends HealthCheck[F] with TransactionStore[F, ConnectionIO]:

    private val healthCheckQuery: ConnectionIO[Int] = sql"select 42".query[Int].unique

    override def healthCheck: F[Unit] = commit(healthCheckQuery).void

    override val lift: [A] => F[A] => ConnectionIO[A]   = [A] => (fa: F[A]) => l(fa)
    override val commit: [A] => ConnectionIO[A] => F[A] = [A] => (txn: ConnectionIO[A]) => txn.transact(tx)

    override val stream: [A] => Stream[ConnectionIO, A] => Stream[F, A] = [A] => (txn: Stream[ConnectionIO, A]) => txn.transact(tx)
