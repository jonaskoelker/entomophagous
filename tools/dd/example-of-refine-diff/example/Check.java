package example;

class Check extends GCD {
    public static void main(String[] argv) {
        System.exit(new Check().run() ? 0 : 1);
    }

    public boolean run() {
        int a = 0;
        int b = Integer.MIN_VALUE;
        int result = gcd(a, b);
        return evaluate_gcd(result, a, b);
    }

    public int gcd(int a, int b) {
        return b == 0 ? iabs(a) : gcd(b, a % b);
    }
}
