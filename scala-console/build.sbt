// Project name
name := "scala-console"

// Publishing organization
organization := "ij-plugins.sf.net"

// Current version
version := "1.1.0"

// Version of scala to use
scalaVersion := "2.9.3"

// set the main class for packaging the main jar
// 'run' will still auto-detect and prompt
// change Compile to Test to set it for the test jar
mainClass in (Compile, packageBin) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

// set the main class for the main 'run' task
// change Compile to Test to set it for 'test:run'
mainClass in (Compile, run) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

// Extra dependent libraries, in addition to those in 'lib' subdirectory
libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-compiler" % "2.9.3",
    "org.scala-lang" % "scala-swing" % "2.9.3"
)

// Test dependencies
libraryDependencies += "junit" % "junit" % "4.+" % "test"

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