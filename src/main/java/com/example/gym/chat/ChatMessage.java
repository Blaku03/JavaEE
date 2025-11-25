package com.example.gym.chat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a chat message with sender, content, and optional recipient for private messages.
 */
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private String sender;
    private String content;
    private String recipient;  // null for broadcast, username for private
    private LocalDateTime timestamp;
    private MessageType type;

    public enum MessageType {
        BROADCAST,   // Message to all users
        PRIVATE,     // Message to specific user
        SYSTEM       // System notification
    }

    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
        this.type = MessageType.BROADCAST;
    }

    public ChatMessage(String sender, String content) {
        this();
        this.sender = sender;
        this.content = content;
    }

    public ChatMessage(String sender, String content, String recipient) {
        this(sender, content);
        this.recipient = recipient;
        this.type = recipient != null && !recipient.isEmpty() ? MessageType.PRIVATE : MessageType.BROADCAST;
    }

    public static ChatMessage systemMessage(String content) {
        ChatMessage msg = new ChatMessage("System", content);
        msg.setType(MessageType.SYSTEM);
        return msg;
    }

    public static ChatMessage broadcastMessage(String sender, String content) {
        return new ChatMessage(sender, content, null);
    }

    public static ChatMessage privateMessage(String sender, String content, String recipient) {
        ChatMessage msg = new ChatMessage(sender, content, recipient);
        msg.setType(MessageType.PRIVATE);
        return msg;
    }

    // Getters and Setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public boolean isPrivate() {
        return type == MessageType.PRIVATE;
    }

    public boolean isSystem() {
        return type == MessageType.SYSTEM;
    }

    /**
     * Formats message for display in chat.
     */
    public String format() {
        String time = timestamp.format(TIME_FORMATTER);
        
        if (type == MessageType.SYSTEM) {
            return String.format("[%s] [SYSTEM] %s", time, content);
        } else if (type == MessageType.PRIVATE) {
            return String.format("[%s] [PRYWATNA] %s -> %s: %s", time, sender, recipient, content);
        } else {
            return String.format("[%s] %s: %s", time, sender, content);
        }
    }

    /**
     * Converts to JSON for WebSocket transmission.
     */
    public String toJson() {
        return String.format(
            "{\"sender\":\"%s\",\"content\":\"%s\",\"recipient\":\"%s\",\"type\":\"%s\",\"time\":\"%s\"}",
            escapeJson(sender),
            escapeJson(content),
            recipient != null ? escapeJson(recipient) : "",
            type.name(),
            timestamp.format(TIME_FORMATTER)
        );
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }

    @Override
    public String toString() {
        return format();
    }
}
