package com.itblee.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

public interface Worker extends Runnable, Listener, Closeable {
    void start();
    void send(String message) throws IOException;
    String receive() throws IOException;
    void close() throws IOException;
    void setSoTimeout(int timeout) throws SocketException;

    void setController(Controller controller);
    void setUid(UUID uid);
    UUID getUid();
    Socket getSocket();
    String getIp();

    boolean isAlive();
    boolean isInterrupted();
}
