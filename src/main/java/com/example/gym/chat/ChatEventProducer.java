package com.example.gym.chat;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;

/**
 * CDI Event producer for chat messages.
 * Fires CDI events when messages are received, allowing observers to react.
 */
@ApplicationScoped
public class ChatEventProducer {

    private static final Logger LOGGER = Logger.getLogger(ChatEventProducer.class.getName());

    @Inject
    private Event<ChatMessage> chatMessageEvent;

    /**
     * Fires a CDI event for a broadcast message.
     */
    public void fireBroadcastMessage(String sender, String content) {
        ChatMessage message = ChatMessage.broadcastMessage(sender, content);
        LOGGER.info(() -> String.format("[CDI EVENT] Firing broadcast message from %s", sender));
        chatMessageEvent.fire(message);
    }

    /**
     * Fires a CDI event for a private message.
     */
    public void firePrivateMessage(String sender, String content, String recipient) {
        ChatMessage message = ChatMessage.privateMessage(sender, content, recipient);
        LOGGER.info(() -> String.format("[CDI EVENT] Firing private message from %s to %s", sender, recipient));
        chatMessageEvent.fire(message);
    }

    /**
     * Fires a CDI event for a system message.
     */
    public void fireSystemMessage(String content) {
        ChatMessage message = ChatMessage.systemMessage(content);
        LOGGER.info(() -> String.format("[CDI EVENT] Firing system message: %s", content));
        chatMessageEvent.fire(message);
    }

    /**
     * Fires a generic chat message event.
     */
    public void fireMessage(ChatMessage message) {
        LOGGER.info(() -> String.format("[CDI EVENT] Firing %s message from %s", message.getType(), message.getSender()));
        chatMessageEvent.fire(message);
    }
}
