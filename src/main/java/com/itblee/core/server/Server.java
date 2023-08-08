package com.itblee.core.server;

import com.itblee.core.Controller;
import com.itblee.core.server.util.ServerPropertyUtil;
import com.itblee.core.server.impl.DispatcherImpl;
import com.itblee.core.server.impl.GateImpl;
import com.itblee.core.server.impl.SecureGate;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

public class Server {

    private static Server server;

    private final int port;
    private Gate gate;
    private Dispatcher dispatcher;
    private Controller controller;

    private final int sslPort;
    private Gate sslGate;
    private Dispatcher sslDispatcher;
    private Controller sslController;

    private ServerService service;

    public Server(int port, int sslPort) {
        this.port = port;
        this.sslPort = sslPort;
        service = new ServerService(new SessionManager());
    }

    public static synchronized Server init(int port, int sslPort) {
        if (server != null)
            throw new IllegalStateException("Already initialized server !");
        server = new Server(port, sslPort);
        server.setController(RequestMapping.CONNECT);
        server.setSslController(RequestMapping.SSL);
        return server;
    }

    public static synchronized Server getInstance() {
        if (server == null)
            throw new IllegalStateException("You have to call init first");
        return server;
    }

    public void launch() throws IOException {
        gate = new GateImpl(port);
        gate.open();

        dispatcher = new DispatcherImpl(gate);
        dispatcher.setController(controller);
        dispatcher.setTimeout(ServerPropertyUtil.getInt("socket.timeout"));
        dispatcher.start();

        String keyStorePassword = ServerPropertyUtil.getString("hash");
        byte[] decodedBytes = Base64.getDecoder().decode(keyStorePassword);
        sslGate = new SecureGate(sslPort, new String(decodedBytes));
        sslGate.open();

        sslDispatcher = new DispatcherImpl(sslGate);
        sslDispatcher.setController(sslController);
        sslDispatcher.setTimeout(ServerPropertyUtil.getInt("socket.ssl.timeout"));
        sslDispatcher.start();

        getSessionManager().start();
    }

    public void shutdown() throws IOException {
        if (server == null)
            throw new IllegalStateException("You have to call init first");
        if (dispatcher != null && dispatcher.isAlive())
            dispatcher.interrupt();
        if (sslDispatcher != null && sslDispatcher.isAlive())
            sslDispatcher.interrupt();
        if (gate != null)
            gate.close();
        if (sslGate != null)
            sslGate.close();
    }

    public void setService(ServerService service) {
        Objects.requireNonNull(service);
        this.service = service;
    }

    public ServerService getService() {
        return service;
    }

    public SessionManager getSessionManager() {
        return service.getSessionManager();
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

}
