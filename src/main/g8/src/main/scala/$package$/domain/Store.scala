package $package$.domain

import fs2.Stream

trait Store[F[_], Txn[_]]:
  val commit: [A] => Txn[A] => F[A]
  val stream: [A] => Stream[Txn, A] => Stream[F, A]
  val lift: [A] => F[A] => Txn[A]

trait TransactionStore[F[_], Txn[_]] extends Store[F, Txn]
