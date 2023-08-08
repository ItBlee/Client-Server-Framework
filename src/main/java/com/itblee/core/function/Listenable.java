package com.itblee.core.function;

@FunctionalInterface
public interface Listenable {
    void listen() throws InterruptedException;
}
