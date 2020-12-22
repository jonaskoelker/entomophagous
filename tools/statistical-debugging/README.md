# ARBITRARY CODE EXECUTION VULNERABILITY
I use the Java Debug Interface to instrument a JVM. On Unix systems
this uses the socket transport, which opens you up to an arbitrary
code execution vulnerability.  In older versions, your JVM could be
remotely controlled by anyone on the internet, if they could connect
to your machine.  In newer versions, it is "only" (other) users on the
machine running the instrumented JVM which can remotely control it.

You may or may not find this an unacceptable risk.  You may also want
to run all of this code inside a non-networked docker image.  (You may
also want to run the automatic download of dependencies is a networked
docker image first.)

# Statistical debugging
Statistical debugging is a technique from Why Programs Fail.  In a
nutshell, it is the following:

 - Run your buggy code multiple times
 - Instrument the code when running it
 - Have an automated way of classifying runs as good or bad
   - A test case assertion is the most natural way of doing this
 - For each line of code (or some other unit), calculate the
   probability that a random run is bad, given that the unit of code
   was executed as part of that run.
 - Present the user with the high-probability bad lines of code.

The assumption is that code units which selectively show up in bad
runs is likely to be buggy.

## Considerations
If a single statement in a straight-line fragment of your program gets
executed (in any given run), then all statements in that fragment get
executed.  In other words, straight-line fragments are atomic with
respect to statistical debugging as I've described it, meaning it can
never tell you _which_ statement in a straight-line fragment is wrong,
only which fragment contains the offending statement.

Note that then- and else-branches of conditionals are often executed
exactly zero or once in a test run; loop bodies are executed a varying
number of times, which impacts the probabilities in interesting ways.

I have only run this on small examples, where the bug is located
inside a single straight-line fragment.  I am interested to see how
this technique fares on bugs which require two distant branches to be
taken in the same run to exhibit the bug.

The definition of "a straight-line fragment" is non-local and thus
_interesting_ in the face of exceptions being thrown.

## Extending the technique
Above, I presented the idea of statistical debugging using "was this
unit of code executed" as the key observation.  You can statistically
analyze any observable characteristic.  Here are some more ideas:

 - For each execution of each function: did it return or throw?
 - For every variant of every sum type returned by a method: was this
   particular variant the one we returned?
 - For numeric values returned by methods: what's their sign?

For example, an `indexOf` method returning a negative number might
indicate a bug, in C-inspired libraries and languages.  You might
exhibit a bug more often when a given method returns a `Left(_)`
rather than a `Right(_)`, or when it throws an exception—although if
your design your APIs such that throwing an exception is always a bug,
this latter observation is not particularly helpful.
