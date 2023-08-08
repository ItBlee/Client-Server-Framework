package com.itblee.core.client;

import com.itblee.core.Controller;
import com.itblee.core.client.Impl.SecureWorker;
import com.itblee.transfer.DefaultDataKey;
import com.itblee.transfer.DefaultRequest;
import com.itblee.transfer.DefaultStatusCode;

public final class RequestMapping {

    static final Controller SESSION = new Controller() {{
        Client client = Client.getInstance();

        map(DefaultRequest.SESSION, (worker, data) -> {
            switch (data.getCode(DefaultStatusCode.class)) {
                case TIMEOUT:
                    client.getService().verify();
                case CONFLICT:
                case FORBIDDEN:
                    client.getService().verify();
                    ((SecureWorker) worker).resend();
                    break;
            }
        });

        map(DefaultRequest.SESSION_KEY, (worker, data) -> {
            if (data.is(DefaultStatusCode.BAD_REQUEST)) {
                client.getSession()
                        .getCertificate()
                        .setSecretKey(null);
                client.getService().verify();
                ((SecureWorker) worker).resend();
            }
        });

    }};

    static final Controller SSL = new Controller() {{
        Client client = Client.getInstance();

        map(DefaultRequest.SESSION, (worker, data) -> {
            switch (data.getCode(DefaultStatusCode.class)) {
                case CREATED:
                    String uid = data.get(DefaultDataKey.SESSION_ID);
                    client.getSession().setUid(uid);
                    worker.complete(data);
                    worker.close();
                    break;
                case BAD_REQUEST:
                    worker.complete(data);
                    break;
            }
        });

        map(DefaultRequest.SESSION_KEY, (worker, data) -> {
            switch (data.getCode(DefaultStatusCode.class)) {
                case OK:
                    worker.complete(data);
                    break;
                case BAD_REQUEST:
                    client.getSession()
                            .getCertificate()
                            .setSecretKey(null);
                    break;
            }
        });
    }};

}
