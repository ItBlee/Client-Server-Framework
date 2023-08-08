package com.itblee.core.function;

import com.itblee.core.Worker;
import com.itblee.transfer.Packet;

import java.io.IOException;

@FunctionalInterface
public interface Executable {
    void execute(Worker worker, Packet data) throws InterruptedException, IOException;
}
