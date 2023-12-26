package $package$.common

abstract class Fail extends Exception

object Fail:
  final case class NotFound(msg: String)       extends Fail
  final case class Conflict(msg: String)       extends Fail
  final case class IncorrectInput(msg: String) extends Fail
