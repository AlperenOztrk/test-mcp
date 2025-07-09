package fin.kk.mcp.controller;

import fin.kk.mcp.dto.ChatMessage;
import fin.kk.mcp.dto.ChatResponse;
import fin.kk.mcp.dto.ConversationContext;
import fin.kk.mcp.model.ApplicationStatus;
import fin.kk.mcp.model.Card;
import fin.kk.mcp.service.CardService;
import fin.kk.mcp.service.DemoDataService;
import fin.kk.mcp.service.McpCardService;
import fin.kk.mcp.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for AI Agent chat functionality
 */
@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*") // For demo purposes - restrict in production
public class AgentController {

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private DemoDataService demoDataService;

    /**
     * Process a chat message
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatMessage message) {
        try {
            logger.info("Received chat message from session {}: {}", message.getSessionId(), message.getMessage());
            
            // Generate session ID if not provided
            if (message.getSessionId() == null || message.getSessionId().trim().isEmpty()) {
                message.setSessionId(UUID.randomUUID().toString());
                logger.info("Generated new session ID: {}", message.getSessionId());
            }
            
            // Set role to user if not specified
            if (message.getRole() == null) {
                message.setRole("user");
            }
            
            // Process message with OpenAI service
            logger.info("Processing message with OpenAI service");
            ChatResponse response = openAiService.processMessage(message);
            
            logger.info("Generated response for session {}: {} (took {}ms)", 
                       response.getSessionId(), 
                       response.getFinalResponse().substring(0, Math.min(100, response.getFinalResponse().length())),
                       response.getProcessingTimeMs());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing chat message: {}", e.getMessage(), e);
            
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setSessionId(message.getSessionId());
            errorResponse.setError("I'm sorry, I encountered an error processing your message. Please try again.");
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * Get conversation history for a session
     */
    @GetMapping("/conversation/{sessionId}")
    public ResponseEntity<ConversationContext> getConversation(@PathVariable String sessionId) {
        try {
            ConversationContext context = openAiService.getConversation(sessionId);
            
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
            openAiService.clearConversation(sessionId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Conversation cleared successfully");
            response.put("sessionId", sessionId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error clearing conversation {}: {}", sessionId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get list of active sessions (for debugging/monitoring)
     */
    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getActiveSessions() {
        try {
            Set<String> activeSessions = openAiService.getActiveSessions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("activeSessions", activeSessions);
            response.put("count", activeSessions.size());
            response.put("mode", "openai");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving active sessions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check endpoint for the agent service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("service", "AI Agent");
        health.put("mode", "openai");
        health.put("timestamp", new Date());
        
        Set<String> activeSessions = openAiService.getActiveSessions();
        health.put("activeSessions", activeSessions.size());
        
        return ResponseEntity.ok(health);
    }

    /**
     * Get available servers information (for UI display)
     */
    @GetMapping("/servers")
    public ResponseEntity<List<Map<String, Object>>> getServers() {
        List<Map<String, Object>> servers = new ArrayList<>();
        
        // OpenAI server information
        Map<String, Object> openAiServer = new HashMap<>();
        openAiServer.put("name", "OpenAI GPT-4");
        openAiServer.put("url", "https://api.openai.com/v1");
        openAiServer.put("healthStatus", "HEALTHY");
        openAiServer.put("description", "AI language model for conversation");
        servers.add(openAiServer);
        
        // MCP server information
        Map<String, Object> mcpServer = new HashMap<>();
        mcpServer.put("name", "KK Credit Card MCP Server");
        mcpServer.put("url", "http://localhost:8080/sse");
        mcpServer.put("healthStatus", "HEALTHY");
        mcpServer.put("description", "Provides credit card tools and information");
        servers.add(mcpServer);
        
        return ResponseEntity.ok(servers);
    }

    /**
     * Get available tools information (for UI display)
     */
    @GetMapping("/tools")
    public ResponseEntity<List<Map<String, Object>>> getTools() {
        List<Map<String, Object>> tools = new ArrayList<>();
        
        // Available MCP tools
        tools.add(createToolInfo("getCards", "Get all available credit cards with details"));
        tools.add(createToolInfo("getCard", "Get details for a specific card by ID"));
        tools.add(createToolInfo("getBonuses", "Get card names and signup bonuses"));
        tools.add(createToolInfo("submitApplication", "Submit a credit card application"));
        tools.add(createToolInfo("getCustomerApplications", "Get all applications for a customer by name"));
        tools.add(createToolInfo("getApplicationStatus", "Get detailed status of a specific application"));
        tools.add(createToolInfo("cancelApplication", "Cancel a pending application"));
        tools.add(createToolInfo("estimateApprovalTime", "Estimate approval time for an application"));
        tools.add(createToolInfo("getApplicationsNeedingAttention", "Get applications pending for a long time"));
        
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationStatus>> getAllApplications() {
        return ResponseEntity.ok(demoDataService.getAllApplications());
    }

    /**
     * Demo scenarios endpoint for quick testing
     */
    @GetMapping("/demo-scenarios")
    public ResponseEntity<List<Map<String, String>>> getDemoScenarios() {
        List<Map<String, String>> scenarios = new ArrayList<>();

        // Scenario 1: Simple status check
        scenarios.add(Map.of(
            "title", "Check Application Status",
            "message", "Hi, I'm John Doe. Can I get an update on my application?",
            "description", "Agent identifies the customer and retrieves their application status."
        ));

        // Scenario 2: Cancel and re-apply
        scenarios.add(Map.of(
            "title", "Cancel & Re-Apply",
            "message", "Hello, this is Jane Smith. I'd like to cancel my pending application and apply for the C24 Smart card instead.",
            "description", "Agent cancels an old application and starts a new one, asking for missing information (birthday)."
        ));

        // Scenario 3: General inquiry with reasoning
        scenarios.add(Map.of(
            "title", "Find a Travel Card",
            "message", "I'm planning a trip abroad. Which credit card is best for international travel?",
            "description", "Agent reasons about travel needs (e.g., no foreign fees) and recommends suitable cards."
        ));

        // Scenario 4: Proactive outreach
        scenarios.add(Map.of(
            "title", "Proactive Application Check",
            "message", "Hi, I'm Emma from KK Credit Cards. I'm calling about an older application from a 'Jane Smith'.",
            "description", "Agent uses tools to find old, unresolved applications and proactively addresses them."
        ));

        // Scenario 5: Compare two cards
        scenarios.add(Map.of(
            "title", "Compare Cards",
            "message", "Can you compare the Barclays Visa and the TF Mastercard Gold for me?",
            "description", "Agent fetches details for multiple cards and provides a summary comparison."
        ));
        
        // Scenario 6: Simple bonus check
        scenarios.add(Map.of(
            "title", "Check for Bonuses",
            "message", "Which cards have a signup bonus right now?",
            "description", "A straightforward tool call to get the latest card bonuses."
        ));

        return ResponseEntity.ok(scenarios);
    }

    // Helper methods
    private Map<String, Object> createToolInfo(String name, String description) {
        Map<String, Object> tool = new HashMap<>();
        tool.put("name", name);
        tool.put("description", description);
        return tool;
    }
    
    private Map<String, Object> createScenario(String title, String message, String description) {
        Map<String, Object> scenario = new HashMap<>();
        scenario.put("title", title);
        scenario.put("message", message);
        scenario.put("description", description);
        return scenario;
    }
} 