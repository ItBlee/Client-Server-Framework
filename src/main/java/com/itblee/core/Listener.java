package com.itblee.core;

import com.itblee.core.function.Listenable;
import com.itblee.transfer.Packet;

public interface Listener {
    void complete(Packet result) throws InterruptedException;
    Packet await() throws InterruptedException;
    Packet await(long timeout) throws InterruptedException;
    Packet await(Listenable listenable) throws InterruptedException;
    boolean isListening();
    Packet getResult();
}
