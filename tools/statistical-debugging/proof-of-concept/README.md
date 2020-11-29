# Statistical Debugging: proof of concept
See `README.md` in the parent folder for an overview of the statistical
debugging technique.

Here we have a proof-of-concept implementation of this technique.  To
run in, open up `sbt` and type `runMain StatisticalDebugger <example>`
where `<example>` is one of `Middle` or `GCD1` or `GCD2`.

As an exercise, you may want to read `evaluate_gcd` to understand the
specification of `gcd`, and then fix `GCD1.gcd`.

In my experience, GCD1 and GCD2 produce 100% bad runs.  Changing
`GCD.modulus` to something small (e.g. between 5 and 10) reduces the
rate of bad runs.  This suggests to me that you may want to combine
statistical debugging with data generation which produces a mix of
good and bad runs.  I think shrinking failing inputs, with `ddmin` or
something similar, is likely to do this.  When generating random data,
you may want to generate small inputs first and large inputs later.
ScalaCheck's [`Gen.sized`](https://github.com/typelevel/scalacheck/blob/master/doc/UserGuide.md#sized-generators) should do exactly this.

The output of `StatisticalDebugger` is a sorted table in which each
row contains the following information:

 - Probability that the run was bad, given that this bit of code was
   executed
 - File name and line number
 - Method name and argument types
 - Byte code index

Run `javap -v -c <class file>` to make use of the byte code index
information: find the method you're investigating and the instruction
at the given byte code index to find the buggy parts within a single
line of code.  This is particularly relevant when finding out which
parts of `evaluate_gcd` returned `false`.

The instrumentation in `StatisticalDebugger.scala` is tightly coupled
to the systems under test and the test framework in `Examples.java`.
This is not good code. Don't try this at home (nor at work).  Also,
output from the instrumented code is not forwarded to `stdout` or
`stderr` of the instrumenting process, i.e. it gets lost.  That's what
you get when you get a proof of concept ;-)
