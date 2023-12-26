import scala.sys.process.*
import scala.util.Try

ThisBuild / scalaVersion := "3.3.1"

ThisBuild / parallelExecution := false

val IntegrationTests = config("it").extend(Test)

lazy val commonSettings = Seq(
  organization      := "$package$",
  run / fork        := true,
  run / javaOptions := Seq("-Dlogback.configurationFile=logback.xml"),
  scalafmtOnCompile := true,
  externalResolvers ++= Seq(CustomResolvers.Evolution),
  scalacOptions := scalacOptions.value
    .filterNot(_ == "-source:3.0-migration") ++ Seq("-source:3.3", "-Wunused:all", "-Wvalue-discard"),
  logo :=
    """
         _____           _         ____
      |  / ____|         | |       |___ \
      | | (___   ___ __ _| | __ _    __) |
      |  \___ \ / __/ _` | |/ _` |  |__ <
      |  ____) | (_| (_| | | (_| |  ___) |
      | |_____/ \___\__,_|_|\__,_| |____/
      |
      |""".stripMargin
)

addCommandAlias("compileAll", "compile; Test / compile; It / compile")
addCommandAlias("testAll", "test; It / test")

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning, JavaAppPackaging, DockerPlugin)
  .settings(commonSettings*)
  .configs(IntegrationTests)
  .settings(inConfig(IntegrationTests)(Defaults.testSettings ++ scalafixConfigSettings(IntegrationTests)): _*)
  .settings(
    console / initialCommands :=
      """
        | import cats.*
        | import cats.implicits.*
        | import cats.effect.*
        | import cats.effect.implicits.*""".stripMargin
  )
  .settings(
    name               := "$name;format="norm"$",
    version            := "0.1.0-SNAPSHOT", // sys.env.getOrElse("BUILD_VERSION", gitVersion),
    dockerBaseImage    := "openjdk:11",
    dockerExposedPorts := Seq(9000, 9001),
    dockerUpdateLatest := true,
    libraryDependencies ++=
        Dependency.cats ++
        Dependency.circe ++
        Dependency.doobie ++
        Dependency.fs2 ++
        Dependency.loggingDependencies ++
        Dependency.sttp ++
        Dependency.tapir ++
        Dependency.test :+
        Dependency.apacheCommonsText :+
        Dependency.ciris :+
        Dependency.flyway :+
        Dependency.http4s :+
        Dependency.scache :+
        Dependency.smlCommon :+
        Dependency.swaggerUI
//    Compile / resourceGenerators += Def.task {
//      val file = (Compile / resourceManaged).value / "app.version"
//      val lines = Seq(
//        sys.env.getOrElse("BUILD_VERSION", version.value),
//        gitVersion
//      )
//      IO.writeLines(file, lines)
//      Seq(file)
//    }
  )

lazy val gitVersion = Try {
  "git rev-parse --short=8 HEAD".!!.trim
}.getOrElse(throw new RuntimeException("Unable to get commit hash"))
