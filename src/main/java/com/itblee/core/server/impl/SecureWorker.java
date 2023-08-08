package com.itblee.core.server.impl;

import com.itblee.core.AbstractSecureWorker;
import com.itblee.core.server.Server;
import com.itblee.core.server.TransferHelper;
import com.itblee.exception.UnverifiedException;
import com.itblee.security.Session;
import com.itblee.transfer.DefaultStatusCode;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class SecureWorker extends AbstractSecureWorker {

    public SecureWorker(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    protected UUID getSender() {
        return null;
    }

    @Override
    protected String getSecretKey(UUID uuid) {
        try {
            Session session = Server.getInstance().getService().requireSession(this, uuid);
            return session.getSecretKey();
        } catch (UnverifiedException e) {
            return null;
        }
    }

    @Override
    public void send(String message) throws IOException {
        try {
            super.send(message);
        } catch (SSLException e) {
            sendException(message);
            throw e;
        }
    }

    @Override
    public String receive() throws IOException {
        try {
            return super.receive();
        } catch (SSLPeerUnverifiedException e) {
            TransferHelper.call(this).warnSession(DefaultStatusCode.FORBIDDEN);
            throw e;
        } catch (SSLKeyException e) {
            TransferHelper.call(this).warnWrongKey();
            throw e;
        }
    }
}
