package fin.kk.mcp.controller;

import fin.kk.mcp.dto.ChatMessage;
import fin.kk.mcp.dto.ChatResponse;
import fin.kk.mcp.dto.ConversationContext;
import fin.kk.mcp.service.IterativeAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Controller for the iterative agentic AI customer service
 * This provides truly agentic behavior with iterative problem solving
 */
@RestController
@RequestMapping("/api/iterative-agent")
@CrossOrigin(origins = "*")
public class IterativeAgentController {

    private static final Logger logger = LoggerFactory.getLogger(IterativeAgentController.class);

    @Autowired
    private IterativeAgentService iterativeAgentService;

    /**
     * Main chat endpoint for iterative agent
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatMessage message) {
        try {
            logger.info("Received iterative agent chat message: {} (session: {})", 
                       message.getMessage(), message.getSessionId());
            
            // Validate input
            if (message.getMessage() == null || message.getMessage().trim().isEmpty()) {
                ChatResponse errorResponse = new ChatResponse();
                errorResponse.setSessionId(message.getSessionId());
                errorResponse.setError("Message cannot be empty");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (message.getSessionId() == null || message.getSessionId().trim().isEmpty()) {
                ChatResponse errorResponse = new ChatResponse();
                errorResponse.setError("Session ID is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Process with iterative agent
            ChatResponse response = iterativeAgentService.processMessage(message);
            
            logger.info("Iterative agent response generated in {}ms", response.getProcessingTimeMs());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error in iterative agent chat endpoint: {}", e.getMessage(), e);
            
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setSessionId(message.getSessionId());
            errorResponse.setError("Internal server error occurred while processing your request");
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get conversation history for a session
     */
    @GetMapping("/conversation/{sessionId}")
    public ResponseEntity<ConversationContext> getConversation(@PathVariable String sessionId) {
        try {
            ConversationContext context = iterativeAgentService.getConversation(sessionId);
            
            if (context == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(context);
            
        } catch (Exception e) {
            logger.error("Error retrieving conversation {}: {}", sessionId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Clear conversation history for a session
     */
    @DeleteMapping("/conversation/{sessionId}")
    public ResponseEntity<Map<String, String>> clearConversation(@PathVariable String sessionId) {
        try {
            iterativeAgentService.clearConversation(sessionId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Conversation cleared successfully");
            response.put("sessionId", sessionId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error clearing conversation {}: {}", sessionId, e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to clear conversation");
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get all active sessions (for debugging/monitoring)
     */
    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getActiveSessions() {
        try {
            Set<String> sessions = iterativeAgentService.getActiveSessions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("activeSessions", sessions);
            response.put("count", sessions.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving active sessions: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve active sessions");
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "iterative-agent");
        response.put("description", "Agentic AI customer service with iterative problem solving");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get agent capabilities and information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getAgentInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("name", "Emma - Iterative AI Agent");
        info.put("description", "Intelligent customer service agent with iterative problem-solving capabilities");
        info.put("capabilities", java.util.List.of(
            "Iterative problem solving",
            "Autonomous decision making", 
            "Multilingual communication",
            "Context building and memory",
            "Tool chaining and orchestration",
            "Persistent problem resolution"
        ));
        
        info.put("availableTools", java.util.List.of(
            "getCards", "getCard", "getBonuses", "submitApplication",
            "getCustomerApplications", "getApplicationStatus", 
            "cancelApplication", "estimateApprovalTime", "getApplicationsNeedingAttention"
        ));
        
        info.put("maxIterations", 10);
        info.put("responseFormat", "ReAct (Reasoning + Acting)");
        
        return ResponseEntity.ok(info);
    }

    /**
     * Get agent reasoning log for a session (Think/Action/Tool calls)
     */
    @GetMapping(value = "/logs/{sessionId}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getAgentLog(@PathVariable String sessionId) {
        try {
            String log = iterativeAgentService.getSessionLog(sessionId);
            if (log == null || log.isEmpty()) {
                return ResponseEntity.ok("No log for this session yet.");
            }
            return ResponseEntity.ok(log);
        } catch (Exception e) {
            logger.error("Error retrieving agent log for session {}: {}", sessionId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to retrieve agent log.");
        }
    }
} 