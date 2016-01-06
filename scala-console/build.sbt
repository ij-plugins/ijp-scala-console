// Project name
name := "scala-console"

// Publishing organization
organization := "net.sf.ij-plugins"

// Current version
version := "1.1.3"

// Version of scala to use
crossScalaVersions := Seq("2.11.7", "2.10.6")

scalaVersion <<= crossScalaVersions {versions => versions.head}

// set the main class for packaging the main jar
// 'run' will still auto-detect and prompt
// change Compile to Test to set it for the test jar
mainClass in (Compile, packageBin) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

// set the main class for the main 'run' task
// change Compile to Test to set it for 'test:run'
mainClass in (Compile, run) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler"  % scalaVersion.value,
  "net.imagej"     % "ij"              % "1.49v",
  "com.fifesoft"   % "rsyntaxtextarea" % "2.0.7",
  "junit"          % "junit"           % "4.12"  % "test",
  "com.novocode"   % "junit-interface" % "0.11" % "test"
)

libraryDependencies ++= (if (scalaVersion.value.startsWith("2.11."))
        Seq("org.scala-lang.modules" %% "scala-swing" % "1.0.2")
      else
        Seq("org.scala-lang" % "scala-swing" % scalaVersion.value))

resolvers += "ImageJ Releases" at "http://maven.imagej.net/content/repositories/releases/"

// fork a new JVM for 'run' and 'test:run'
fork := true

// add a JVM option to use when forking a JVM for 'run'
javaOptions += "-Xmx2G"

// Set the prompt (for this build) to include the project id.
shellPrompt in ThisBuild := { state => "sbt:"+Project.extract(state).currentRef.project + "> " }

enablePlugins(SbtImageJ)

ijRuntimeSubDir := "sandbox"

ijPluginsSubDir := "ij-plugins"

ijExclusions += """nativelibs4java\S*"""

cleanFiles += ijPluginsDir.value

// Info needed sync with Maven central.
pomExtra in Global := {
  <url>(your project URL)</url>
  <licenses>
    <license>
      <name>GNU Lesser General Public License</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>
  <scm>
    <connection>scm:svn://svn.code.sf.net/p/ij-plugins/code/trunk</connection>
    <developerConnection>scm:svn://svn.code.sf.net/p/ij-plugins/code/trunk</developerConnection>
    <url>http://sourceforge.net/projects/ij-plugins/</url>
  </scm>
  <developers>
    <developer>
      <id>jsacha</id>
      <name>Jarek Sacha</name>
    </developer>
  </developers>
}