package com.itblee.core.server;

import com.itblee.core.Worker;
import com.itblee.exception.UnverifiedException;
import com.itblee.security.Certificate;
import com.itblee.security.Session;
import com.itblee.utils.ObjectUtil;
import com.itblee.utils.StringUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class ServerService {

    private final SessionManager sessionManager;

    public ServerService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public final Session requireSession(Worker worker, UUID uuid) throws UnverifiedException {
        Optional<Session> result = sessionManager.get(uuid);
        if (!result.isPresent()) {
            //TransferHelper.call(worker).warnSession(DefaultStatusCode.FORBIDDEN);
            throw new UnverifiedException();
        }
        Session session = result.get();
        if (session.getWorker() != null
                && session.getWorker() != worker
                && session.getWorker().isAlive()) {
            //TransferHelper.call(worker).warnSession(DefaultStatusCode.CONFLICT);
            throw new UnverifiedException();
        }
        worker.setUid(session.getUid());
        session.setWorker(worker);
        session.resetTimer();
        return session;
    }

    public final Session createSession(String secretKey) {
        if (StringUtil.isBlank(secretKey))
            throw new IllegalArgumentException();
        Certificate certificate = new Certificate(UUID.randomUUID(), secretKey);
        Session session = new Session();
        session.setCertificate(certificate);
        Server.getInstance()
                .getSessionManager()
                .add(session);
        session.resetTimer();
        return session;
    }

    public final boolean changeSessionKey(UUID uuid, String secretKey) {
        ObjectUtil.requireNonNull(uuid);
        if (StringUtil.isBlank(secretKey))
            return false;
        Optional<Session> result = sessionManager.get(uuid);
        if (!result.isPresent())
            return false;
        Session session = result.get();
        session.getCertificate().setSecretKey(secretKey);
        return true;
    }

    public void breakConnection(Worker worker) throws IOException {
        worker.close();
    }

    public void removeSession(Session session) {
        Worker worker = session.getWorker();
        if (worker.isAlive()) {
            //TransferHelper.call(worker).warnSession(DefaultStatusCode.TIMEOUT);
            try {
                worker.close();
            } catch (IOException ignored) {}
        }
        sessionManager.remove(session);
    }

    public void renewSessionTimer(Session session) {
        session.resetTimer();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
