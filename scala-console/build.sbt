// @formatter:off

name         := "ijp-scala-console"
organization := "net.sf.ij-plugins"
version      := "1.5.2-SNAPSHOT"

homepage     := Some(new URL("https://github.com/ij-plugins/ijp-scala-console"))
startYear    := Some(2013)
licenses     := Seq(("LGPL-2.1", new URL("http://opensource.org/licenses/LGPL-2.1")))
description  :=
  "Simple user interface for executing Scala scripts. Can be run stand-alone or embedded in a desktop application."

crossScalaVersions := Seq("2.12.17")
scalaVersion := crossScalaVersions.value.head

// set the main class for packaging the main jar
// 'run' will still auto-detect and prompt
// change Compile to Test to set it for the test jar
Compile/packageBin/mainClass := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

// set the main class for the main 'run' task
// change Compile to Test to set it for 'test:run'
Compile/ run /mainClass := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

libraryDependencies ++= Seq(
  "com.beachape"           %% "enumeratum"          % "1.7.2",
  "org.fxmisc.richtext"     % "richtextfx"          % "0.11.0",
  "org.scala-lang"          % "scala-compiler"      % scalaVersion.value,
  "org.scala-lang.modules" %% "scala-java8-compat"  % "1.0.2",
  "org.scalafx"            %% "scalafx"             % "19.0.0-R30",
  "org.scalafx"            %% "scalafxml-core-sfx8" % "0.5",
  "org.scalafx"            %% "scalafx-extras"      % "0.7.0",
  "net.imagej"              % "ij"                  % "1.53v",
  "org.scalatest"          %% "scalatest"           % "3.2.14" % "test"
)

Compile/ compile /scalacOptions ++= Seq(
      "-release", "8",
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-feature",
      "-Xfuture",
      "–optimise",
//      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen"
//      "-Ywarn-value-discard",
//      "-Ywarn-unused",
//      "-Ywarn-unused-import"
    )

//resolvers += "ImageJ Releases" at "http://maven.imagej.net/content/repositories/releases/"

// fork a new JVM for 'run' and 'test:run'
fork := true

// add a JVM option to use when forking a JVM for 'run'
javaOptions += "-Xmx2G"

// Needed by ScalaFXML
autoCompilerPlugins := true
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

enablePlugins(SbtImageJ)
ijRuntimeSubDir := "sandbox"
ijPluginsSubDir := "ij-plugins"
cleanFiles      += ijPluginsDir.value

publishTo := {
  val sonatype = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at sonatype + "content/repositories/snapshots")
  else
    Some("releases"  at sonatype + "service/local/staging/deploy/maven2")
}

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