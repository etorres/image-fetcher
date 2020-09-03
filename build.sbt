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

libraryDependencies ++= Seq(
  "com.github.pathikrit" %% "better-files" % "3.9.1",
  "commons-io" % "commons-io" % "2.7",
  "eu.timepit" %% "refined-cats" % "0.9.15",
  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  "net.coobird" % "thumbnailator" % "0.4.12"
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
  case PathList(ps @ _*) if ps.last == "Log4j2Plugins.dat" =>
    Log4j2MergeStrategy.plugincache
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