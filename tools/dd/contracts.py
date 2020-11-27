#!/usr/bin/env python3
"""Export precondition and postcondition decorators.  If __debug__ is
disable they do nothing.  If __debug__ is enabled, they assert that
their arguments return True when invoked on the decorated function's
arguments (and for postcondition, the return value).

Chapter 10 of Why Programs Fail suggest sprinkling your program with
assertions, e.g. data invariants and pre- and postconditions.  These
two functions help with that.

"""

if __debug__:
    def precondition(predicate):
        def decorator(f):
            def decorated(*args, **kwargs):
                assert predicate(*args, **kwargs), (args, kwargs)
                return f(*args, **kwargs)
            return decorated
        return decorator

    def postcondition(predicate):
        def decorator(f):
            if not __debug__: return f
            def decorated(*args, **kwargs):
                result = f(*args, **kwargs)
                assert predicate(result, *args, **kwargs), (result, args, kwargs)
                return result
            return decorated
        return decorator
else:
    precondition  = lambda predicate: lambda f: f
    postcondition = lambda predicate: lambda f: f
