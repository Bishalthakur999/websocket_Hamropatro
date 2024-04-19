package com.example;

import io.micronaut.http.annotation.PathVariable;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;


import java.util.concurrent.ConcurrentHashMap;

@ServerWebSocket("/server1/{username}")
public class MessageServer {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final WebSocketBroadcaster broadcaster;

    public MessageServer(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @OnOpen
    public void onOpen(String username, WebSocketSession session) {
        sessions.put(username, session);
        broadcaster.broadcastSync(username + " joined the chat");
    }

    @OnMessage
    public void onMessage(String message, WebSocketSession session, @PathVariable String username) {
        if (message.startsWith("/createUser")) {
            createUser(message.substring("/createUser".length()).trim(), username);
        } else {
            broadcaster.broadcastSync(username + ": " + message);
        }
    }

    private void createUser(String message, String username) {
        sessions.put(username, null);
        broadcaster.broadcastSync("User '" + username + "' created with message: " + message);
    }

    @OnClose
    public void onClose(String username, WebSocketSession session) {
        sessions.remove(username);
        broadcaster.broadcastSync(username + " left the chat");
    }




}
