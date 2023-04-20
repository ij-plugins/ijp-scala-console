import xerial.sbt.Sonatype.GitHubHosting

// @formatter:off
name := "ijp-scala-console-project"

lazy val _scalaVersions = Seq("3.2.2", "2.13.10", "2.12.17")
lazy val _scalaVersion  = _scalaVersions.head

ThisBuild / version             := "1.7.1.1-SNAPSHOT"
ThisBuild / versionScheme       := Some("early-semver")
ThisBuild / organization        := "net.sf.ij-plugins"
ThisBuild / sonatypeProfileName := "net.sf.ij-plugins"
ThisBuild / homepage            := Some(new URL("https://github.com/ij-plugins/ijp-scala-console"))
ThisBuild / startYear           := Some(2013)
ThisBuild / licenses            := Seq(("LGPL-2.1", new URL("https://opensource.org/licenses/LGPL-2.1")))
ThisBuild / description         :=
  "Simple user interface for executing Scala scripts. Can be run stand-alone or embedded in a desktop application."
ThisBuild / developers          := List(
  Developer(id="jpsacha", name="Jarek Sacha", email="jpsacha@gmail.com", url=url("https://github.com/jpsacha"))
)

publishArtifact     := false
publish / skip      := true


def isScala2(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, _)) => true
    case _            => false
  }

def isScala2_12(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 12)) => true
    case _             => false
  }
def isScala2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 13)) => true
    case _             => false
  }

val commonSettings = Seq(
  //
  crossScalaVersions := _scalaVersions,
  scalaVersion := _scalaVersion,
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-unchecked",
    "-release", "8",
    "-deprecation",
    ) ++ (
    if(isScala2(scalaVersion.value))
      Seq(
      "-explaintypes",
      "-feature",
//      "–optimise",
      "-Xsource:3",
      "-Xlint",
      "-Xcheckinit",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Xlint:missing-interpolator",
      "-Ywarn-dead-code",
      "-Ywarn-unused:-patvars,_",
    )
    else
      Seq(
        "-explain",
        "-explain-types"
      )
    ),
  //
  exportJars := true,
  //
  autoCompilerPlugins := true,
  // fork a new JVM for 'run' and 'test:run'
  fork := true,
  // add a JVM option to use when forking a JVM for 'run'
  javaOptions += "-Xmx2G",
  //
  manifestSetting,
  // Setup publishing
  publishMavenStyle := true,
  publishTo := sonatypePublishToBundle.value,
  sonatypeProjectHosting := Some(GitHubHosting("ij-plugins", "ijp-scala-console", "jpsacha@gmail.com"))
  )

// The core ijp-color module
lazy val scala_console = (project in file("scala-console"))
  .settings(
    name := "scala-console",
    description := "Scala Console Core",
    commonSettings,
    // set the main class for packaging the main jar
    // 'run' will still auto-detect and prompt
    // change Compile to Test to set it for the test jar
    Compile / packageBin / mainClass := Some("ij_plugins.scala.console.ScalaConsoleApp"),
    // set the main class for the main 'run' task
    // change Compile to Test to set it for 'test:run'
    Compile / run / mainClass := Some("ij_plugins.scala.console.ScalaConsoleApp"),
    //
    libraryDependencies ++= Seq(
      "com.beachape"           %% "enumeratum"          % "1.7.2",
      "org.fxmisc.richtext"     % "richtextfx"          % "0.11.0",
      "org.scala-lang.modules" %% "scala-java8-compat"  % "1.0.2",
      "org.scalafx"            %% "scalafx"             % "19.0.0-R30",
//      "org.scalafx"            %% "scalafxml-core-sfx8" % "0.5",
      "org.scalafx"            %% "scalafx-extras"      % "0.7.0",
      "org.scalatest"          %% "scalatest"           % "3.2.15" % "test"
    ),
    // Exclude due to security issue with its dependency  "com.google.protobuf":"protobuf-java":"3.7.0"
    libraryDependencies ++= (
      if(isScala2(scalaVersion.value))
        Seq("org.scala-lang" % "scala-compiler" % scalaVersion.value exclude("org.scala-sbt", "compiler-interface"))
      else
        Seq("org.scala-lang" % "scala3-compiler_3" % scalaVersion.value exclude("org.scala-sbt", "compiler-interface"))
      ),
    // // @formatter:on
    libraryDependencies ++= (
      if (isScala2_12(scalaVersion.value))
        Seq(compilerPlugin(
          "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
          ))
      else
        Seq.empty[sbt.ModuleID]
      ),
    // If using Scala 2.13 or better, enable macro processing through compiler option
    scalacOptions += (if (isScala2_13(scalaVersion.value)) "-Ymacro-annotations" else "")
    )

lazy val scala_console_plugins = (project in file("scala-console-plugins"))
  .settings(
    name := "scala-console-plugins",
    description := "Scala Console ImageJ Plugins",
    commonSettings,
    libraryDependencies ++= Seq(
      "net.imagej" % "ij" % "1.54d",
      "org.scalatest" %% "scalatest" % "3.2.15" % "test"
      ),
    //resolvers += "ImageJ Releases" at "http://maven.imagej.net/content/repositories/releases/"
    // Customize `sbt-imagej` plugin
    ijRuntimeSubDir := "sandbox",
    ijPluginsSubDir := "ij-plugins",
    ijCleanBeforePrepareRun := true,
    cleanFiles += ijPluginsDir.value,
    )
  .dependsOn(scala_console)

lazy val manifestSetting = packageOptions += {
  Package.ManifestAttributes(
    "Created-By" -> "Simple Build Tool",
    "Built-By" -> Option(System.getenv("JAR_BUILT_BY")).getOrElse(System.getProperty("user.name")),
    "Build-Jdk" -> System.getProperty("java.version"),
    "Specification-Title" -> name.value,
    "Specification-Version" -> version.value,
    "Specification-Vendor" -> organization.value,
    "Implementation-Title" -> name.value,
    "Implementation-Version" -> version.value,
    "Implementation-Vendor-Id" -> organization.value,
    "Implementation-Vendor" -> organization.value
    )
}


enablePlugins(SbtImageJ)
ijRuntimeSubDir := "sandbox"
ijPluginsSubDir := "ij-plugins"
cleanFiles += ijPluginsDir.value

addCommandAlias("ijRun", "scala_console_plugins/ijRun")
addCommandAlias("run", "scala_console/run")



