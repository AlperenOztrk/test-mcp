package fin.kk.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for chat responses from the AI agent
 */
public class ChatResponse {
    
    @JsonProperty("finalResponse")
    private String finalResponse;
    
    @JsonProperty("sessionId")
    private String sessionId;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("toolsUsed")
    private List<String> toolsUsed;
    
    @JsonProperty("conversationContext")
    private Object conversationContext;
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("processingTimeMs")
    private Long processingTimeMs;
    
    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }
    
    public ChatResponse(String finalResponse, String sessionId) {
        this();
        this.finalResponse = finalResponse;
        this.sessionId = sessionId;
    }
    
    // Getters and setters
    public String getFinalResponse() {
        return finalResponse;
    }
    
    public void setFinalResponse(String finalResponse) {
        this.finalResponse = finalResponse;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<String> getToolsUsed() {
        return toolsUsed;
    }
    
    public void setToolsUsed(List<String> toolsUsed) {
        this.toolsUsed = toolsUsed;
    }
    
    public Object getConversationContext() {
        return conversationContext;
    }
    
    public void setConversationContext(Object conversationContext) {
        this.conversationContext = conversationContext;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
        this.success = false;
    }
    
    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
} 