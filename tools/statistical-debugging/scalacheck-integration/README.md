# Scalacheck integration of statistical debugging
This folder contains an implementation of statistical debugging which
integrates with [ScalaCheck](https://github.com/typelevel/scalacheck).

Using [JDI](https://docs.oracle.com/javase/8/docs/jdk/api/jpda/jdi/)
this implementation instruments a JVM running ScalaCheck property
tests—the arbitrary code execution warning from the parent folder
applies here!—using the multiple runs and the shrinking process
inherent in property testing to produce a mix of good and bad runs.

The report being produced when the run is complete has the same format
as the proof of concept: each line has four fields, indicating the bad
run probability, the filename and line number, the class and method
(by name and types) and the bytecode index.  See the proof-of-concept
documentation for further detail.

To build the scalacheck integration, run `sbt'test:compile package`.

To run the scalacheck integration, you must set two environment
variables:

 - `ENTOMOPHAGOUS_STATISTICAL_DEBUGGER_MAIN`, specifying the class
   whose main method you want to run.  If you write your ScalaCheck
   properties as `object Foo extends Properties("Bar") { ... }`, you
   want to use the name `Foo` and you can add command line arguments
   accepted by ScalaCheck.  If instead of `object` you write `class`,
   you want to copy or symlink the bundled `PropertyRunner.scala` into
   the project you're debugging and run `PropertyRunner` as your main.
   It instantiates a class and runs its `main()` method.

 - `ENTOMOPHAGOUS_STATISTICAL_DEBUGGER_OPTIONS`, containing the
   classpath and other JVM options.  See the `debug_2_11.sh` and
   `debug_2_12.sh` scripts in the `sample-application` folder to see
   two examples that work on my machine.  I determined them by running
   the `scala` shell and looking at my process table for command line
   arguments.  Expect to do some work here to get this to run.  You
   may want to take my scripts as a starting point which you modify.
