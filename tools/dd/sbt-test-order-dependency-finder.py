#!/usr/bin/env python3
"""Detect order-dependencies in your sbt tests: this runs your test
suite 100 times (with classes in a random order) or until a failure
happens, whichever happens first.  If a failure occurs, re-run ever
smaller sets of tests until a minimal failing set of test cases is
found.

If you have n test classes t1, t2, ... tn such that t1 succeeds if it
is run before t2 but fails if it is run after t2, and t3..tn always
succeed, this will return [t2, t1].

Try running it in the example subdirectory.

"""

import dd, os, random, sys

def main():
    result = find_minimal_set()
    if result == None:
        sys.exit("No minimal failing set of test cases found")
    for test_case in result: print(test_case)

def find_minimal_set():
    test_cases = extract_test_cases()
    evaluate = partial_evaluate(test_cases)
    all_of_them = list(range(len(test_cases)))
    for _ in range(100):
        random.shuffle(test_cases)
        has_bug = evaluate(all_of_them)
        if not has_bug: continue
        minimal_set = dd.ddmin(len(test_cases), evaluate)
        case_combination = [test_cases[i] for i in minimal_set]
        return case_combination

def partial_evaluate(test_cases):
    def evaluate(indices):
        cases = [test_cases[i] for i in indices]
        command = "sbt 'testOnly %s'" % ' '.join(cases)
        command_result = os.system(command)
        return not not command_result
    return evaluate

def extract_test_cases():
    test_cases = []
    pattern = '[info] * '
    output = os.popen("sbt 'show test:definedTestNames'")
    for line in output:
        if line.startswith(pattern):
            test_cases.append(line[len(pattern):-1])
    return sorted(test_cases)

if __name__ == '__main__': main()
