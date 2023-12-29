import sbt.{Resolvers as _, *}

object Dependency {

  private object Version {
    val apacheCommonsText     = "1.10.0"
    val catsCore              = "2.9.0"
    val catsEffect            = "3.5.1"
    val catsRetry             = "3.1.0"
    val circe                 = "0.14.3"
    val ciris                 = "3.1.0"
    val doobie                = "1.0.0-RC1"
    val flyway                = "9.21.1"
    val fs2                   = "3.7.0"
    val http4s                = "0.23.18"
    val janino                = "3.1.9"
    val logback               = "1.4.7"
    val logEffect             = "0.17.0"
    val logstashEncoder       = "7.3"
    val log4cats              = "2.5.0"
    val mouse                 = "1.2.1"
    val munit                 = "0.7.29"
    val munitCatsEffect       = "1.0.7"
    val munitScalaCheckEffect = "1.0.4"
    val smlCommon             = "2.3.4"
    val sttp                  = "3.9.0"
    val scacheVersion         = "5.1.2"
    val scalaLogging          = "3.9.5"
    val swaggerUI             = "1.2.10"
    val tapir                 = "1.1.4"
    val testContainers        = "0.40.12"
  }

  lazy val apacheCommonsText = "org.apache.commons" % "commons-text" % Version.apacheCommonsText

  lazy val cats = Seq(
    "org.typelevel"    %% "cats-core"   % Version.catsCore,
    "org.typelevel"    %% "cats-effect" % Version.catsEffect,
    "com.github.cb372" %% "cats-retry"  % Version.catsRetry,
    "org.typelevel"    %% "mouse"       % Version.mouse
  )

  lazy val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-literal"
  ).map(_ % Version.circe)

  lazy val ciris = "is.cir" %% "ciris" % Version.ciris

  lazy val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres",
    "org.tpolecat" %% "doobie-postgres-circe"
  ).map(_ % Version.doobie)

  lazy val flyway = "org.flywaydb" % "flyway-core" % Version.flyway

  lazy val fs2 = Seq(
    "co.fs2" %% "fs2-core",
    "co.fs2" %% "fs2-io"
  ).map(_ % Version.fs2)

  lazy val http4s = "org.http4s" %% "http4s-ember-server" % Version.http4s

  lazy val loggingDependencies = Seq(
    "org.codehaus.janino"         % "janino"                   % Version.janino          % Runtime,
    "ch.qos.logback"              % "logback-classic"          % Version.logback,
    "net.logstash.logback"        % "logstash-logback-encoder" % Version.logstashEncoder % Runtime,
    "org.typelevel"              %% "log4cats-core"            % Version.log4cats,
    "org.typelevel"              %% "log4cats-slf4j"           % Version.log4cats,
    "com.typesafe.scala-logging" %% "scala-logging"            % Version.scalaLogging
  )

  lazy val smlCommon = "com.softwaremill.common" %% "tagging" % Version.smlCommon

  lazy val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core",
    "com.softwaremill.sttp.client3" %% "circe",
    "com.softwaremill.sttp.client3" %% "cats",
    "com.softwaremill.sttp.client3" %% "fs2"
  ).map(_ % Version.sttp)

  lazy val scache = "com.evolution" %% "scache" % Version.scacheVersion

  lazy val swaggerUI = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Version.swaggerUI

  lazy val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe",
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server",
    "com.softwaremill.sttp.tapir" %% "tapir-cats"
  ).map(_ % Version.tapir)

  val test = Seq(
    "org.tpolecat"  %% "doobie-munit"                    % Version.doobie,
    "org.scalameta" %% "munit"                           % Version.munit,
    "org.typelevel" %% "munit-cats-effect-3"             % Version.munitCatsEffect,
    "org.typelevel" %% "scalacheck-effect-munit"         % Version.munitScalaCheckEffect,
    "com.dimafeng"  %% "testcontainers-scala-munit"      % Version.testContainers,
    "com.dimafeng"  %% "testcontainers-scala-postgresql" % Version.testContainers
  ).map(_ % "test,it")

}
