# Example Shrinking

Chapter 5 of [Why Programs Fail](http://www.whyprogramsfail.com/) is
about shrinking a given failing input to a minimal failing input,
using the `ddmin` algorithm.

[ScalaCheck](https://www.scalacheck.org/) comes with a `Shrink` trait
and implementations for many types which performs minimization based
on similar but not exactly identical ideas.

In `ExampleShrinker.scala` you'll find a template which will let you
shrink a given example to its minimum.  In `Geegg.scala` you'll find
an example of how to use it.  Run `sbt test` to see it in action.

# Using this in your own project

To use this in your own project, copy `ExampleShrinker.scala` and
`object GeeggExample extends ExampleShrinker("Geegg")` into your
source test folder, then edit `GeeggExample` to fit your scenario:

 - Change `type Input = String` to the type of your example.
 - Change the value of `knownFailingExample` to your example.
 - Change `runSut` to run your particular system under test.
 - Keep `val shrinker: Shrinker = implicitly` as is.
 - Optionally change `"Geegg"` to something else.
 - Optionally rename `object GeeggExample`.
 - Add ScalaCheck to your `build.sbt` (e.g. like in my `build.sbt`).

If you are familiar with ScalaCheck, you may want to change `shrinker`
to better suit your needs.  The `Shrinker` type is a simple alias for
`Shrink[Input]`.
