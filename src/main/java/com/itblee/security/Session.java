package com.itblee.security;

import com.itblee.core.Worker;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Session implements Serializable {
    private Worker worker;
    private Certificate certificate;
    private Long latestAccessTime;

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public UUID getUid() {
        return certificate != null ? certificate.getUid() : null;
    }

    public void setUid(String uid) {
        certificate.setUid(UUID.fromString(uid));
    }

    public void setUid(UUID uid) {
        certificate.setUid(uid);
    }

    public String getSecretKey() {
        return certificate != null ? certificate.getSecretKey() : null;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Long getLatestAccessTime() {
        return latestAccessTime;
    }

    public void resetTimer() {
        latestAccessTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;
        Session that = (Session) o;
        return getCertificate().equals(that.getCertificate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCertificate());
    }
}
