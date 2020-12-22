# Entomophagous

Entomophagous eats your bugs.

This is a collection of scripts and tools which implement ideas from
the book [Why Programs Fail](http://www.whyprogramsfail.com/) by
Andreas Zeller, which is about both manual and automatic debugging
techniques.

At the moment, Entomophagous offers the following:

 - A small and simple implementation of programming by contract for
   python: a `precondition` and `postcondition` decorator.  See
   `tools/dd/contracts.py`.

 - A python implementation of the `ddmin` algorithm, see `tools/dd`
   and `tools/dd/dd.py` in particular.  This also demonstrates how to
   use the `precondition` and `postcondition` decorators.

 - An application of `ddmin` which shrinks a patch file to its minimal
   set of failure-inducing hunks, relying on manual adjudication.  See
   `tools/dd/refine-diff.py` and `tools/dd/example-of-refine-diff/`

 - An application of `ddmin` which finds a minimal set of test cases
   which makes your `sbt test` suite fail.  This is useful if your
   tests have an order dependency among them, i.e. if your test suite
   has that particular flavor of bug.

 - Statistical debugging: a technique where you run your test multiple
   times, group coverage data by success/failure outcomes and use this
   to predict which lines of code are most likely to exhibit your bug.
   See `tools/statistical-debugging/` for both a proof of concept and
   a ScalaCheck integration.

# Future roadmap

There are more techniques to implement.  I intend to implement some of
them, maybe all.

Using JDI, it seems straightforward to record all writes to fields on
all objects and all function arguments and return values.  This means
that implementing omniscient debugging where you can rewind through
time seems straightforward, as does "Algorithmic debugging" where you
do a top down search through the call tree to find the first wrong
return value (and hence the location of the bug).

Finding race conditions by gradually morphing a failing schedule into
a succeeding schedule (or vice versa) seems more tricky.  I see no
fundamental reason why it can't be done, merely challenges around how
to actually carry it out.  What data structure represents a thread
schedule? A list of which thread to run for how many instructions?
Is the set of threads existing at time t a function of how threads
were scheduled before t?

There is also a technique for isolating cause-effect chains, where you
follow some faulty data through your program, from the point where the
bug induces the faultiness of the data to the point where you observe
it.  I need to review the ideas and implementation techniques.  It
looks potentially more straightforward than minimizing thread schedule
differences, but it's also less clear in my mind at the moment.

The implementations so far are not battle-hardened.  Additionally they
are optimized for my workflow, which may differ from yours.  I haven't
gone out of my way to be incompatible with anything, but on the other
hand I only integrate statistical debugging with ScalaCheck and not
e.g. JUnit.  The scripts for running the statistical debugger are not
the most wonderful thing ever.  I might improve entomophagous along
these dimensions.
