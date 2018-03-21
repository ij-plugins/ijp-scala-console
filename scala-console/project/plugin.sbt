resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

scalacOptions ++= Seq("-unchecked", "-deprecation")

addSbtPlugin("net.sf.ij-plugins" % "sbt-imagej" % "2.0.1")
