import sbt.*

object Plugin {

  private object Version {
    val DotEnv         = "3.0.0"
    val Git            = "2.0.0"
    val NativePackager = "1.9.11"
    val SbtUpdates     = "0.6.4"
    val SbtWelcome     = "0.2.2"
    val Scalafix       = "0.10.4"
    val ScalaFmt       = "2.5.0"
    val Tpolecat       = "0.4.1"
  }

  val DotEnv         = "nl.gn0s1s"                %% "sbt-dotenv"          % Version.DotEnv
  val Git            = "com.github.sbt"            % "sbt-git"             % Version.Git
  val NativePackager = "com.github.sbt"            % "sbt-native-packager" % Version.NativePackager
  val SbtUpdates     = "com.timushev.sbt"         %% "sbt-updates"         % Version.SbtUpdates
  val SbtWelcome     = "com.github.reibitto"       % "sbt-welcome"         % Version.SbtWelcome
  val Scalafix       = "ch.epfl.scala"             % "sbt-scalafix"        % Version.Scalafix
  val ScalaFmt       = "org.scalameta"             % "sbt-scalafmt"        % Version.ScalaFmt
  val Tpolecat       = "io.github.davidgregory084" % "sbt-tpolecat"        % Version.Tpolecat

}
