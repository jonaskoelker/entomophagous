#!/usr/bin/env python3
"""Use ddmin to simplify a patch file down to its relevant hunks, as
suggested in section 13.7 of Why Programs Fail.  Running this is a
good follow-up to a `git bisect` which spits out a large commit.

This requires `filterdiff` to be installed, which on Debian and Ubuntu
is found in the `patchutils` package.

"""

import dd, functools, subprocess, sys

def main(name_of_patch_file):
    with open(name_of_patch_file) as patchfile:
        n = sum(1 for line in patchfile if line.startswith('@@ '))
    print("Found %d hunks in %s" % (n, name_of_patch_file))
    judge = functools.partial(adjudicate, name_of_patch_file, n)
    hunk_indices = dd.ddmin(n, judge)
    hunks = ','.join([str(i) for i in hunk_indices])
    cmd = "filterdiff '%s' --hunks %s" % (name_of_patch_file, hunks)
    print("Generate the smallest sets of buggy hunks with this command:")
    print(cmd)
    subprocess.run(cmd, shell = True)

def adjudicate(patch, n, hunk_indices):
    hunks = ','.join([str(i) for i in hunk_indices])
    cmd = "filterdiff '%s' --hunks %s | patch -p0" % (patch, hunks)
    subprocess.run(cmd, shell = True)

    print("Applied hunks %s" % hunks)
    yn = input("Does this set have the bug [yn]? ")
    while yn not in 'yn':
        yn = input("Invalid response: '%s'; bug [yn]? " % yn)

    cmd = "filterdiff '%s' --hunks %s | patch -Rp0" % (patch, hunks)
    subprocess.run(cmd, shell = True)

    return yn == 'y'

if __name__ == '__main__':
    try: path_to_patch_file = sys.argv[1]
    except: sys.exit("Usage: refine-diff.py <PATCH-FILE>")
    main(path_to_patch_file)
