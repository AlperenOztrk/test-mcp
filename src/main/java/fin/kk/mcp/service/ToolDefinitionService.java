package fin.kk.mcp.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for managing tool definitions and metadata
 */
@Service
public class ToolDefinitionService {

    /**
     * Get all available tool definitions for the AI agent
     */
    public List<Map<String, Object>> getAllToolDefinitions() {
        List<Map<String, Object>> tools = new ArrayList<>();
        
        tools.add(createToolDefinition(
            "getCards",
            "Get a list of all available credit cards with their details",
            "Retrieves comprehensive information about all credit cards including fees, benefits, and terms",
            Map.of(),
            List.of("credit cards", "card listing", "available cards")
        ));
        
        tools.add(createToolDefinition(
            "getCard",
            "Get details for a single credit card by its ID",
            "Retrieves detailed information for a specific credit card using its unique identifier",
            Map.of(
                "id", Map.of(
                    "type", "integer",
                    "description", "The unique ID of the credit card to retrieve",
                    "required", true
                )
            ),
            List.of("card details", "specific card", "card information")
        ));
        
        tools.add(createToolDefinition(
            "getBonuses",
            "Get a simplified list showing only card names and their signup bonuses",
            "Provides a quick overview of signup bonuses for easy comparison between cards",
            Map.of(),
            List.of("signup bonuses", "card bonuses", "rewards", "promotions")
        ));
        
        tools.add(createToolDefinition(
            "submitApplication",
            "Submit a credit card application with personal and financial details",
            "Processes a new credit card application with validation and returns application status",
            Map.of(
                "name", Map.of(
                    "type", "string",
                    "description", "First name of the applicant",
                    "required", true
                ),
                "surname", Map.of(
                    "type", "string",
                    "description", "Last name of the applicant",
                    "required", true
                ),
                "salary", Map.of(
                    "type", "number",
                    "description", "Annual salary in USD (must be greater than 0)",
                    "required", true
                ),
                "birthday", Map.of(
                    "type", "string",
                    "description", "Date of birth in YYYY-MM-DD format",
                    "required", true
                )
            ),
            List.of("apply", "application", "credit card application", "submit application")
        ));
        
        // Customer application management tools
        tools.add(createToolDefinition(
            "getCustomerApplications",
            "Get all applications for a customer by name",
            "Retrieves all credit card applications associated with a customer's name",
            Map.of(
                "customerName", Map.of(
                    "type", "string",
                    "description", "Customer's full name (first and last name)",
                    "required", true
                )
            ),
            List.of("customer applications", "my applications", "application history")
        ));
        
        tools.add(createToolDefinition(
            "getApplicationStatus",
            "Get detailed status of a specific application",
            "Provides comprehensive status information for a specific application by ID",
            Map.of(
                "applicationId", Map.of(
                    "type", "string",
                    "description", "The unique application ID",
                    "required", true
                )
            ),
            List.of("application status", "check application", "application details")
        ));
        
        tools.add(createToolDefinition(
            "cancelApplication",
            "Cancel a pending application",
            "Cancels a pending credit card application that hasn't been approved or rejected yet",
            Map.of(
                "applicationId", Map.of(
                    "type", "string",
                    "description", "The unique application ID to cancel",
                    "required", true
                )
            ),
            List.of("cancel application", "withdraw application", "stop application")
        ));
        
        tools.add(createToolDefinition(
            "estimateApprovalTime",
            "Estimate approval time for an application",
            "Provides an estimated timeline for when an application decision will be made",
            Map.of(
                "applicationId", Map.of(
                    "type", "string",
                    "description", "The unique application ID",
                    "required", true
                )
            ),
            List.of("approval time", "decision timeline", "when will I hear back")
        ));
        
        tools.add(createToolDefinition(
            "getApplicationsNeedingAttention",
            "Get applications that have been pending for a long time and might need follow-up",
            "Identifies applications that have been in the system for an extended period and may require attention",
            Map.of(),
            List.of("old applications", "pending applications", "applications needing attention")
        ));
        
        return tools;
    }

