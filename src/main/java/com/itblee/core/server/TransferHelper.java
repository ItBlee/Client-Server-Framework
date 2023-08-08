package com.itblee.core.server;

import com.itblee.core.Worker;
import com.itblee.security.Session;
import com.itblee.transfer.DefaultDataKey;
import com.itblee.transfer.DefaultStatusCode;
import com.itblee.transfer.Packet;
import com.itblee.transfer.DefaultRequest;
import com.itblee.utils.JsonParser;
import com.itblee.utils.ObjectUtil;

import java.io.IOException;
import java.util.UUID;

public final class TransferHelper {

    private static final ThreadLocal<Caller> CALLERS = ThreadLocal.withInitial(Caller::new);

    private TransferHelper() {
        throw new AssertionError();
    }

    public static Packet get() {
        return CALLERS.get().getPacket();
    }

    public static Caller call(Worker worker) {
        return CALLERS.get().setTarget(worker);
    }

    public static Caller call(Session session) {
        return CALLERS.get().setTarget(session.getWorker());
    }

    public static Packet responseSession(UUID uuid, DefaultStatusCode code) {
        Packet packet = get();
        packet.setHeader(DefaultRequest.SESSION);
        packet.setCode(code);
        packet.putData(DefaultDataKey.SESSION_ID, uuid);
        return packet;
    }

    public static Packet responseChangeKey(DefaultStatusCode code) {
        Packet packet = get();
        packet.setHeader(DefaultRequest.SESSION_KEY);
        packet.setCode(code);
        return packet;
    }

    public static Packet warnSession(DefaultStatusCode code) {
        Packet packet = get();
        packet.setHeader(DefaultRequest.SESSION);
        packet.setCode(code);
        return packet;
    }

    public static Packet warnWrongKey() {
        Packet packet = get();
        packet.setHeader(DefaultRequest.SESSION_KEY);
        packet.setCode(DefaultStatusCode.BAD_REQUEST);
        return packet;
    }

    public static class Caller {
        private Worker target;
        private final Packet packet;

        public Caller() {
            packet = new Packet();
        }

        private Caller setTarget(Worker target) {
            this.target = ObjectUtil.requireNonNull(target);
            return this;
        }

        private Packet getPacket() {
            return packet.clear();
        }

        public void send(String message) throws IOException {
            target.send(message);
        }

        public void send(Packet packet) {
            try {
                send(JsonParser.toJson(packet));
            } catch (IOException ignored) {}
        }

        public void responseSession(DefaultStatusCode code) {
            send(TransferHelper.responseSession(null, code));
        }

        public void responseSession(UUID uuid, DefaultStatusCode code) {
            send(TransferHelper.responseSession(uuid, code));
        }

        public void responseChangeKey(DefaultStatusCode code) {
            send(TransferHelper.responseChangeKey(code));
        }

        public void warnSession(DefaultStatusCode code) {
            send(TransferHelper.warnSession(code));
        }

        public void warnWrongKey() {
            send(TransferHelper.warnWrongKey());
        }
    }

}
