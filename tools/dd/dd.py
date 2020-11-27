#!/usr/bin/env python3
"""Here we implement (a variant of) ddmin, the input simplification
algorithm described in chapter 5 of Why Programs fail.  We also
implement ddmax from ddmin by negating the sense of the set of data to
be tested.

Automatic input data simplification is a cornerstone part of property
testing frameworks such as hypothesis (python) and scalacheck (scala).

The book suggests caching test results (which we don't), and
optionally stopping early, for example when:
 - you have tested for some fixed amount of time
 - when no progress has been made for a while
 - when the simplification granularity is sufficiently fine.
We never stop early.

We do not implement the dd-isolate algorithm.

"""

from contracts import precondition, postcondition

@precondition(lambda n, evaluate: n > 0)
def ddmin(n, evaluate):
    candidates = list(range(n))
    granularity = 2
    while True:
        assert granularity >= 2
        stride = len(candidates) // granularity
        assert 0 <= stride <= len(candidates)
        if stride == 0: return candidates
        assert 0 < stride < len(candidates)
        for i in range(0, len(candidates), stride):
            copy = list(candidates)
            del copy[i:i+stride]
            if evaluate(copy):
                assert 0 < len(copy) < len(candidates)
                candidates = copy
                granularity = max(2, granularity - 1)
                break
        else:
            assert granularity <= len(candidates) or len(candidates) == 1
            granularity *= 2

@precondition(lambda n, indices: all(0 <= i < n for i in indices))
@postcondition(lambda ret, n, idxs: set(ret) | set(idxs) == set(range(n)) and len(set(ret) & set(idxs)) == 0)
def complement(n, indices):
    index_set = set(indices)
    complement = [i for i in range(n) if i not in index_set]
    return complement

@precondition(lambda n, evaluate: n > 0)
def ddmax(n, evaluate):
    return ddmin(n, lambda indices: evaluate(complement(n, indices)))
