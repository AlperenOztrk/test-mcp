package fin.kk.mcp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fin.kk.mcp.model.ApplicationStatus;
import fin.kk.mcp.service.McpCardService;

/**
 * MCP Server Test Controller
 * 
 * Provides endpoints to verify MCP server configuration and tools
 */
@RestController
@RequestMapping("/mcp-test")
public class McpTestController {

    private final McpCardService mcpCardService;

    public McpTestController(McpCardService mcpCardService) {
        this.mcpCardService = mcpCardService;
    }

    /**
     * Test endpoint to verify MCP server status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "MCP Server is running");
        status.put("endpoints", Map.of(
            "sse", "/sse",
            "message", "/mcp/message"
        ));
        status.put("mcpTools", List.of("getCards", "getCard", "submitApplication", "getBonuses"));
        
        return ResponseEntity.ok(status);
    }

    /**
     * Information about MCP endpoints
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("description", "KK Credit Card MCP Server");
        info.put("mcpEndpoints", Map.of(
            "sse", "Connect to /sse for MCP client communication",
            "message", "Send MCP messages to /mcp/message"
        ));
        info.put("testEndpoints", Map.of(
            "demoWeb", "/ - Web interface for demonstration",
            "restApi", "/api/* - REST API endpoints",
            "mcpTest", "/mcp-test/* - MCP server test endpoints"
        ));
        info.put("availableTools", Map.of(
            "getCards", "Get all available credit cards with details",
            "getCard", "Get a single credit card by ID",
            "submitApplication", "Submit a credit card application",
            "getBonuses", "Get card names and signup bonuses only"
        ));
        info.put("usage", "Connect your MCP client (like Claude Desktop) to this server using the /sse endpoint");
        
        return ResponseEntity.ok(info);
    }

    @GetMapping("/demo-data/test")
    public ResponseEntity<Map<String, Object>> testDemoData() {
        Map<String, Object> response = new HashMap<>();
        
        // Test demo data service
        List<ApplicationStatus> janeApplications = mcpCardService.getCustomerApplications("Jane Smith");
        List<ApplicationStatus> needingAttention = mcpCardService.getApplicationsNeedingAttention();
        
        response.put("janeApplications", janeApplications);
        response.put("applicationsNeedingAttention", needingAttention);
        response.put("totalApplications", needingAttention.size());
        response.put("status", "Demo data test successful");
        
        return ResponseEntity.ok(response);
    }
} 