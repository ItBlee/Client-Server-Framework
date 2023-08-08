package com.itblee.core.client.Impl;

import com.itblee.core.client.util.ClientPropertyUtil;
import com.itblee.core.client.Connector;
import com.itblee.core.client.constant.ClientConstant;
import com.itblee.security.SSLFactories;
import org.openeuler.com.sun.net.ssl.internal.ssl.Provider;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;

public class SecureConnector extends ConnectorImpl implements Connector {

    private final String trustStorePassword;

    private static final class InstanceHolder {
        static final SocketFactory socketFactory = SSLSocketFactory.getDefault();
    }

    public SecureConnector(String ip, int port, String password) {
        this(ip, port, password, ClientPropertyUtil.getInt("socket.ssl.timeout"));
    }

    public SecureConnector(String ip, int port, String password, int timeout) {
        super(ip, port, timeout);
        this.trustStorePassword = password;
    }

    @Override
    public SSLSocket connect() throws IOException {
        if (socket == null) {
            try {
                addProvider();
            } catch (Exception e) {
                throw new SecurityException("Fail to apply SSL: ", e);
            }
            socket = InstanceHolder.socketFactory.createSocket(ip, port);
            socket.setSoTimeout(timeout);
        }
        if (isClosed())
            throw new ConnectException();
        return (SSLSocket) socket;
    }

    private void addProvider() throws Exception {
        java.security.Security.addProvider(new Provider());
        String trustStorePath = ClientConstant.RESOURCE_PATH + ClientPropertyUtil.getString("jsse.truststore.path");
        InputStream truststoreInput = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(trustStorePath);
        if (truststoreInput == null)
            throw new IllegalStateException("Trust store not found !");
        SSLFactories.setTrustStore(truststoreInput, trustStorePassword);
        truststoreInput.close();

        /*String realPath = this.getClass().getClassLoader().getResource(trustStorePath).toString();
        System.setProperty("javax.net.ssl.trustStore", realPath);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);*/
        //System.setProperty("javax.net.debug","all");
    }

}
