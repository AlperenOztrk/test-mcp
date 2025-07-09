package fin.kk.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO for managing conversation context and history
 */
public class ConversationContext {
    
    @JsonProperty("sessionId")
    private String sessionId;
    
    @JsonProperty("messages")
    private List<ChatMessage> messages;
    
    @JsonProperty("customerContext")
    private Map<String, Object> customerContext;
    
    @JsonProperty("lastActivity")
    private LocalDateTime lastActivity;
    
    @JsonProperty("totalMessages")
    private int totalMessages;
    
    @JsonProperty("toolsUsedInSession")
    private List<String> toolsUsedInSession;
    
    public ConversationContext() {
        this.messages = new ArrayList<>();
        this.customerContext = new HashMap<>();
        this.toolsUsedInSession = new ArrayList<>();
        this.lastActivity = LocalDateTime.now();
        this.totalMessages = 0;
    }
    
    public ConversationContext(String sessionId) {
        this();
        this.sessionId = sessionId;
    }
    
    /**
     * Add a message to the conversation
     */
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        this.totalMessages++;
        this.lastActivity = LocalDateTime.now();
    }
    
    /**
     * Add customer context information
     */
    public void addCustomerContext(String key, Object value) {
        this.customerContext.put(key, value);
    }
    
    /**
     * Record tool usage
     */
    public void recordToolUsage(String toolName) {
        if (!this.toolsUsedInSession.contains(toolName)) {
            this.toolsUsedInSession.add(toolName);
        }
    }
    
    /**
     * Get recent messages (last N messages)
     */
    public List<ChatMessage> getRecentMessages(int count) {
        int size = messages.size();
        int fromIndex = Math.max(0, size - count);
        return messages.subList(fromIndex, size);
    }
    
    // Getters and setters
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public List<ChatMessage> getMessages() {
        return messages;
    }
    
    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
    
    public Map<String, Object> getCustomerContext() {
        return customerContext;
    }
    
    public void setCustomerContext(Map<String, Object> customerContext) {
        this.customerContext = customerContext;
    }
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public int getTotalMessages() {
        return totalMessages;
    }
    
    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }
    
    public List<String> getToolsUsedInSession() {
        return toolsUsedInSession;
    }
    
    public void setToolsUsedInSession(List<String> toolsUsedInSession) {
        this.toolsUsedInSession = toolsUsedInSession;
    }
} 