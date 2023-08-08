package com.itblee.core.client;

import com.itblee.core.Controller;
import com.itblee.core.Worker;
import com.itblee.core.WorkerImpl;
import com.itblee.core.client.Impl.ConnectorImpl;
import com.itblee.core.client.Impl.SecureConnector;
import com.itblee.core.client.Impl.SecureWorker;
import com.itblee.core.client.util.ClientPropertyUtil;
import com.itblee.core.client.util.SessionUtil;
import com.itblee.exception.UnverifiedException;
import com.itblee.security.Certificate;
import com.itblee.security.Session;
import com.itblee.transfer.Packet;
import com.itblee.transfer.DefaultRequest;
import com.itblee.utils.JsonParser;
import com.itblee.utils.StringUtil;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

public class Client {

    private static Client client;

    private ClientService service;

    private Session session;
    private final String ip;

    private final int port;
    private Connector connector;
    private Worker worker;
    private Controller controller;

    private final int sslPort;
    private Connector sslConnector;
    private Worker sslWorker;
    private Controller sslController;

    private Client(String ip, int port, int sslPort) {
        this.ip = ip;
        this.port = port;
        this.sslPort = sslPort;
        service = new ClientService(this);
    }

    public static synchronized Client init(String ip, int port, int sslPort) {
        if (client != null)
            throw new IllegalStateException("Already initialized client !");
        client = new Client(ip, port, sslPort);
        client.loadLocalSession();
        client.setController(RequestMapping.SESSION);
        client.setSslController(RequestMapping.SSL);
        return client;
    }

    public static synchronized Client getInstance() {
        if (client == null)
            throw new IllegalStateException("You have to call init first");
        return client;
    }

    private void loadLocalSession() {
        if (client == null)
            throw new IllegalStateException();
        if (session == null)
            session = new Session();
        session.setCertificate(SessionUtil.load());
        if (session.getCertificate() != null)
            System.out.println("Loaded Session: " + session.getUid() + "|" + session.getSecretKey());
    }

    public Worker getConnection() throws IOException, InterruptedException {
        try {
            return connect();
        } catch (UnverifiedException e) {
            try {
                service.verify();
                return connect();
            } catch (UnverifiedException ignored) {
                throw new IOException();
            }
        }
    }

    public Worker connect() throws IOException, UnverifiedException {
        if(!verified())
            throw new UnverifiedException();
        if (connector != null && worker != null
                && connector.isConnected() && worker.isAlive())
            return worker;
        if (connector != null && worker != null
                && (connector.isClosed() || worker.isInterrupted()))
            return reconnect();
        connector = new ConnectorImpl(ip, port);
        worker = new SecureWorker(connector.connect());
        worker.setController(controller);
        worker.setUid(session.getUid());
        worker.start();
        return worker;
    }

    public Worker reconnect() throws IOException, UnverifiedException {
        if(!verified())
            throw new UnverifiedException();
        if (connector == null)
            throw new IllegalStateException("Client not initialized connection.");
        if (worker != null)
            worker.close();
        worker = new SecureWorker(connector.reconnect());
        worker.setController(controller);
        worker.setUid(session.getUid());
        worker.start();
        return worker;
    }

    public Worker connectSSL() throws IOException {
        if (sslConnector != null && sslWorker != null
                && sslConnector.isConnected() && sslWorker.isAlive())
            return sslWorker;
        if (sslConnector != null && sslWorker != null
                && (sslConnector.isClosed() || sslWorker.isInterrupted()))
            return reconnectSSL();
        String trustStorePwd = ClientPropertyUtil.getString("jsse.truststore.pwd");
        byte[] decodedBytes = Base64.getDecoder().decode(trustStorePwd);
        sslConnector = new SecureConnector(ip, sslPort, new String(decodedBytes));
        SSLSocket sslSocket = (SSLSocket) sslConnector.connect();
        sslWorker = new WorkerImpl(sslSocket);
        sslWorker.setController(sslController);
        sslWorker.start();
        return sslWorker;
    }

    public Worker reconnectSSL() throws IOException {
        if (sslConnector == null)
            throw new IllegalStateException("Client not initialized auth connection.");
        if (sslWorker != null)
            sslWorker.close();
        SSLSocket sslSocket = (SSLSocket) sslConnector.reconnect();
        sslWorker = new WorkerImpl(sslSocket);
        sslWorker.setController(sslController);
        sslWorker.start();
        return sslWorker;
    }

    public void closeConnection() throws IOException {
        if (sslWorker != null && sslWorker.isAlive()) {
            Packet request = new Packet();
            request.setHeader(DefaultRequest.BREAK_CONNECT);
            sslWorker.send(JsonParser.toJson(request));
            sslWorker.close();
            sslConnector = null;
            sslWorker = null;
        }
        if (worker != null && worker.isAlive()) {
            Packet request = new Packet();
            request.setHeader(DefaultRequest.BREAK_CONNECT);
            worker.send(JsonParser.toJson(request));
            worker.close();
            connector = null;
            worker = null;
        }
        if (verified())
            SessionUtil.save();
    }



    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setSslController(Controller sslController) {
        this.sslController = sslController;
    }

    public void addController(Controller controller) {
        if (this.controller == null)
            throw new IllegalStateException();
        this.controller.mapAll(controller.getMethods());
    }

    public void addSslController(Controller sslController) {
        if (this.sslController == null)
            throw new IllegalStateException();
        this.sslController.mapAll(sslController.getMethods());
    }

    public boolean verified() {
        return isValidCertificate(session.getCertificate());
    }

    private boolean isValidCertificate(Certificate certificate) {
        return certificate != null
                && certificate.getUid() != null
                && StringUtil.isNotBlank(certificate.getSecretKey());
    }

    public Connector getConnector() {
        return connector;
    }

    public Worker getWorker() {
        return worker;
    }

    public Session getSession() {
        return session;
    }

    public ClientService getService() {
        return service;
    }

    public void setService(ClientService service) {
        Objects.requireNonNull(service);
        this.service = service;
    }
}
