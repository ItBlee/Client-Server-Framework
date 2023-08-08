package com.itblee.core;

import com.google.gson.JsonParseException;
import com.itblee.core.Controller;
import com.itblee.core.Worker;
import com.itblee.core.function.Listenable;
import com.itblee.transfer.Packet;
import com.itblee.utils.JsonParser;
import com.itblee.utils.StringUtil;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.UUID;

public class WorkerImpl extends Thread implements Worker {

    private UUID uid;
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private Controller controller;

    private boolean isListening;
    private Packet result;

    public WorkerImpl(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        if (controller == null)
            throw new IllegalStateException("Worker run without any method");
        while (!isInterrupted() && !socket.isClosed()) {
            try {
                String message;
                try {
                    message = receive();
                    if (StringUtil.isBlank(message))
                        continue;
                } catch (SocketTimeoutException e) {
                    close();
                    break;
                }
                Packet data = JsonParser.fromJson(message, Packet.class)
                        .orElseThrow(() -> new JsonParseException("Invalid message type (require JSON) !"));
                controller.resolve(this, data);
            } catch (IOException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void send(String message) throws IOException, SecurityException {
        writer.write(message);
        writer.newLine();
        writer.flush();
        System.out.println("s:" + message);
    }


    @Override
    public String receive() throws IOException, SecurityException {
        String rei = reader.readLine();
        if (StringUtil.isNotBlank(rei))
            System.out.println("r:" + rei);
        return rei;
    }

    @Override
    public void close() throws IOException {
        if (reader != null)
            reader.close();
        if (writer != null)
            writer.close();
        if (socket != null)
            socket.close();
        interrupt();
        System.out.println(uid + " worker closed");
    }

    @Override
    public synchronized void complete(Packet result) throws InterruptedException {
        Objects.requireNonNull(result);
        if (!isListening())
            return;
        this.result = result;
        stopListening();
        notifyAll();
        Thread.sleep(200);
    }

    @Override
    public Packet await() throws InterruptedException {
        return await(0L);
    }

    @Override
    public synchronized Packet await(long timeout) throws InterruptedException {
        return await(() -> {
            long startTime = System.currentTimeMillis();
            while (isListening()) {
                wait(timeout);
                if (timeout > 0L && (System.currentTimeMillis() - timeout) >= startTime) {
                    if (result == null)
                        result = new Packet();
                    break;
                }
            }
        });
    }

    @Override
    public Packet await(Listenable listenable) throws InterruptedException {
        startListening();
        listenable.listen();
        stopListening();
        return getResult();
    }

    @Override
    public boolean isListening() {
        return isListening && !isInterrupted() && !socket.isClosed();
    }

    @Override
    public Packet getResult() {
        Packet temp;
        if (result != null) {
            temp = result.clone();
            result = null;
        } else temp = new Packet();
        return temp;
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public UUID getUid() {
        return uid;
    }

    @Override
    public void setUid(UUID uid) {
        this.uid = uid;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public String getIp() {
        if (socket.isClosed())
            return null;
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public void setSoTimeout(int timeout) throws SocketException {
        if (timeout < 0)
            throw new IllegalArgumentException("Timeout can't be negative !");
        this.socket.setSoTimeout(timeout);
    }

    private void startListening() {
        isListening = true;
    }

    private void stopListening() {
        isListening = false;
    }

}
