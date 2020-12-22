# DD-min
We implement the `ddmin` algorithm in python, and provide two example
applications:

 - `sbt-test-order-dependency-finder.py` which finds ordering
   dependencies among your scalacheck tests.  See the `example` folder.

 - `refine-diff.py` which shrinks a patch file down to its minimal set
   of failure-exhibiting hunks.  See the `example-of-refine-diff`
   folder.

# Assertions
We implement `precondition` and `postcondition` decorators in python
and use them (lightly) in the `ddmin` implementation.
