scalaVersion := "2.11.12"
Compile / unmanagedSources / includeFilter := "StatisticalDebugger.scala"
Global / concurrentRestrictions := Seq(Tags.limit(Tags.Test, 1))
