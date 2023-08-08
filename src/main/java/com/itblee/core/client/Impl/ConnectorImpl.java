package com.itblee.core.client.Impl;

import com.itblee.core.client.Connector;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.channels.NotYetConnectedException;

public class ConnectorImpl implements Connector {

    protected final int port;
    protected final String ip;
    protected Socket socket;
    protected int timeout;

    public ConnectorImpl(String ip, int port) {
            this(ip, port, 0);
    }

    public ConnectorImpl(String ip, int port, int timeout) {
        this.port = port;
        this.ip = ip;
        this.timeout = timeout;
    }

    @Override
    public Socket connect() throws IOException {
        if (socket == null) {
            socket = new Socket(ip, port);
            socket.setSoTimeout(timeout);
        }
        if (isClosed())
            throw new ConnectException();
        return socket;
    }

    @Override
    public Socket reconnect() throws IOException {
        if (socket == null)
            throw new NotYetConnectedException();
        socket.close();
        socket = null;
        socket = connect();
        return socket;
    }

    @Override
    public boolean isClosed() {
        if (socket != null)
            return socket.isClosed();
        return false;
    }

    @Override
    public boolean isConnected() {
        if (socket != null)
            return socket.isConnected();
        return false;
    }

    @Override
    public void close() throws IOException {
        if (socket != null)
            socket.close();
    }

    @Override
    public void setTimeout(int timeout) {
        if (timeout < 0)
            throw new IllegalArgumentException("Timeout can't be negative !");
        this.timeout = timeout;
    }

}
