import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys._
import sbt._
import sbtassembly.Log4j2MergeStrategy
import sbtrelease.Version
import wartremover.Wart
import wartremover.WartRemover.autoImport._

organization := "es.eriktorr"
name := "image-fetcher"
version := (version in ThisBuild).value

scalaVersion := "2.13.3"

val log4jVersion = "2.13.2"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.855",
  "com.amazonaws" % "aws-lambda-java-events" % "3.2.0",
  "com.amazonaws" % "aws-lambda-java-log4j2" % "1.2.0",
  "com.github.pathikrit" %% "better-files" % "3.9.1",
  "commons-io" % "commons-io" % "2.7",
  "com.softwaremill.sttp.client" %% "core" % "2.2.7",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion % Test,
  "org.apache.logging.log4j" % "log4j-jcl" % log4jVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  "io.spray" %% "spray-json" % "1.3.5",
  "net.coobird" % "thumbnailator" % "0.4.12",
  "com.github.tomakehurst" % "wiremock-jre8" % "2.27.1" % Test
)

scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-Xfatal-warnings",
  "-Xlint",
  "-deprecation",
  "-unchecked"
)

javacOptions ++= Seq(
  "-g:none",
  "-source",
  "11",
  "-target",
  "11",
  "-encoding",
  "UTF-8"
)

scalafmtOnCompile := true

val warts: Seq[Wart] = Warts.unsafe

wartremoverErrors in (Compile, compile) ++= warts
wartremoverErrors in (Test, compile) ++= warts

assemblyJarName in assembly := "image-fetcher.jar"

assemblyMergeStrategy in assembly := {
  case "module-info.class" => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last == "Log4j2Plugins.dat" => Log4j2MergeStrategy.plugincache
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

releaseNextVersion := { ver => Version(ver).map(_.bumpMinor.string).getOrElse("Error") }

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "es.eriktorr.image",
    buildInfoOptions := Seq(BuildInfoOption.BuildTime)
  )
