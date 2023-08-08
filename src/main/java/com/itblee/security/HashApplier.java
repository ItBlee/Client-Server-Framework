package com.itblee.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class HashApplier {

    public static final String ALGORITHM = "SHA-256";

    public static final int BIT_LENGTH = 16;

    public HashApplier() {
        throw new AssertionError();
    }

    public static String getSalt() {
        Random random = new SecureRandom();
        byte[] salt = new byte[BIT_LENGTH];
        random.nextBytes(salt);
        return new String(salt);
    }

    public static String applySha256(String source, String salt) {
        String hashString;
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] bytes = md.digest(source.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes)
                sb.append(Integer.toString((aByte & 0xff) + 0x100, BIT_LENGTH).substring(1));
            hashString = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return hashString;
    }

}
