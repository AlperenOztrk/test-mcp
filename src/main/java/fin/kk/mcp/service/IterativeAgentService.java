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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intelligent AI Agent Service that uses natural reasoning and autonomous tool selection
 * No hardcoded responses or rigid workflows - the AI determines its own behavior
 */
@Service
public class IterativeAgentService {

    private static final Logger logger = LoggerFactory.getLogger(IterativeAgentService.class);
    
    private final WebClient openAiWebClient;
    private final OpenAiConfig openAiConfig;
    private final McpCardService mcpCardService;
    private final ObjectMapper objectMapper;
    
    private final Map<String, ConversationContext> conversations = new ConcurrentHashMap<>();
    // Store extracted facts per session (e.g. name, surname, etc.)
    private final Map<String, Map<String, String>> sessionFacts = new ConcurrentHashMap<>();
    
    // Store agent reasoning logs per session
    private final Map<String, String> sessionLogs = new ConcurrentHashMap<>();
    
    private static final int MAX_ITERATIONS = 8;
    
    private static final String SYSTEM_PROMPT = """
        You are Emma, an autonomous and highly intelligent customer service agent for KK Credit Cards.

        ## Your Capabilities:
        - **Agentic Reasoning**: You decide, step by step, how to help the customer using your own judgment and the tools available. You are not bound by any fixed workflow.
        - **Perfect Memory**: You remember all relevant information from the current conversation and use it naturally. You never ask for the same information twice.
        - **Proactive Service**: You anticipate customer needs, use tools as needed, and provide comprehensive, helpful, and context-aware responses.
        - **Natural Recovery**: If you encounter an error, ambiguity, or missing information, you handle it gracefully and naturally, without relying on any fixed fallback phrase.
        - **No Hardcoded Workflows**: You are not limited by any rigid rules or step-by-step instructions. You use your intelligence and the conversation context to decide what to do next.
        - **IMPORTANT: You must always respond in English, regardless of the user's language.**

        ## Non-Negotiable Instructions:
        - If a customer provides their name (e.g., "I'm Jane Smith"), you MUST ALWAYS use the getCustomerApplications(name) tool as your first action, even if the user does not specify a request.
        - NEVER default to a generic greeting or message if a tool can be used. Tool use is ALWAYS preferred over generic responses.
        - ALWAYS respond in English, regardless of the user's language.
        - **Data Reuse:** Before asking a customer for information (like salary or birthday), you MUST scan the entire conversation history, including previous 'Result:' blocks, to see if the information is already there. Re-use data whenever possible. For example, if 'getCustomerApplications' returns a salary, use that salary for a subsequent 'submitApplication' call.

        ## Tools Available:
        - getCards()
        - getCard(id)
        - getBonuses()
        - submitApplication(name, surname, salary, birthday, cardId, cardName) - Use this to submit a new application for a specific card. Before using this, you might need to use getCards() to find the correct cardId for the card the user wants. Always look for other customer details (salary, birthday) from previous tool calls before asking the user.
        - getCustomerApplications(customerName)
        - getApplicationStatus(applicationId)
        - cancelApplication(applicationId)
        - estimateApprovalTime(applicationId)
        - getApplicationsNeedingAttention()

        ## How You Work:
        - You analyze the conversation, decide what information or action is needed, and use tools as appropriate.
        - You never repeat yourself or ask for information you already have.
        - You always communicate in a natural, helpful, and context-aware way, in English.
        - If you need to clarify, recover from an error, or provide a fallback, you do so in a way that fits the conversation.
        - You are not a workflow engine. You are a smart, adaptive, and autonomous agent.

        ## Response Format:
        - Think: [Your reasoning about what to do next.]
        - Action: [The tool you will use (e.g., getCards()), or respond: [your message to the customer]]
        - You must always use English in your response.
        """;

    @Autowired
    public IterativeAgentService(@Qualifier("openAiWebClient") WebClient openAiWebClient,
                                OpenAiConfig openAiConfig,
                                McpCardService mcpCardService,
                                ObjectMapper objectMapper) {
        this.openAiWebClient = openAiWebClient;
        this.openAiConfig = openAiConfig;
        this.mcpCardService = mcpCardService;
        this.objectMapper = objectMapper;
    }

    public ChatResponse processMessage(ChatMessage message) {
        long startTime = System.currentTimeMillis();
        
        try {
            ConversationContext context = getOrCreateConversation(message.getSessionId());
            context.addMessage(message);
            // Extract and store facts from the new message
            extractAndStoreFacts(message.getSessionId(), message.getMessage());
            String result = runAgentLoop(message.getMessage(), context, message.getSessionId());
            
            ChatResponse response = new ChatResponse(result, message.getSessionId());
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            
            ChatMessage assistantMessage = new ChatMessage(result, message.getSessionId(), "assistant");
            context.addMessage(assistantMessage);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setSessionId(message.getSessionId());
            errorResponse.setError("I apologize, but I encountered an error. Please try again.");
            errorResponse.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            return errorResponse;
        }
    }

