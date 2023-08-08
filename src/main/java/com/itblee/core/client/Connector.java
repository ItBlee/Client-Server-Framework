package com.itblee.core.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public interface Connector extends Cloneable, Closeable {
    Socket connect() throws IOException;

    Socket reconnect() throws IOException;

    boolean isClosed();

    boolean isConnected();

    void setTimeout(int timeout);
}
