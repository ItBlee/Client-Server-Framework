package com.itblee.core.client;

import com.itblee.core.Worker;
import com.itblee.security.Certificate;
import com.itblee.security.Encryptor;
import com.itblee.security.Session;
import com.itblee.transfer.DefaultDataKey;
import com.itblee.transfer.DefaultStatusCode;
import com.itblee.transfer.Packet;
import com.itblee.transfer.DefaultRequest;
import com.itblee.utils.JsonParser;
import com.itblee.utils.StringUtil;

import java.io.IOException;

public class ClientService {

    private final Client client;

    public ClientService(Client client) {
        this.client = client;
    }

    public void verify() throws IOException, InterruptedException {
        Session session = client.getSession();
        Packet resp;
        do {
            Worker worker;
            if (session.getCertificate() != null && session.getUid() != null
                    && StringUtil.isBlank(session.getCertificate().getSecretKey())) {
                worker = changeSecretKey();
            } else worker = requestSession();
            resp = worker.await();
        } while (!resp.is(DefaultStatusCode.CREATED) && !resp.is(DefaultStatusCode.OK));
    }

    public Worker requestSession() throws IOException {
        Session session = client.getSession();
        Worker worker = client.connectSSL();

        Certificate certificate = new Certificate(null, Encryptor.generateSecretKey());
        session.setCertificate(certificate);

        Packet request = new Packet();
        request.setHeader(DefaultRequest.SESSION);
        request.putData(DefaultDataKey.SECRET_KEY, session.getSecretKey());
        worker.send(JsonParser.toJson(request));
        return worker;
    }

    public Worker changeSecretKey() throws IOException {
        Session session = client.getSession();
        if (session.getCertificate() == null || session.getUid() == null)
            throw new IllegalStateException();
        Worker worker = client.connectSSL();
        String newKey = Encryptor.generateSecretKey();
        session.getCertificate().setSecretKey(newKey);
        Packet request = new Packet();
        request.setHeader(DefaultRequest.SESSION_KEY);
        request.putData(DefaultDataKey.SESSION_ID, session.getUid());
        request.putData(DefaultDataKey.SECRET_KEY, newKey);
        worker.send(JsonParser.toJson(request));
        return worker;
    }

}
