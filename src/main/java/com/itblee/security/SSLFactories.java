package com.itblee.security;

import com.itblee.core.server.util.ServerPropertyUtil;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Base64;

public class SSLFactories {

    public static void setKeyStore(InputStream keyStream, String keyStorePassword) throws Exception {
        // Get keyStore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // if your store is password protected then declare it (it can be null however)
        char[] keyPassword = keyStorePassword.toCharArray();

        // load the stream to your store
        keyStore.load(keyStream, keyPassword);

        // initialize a key manager factory with the key store
        KeyManagerFactory keyFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(keyStore, keyPassword);

        // get the key managers from the factory
        KeyManager[] keyManagers = keyFactory.getKeyManagers();

        // initialize an ssl context to use these managers and set as default
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(keyManagers, null, null);
        SSLContext.setDefault(sslContext);

        checkKey(keyStore, keyPassword);
    }

    public static void setTrustStore(InputStream trustStream, String trustStorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // if your store is password protected then declare it (it can be null however)
        char[] trustPassword = trustStorePassword.toCharArray();

        // load the stream to your store
        trustStore.load(trustStream, trustPassword);

        // initialize a trust manager factory with the trusted store
        TrustManagerFactory trustFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trustStore);

        // get the trust managers from the factory
        TrustManager[] trustManagers = trustFactory.getTrustManagers();

        // initialize an ssl context to use these managers and set as default
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, null);
        SSLContext.setDefault(sslContext);
    }

    private static void checkKey(KeyStore ks, char[] keyPassword) {
        try {
            /*KeyStore ks = KeyStore.getInstance("jks");
            ks.load(keyStream, keyStorePassword.toCharArray());*/
            String keyStoreAlias = ServerPropertyUtil.getString("jsse.keystore.alias");
            Key key = ks.getKey(keyStoreAlias, keyPassword);
            Certificate cert = ks.getCertificate(keyStoreAlias);
            System.out.println("--- Certificate START ---");
            System.out.println(cert);
            System.out.println("--- Certificate END ---\n");
            System.out.println("Public key: " + Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded()));
            System.out.println("Private key: " + Base64.getEncoder().encodeToString(key.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
