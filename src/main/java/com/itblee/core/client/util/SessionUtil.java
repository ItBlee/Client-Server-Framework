package com.itblee.core.client.util;

import com.itblee.core.client.Client;
import com.itblee.core.client.constant.ClientConstant;
import com.itblee.security.Certificate;
import com.itblee.security.Session;
import com.itblee.utils.DateUtil;

import java.util.*;

public class SessionUtil {

    public static Certificate load() {
        String path = ClientConstant.RESOURCE_PATH + ClientPropertyUtil.getString("session.store.path");
        String sessionInfo = FileUtil.readLineThenDelete(path);
        if (sessionInfo == null)
            return null;
        StringTokenizer tokenizer = new StringTokenizer(sessionInfo, "|");
        try {
            UUID uuid = UUID.fromString(tokenizer.nextToken());
            String secretKey = tokenizer.nextToken();
            Date date = DateUtil.stringToDate(tokenizer.nextToken());
            long timeout = ClientPropertyUtil.getInt("session.timeout");
            if (DateUtil.between(new Date(), date) >= timeout)
                return null;
            return new Certificate(uuid, secretKey);
        } catch (Exception e) {
            return null;
        }
    }

    public static void save() {
        String path = ClientConstant.RESOURCE_PATH + ClientPropertyUtil.getString("session.store.path");
        Session session = Client.getInstance().getSession();
        String line = session.getUid()
                + "|" + session.getSecretKey()
                + "|" + DateUtil.dateToString(new Date());
        FileUtil.write(path, Collections.singleton(line), true);
    }

}
