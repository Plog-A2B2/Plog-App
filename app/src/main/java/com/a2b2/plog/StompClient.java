package com.a2b2.plog;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class StompClient extends WebSocketListener {

    private WebSocket webSocket;

    public void connect(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, this);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        //String connectMessage = "CONNECT\naccept-version:1.2\nhost:your-host\nheart-beat:0,0\n\n\u0000";
        String connectMessage = "CONNECT\naccept-version:1.2\nhost:15.164.152.246\nheart-beat:0,0\n\n\u0000";
        webSocket.send(connectMessage);
        System.out.println("Connected to the server");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("Received: " + text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("Received bytes: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        System.out.println("Closed: " + code + " / " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }

    public void sendMessage(String destination, String message) {
        String stompMessage = "SEND\ndestination:" + destination + "\n\n" + message + "\u0000";
        webSocket.send(stompMessage);
    }

    public void subscribe(String destination) {
        String subscribeMessage = "SUBSCRIBE\ndestination:" + destination + "\nid:sub-0\nack:auto\n\n\u0000";
        webSocket.send(subscribeMessage);
    }
}
