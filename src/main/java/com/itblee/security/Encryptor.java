package com.itblee.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public final class Encryptor {

    public static final String ALGORITHM = "AES";

    public static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5PADDING";

    public Encryptor() {
        throw new AssertionError();
    }

    public static String generateSecretKey() {
        String secretKey;
        do {
            try {
                String keyId = UUID.randomUUID().toString();
                MessageDigest salt = MessageDigest.getInstance(HashApplier.ALGORITHM);
                salt.update(keyId.getBytes(StandardCharsets.UTF_8));
                secretKey = Long.toHexString(ByteBuffer.wrap(salt.digest()).getLong());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } while (secretKey.length() != HashApplier.BIT_LENGTH);
        return secretKey;
    }

    public static String encrypt(String plaintext, String secretKey) {
        try {
            SecretKeySpec sKeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
            byte[] byteEncrypted = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(byteEncrypted);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String ciphertext, String secretKey) throws Exception {
        byte[] byteEncrypted = Base64.getDecoder().decode(ciphertext);
        SecretKeySpec sKeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
        byte[] byteDecrypted = cipher.doFinal(byteEncrypted);
        return new String(byteDecrypted);
    }

}
