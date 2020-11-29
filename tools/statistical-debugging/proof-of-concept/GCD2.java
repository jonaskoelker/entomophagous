package example;

class GCD2 extends GCD {
    public static void main(String[] argv) {
        TestFramework.run(new GCD2());
    }

    public int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            b = t;
            a = b;
        }
        return iabs(a);
    }
}
