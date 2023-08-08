package com.itblee.core.server.impl;

import com.itblee.core.server.Gate;
import com.itblee.core.server.util.ServerPropertyUtil;
import com.itblee.core.server.constant.ServerConstant;
import com.itblee.security.SSLFactories;
import org.openeuler.com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Base64;

public class SecureGate extends GateImpl implements Gate {

    public static final boolean SSL_DEBUG_ENABLE = false;

    private final String keyStorePassword;

    private static final class InstanceHolder {
        static final ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
    }

    public SecureGate(int port, String password) {
        super(port);
        this.keyStorePassword = password;
    }

    @Override
    public void open() throws IOException {
        if (isOpened())
            throw new ServerException("Already opened Server !");
        try {
            addProvider();
        } catch (Exception e) {
            throw new SecurityException("Fail to apply SSL: ", e);
        }
        serverSocket = InstanceHolder.serverSocketFactory.createServerSocket(port);
    }

    private void addProvider() throws Exception {
        java.security.Security.addProvider(new Provider());
        String keyStorePath = ServerConstant.RESOURCE_PATH + ServerPropertyUtil.getString("jsse.keystore.path");
        InputStream keystoreInput = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(keyStorePath);
        if (keystoreInput == null)
            throw new IllegalStateException("Key store not found !");
        SSLFactories.setKeyStore(keystoreInput, keyStorePassword);
        /*
        String realPath = this.getClass().getClassLoader().getResource(keyStorePath).toString();
        System.setProperty("javax.net.ssl.keyStore", realPath);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);*/
        if (SSL_DEBUG_ENABLE)
            System.setProperty("javax.net.debug","all");
        keystoreInput.close();
    }
}
