Compile / unmanagedSources / includeFilter := "Implementation.scala"
Test / unmanagedSources / includeFilter := "PropertyTests.scala"

Global / concurrentRestrictions := Seq(Tags.limit(Tags.Test, 1))
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1"

lazy val sampleApplication = (project in file(".")).settings(
  crossScalaVersions := List("2.11.12", "2.12.10")
)
