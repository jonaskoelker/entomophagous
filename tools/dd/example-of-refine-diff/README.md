# Example application of `refine-diff.py`

Commit `f2fe93d83e0165632516c2a53f517a655f63484e` fixed a corner case
in the specification of `gcd` in the proof-of-concept implementation
of statistical debugging.  Let's apply `refine-diff.py` to this
example.

`example/Check.java` contains a correct implementation of `gcd` which
it uses to probe the `gcd` specification in `evaluate_gcd`.  Next to
it is `example/Examples.java` which contains a correct specification
of `gcd` in the `evaluate_gcd` method.

In `revert-f2fe93d83e0165632516c2a53f517a655f63484e.patch` we have a
set of changes that undo `f2fe93d83e0165632516c2a53f517a655f63484e`,
which would restore the older defective specification.

The patch file contains multiple changes unrelated to this defect.  To
shrink the patch down to just the defect-related parts, simply run
`../refine-diff.py revert-f2fe93d83e0165632516c2a53f517a655f63484e.patch`.

It will print some output telling you which diff hunks it's trying
out, then ask you `Does this set have the bug [yn]? `.  Enter `y` or
`n`.  To determine whether a given set of diff hunks exhibits the bug,
you can run `./run.sh`.  The exit status tells you whether the bug is
present: `0` means absent, `1` is present and `125` is undecided (same
as `git bisect run` commands).

If you give the inputs suggested by `run.sh` then `refine-diff.py`
should produce the output line
`filterdiff 'revert-f2fe93d83e0165632516c2a53f517a655f63484e.patch' --hunks 1`
followed by the output of that command.
