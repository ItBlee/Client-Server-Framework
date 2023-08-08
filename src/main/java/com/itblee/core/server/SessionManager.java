package com.itblee.core.server;

import com.itblee.core.server.util.ServerPropertyUtil;
import com.itblee.security.Session;

import java.util.*;

public class SessionManager extends Thread {

    private final Map<UUID, Session> sessionMap = Collections.synchronizedMap(new HashMap<>());

    private static final long TIMEOUT = ServerPropertyUtil.getInt("session.timeout");

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        while (!interrupted()) {
            try {
                Thread.sleep(minSessionTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            long current = System.currentTimeMillis();
            for (Session session : sessionMap.values()) {
                if (current - session.getLatestAccessTime() >= TIMEOUT) {
                    Server.getInstance().getService().removeSession(session);
                }
            }
        }
    }

    private long minSessionTime() {
        if (sessionMap.isEmpty())
            return TIMEOUT;
        long min = TIMEOUT;
        long currentTime = System.currentTimeMillis();
        for (Session session : sessionMap.values()) {
            long time = currentTime - (session.getLatestAccessTime() + TIMEOUT);
            if (time < 0)
                continue;
            if (time < min)
                min = time;
        }
        return min;
    }

    public void add(Session session) {
        sessionMap.put(session.getUid(), session);
    }

    public Optional<Session> get(UUID sessionId) {
        return Optional.ofNullable(sessionMap.get(sessionId));
    }

    public List<Session> getSessions() {
        return new ArrayList<>(sessionMap.values());
    }

    public boolean contain(UUID sessionId) {
        return sessionMap.containsKey(sessionId);
    }

    public void clear() {
        sessionMap.clear();
    }

    public void remove(UUID sessionId) {
        remove(sessionMap.get(sessionId));
    }

    public void remove(Session session) {
        sessionMap.remove(session.getUid());
    }

}