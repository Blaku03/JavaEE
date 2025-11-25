package com.example.gym.websocket;

import com.example.gym.chat.ChatEventObserver;
import com.example.gym.chat.ChatEventProducer;
import com.example.gym.chat.ChatMessage;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * WebSocket endpoint for real-time chat functionality.
 * Uses CDI Events for message handling - supports broadcast and private messages.
 */
@Dependent
@ServerEndpoint(value = "/chat", configurator = ChatEndpointConfigurator.class)
public class ChatEndpoint {

    private static final Logger LOGGER = Logger.getLogger(ChatEndpoint.class.getName());
    
    // Maps session ID to username
    private static final Map<String, String> sessionUsernames = new ConcurrentHashMap<>();
    // Maps session ID to Session object
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    private ChatEventProducer eventProducer;

    @Inject
    private ChatEventObserver eventObserver;

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        LOGGER.info("New WebSocket connection opened: " + session.getId());
        
        // Send initial user list
        sendUserList(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.info("Message received from " + session.getId() + ": " + message);
        
        try {
            // Parse JSON message: {"type":"...", "username":"...", "content":"...", "recipient":"..."}
            if (message.startsWith("{")) {
                handleJsonMessage(message, session);
            } else {
                // Legacy plain text format: username:message
                handlePlainTextMessage(message, session);
            }
        } catch (Exception e) {
            LOGGER.warning("Error processing message: " + e.getMessage());
        }
    }

    private void handleJsonMessage(String json, Session session) {
        // Simple JSON parsing (without external library)
        String type = extractJsonValue(json, "type");
        String username = extractJsonValue(json, "username");
        String content = extractJsonValue(json, "content");
        String recipient = extractJsonValue(json, "recipient");

        if ("register".equals(type)) {
            // Register user with their username
            registerUser(session, username);
        } else if ("message".equals(type)) {
            // Handle chat message
            if (recipient != null && !recipient.isEmpty() && !"all".equals(recipient)) {
                // Private message
                eventProducer.firePrivateMessage(username, content, recipient);
            } else {
                // Broadcast message
                eventProducer.fireBroadcastMessage(username, content);
            }
        } else if ("userlist".equals(type)) {
            sendUserList(session);
        }
    }

    private void handlePlainTextMessage(String message, Session session) {
        String username = sessionUsernames.getOrDefault(session.getId(), "Anonymous");
        String content = message;
        
        // Legacy format: username:message
        if (message.contains(":")) {
            int colonIndex = message.indexOf(":");
            String possibleUsername = message.substring(0, colonIndex).trim();
            if (!possibleUsername.isEmpty() && possibleUsername.length() < 20) {
                username = possibleUsername;
                content = message.substring(colonIndex + 1).trim();
            }
        }
        
        eventProducer.fireBroadcastMessage(username, content);
    }

    private void registerUser(Session session, String username) {
        String sessionId = session.getId();
        
        // Remove old registration if exists
        String oldUsername = sessionUsernames.get(sessionId);
        if (oldUsername != null) {
            eventObserver.unregisterUser(oldUsername);
        }
        
        // Register new username
        sessionUsernames.put(sessionId, username);
        
        // Register with CDI observer for receiving messages
        eventObserver.registerUser(username, (msg) -> sendToSession(session, msg));
        
        LOGGER.info("User registered: " + username + " (session: " + sessionId + ")");
        
        // Notify all users about the new user
        eventProducer.fireSystemMessage(username + " dołączył(a) do czatu. Online: " + eventObserver.getOnlineCount());
        
        // Send updated user list to all
        broadcastUserList();
    }

    @OnClose
    public void onClose(Session session) {
        String sessionId = session.getId();
        String username = sessionUsernames.remove(sessionId);
        sessions.remove(sessionId);
        
        if (username != null) {
            eventObserver.unregisterUser(username);
            eventProducer.fireSystemMessage(username + " opuścił(a) czat. Online: " + eventObserver.getOnlineCount());
            broadcastUserList();
        }
        
        LOGGER.info("WebSocket connection closed: " + sessionId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.warning("WebSocket error for session " + session.getId() + ": " + throwable.getMessage());
        onClose(session);
    }

    private void sendUserList(Session session) {
        Set<String> users = eventObserver.getConnectedUsers();
        String userListJson = "{\"type\":\"userlist\",\"users\":[" + 
            users.stream().map(u -> "\"" + u + "\"").collect(Collectors.joining(",")) + 
            "]}";
        sendToSession(session, userListJson);
    }

    private void broadcastUserList() {
        Set<String> users = eventObserver.getConnectedUsers();
        String userListJson = "{\"type\":\"userlist\",\"users\":[" + 
            users.stream().map(u -> "\"" + u + "\"").collect(Collectors.joining(",")) + 
            "]}";
        
        for (Session session : sessions.values()) {
            sendToSession(session, userListJson);
        }
    }

    private void sendToSession(Session session, String message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            LOGGER.warning("Error sending message to session " + session.getId() + ": " + e.getMessage());
        }
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }
        return json.substring(startIndex, endIndex);
    }
}
