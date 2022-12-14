name := "ijp-scala-console-project"

ThisBuild / organization := "net.sf.ij-plugins"
ThisBuild / version := "1.6.0"
ThisBuild / homepage := Some(new URL("https://github.com/ij-plugins/ijp-scala-console"))
ThisBuild / startYear := Some(2013)
ThisBuild / licenses := Seq(("LGPL-2.1", new URL("http://opensource.org/licenses/LGPL-2.1")))
ThisBuild / description :=
  "Simple user interface for executing Scala scripts. Can be run stand-alone or embedded in a desktop application."

lazy val _scalaVersions = Seq("2.13.10", "2.12.17")
lazy val _scalaVersion  = _scalaVersions.head

def isScala2_12(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 12)) => true
    case _ => false
  }
def isScala2_13(scalaVersion: String): Boolean =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 13)) => true
    case _ => false
  }

val commonSettings = Seq(
  //
  crossScalaVersions := _scalaVersions,
  scalaVersion := _scalaVersion,
  scalacOptions ++= Seq(
    "-release", "8",
    "-encoding", "UTF-8",
    "-explaintypes",
    "-unchecked",
    "-deprecation",
    "-Xlint",
    "-Xcheckinit",
    "-feature",
    "–optimise",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xlint:missing-interpolator",
    "-Ywarn-dead-code",
    "-Ywarn-unused:-patvars,_",
    ),
  // fork a new JVM for 'run' and 'test:run'
  fork := true,
  // add a JVM option to use when forking a JVM for 'run'
  javaOptions += "-Xmx2G",
  publishTo := {
    val sonatype = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at sonatype + "content/repositories/snapshots")
    else
      Some("releases" at sonatype + "service/local/staging/deploy/maven2")
  },
  // Info needed sync with Maven central.
  pomExtra in Global := {
    <scm>
      <url>https://github.com/ij-plugins/ijp-scala-console</url>
      <connection>scm:https://github.com/ij-plugins/ijp-scala-console.git</connection>
    </scm>
      <developers>
        <developer>
          <id>jpsacha</id>
          <name>Jarek Sacha</name>
          <url>https://github.com/jpsacha</url>
        </developer>
      </developers>
  }
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
    Compile / packageBin / mainClass := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp"),
    // set the main class for the main 'run' task
    // change Compile to Test to set it for 'test:run'
    Compile / run / mainClass := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp"),
    // // @formatter:off
    libraryDependencies ++= Seq(
      "com.beachape"           %% "enumeratum"          % "1.7.2",
      "org.fxmisc.richtext"     % "richtextfx"          % "0.11.0",
      "org.scala-lang"          % "scala-compiler"      % scalaVersion.value,
      "org.scala-lang.modules" %% "scala-java8-compat"  % "1.0.2",
      "org.scalafx"            %% "scalafx"             % "19.0.0-R30",
      "org.scalafx"            %% "scalafxml-core-sfx8" % "0.5",
      "org.scalafx"            %% "scalafx-extras"      % "0.7.0",
      "org.scalatest"          %% "scalatest"           % "3.2.14" % "test"
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
      "net.imagej" % "ij" % "1.53v",
      "org.scalatest" %% "scalatest" % "3.2.14" % "test"
      )
    //resolvers += "ImageJ Releases" at "http://maven.imagej.net/content/repositories/releases/"
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

addCommandAlias("ijRun", "scala-console-plugins/ijRun")



