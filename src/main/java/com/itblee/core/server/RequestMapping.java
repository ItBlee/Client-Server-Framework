package com.itblee.core.server;

import com.itblee.core.Controller;
import com.itblee.security.Session;
import com.itblee.transfer.DefaultDataKey;
import com.itblee.transfer.DefaultRequest;
import com.itblee.transfer.DefaultStatusCode;

import java.util.UUID;

public final class RequestMapping {

    static final Controller CONNECT = new Controller() {{

        map(DefaultRequest.BREAK_CONNECT, (worker, data) -> Server.getInstance()
                .getService()
                .breakConnection(worker));

    }};

    static final Controller SSL = new Controller() {{

        map(DefaultRequest.BREAK_CONNECT, (worker, data) -> worker.close());

        map(DefaultRequest.SESSION, (worker, data) -> {
            String secretKey = data.get(DefaultDataKey.SECRET_KEY);
            try {
                Session session = Server.getInstance().getService().createSession(secretKey);
                TransferHelper.call(worker).responseSession(session.getUid(), DefaultStatusCode.CREATED);
                worker.close();
            } catch (IllegalArgumentException e) {
                TransferHelper.call(worker).responseSession(DefaultStatusCode.BAD_REQUEST);
            }
        });

        map(DefaultRequest.SESSION_KEY, (worker, data) -> {
            String stringId = data.get(DefaultDataKey.SESSION_ID);
            String secretKey = data.get(DefaultDataKey.SECRET_KEY);
            try {
                UUID uid = UUID.fromString(stringId);
                if (Server.getInstance().getService().changeSessionKey(uid, secretKey)) {
                    TransferHelper.call(worker).responseChangeKey(DefaultStatusCode.OK);
                    worker.close();
                } else throw new IllegalStateException();
            } catch (Exception exception) {
                TransferHelper.call(worker).responseSession(DefaultStatusCode.BAD_REQUEST);
            }

        });

    }};

}
