# Tools overview

## Implementation and application of ddmin

In the `dd` folder, you'll find an implementation of the `ddmin`
algorithm from [Why Programs Fail](http://www.whyprogramsfail.com/),
and two applications: one to shrink a patch file to a minimal set of
hunks, and one to shrink an `sbt` test suite to a minimal set of
failing test cases (useful in case of unwanted dependencies between
test cases, including order dependencies).

## Automatic shrinking using ScalaCheck

In the `example-shrinker` folder you'll find a small template for
shrinking a known failing input to a minimal failing input, using
[ScalaCheck](https://www.scalacheck.org/).

This is useful e.g. if you want to apply a `ddmin`-like automatic
simplification to a one-off job but don't want to spend the time to
[learn ScalaCheck](https://www.scalacheck.org/documentation.html).

## Staticistal debugging

In the `statistical-debugging` folder, you'll find:

 - A proof-of-concept implementation of statistical debugging, applied
   to a few simple examples which cooperate with the implementation.

 - A more robust implementation of statistical debugging which works
   on ScalaCheck properties.

 - A sample application which uses ScalaCheck but does not otherwise
   cooperate with statistical debugging, to which the ScalaCheck
   implementation of statistical debugging can be applied.

In both implementations, statistics are collected about coverage at
the level of JVM bytecode instructions.
