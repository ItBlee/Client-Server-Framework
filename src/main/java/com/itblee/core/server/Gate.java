package com.itblee.core.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.rmi.server.ServerNotActiveException;

public interface Gate extends Cloneable, Closeable {
    void open() throws IOException;

    Socket accept() throws ServerNotActiveException, IOException;

    boolean isOpened();

    boolean isClosed();
}
