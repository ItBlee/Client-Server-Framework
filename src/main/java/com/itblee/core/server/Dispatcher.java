package com.itblee.core.server;

import com.itblee.core.Controller;

public interface Dispatcher extends Runnable {
    void start();
    void setController(Controller controller);
    void setTimeout(int timeout);
    void interrupt();
    boolean isAlive();
    boolean isInterrupted();
}
