package $package$.common
  
object IterableSyntax:

  extension [A](iterable: Iterable[A])

    def mostFrequentElement: Option[A] =
      iterable
        .groupMapReduce(identity)(_ => 1)(_ + _)
        .maxByOption { case (_, count) => count }
        .map { case (mostFrequentItem, _) => mostFrequentItem }
