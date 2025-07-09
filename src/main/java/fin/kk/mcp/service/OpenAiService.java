package fin.kk.mcp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fin.kk.mcp.config.OpenAiConfig;
import fin.kk.mcp.dto.ChatMessage;
import fin.kk.mcp.dto.ChatResponse;
import fin.kk.mcp.dto.ConversationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Service for OpenAI GPT-4 integration with function calling
 */
@Service
public class OpenAiService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);
    
    private final WebClient openAiWebClient;
    private final OpenAiConfig openAiConfig;
    private final McpCardService mcpCardService;
    private final ObjectMapper objectMapper;
    
    // In-memory conversation storage (for demo purposes)
    private final Map<String, ConversationContext> conversations = new ConcurrentHashMap<>();
    
    private static final String SYSTEM_PROMPT = """
            You are Emma, a highly skilled and proactive customer service agent for KK Credit Cards. 
            You excel at understanding customer needs and using available tools intelligently to provide exceptional service.
            
            ## Your Core Intelligence:
            - **Agentic Behavior**: You autonomously decide which tools to use based on customer needs
            - **Natural Communication**: Respond in the customer's language and communication style naturally
            - **Data Interpretation**: When you receive raw data from tools, intelligently analyze and present it in a helpful way
            - **Multilingual Support**: Communicate fluently in any language the customer uses
            - **Context Awareness**: Remember conversation history and build upon previous interactions
            
            ## Your Personality & Approach:
            - Warm, professional, and genuinely helpful
            - Proactive in anticipating customer needs
            - Explain your actions clearly when using tools
            - Always follow up with relevant suggestions
            - Adapt your communication style to match the customer's preferences
            
            ## Core Behavioral Rules:
            1. **Name Recognition**: When a customer provides their name, IMMEDIATELY call getCustomerApplications to check their history
            2. **Proactive Service**: If you find old applications (>7 days), mention them proactively and offer updates
            3. **Tool Chaining**: Use multiple tools in sequence when needed for comprehensive assistance
            4. **Intelligent Analysis**: When you receive raw data from tools, analyze it and present insights naturally
            5. **Language Adaptation**: Respond in whatever language the customer uses, maintaining natural conversation flow
            
            ## Available Tools & When to Use Them:
            - **getCards**: When customers ask about available cards, want to browse options, or need comprehensive card information
            - **getCard**: When customers ask about a specific card by ID or when you need detailed information about one card
            - **getBonuses**: When customers want to compare signup bonuses, ask "which card has the best bonus", or want quick comparisons
            - **submitApplication**: When customers want to apply for a card and provide all required information
            - **getCustomerApplications**: CRITICAL - Use immediately when customer provides their name or asks about "my application"
            - **getApplicationStatus**: When you need detailed status of a specific application ID
            - **cancelApplication**: When customers want to cancel an existing application
            - **estimateApprovalTime**: When customers ask about timing or you want to provide proactive updates
            - **getApplicationsNeedingAttention**: Use proactively to check for customers who might need follow-up
            
            ## Data Processing Approach:
            When you receive raw data from tools:
            1. **Analyze** the data structure and content intelligently
            2. **Extract** the most relevant information for the customer's needs
            3. **Present** it in a natural, conversational way
            4. **Suggest** logical next steps or related actions
            5. **Maintain** the conversation flow smoothly
            
            ## Language & Communication:
            - Detect and match the customer's language automatically
            - Use natural, conversational tone appropriate for the language/culture
            - Present technical information in an accessible way
            - Adapt formality level to match customer's style
            - Use appropriate cultural context and expressions
            
            ## Conversation Flow Examples:
            
            **New Customer Scenario:**
            Customer: "I want information about credit cards"
            You: "I'd be happy to help you find the perfect credit card! Let me show you our current options and their signup bonuses."
            [Call getBonuses() and getCards()]
            You: [Analyze raw data and present intelligently] "Based on our current offerings, I can see several excellent options for you..."
            
            **Existing Customer with Name:**
            Customer: "Hi, I'm Jane Smith"
            You: "Hello Jane! Let me check your account and any applications you might have with us."
            [Call getCustomerApplications("Jane Smith")]
            You: [Analyze application data] "I found your application for the American Express Platinum Card from 8 days ago..."
            
            **Multilingual Example:**
            Customer: "Hola, quiero información sobre tarjetas de crédito"
            You: "¡Hola! Me encantaría ayudarte a encontrar la tarjeta de crédito perfecta. Déjame mostrarte nuestras opciones actuales..."
            
            ## Response Guidelines:
            - Always start responses warmly and professionally
            - When using tools, explain what you're doing naturally
            - Analyze raw tool data and present insights intelligently
            - End with proactive suggestions or follow-up questions
            - If you find concerning information, address it constructively
            - Keep responses conversational and naturally flowing
            - Adapt to the customer's communication style and language preferences
            
            Remember: You're an intelligent agent that uses tools to gather information and then provides thoughtful, natural responses. You're not just displaying data - you're interpreting it and helping customers understand what it means for them.
            """;

    @Autowired
    public OpenAiService(@Qualifier("openAiWebClient") WebClient openAiWebClient,
                        OpenAiConfig openAiConfig,
                        McpCardService mcpCardService,
                        ObjectMapper objectMapper) {
        this.openAiWebClient = openAiWebClient;
        this.openAiConfig = openAiConfig;
        this.mcpCardService = mcpCardService;
        this.objectMapper = objectMapper;
    }

    /**
     * Process a chat message and generate a response using OpenAI
     */
    public ChatResponse processMessage(ChatMessage message) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Get or create conversation context
            ConversationContext context = getOrCreateConversation(message.getSessionId());
            
            // Add user message to context
            context.addMessage(message);
            
            // Build OpenAI request
            Map<String, Object> request = buildOpenAiRequest(context);
            
            // Call OpenAI API
            String aiResponse = callOpenAiApi(request);
            
            // Create response
            ChatResponse response = new ChatResponse(aiResponse, message.getSessionId());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            
            // Add assistant response to context
            ChatMessage assistantMessage = new ChatMessage(aiResponse, message.getSessionId(), "assistant");
            context.addMessage(assistantMessage);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setSessionId(message.getSessionId());
            errorResponse.setError("Sorry, I encountered an error processing your request. Please try again.");
            errorResponse.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            return errorResponse;
        }
    }

    /**
     * Get or create conversation context
     */
    private ConversationContext getOrCreateConversation(String sessionId) {
        return conversations.computeIfAbsent(sessionId, ConversationContext::new);
    }

    /**
     * Build OpenAI API request with function calling
     */
    private Map<String, Object> buildOpenAiRequest(ConversationContext context) {
        Map<String, Object> request = new HashMap<>();
        
        // Model and basic parameters
        request.put("model", openAiConfig.getModel());
        request.put("temperature", openAiConfig.getTemperature());
        request.put("max_tokens", openAiConfig.getMaxTokens());
        
        // Messages
        List<Map<String, Object>> messages = new ArrayList<>();
        
        // System message
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", SYSTEM_PROMPT);
        messages.add(systemMessage);
        
        // Conversation history (last 10 messages to keep context manageable)
        List<ChatMessage> recentMessages = context.getRecentMessages(10);
        for (ChatMessage msg : recentMessages) {
            Map<String, Object> openAiMessage = new HashMap<>();
            openAiMessage.put("role", msg.getRole());
            openAiMessage.put("content", msg.getMessage());
            messages.add(openAiMessage);
        }
        
        request.put("messages", messages);
        
        // Function definitions
        if (openAiConfig.getFunctionCallingEnabled()) {
            request.put("tools", buildToolDefinitions());
            request.put("tool_choice", "auto");
        }
        
        return request;
    }

    /**
     * Build tool definitions for OpenAI function calling
     */
    private List<Map<String, Object>> buildToolDefinitions() {
        List<Map<String, Object>> tools = new ArrayList<>();
        
        // getCards tool
        tools.add(createToolDefinition(
            "getCards",
            "Get a list of all available credit cards with their details",
            Map.of()
        ));
        
        // getCard tool
        tools.add(createToolDefinition(
            "getCard",
            "Get details for a single credit card by its ID",
            Map.of(
                "id", Map.of(
                    "type", "integer",
                    "description", "The unique ID of the credit card to retrieve"
                )
            )
        ));
        
        // getBonuses tool
        tools.add(createToolDefinition(
            "getBonuses",
            "Get card names and signup bonuses",
            Map.of()
        ));
        
        // submitApplication tool
        tools.add(createToolDefinition(
            "submitApplication",
            "Submit a credit card application with personal and financial details",
            Map.of(
                "name", Map.of(
                    "type", "string",
                    "description", "First name of the applicant"
                ),
                "surname", Map.of(
                    "type", "string",
                    "description", "Last name of the applicant"
                ),
                "salary", Map.of(
                    "type", "number",
                    "description", "Annual salary in USD (must be greater than 0)"
                ),
                "birthday", Map.of(
                    "type", "string",
                    "description", "Date of birth in YYYY-MM-DD format"
                ),
                "cardId", Map.of(
                    "type", "integer",
                    "description", "The unique ID of the card to apply for"
                ),
                "cardName", Map.of(
                    "type", "string",
                    "description", "The name of the card to apply for"
                )
            )
        ));
        
        // getCustomerApplications tool
        tools.add(createToolDefinition(
            "getCustomerApplications",
            "Get all applications for a customer by name - USE THIS IMMEDIATELY when customer provides their name",
            Map.of(
                "customerName", Map.of(
                    "type", "string",
                    "description", "Customer's full name (first and last name)"
                )
            )
        ));
        
        // getApplicationStatus tool
        tools.add(createToolDefinition(
            "getApplicationStatus",
            "Get detailed status of a specific application",
            Map.of(
                "applicationId", Map.of(
                    "type", "string",
                    "description", "The unique application ID"
                )
            )
        ));
        
        // cancelApplication tool
        tools.add(createToolDefinition(
            "cancelApplication",
            "Cancel a pending application",
            Map.of(
                "applicationId", Map.of(
                    "type", "string",
                    "description", "The unique application ID to cancel"
                )
            )
        ));
        
        // estimateApprovalTime tool
        tools.add(createToolDefinition(
            "estimateApprovalTime",
            "Estimate approval time for an application",
            Map.of(
                "applicationId", Map.of(
                    "type", "string",
                    "description", "The unique application ID"
                )
            )
        ));
        
        // getApplicationsNeedingAttention tool
        tools.add(createToolDefinition(
            "getApplicationsNeedingAttention",
            "Get applications that have been pending for a long time and might need follow-up",
            Map.of()
        ));
        
        return tools;
    }

    /**
     * Create a tool definition for OpenAI function calling
     */
    private Map<String, Object> createToolDefinition(String name, String description, Map<String, Object> parameters) {
        Map<String, Object> function = new HashMap<>();
        function.put("name", name);
        function.put("description", description);
        
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("properties", parameters);
        schema.put("required", new ArrayList<>(parameters.keySet()));
        
        function.put("parameters", schema);
        
        Map<String, Object> tool = new HashMap<>();
        tool.put("type", "function");
        tool.put("function", function);
        
        return tool;
    }

    /**
     * Call OpenAI API and handle function calling
     */
    private String callOpenAiApi(Map<String, Object> request) {
        try {
            // Make the API call
            Mono<String> responseMono = openAiWebClient
                .post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30));
            
            String responseBody = responseMono.block();
            
            // Parse response
            JsonNode responseJson = objectMapper.readTree(responseBody);
            JsonNode choices = responseJson.get("choices");
            
            if (choices == null || choices.isEmpty()) {
                return "I apologize, but I didn't receive a proper response. Please try again.";
            }
            
            JsonNode message = choices.get(0).get("message");
            JsonNode toolCalls = message.get("tool_calls");
            
            // Handle function calls if present
            if (toolCalls != null && !toolCalls.isEmpty()) {
                return handleFunctionCallsWithFollowUp(toolCalls, request);
            }
            
            // Return direct content if no function calls
            JsonNode content = message.get("content");
            return content != null ? content.asText() : "I'm here to help! How can I assist you with credit cards today?";
            
        } catch (Exception e) {
            logger.error("Error calling OpenAI API: {}", e.getMessage(), e);
            return "I apologize, but I'm having trouble processing your request right now. Please try again in a moment.";
        }
    }

    /**
     * Handle function calls by executing them and making a follow-up call to OpenAI with the results
     */
    private String handleFunctionCallsWithFollowUp(JsonNode toolCalls, Map<String, Object> originalRequest) {
        try {
            // Execute all tool calls and collect results
            List<Map<String, Object>> toolResults = new ArrayList<>();
            
            for (JsonNode toolCall : toolCalls) {
                JsonNode function = toolCall.get("function");
                String functionName = function.get("name").asText();
                String argumentsJson = function.get("arguments").asText();
                String toolCallId = toolCall.get("id").asText();
                
                logger.info("Executing function: {} with arguments: {}", functionName, argumentsJson);
                
                try {
                    // Parse arguments and execute function
                    JsonNode arguments = objectMapper.readTree(argumentsJson);
                    Object result = executeMcpFunction(functionName, arguments);
                    
                    // Create tool result message
                    Map<String, Object> toolResult = new HashMap<>();
                    toolResult.put("tool_call_id", toolCallId);
                    toolResult.put("role", "tool");
                    toolResult.put("content", objectMapper.writeValueAsString(result));
                    
                    toolResults.add(toolResult);
                    
                } catch (Exception e) {
                    logger.error("Error executing function {}: {}", functionName, e.getMessage(), e);
                    
                    // Add error result
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("tool_call_id", toolCallId);
                    errorResult.put("role", "tool");
                    errorResult.put("content", "{\"error\": \"Failed to execute " + functionName + "\"}");
                    
                    toolResults.add(errorResult);
                }
            }
            
            // Add the assistant's tool call message to conversation
            Map<String, Object> assistantMessage = new HashMap<>();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", null);
            assistantMessage.put("tool_calls", toolCalls);
            
            // Add tool results to conversation
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> messages = (List<Map<String, Object>>) originalRequest.get("messages");
            messages.add(assistantMessage);
            messages.addAll(toolResults);
            
            // Make follow-up call to OpenAI with tool results
            Map<String, Object> followUpRequest = new HashMap<>(originalRequest);
            followUpRequest.put("messages", messages);
            
            // Make the follow-up API call
            Mono<String> followUpMono = openAiWebClient
                .post()
                .uri("/chat/completions")
                .bodyValue(followUpRequest)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30));
            
            String followUpResponse = followUpMono.block();
            
            // Parse follow-up response
            JsonNode followUpJson = objectMapper.readTree(followUpResponse);
            JsonNode followUpChoices = followUpJson.get("choices");
            
            if (followUpChoices != null && !followUpChoices.isEmpty()) {
                JsonNode followUpMessage = followUpChoices.get(0).get("message");
                JsonNode followUpContent = followUpMessage.get("content");
                
                if (followUpContent != null) {
                    return followUpContent.asText();
                }
            }
            
            return "I retrieved the information but had trouble processing it. Please try again.";
            
        } catch (Exception e) {
            logger.error("Error handling function calls: {}", e.getMessage(), e);
            return "I encountered an error while processing your request. Please try again.";
        }
    }

    /**
     * Execute MCP function based on name and arguments
     */
    private Object executeMcpFunction(String functionName, JsonNode arguments) {
        switch (functionName) {
            case "getCards":
                return mcpCardService.getCards();
                
            case "getCard":
                Long id = arguments.get("id").asLong();
                return mcpCardService.getCard(id);
                
            case "getBonuses":
                return mcpCardService.getBonuses();
                
            case "submitApplication":
                return mcpCardService.submitApplication(
                    arguments.get("name").asText(),
                    arguments.get("surname").asText(),
                    arguments.get("salary").decimalValue(),
                    java.time.LocalDate.parse(arguments.get("birthday").asText()),
                    arguments.get("cardId").asLong(),
                    arguments.get("cardName").asText()
                );
                
            case "getCustomerApplications":
                return mcpCardService.getCustomerApplications(
                    arguments.get("customerName").asText()
                );
                
            case "getApplicationStatus":
                return mcpCardService.getApplicationStatus(
                    arguments.get("applicationId").asText()
                );
                
            case "cancelApplication":
                return mcpCardService.cancelApplication(
                    arguments.get("applicationId").asText()
                );
                
            case "estimateApprovalTime":
                return mcpCardService.estimateApprovalTime(
                    arguments.get("applicationId").asText()
                );
                
            case "getApplicationsNeedingAttention":
                return mcpCardService.getApplicationsNeedingAttention();
                
            default:
                throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }


    /**
     * Get conversation history
     */
    public ConversationContext getConversation(String sessionId) {
        return conversations.get(sessionId);
    }

    /**
     * Clear conversation
     */
    public void clearConversation(String sessionId) {
        conversations.remove(sessionId);
    }

    /**
     * Get all active sessions (for debugging)
     */
    public Set<String> getActiveSessions() {
        return conversations.keySet();
    }
} 