// sbt-imagej configuration keys
import ImageJKeys._

// Project name
name := "scala-console"

// Publishing organization
organization := "net.sf.ij-plugins"

// Current version
version := "1.1.1"

// Version of scala to use
crossScalaVersions := Seq("2.9.3", "2.10.3")

scalaVersion <<= crossScalaVersions {versions => versions.head}

// set the main class for packaging the main jar
// 'run' will still auto-detect and prompt
// change Compile to Test to set it for the test jar
mainClass in (Compile, packageBin) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

// set the main class for the main 'run' task
// change Compile to Test to set it for 'test:run'
mainClass in (Compile, run) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

// Extra dependent libraries, in addition to those in 'lib' subdirectory
libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-compiler" % _ }

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }

// Test dependencies
libraryDependencies += "junit" % "junit" % "4.11" % "test"

//
// Use ScalaCL compiler plugin
//
resolvers += "Sonatype OSS Snapshots Repository" at "http://oss.sonatype.org/content/groups/public/"

resolvers += "NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/"

// libraryDependencies += "com.nativelibs4java" % "scalacl" % "0.2"

autoCompilerPlugins := true

addCompilerPlugin("com.nativelibs4java" % "scalacl-compiler-plugin" % "0.2")

// fork a new JVM for 'run' and 'test:run'
fork := true

// add a JVM option to use when forking a JVM for 'run'
javaOptions += "-Xmx2G"

// Set the prompt (for this build) to include the project id.
shellPrompt in ThisBuild := { state => "sbt:"+Project.extract(state).currentRef.project + "> " }

// sbt-imagej plugin
imageJSettings

imageJRuntimeDir := "sandbox"

imageJPluginsSubDir := "ij-plugins"

imageJExclusions += """nativelibs4java\S*"""