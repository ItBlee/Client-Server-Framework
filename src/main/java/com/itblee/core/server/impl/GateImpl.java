package com.itblee.core.server.impl;

import com.itblee.core.server.Gate;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerException;
import java.rmi.server.ServerNotActiveException;

public class GateImpl implements Gate {

    protected final int port;
    protected ServerSocket serverSocket;

    public GateImpl(int port) {
        this.port = port;
    }

    @Override
    public void open() throws IOException {
        if (isOpened())
            throw new ServerException("Already opened Server !");
        serverSocket = new ServerSocket(port);
    }

    @Override
    public Socket accept() throws ServerNotActiveException, IOException {
        if (serverSocket == null)
            throw new ServerNotActiveException();
        return serverSocket.accept();
    }

    @Override
    public boolean isOpened() {
        return serverSocket != null;
    }

    @Override
    public boolean isClosed() {
        if (serverSocket != null)
            return serverSocket.isClosed();
        return false;
    }

    @Override
    public void close() throws IOException {
        if (serverSocket != null)
            serverSocket.close();
    }

}
