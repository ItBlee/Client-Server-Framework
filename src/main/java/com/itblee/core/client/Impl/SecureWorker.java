package com.itblee.core.client.Impl;

import com.itblee.core.AbstractSecureWorker;
import com.itblee.core.client.Client;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class SecureWorker extends AbstractSecureWorker {

    private String lastMsg;

    public SecureWorker(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    protected UUID getSender() {
        return Client.getInstance().getSession().getUid();
    }

    @Override
    protected String getSecretKey(UUID uuid) {
        return Client.getInstance().getSession().getSecretKey();
    }

    @Override
    public void send(String message) throws IOException {
        lastMsg = message;
        super.send(message);
    }

    public void resend() throws IOException {
        send(lastMsg);
    }

    @Override
    public String receive() throws IOException {
        try {
            return super.receive();
        } catch (SSLException e) {
            return receiveException();
        }
    }
}
