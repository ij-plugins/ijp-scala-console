// sbt-imagej configuration keys
import ImageJKeys._

// Project name
name := "scala-console"

// Publishing organization
organization := "net.sf.ij-plugins"

// Current version
version := "1.1.2"

// Version of scala to use
crossScalaVersions := Seq("2.10.3", "2.9.3")

scalaVersion <<= crossScalaVersions {versions => versions.head}

// set the main class for packaging the main jar
// 'run' will still auto-detect and prompt
// change Compile to Test to set it for the test jar
mainClass in (Compile, packageBin) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

// set the main class for the main 'run' task
// change Compile to Test to set it for 'test:run'
mainClass in (Compile, run) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.scala-lang" % "scala-swing" % scalaVersion.value,
  "net.imagej" % "ij" % "1.47v",
  "com.fifesoft" % "rsyntaxtextarea" % "2.0.6",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test"
)

resolvers += "ImageJ Releases" at "http://maven.imagej.net/content/repositories/releases/"

// fork a new JVM for 'run' and 'test:run'
fork := true

// add a JVM option to use when forking a JVM for 'run'
javaOptions += "-Xmx2G"

// Set the prompt (for this build) to include the project id.
shellPrompt in ThisBuild := { state => "sbt:"+Project.extract(state).currentRef.project + "> " }

// sbt-imagej plugin
ijSettings

ijRuntimeSubDir := "sandbox"

ijPluginsSubDir := "ij-plugins"

ijExclusions += """nativelibs4java\S*"""

cleanFiles += ijPluginsDir.value