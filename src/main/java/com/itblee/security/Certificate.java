package com.itblee.security;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public final class Certificate implements Serializable {
    private UUID uid;
    private String secretKey;

    public Certificate(UUID uuid, String secretKey) {
        this.uid = uuid;
        this.secretKey = secretKey;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certificate)) return false;
        Certificate that = (Certificate) o;
        return getUid().equals(that.getUid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid());
    }
}
