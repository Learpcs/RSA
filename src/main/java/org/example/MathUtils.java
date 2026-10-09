package org.example;

import java.util.Random;

public class MathUtils {

    private static final int UPPER_BOUND = 100;
    private static final Random random = new Random();

    public static boolean isPrime(int n) {
        if (n == 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }
        if (n % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static int getNextPrime(int n) {
        while (true) {
            ++n;
            if (isPrime(n)) {
                return n;
            }
        }
    }

    public static int getFirstPrime() {
        return getNextPrime(getRandomNumber());
    }

    private static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private static long lcm(long a, long b) {
        return a / gcd(a, b) * (long)b;
    }

    public static long CarmichaelsTotientFunction(long a, long b) {
        return lcm(a - 1, b - 1);
    }

    public static long getRandomCoprime(long n) {
        long pick = getRandomNumber();
        while (gcd(pick, n) > 1 && pick > 1 && pick < n) {
            pick = getRandomNumber();
        }
        return pick;
    }

    public static long phi(long n) {
        long result = 1;
        for (int i = 2; (long)i * i <= n; ++i) {
            if (n % i == 0) {
                int p = 1;
                n /= i;
                while (n % i == 0) {
                    n /= i;
                    p *= i;
                }
                result *= (long) p * (i - 1);
            }
        }
        if (n > 1) {
            result *= n - 1;
        }
        return result;
    }

    public static long add(long a, long b, long mod) {
        return (a + b) % mod;
    }

    public static long mul(long a, long b, long mod) {
        return (a * b) % mod;
    }

    public static long binpow(long a, long degree, long mod) {
        long result = 1;
        while (degree > 0) {
            if (degree % 2 == 1) {
                result = mul(result, a, mod);
            }
            a = mul(a, a, mod);
            degree /= 2;
        }
        return result;
    }

    private static int getRandomNumber() {
        return random.nextInt(UPPER_BOUND);
    }
}
