package com.example.gym.chat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * CDI Event observer that handles chat messages and manages user sessions.
 * This component observes ChatMessage events and delegates to ChatBroadcaster.
 */
@ApplicationScoped
public class ChatEventObserver {

    private static final Logger LOGGER = Logger.getLogger(ChatEventObserver.class.getName());

    // Stores mapping from username to ChatBroadcaster callback
    private final Map<String, MessageSender> userSenders = new ConcurrentHashMap<>();
    
    // Functional interface for sending messages
    @FunctionalInterface
    public interface MessageSender {
        void send(String message);
    }

    /**
     * Registers a user's message sender callback.
     */
    public void registerUser(String username, MessageSender sender) {
        userSenders.put(username, sender);
        LOGGER.info(() -> String.format("[OBSERVER] User registered: %s (total: %d)", username, userSenders.size()));
    }

    /**
     * Unregisters a user.
     */
    public void unregisterUser(String username) {
        userSenders.remove(username);
        LOGGER.info(() -> String.format("[OBSERVER] User unregistered: %s (total: %d)", username, userSenders.size()));
    }

    /**
     * Returns set of currently connected usernames.
     */
    public Set<String> getConnectedUsers() {
        return Collections.unmodifiableSet(userSenders.keySet());
    }

    /**
     * Returns the number of connected users.
     */
    public int getOnlineCount() {
        return userSenders.size();
    }

    /**
     * CDI Event observer method - called when ChatMessage event is fired.
     */
    public void onChatMessage(@Observes ChatMessage message) {
        LOGGER.info(() -> String.format("[OBSERVER] Received %s message from %s", message.getType(), message.getSender()));

        switch (message.getType()) {
            case PRIVATE:
                handlePrivateMessage(message);
                break;
            case BROADCAST:
            case SYSTEM:
            default:
                handleBroadcastMessage(message);
                break;
        }
    }

    /**
     * Handles broadcast messages - sends to all connected users.
     */
    private void handleBroadcastMessage(ChatMessage message) {
        String formattedMessage = message.toJson();
        
        for (Map.Entry<String, MessageSender> entry : userSenders.entrySet()) {
            try {
                entry.getValue().send(formattedMessage);
            } catch (Exception e) {
                LOGGER.warning(() -> String.format("[OBSERVER] Failed to send to %s: %s", entry.getKey(), e.getMessage()));
            }
        }
    }

    /**
     * Handles private messages - sends only to sender and recipient.
     */
    private void handlePrivateMessage(ChatMessage message) {
        String formattedMessage = message.toJson();
        String sender = message.getSender();
        String recipient = message.getRecipient();

        // Send to recipient
        MessageSender recipientSender = userSenders.get(recipient);
        if (recipientSender != null) {
            try {
                recipientSender.send(formattedMessage);
            } catch (Exception e) {
                LOGGER.warning(() -> String.format("[OBSERVER] Failed to send private msg to %s: %s", recipient, e.getMessage()));
            }
        } else {
            LOGGER.warning(() -> String.format("[OBSERVER] Recipient %s not found online", recipient));
        }

        // Send copy to sender (so they see their own message)
        MessageSender senderSender = userSenders.get(sender);
        if (senderSender != null && !sender.equals(recipient)) {
            try {
                senderSender.send(formattedMessage);
            } catch (Exception e) {
                LOGGER.warning(() -> String.format("[OBSERVER] Failed to send private msg copy to sender %s: %s", sender, e.getMessage()));
            }
        }
    }
}
