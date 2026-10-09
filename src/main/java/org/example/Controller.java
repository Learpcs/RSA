package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@ShellComponent
@RequiredArgsConstructor
public class Controller {

    private final Service service;

    @ShellMethod(key = "generate", value = "Generates secret keys")
    public void generateRSAKeys(String fileName) throws IOException {
        long[] keys = service.generateRSAKeys(); //n, e, d
        ByteBuffer publicBuffer = ByteBuffer.allocate(16);
        publicBuffer.putLong(keys[0]);
        publicBuffer.putLong(keys[1]);
        ByteBuffer privateBuffer = ByteBuffer.allocate(16);
        privateBuffer.putLong(keys[0]);
        privateBuffer.putLong(keys[2]);
        Files.write(Paths.get(fileName + ".public"), publicBuffer.array(), StandardOpenOption.CREATE);
        Files.write(Paths.get(fileName + ".private"), privateBuffer.array(), StandardOpenOption.CREATE);
    }

    @ShellMethod(key = "read", value = "Generates secret keys")
    public void readRSAKeys(String fileName) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(fileName));
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        long n = buf.getLong();
        long s = buf.getLong();
        System.out.println("n: " + n + "\n" + (fileName.endsWith(".private") ? "d: " : "e: ") + s);

    }

    @ShellMethod(key = "encrypt", value = "Encrypts selected file with selected public key")
    public void encryptFile(String fileName, String publicKeyFileName) throws IOException {
        service.encryptFile(fileName, publicKeyFileName);
    }

    @ShellMethod(key = "decrypt", value = "Decrypts selected file with selected private key")
    public void decryptFile(String fileName, String privateKeyFileName) throws IOException {
        service.decryptFile(fileName, privateKeyFileName);
    }


}
