package example;

import java.util.Random;
import java.util.Arrays;

class TestFramework {
    public static final Random random = new Random();

    public static final int runs = 100;

    public static void run(TestCase test) {
        int bad_runs = 0;
        for (int i = 0; i < runs; ++i) {
            if (!test.runTest()) {
                ++bad_runs;
            }
        }
        System.out.println("Bad runs: " + bad_runs + " out of " + runs);
    }
}

interface TestCase {
    boolean runTest();
}

abstract class CatchingTestCase implements TestCase {
    abstract protected boolean run();

    public boolean runTest() {
        try {
            return run();
        } catch (Throwable t) {
            System.err.println("Test case threw " + t);
            return false;
        }
    }
}

class Middle extends CatchingTestCase {
    public static void main(String[] argv) {
        TestFramework.run(new Middle());
    }

    public boolean run() {
        int x = TestFramework.random.nextInt();
        int y = TestFramework.random.nextInt();
        int z = TestFramework.random.nextInt();
        int result = middle(x, y, z);
        return evaluate_middle(result, new int[] { x, y, z });
    }

    public static int middle(int x, int y, int z) {
        int m = z;
        if (y < z) {
            if (x < y)
                m = y;
            else if (x < z)
                m = y;
        } else {
            if (x > y)
                m = y;
            else if (x > z)
                m = x;
        }
        return m;
    }

    public static boolean evaluate_middle(int result, int[] ints) {
        Arrays.sort(ints);
        return result == ints[1];
    }
}

abstract class GCD extends CatchingTestCase {
    public abstract int gcd(int a, int b);

    private static int modulus = Integer.MIN_VALUE;

    public boolean run() {
        int a = TestFramework.random.nextInt() % modulus;
        int b = TestFramework.random.nextInt() % modulus;
        int result = gcd(a, b);
        return evaluate_gcd(result, a, b);
    }

    public boolean evaluate_gcd(int result, int x, int y) {
        // 0 is a special case
        if (x == 0 && y == 0) return result == 0;
        if (result == 0) return false;

        // Note that -Integer.MIN_VALUE == Integer.MIN_VALUE.
        // These are the only inputs for which the result is negative.
        if ((x == 0 || x == Integer.MIN_VALUE) &&
            (y == 0 || y == Integer.MIN_VALUE))
            return result == Integer.MIN_VALUE;
        if (result < 0) return false;

        // In all other cases, gcd should be the largest number which is a
        // divisor of both inputs, except gcd(0, x) = gcd(x, 0) = x.
        // (Every number is a divisor of 0; that's why ^ is an exception.)

        if (x == 0) return result == y || result == -y;
        if (y == 0) return result == x || result == -x;

        // the greatest common divisor is a common divisor
        if (x % result != 0) return false;
        if (y % result != 0) return false;

        // gcd is the *greatest* common divisor.
        // In other words, x and y have no other common divisors.
        // Stated in math: gcd(x/d, y/d) == 1 where d = gcd(x, y).
        if (gcd(x / result, y / result) != 1) return false;

        // Using a buggy gcd to verify itself is slightly fishy.  It may allow
        // an implementation with an error if that error passes all other
        // checks and conveniently masks itself out in this check.

        return true;
    }

    public static int iabs(int x) {
        return x < 0 ? -x : x;
    }
}

class GCD1 extends GCD {
    public static void main(String[] argv) {
        TestFramework.run(new GCD1());
    }

    public int gcd(int a, int b) {
        int d = b;
        int r = a % b;

        while (r > 0) {
            int n = d;
            d = r;
            r = n / d;
        }

        return d;
    }
}
