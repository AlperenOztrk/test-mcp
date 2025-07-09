package fin.kk.mcp.service;

import fin.kk.mcp.model.ApplicationRequest;
import fin.kk.mcp.model.ApplicationStatus;
import fin.kk.mcp.model.Card;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class McpCardService {

    @Autowired
    private CardService cardService;
    
    @Autowired
    private DemoDataService demoDataService;

    /**
     * MCP Tool: Get all credit cards from Check24 API
     */
    @Tool(name = "getCards", description = "Get a list of all available credit cards with their details")
    public List<Card> getCards() {
        return cardService.fetchCardsFromCheck24Api();
    }

    /**
     * MCP Tool: Get a single credit card by ID from Check24 API
     */
    @Tool(name = "getCard", description = "Get details for a single credit card by its ID")
    public Card getCard(@ToolParam(description = "The unique ID of the credit card to retrieve") Long id) {
        // Get from Check24 API only
        List<Card> apiCards = cardService.fetchCardsFromCheck24Api();
        return apiCards.stream()
                .filter(card -> card.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * MCP Tool: Submit a credit card application
     * This function validates and processes the application
     */
    @Tool(name = "submitApplication", description = "Submit a credit card application with personal and financial details")
    public Map<String, Object> submitApplication(
            @ToolParam(description = "First name of the applicant") String name, 
            @ToolParam(description = "Last name of the applicant") String surname, 
            @ToolParam(description = "Annual salary in USD (must be greater than 0)") BigDecimal salary, 
            @ToolParam(description = "Date of birth in YYYY-MM-DD format") LocalDate birthday,
            @ToolParam(description = "The unique ID of the card to apply for") Long cardId,
            @ToolParam(description = "The name of the card to apply for") String cardName) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Basic validation
            if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
                response.put("success", false);
                response.put("message", "Salary must be greater than 0");
                return response;
            }
            
            if (birthday == null) {
                response.put("success", false);
                response.put("message", "Birthday is required");
                return response;
            }
            
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Name is required");
                return response;
            }
            
            if (surname == null || surname.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Surname is required");
                return response;
            }
            
            // Create application in demo data service
            String fullName = name + " " + surname;
            
            ApplicationStatus application = demoDataService.addApplication(fullName, cardId, cardName, salary, birthday);
            
            // Enhanced response with application details
            response.put("success", true);
            response.put("message", "Application submitted successfully for " + fullName + 
                        ". Your application ID is " + application.getApplicationId() + 
                        ". You will be contacted within 7-10 business days.");
            response.put("statusCode", 200);
            response.put("applicantName", fullName);
            response.put("salary", salary);
            response.put("applicationId", application.getApplicationId());
            response.put("cardName", cardName);
            response.put("estimatedDecisionDate", application.getEstimatedDecisionDate());
            response.put("statusMessage", application.getStatusMessage());
            response.put("nextSteps", application.getNextSteps());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing application: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return response;
    }

    /**
     * MCP Tool: Get simplified card information showing only names and bonuses from Check24 API
     * This is a new capability that doesn't exist in the legacy API
     */
    @Tool(name = "getBonuses", description = "Get a simplified list showing only card names and their signup bonuses")
    public List<Map<String, String>> getBonuses() {
        List<Card> apiCards = cardService.fetchCardsFromCheck24Api();
        
        if (apiCards == null) {
            return List.of();
        }
        
        return apiCards.stream()
                .map(card -> {
                    Map<String, String> cardBonus = new HashMap<>();
                    cardBonus.put("cardName", card.getCardName());
                    cardBonus.put("bonus", card.getSignupBonus());
                    return cardBonus;
                })
                .collect(Collectors.toList());
    }

    /**
     * MCP Tool: Get all applications for a customer by name
     */
    @Tool(name = "getCustomerApplications", description = "Get all applications for a customer by name")
    public List<ApplicationStatus> getCustomerApplications(@ToolParam(description = "Customer's full name (first and last name)") String customerName) {
        return demoDataService.getCustomerApplications(customerName);
    }

    /**
     * MCP Tool: Get detailed status of a specific application
     */
    @Tool(name = "getApplicationStatus", description = "Get detailed status of a specific application")
    public ApplicationStatus getApplicationStatus(@ToolParam(description = "The unique application ID") String applicationId) {
        return demoDataService.getApplicationById(applicationId);
    }

    /**
     * MCP Tool: Cancel a pending application
     */
    @Tool(name = "cancelApplication", description = "Cancel a pending application")
    public Map<String, Object> cancelApplication(@ToolParam(description = "The unique application ID to cancel") String applicationId) {
        return demoDataService.cancelApplication(applicationId);
    }

    /**
     * MCP Tool: Estimate approval time for an application
     */
    @Tool(name = "estimateApprovalTime", description = "Estimate approval time for an application")
    public Map<String, Object> estimateApprovalTime(@ToolParam(description = "The unique application ID") String applicationId) {
        return demoDataService.estimateApprovalTime(applicationId);
    }

    /**
     * MCP Tool: Get applications that might need proactive attention
     */
    @Tool(name = "getApplicationsNeedingAttention", description = "Get applications that have been pending for a long time and might need follow-up")
    public List<ApplicationStatus> getApplicationsNeedingAttention() {
        return demoDataService.getApplicationsNeedingAttention();
    }


} 