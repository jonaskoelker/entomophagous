# Sample "application" to which the ScalaCheck integration applies
This is a sample application which is designed to vaguely share some
structural features with real applications, in particular as far as
the ScalaCheck statistical debugging implementation is concerned.

In `Implementation.scala` you find the "application" which prints the
outputs of a small selection of buggy methods.  The methods including
bugs are examples from Why Programs Fail, and are basically the same
as in the proof-of-concept example of statistical debugging.  To run
it, a simple `sbt run` should suffice.

In `PropertyTests.scala` you have property tests for the methods.  Run
`sbt test` to run the tests.

The sample application can be compiled with both Scala version 2.11.12
and Scala version 2.12.10.  The `sbt +test:compile` compiles for both
versions. In `debug_2_11.sh` and `debug_2_12.sh` you have launcher
scripts which run the statistical debugger on the 2.11 and 2.12
versions of the sample application, respectively.  The value of
`ENTOMOPHAGOUS_STATISTICAL_DEBUGGER_OPTIONS` is derived by running the
`scala` shell and copy-pasting from the process table, abstracting out
my particular `${HOME}` folder and other paths.

I could only get the thing to run if I include the Java 8 JDI tools on
the classpath, even when I think I'm running Java 11.  That's why the
scripts refer to `tools.jar`.  If you know what I'm missing, please
let me know :-)
