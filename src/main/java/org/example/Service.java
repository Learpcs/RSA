package org.example;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class Service {

    long[] generateRSAKeys() {
        int p = MathUtils.getFirstPrime();
        int q = MathUtils.getNextPrime(p);
        long n = (long)p * q;
        long L = MathUtils.CarmichaelsTotientFunction(p, q);
        long e = MathUtils.getRandomCoprime(L);
        long d = MathUtils.binpow(e, MathUtils.phi(L) - 1, L);

        return new long[]{n, e, d};
    }

    private static int blockSize(long n) {
        int k = 0;
        long limit = 1;
        while (limit <= n / 256) {
            limit *= 256;
            k++;
        }
        return Math.max(1, k);
    }

    void encryptFile(String fileName, String publicKeyFileName) throws IOException {
        long[] pub = readKey(publicKeyFileName);   // [n, e]
        long n = pub[0];
        long e = pub[1];

        byte[] plain = Files.readAllBytes(Paths.get(fileName));
        int k = blockSize(n);
        int blocks = (plain.length + k - 1) / k;

        ByteBuffer out = ByteBuffer
                .allocate(8 + 4 + blocks * 8);
        out.putLong(n);
        out.putInt(plain.length);

        for (int b = 0; b < blocks; b++) {
            long m = 0;
            for (int i = 0; i < k; i++) {
                int idx = b * k + i;
                long byteVal = (idx < plain.length) ? (plain[idx] & 0xFFL) : 0L;
                m |= byteVal << (8 * i);
            }
            long c = MathUtils.binpow(m, e, n);
            out.putLong(c);
        }

        Path path = Paths.get(fileName + ".enc");
        Files.write(path, out.array(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    void decryptFile(String fileName, String privateKeyFileName) throws IOException {
        long[] priv = readKey(privateKeyFileName); // [n, d]
        long n = priv[0];
        long d = priv[1];

        byte[] in = Files.readAllBytes(Paths.get(fileName));
        ByteBuffer buf = ByteBuffer.wrap(in);

        long fileN = buf.getLong();
        if (fileN != n) {
            throw new IllegalArgumentException(
                    "Key mismatch: file encrypted with n=" + fileN + ", key has n=" + n);
        }
        int originalLen = buf.getInt();
        int k = blockSize(n);
        int blocks = (originalLen + k - 1) / k;

        byte[] plain = new byte[originalLen];
        for (int b = 0; b < blocks; b++) {
            long c = buf.getLong();
            long m = MathUtils.binpow(c, d, n);
            for (int i = 0; i < k; i++) {
                int idx = b * k + i;
                if (idx < originalLen) {
                    plain[idx] = (byte) ((m >>> (8 * i)) & 0xFF);
                }
            }
        }

        Path path = Paths.get(fileName + ".dec");
        Files.write(path, plain,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    private long[] readKey(String fileName) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(fileName));
        ByteBuffer b = ByteBuffer.wrap(bytes);
        return new long[]{b.getLong(), b.getLong()};
    }

}
