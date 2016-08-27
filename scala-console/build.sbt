// @formatter:off

name         := "ijp-scala-console"
organization := "net.sf.ij-plugins"
version      := "1.4.0-SNAPSHOT"

crossScalaVersions := Seq("2.11.8", "2.10.6", "2.12.0-M4", "2.12.0-M5")
scalaVersion <<= crossScalaVersions { versions => versions.head }

// set the main class for packaging the main jar
// 'run' will still auto-detect and prompt
// change Compile to Test to set it for the test jar
mainClass in(Compile, packageBin) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

// set the main class for the main 'run' task
// change Compile to Test to set it for 'test:run'
mainClass in(Compile, run) := Some("net.sf.ij_plugins.scala.console.ScalaConsoleApp")

libraryDependencies ++= Seq(
  "org.scala-lang"          % "scala-compiler"  % scalaVersion.value,
  "org.scala-lang.modules" %% "scala-swing"     % "2.0.0-M2",
  "net.imagej"              % "ij"              % "1.51f",
  "com.fifesoft"            % "rsyntaxtextarea" % "2.5.8",
  "junit"                   % "junit"           % "4.12" % "test",
  "com.novocode"            % "junit-interface" % "0.11" % "test"
)

scalacOptions in(Compile, compile) ++= Seq(
//      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-feature",
      "-Xfuture",
      "–optimise",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen"
//      "-Ywarn-value-discard",
//      "-Ywarn-unused",
//      "-Ywarn-unused-import"
    )

resolvers += "ImageJ Releases" at "http://maven.imagej.net/content/repositories/releases/"

// fork a new JVM for 'run' and 'test:run'
fork := true

// add a JVM option to use when forking a JVM for 'run'
javaOptions += "-Xmx2G"

// Set the prompt (for this build) to include the project id.
shellPrompt in ThisBuild := { state => "sbt:" + Project.extract(state).currentRef.project + "> " }

enablePlugins(SbtImageJ)

ijRuntimeSubDir := "sandbox"
ijPluginsSubDir := "ij-plugins"
cleanFiles      += ijPluginsDir.value

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