    private String runAgentLoop(String userMessage, ConversationContext context, String sessionId) {
        StringBuilder conversationHistory = new StringBuilder();
        conversationHistory.append("<span class='log-user'><b>Customer:</b> ")
                .append(escapeHtml(userMessage)).append("</span><br><br>");
        
        int iteration = 0;
        
        while (iteration < MAX_ITERATIONS) {
            iteration++;
            
            String prompt = buildPrompt(conversationHistory.toString(), context, sessionId);
            String agentResponse = callOpenAi(prompt);
            logger.info("[Agent][Session:{}][Iteration:{}] LLM Raw Output:\n{}", sessionId, iteration, agentResponse);
            
            if (agentResponse == null || agentResponse.trim().isEmpty()) {
                break;
            }
            
            // Parse the agent's response
            AgentStep step = parseAgentResponse(agentResponse);
            if (step == null) {
                conversationHistory.append("<span class='log-error'><b>System:</b> Your last response was not in the correct format. Please respond with 'Think: ...' and 'Action: ...'.</span><br><br>");
                logger.warn("[Agent][Session:{}][Iteration:{}] Invalid format. Prompting LLM to correct.", sessionId, iteration);
                continue;
            }
            
            conversationHistory.append("<span class='log-think'><b>Think:</b> ")
                .append(escapeHtml(step.thought)).append("</span><br>");
            logger.info("[Agent][Session:{}][Iteration:{}] Think: {}", sessionId, iteration, step.thought);
            
            // Robust respond action detection
            String actionLower = step.action.trim().toLowerCase(Locale.ROOT);
            if (actionLower.startsWith("respond:") || actionLower.startsWith("respond :") || actionLower.startsWith("action: respond:") || actionLower.startsWith("action: respond :")) {
                // Extract the message after the first colon (case-insensitive, tolerant)
                String msg = step.action.replaceFirst("(?i)action:\\s*respond\\s*:\\s*", "").replaceFirst("(?i)respond\\s*:\\s*", "").trim();
                logger.info("[Agent][Session:{}][Iteration:{}] Action: respond", sessionId, iteration);
                conversationHistory.append("<span class='log-action'><b>Action:</b> respond</span><br>");
                conversationHistory.append("<span class='log-result'><b>Result:</b> ")
                    .append(escapeHtml(msg)).append("</span><br><br>");
                sessionLogs.put(sessionId, conversationHistory.toString());
                return msg;
            }
            // If the action is a quoted string, treat it as a user response
            if (step.action.trim().startsWith("\"") && step.action.trim().endsWith("\"")) {
                String msg = step.action.trim();
                // Remove the surrounding quotes
                msg = msg.substring(1, msg.length() - 1);
                logger.info("[Agent][Session:{}][Iteration:{}] Action: respond (quoted string)", sessionId, iteration);
                conversationHistory.append("<span class='log-action'><b>Action:</b> respond (quoted string)</span><br>");
                conversationHistory.append("<span class='log-result'><b>Result:</b> ")
                    .append(escapeHtml(msg)).append("</span><br><br>");
                sessionLogs.put(sessionId, conversationHistory.toString());
                return msg;
            }
            // If the action is a plain sentence (not a tool call), treat it as a user response
            if (!step.action.contains("(") && !step.action.contains(")")) {
                String msg = step.action.trim();
                logger.info("[Agent][Session:{}][Iteration:{}] Action: respond (plain sentence)", sessionId, iteration);
                conversationHistory.append("<span class='log-action'><b>Action:</b> respond (plain sentence)</span><br>");
                conversationHistory.append("<span class='log-result'><b>Result:</b> ")
                    .append(escapeHtml(msg)).append("</span><br><br>");
                sessionLogs.put(sessionId, conversationHistory.toString());
                return msg;
            }
            
            // Execute tool action
            try {
                String toolResult = executeActionFlexible(step.action);
                conversationHistory.append("<span class='log-action'><b>Action:</b> ")
                    .append(escapeHtml(step.action)).append("</span><br>");
                conversationHistory.append("<span class='log-result'><b>Result:</b> ")
                    .append(escapeHtml(toolResult)).append("</span><br><br>");
                logger.info("[Agent][Session:{}][Iteration:{}] Action: {} | Result: {}", sessionId, iteration, step.action, toolResult);
            } catch (Exception e) {
                logger.error("[Agent][Session:{}][Iteration:{}] Error executing action: {}", sessionId, iteration, e.getMessage());
                conversationHistory.append("<span class='log-action'><b>Action:</b> ")
                    .append(escapeHtml(step.action)).append("</span><br>");
                conversationHistory.append("<span class='log-error'><b>Error:</b> ")
                    .append(escapeHtml(e.getMessage())).append("</span><br><br>");
                // No hardcoded fallback. The LLM will see the error and decide how to recover in the next step.
            }
        }
        
        // No hardcoded fallback. The LLM will decide how to recover or clarify if needed.
        sessionLogs.put(sessionId, conversationHistory.toString());
        return "";
    }