    /**
     * Get tool definition by name
     */
    public Map<String, Object> getToolDefinition(String toolName) {
        return getAllToolDefinitions().stream()
                .filter(tool -> toolName.equals(tool.get("name")))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get tool suggestions based on user intent keywords
     */
    public List<String> suggestTools(String userMessage) {
        String messageLower = userMessage.toLowerCase();
        List<String> suggestions = new ArrayList<>();
        
        // Simple keyword-based suggestions
        if (messageLower.contains("card") || messageLower.contains("credit")) {
            if (messageLower.contains("bonus") || messageLower.contains("reward") || messageLower.contains("signup")) {
                suggestions.add("getBonuses");
            } else if (messageLower.contains("all") || messageLower.contains("available") || messageLower.contains("list")) {
                suggestions.add("getCards");
            } else if (messageLower.contains("specific") || messageLower.contains("details")) {
                suggestions.add("getCard");
            } else {
                suggestions.add("getBonuses"); // Default for card inquiries
            }
        }
        
        if (messageLower.contains("apply") || messageLower.contains("application")) {
            suggestions.add("submitApplication");
        }
        
        if (messageLower.contains("compare") || messageLower.contains("best") || messageLower.contains("which")) {
            suggestions.add("getBonuses");
            suggestions.add("getCards");
        }
        
        return suggestions;
    }

    /**
     * Get usage statistics for tools (mock data for demo)
     */
    public Map<String, Object> getToolUsageStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Map<String, Integer> usageCounts = new HashMap<>();
        usageCounts.put("getBonuses", 45);
        usageCounts.put("getCards", 32);
        usageCounts.put("submitApplication", 18);
        usageCounts.put("getCard", 12);
        
        stats.put("usageCounts", usageCounts);
        stats.put("totalCalls", 107);
        stats.put("mostPopular", "getBonuses");
        stats.put("successRate", 0.95);
        
        return stats;
    }

    /**
     * Validate tool parameters
     */
    public Map<String, Object> validateToolParameters(String toolName, Map<String, Object> parameters) {
        Map<String, Object> validation = new HashMap<>();
        List<String> errors = new ArrayList<>();
        
        Map<String, Object> toolDef = getToolDefinition(toolName);
        if (toolDef == null) {
            errors.add("Unknown tool: " + toolName);
            validation.put("valid", false);
            validation.put("errors", errors);
            return validation;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> expectedParams = (Map<String, Object>) toolDef.get("parameters");
        
        if (expectedParams != null) {
            for (Map.Entry<String, Object> entry : expectedParams.entrySet()) {
                String paramName = entry.getKey();
                @SuppressWarnings("unchecked")
                Map<String, Object> paramDef = (Map<String, Object>) entry.getValue();
                
                boolean required = Boolean.TRUE.equals(paramDef.get("required"));
                
                if (required && !parameters.containsKey(paramName)) {
                    errors.add("Missing required parameter: " + paramName);
                }
                
                if (parameters.containsKey(paramName)) {
                    Object value = parameters.get(paramName);
                    String expectedType = (String) paramDef.get("type");
                    
                    if (!isValidType(value, expectedType)) {
                        errors.add("Invalid type for parameter " + paramName + ": expected " + expectedType);
                    }
                }
            }
        }
        
        validation.put("valid", errors.isEmpty());
        validation.put("errors", errors);
        
        return validation;
    }

    // Helper methods
    private Map<String, Object> createToolDefinition(String name, String description, String detailedDescription, 
                                                   Map<String, Object> parameters, List<String> keywords) {
        Map<String, Object> tool = new HashMap<>();
        tool.put("name", name);
        tool.put("description", description);
        tool.put("detailedDescription", detailedDescription);
        tool.put("parameters", parameters);
        tool.put("keywords", keywords);
        tool.put("category", "Credit Card Management");
        tool.put("version", "1.0");
        return tool;
    }

    private boolean isValidType(Object value, String expectedType) {
        if (value == null) return true; // Null values are handled by required check
        
        switch (expectedType) {
            case "string":
                return value instanceof String;
            case "integer":
                return value instanceof Integer || value instanceof Long;
            case "number":
                return value instanceof Number;
            case "boolean":
                return value instanceof Boolean;
            default:
                return true; // Unknown types are allowed
        }
    }
} 