    private String buildPrompt(String conversationHistory, ConversationContext context, String sessionId) {
        StringBuilder prompt = new StringBuilder();
        // Add explicit user message only (no language detection)
        String lastUserMessage = "";
        List<ChatMessage> recentMessages = context.getRecentMessages(1);
        if (!recentMessages.isEmpty()) {
            lastUserMessage = recentMessages.get(0).getMessage();
        }
        prompt.append("User's last message: \"").append(lastUserMessage).append("\"\n");
        // Removed duplicate language instruction here
        // Add known facts
        Map<String, String> facts = sessionFacts.getOrDefault(sessionId, Collections.emptyMap());
        if (!facts.isEmpty()) {
            prompt.append("Known Facts (from this conversation):\n");
            for (Map.Entry<String, String> entry : facts.entrySet()) {
                prompt.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            prompt.append("\n");
        }
        // Add recent conversation context if available
        List<ChatMessage> recentContext = context.getRecentMessages(5);
        if (recentContext.size() > 1) {
            prompt.append("Recent conversation context:\n");
            for (int i = 0; i < recentContext.size() - 1; i++) {
                ChatMessage msg = recentContext.get(i);
                prompt.append(msg.getRole()).append(": ").append(msg.getMessage()).append("\n");
            }
            prompt.append("\n");
        }
        prompt.append("Current conversation:\n");
        prompt.append(conversationHistory);
        prompt.append("What should you do next? Think about the customer's needs and respond appropriately.\n\n");
        return prompt.toString();
    }

    private AgentStep parseAgentResponse(String response) {
        try {
            // Use regex to capture multiline thought and action, being case-insensitive.
            // Pattern.DOTALL allows '.' to match newline characters.
            final Pattern pattern = Pattern.compile("Think:(.*?)\n?Action:(.*)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(response.trim());

            if (matcher.find()) {
                String thought = matcher.group(1).trim();
                String action = matcher.group(2).trim();
                return new AgentStep(thought, action);
            }
            
            // Fallback for action-only responses
            final Pattern actionOnlyPattern = Pattern.compile("Action:(.*)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher actionOnlyMatcher = actionOnlyPattern.matcher(response.trim());
            if (actionOnlyMatcher.find()) {
                String thought = "(No thought provided)";
                String action = actionOnlyMatcher.group(1).trim();
                return new AgentStep(thought, action);
            }
            
            logger.warn("Could not parse agent response with standard 'Think/Action' format. Response was: {}", response);
            return null; // Let the calling loop handle the invalid format.

        } catch (Exception e) {
            logger.error("Error parsing agent response: {}", e.getMessage(), e);
            return null;
        }
    }

    // Robust tool call parser: case-insensitive, whitespace-tolerant
    private String executeActionFlexible(String action) throws Exception {
        // Try to match tool call: toolName(args)
        Pattern pattern = Pattern.compile("(?i)(\\w+)\\s*\\((.*)\\)");
        Matcher matcher = pattern.matcher(action.trim());
        if (matcher.matches()) {
            String toolName = matcher.group(1).trim();
            String argsString = matcher.group(2).trim();
            Object result = executeToolCall(toolName, argsString);
            return objectMapper.writeValueAsString(result);
        }
        throw new IllegalArgumentException("Invalid action format: " + action);
    }

    private Object executeToolCall(String toolName, String argsString) throws Exception {
        switch (toolName) {
            case "getCards":
                return mcpCardService.getCards();
                
            case "getCard":
                String idString = argsString.trim().replaceAll("\"", "");
                Long id = Long.parseLong(idString);
                return mcpCardService.getCard(id);
                
            case "getBonuses":
                return mcpCardService.getBonuses();
                
            case "submitApplication":
                return parseAndExecuteSubmitApplication(argsString);
                
            case "getCustomerApplications":
                String customerName = argsString.trim().replaceAll("\"", "");
                return mcpCardService.getCustomerApplications(customerName);
                
            case "getApplicationStatus":
                String appId = argsString.trim().replaceAll("\"", "");
                return mcpCardService.getApplicationStatus(appId);
                
            case "cancelApplication":
                String cancelAppId = argsString.trim().replaceAll("\"", "");
                return mcpCardService.cancelApplication(cancelAppId);
                
            case "estimateApprovalTime":
                String estimateAppId = argsString.trim().replaceAll("\"", "");
                return mcpCardService.estimateApprovalTime(estimateAppId);
                
            case "getApplicationsNeedingAttention":
                return mcpCardService.getApplicationsNeedingAttention();
                
            default:
                throw new IllegalArgumentException("Unknown tool: " + toolName);
        }
    }

    private Object parseAndExecuteSubmitApplication(String argsString) throws Exception {
        // Simple parsing - in production, use proper JSON parsing
        String[] parts = argsString.split(",");
        if (parts.length < 6) {
            throw new IllegalArgumentException("submitApplication requires 6 parameters: name, surname, salary, birthday, cardId, cardName");
        }
        
        String name = parts[0].trim().replaceAll("\"", "");
        String surname = parts[1].trim().replaceAll("\"", "");
        java.math.BigDecimal salary = new java.math.BigDecimal(parts[2].trim().replaceAll("\"", ""));
        java.time.LocalDate birthday = java.time.LocalDate.parse(parts[3].trim().replaceAll("\"", ""));
        Long cardId = Long.parseLong(parts[4].trim().replaceAll("\"", ""));
        String cardName = parts[5].trim().replaceAll("\"", "");
        
        return mcpCardService.submitApplication(name, surname, salary, birthday, cardId, cardName);
    }

    private String callOpenAi(String prompt) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", openAiConfig.getModel());
            request.put("temperature", 0.3);
            request.put("max_tokens", 1000);

            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", SYSTEM_PROMPT);
            messages.add(systemMessage);

            // Enforce English language
            Map<String, Object> langInstruction = new HashMap<>();
            langInstruction.put("role", "system");
            langInstruction.put("content", "Always respond in English.");
            messages.add(langInstruction);

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            request.put("messages", messages);

            Mono<String> responseMono = openAiWebClient
                .post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30));

            String responseBody = responseMono.block();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            JsonNode choices = responseJson.get("choices");

            if (choices != null && !choices.isEmpty()) {
                JsonNode content = choices.get(0).get("message").get("content");
                return content != null ? content.asText() : null;
            }

            return null;

        } catch (Exception e) {
            logger.error("Error calling OpenAI: {}", e.getMessage(), e);
            return null;
        }
    }

    private ConversationContext getOrCreateConversation(String sessionId) {
        return conversations.computeIfAbsent(sessionId, ConversationContext::new);
    }

    public ConversationContext getConversation(String sessionId) {
        return conversations.get(sessionId);
    }

    public void clearConversation(String sessionId) {
        conversations.remove(sessionId);
    }

    public Set<String> getActiveSessions() {
        return conversations.keySet();
    }

    // Extracts facts (like name) from the message and stores them in sessionFacts
    private void extractAndStoreFacts(String sessionId, String message) {
        Map<String, String> facts = sessionFacts.computeIfAbsent(sessionId, k -> new HashMap<>());
        // Name extraction (very basic, can be improved)
        Pattern namePattern = Pattern.compile("(?:I'm|I am|My name is|This is|Hi, I'm|Hello, I'm|^)([A-Z][a-z]+) ([A-Z][a-z]+)");
        Matcher matcher = namePattern.matcher(message);
        if (matcher.find()) {
            facts.put("name", matcher.group(1));
            facts.put("surname", matcher.group(2));
        }
        // If message is just "Jane Smith" etc.
        Pattern justName = Pattern.compile("^([A-Z][a-z]+) ([A-Z][a-z]+)$");
        Matcher justNameMatcher = justName.matcher(message.trim());
        if (justNameMatcher.matches()) {
            facts.put("name", justNameMatcher.group(1));
            facts.put("surname", justNameMatcher.group(2));
        }
        // TODO: Add more robust extraction for salary, birthday, etc. if needed
    }

    private static class AgentStep {
        private final String thought;
        private final String action;

        public AgentStep(String thought, String action) {
            this.thought = thought;
            this.action = action;
        }

        public String getThought() { return thought; }
        public String getAction() { return action; }
    }

    // Expose the agent reasoning log for a session
    public String getSessionLog(String sessionId) {
        return sessionLogs.getOrDefault(sessionId, "");
    }

    // Utility to escape HTML for safe log rendering
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }
}