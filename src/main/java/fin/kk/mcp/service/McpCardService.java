package fin.kk.mcp.service;

import fin.kk.mcp.model.ApplicationRequest;
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

    /**
     * MCP Tool: Get all credit cards
     */
    @Tool(name = "getCards", description = "Get a list of all available credit cards with their details")
    public List<Card> getCards() {
        return cardService.findAll();
    }

    /**
     * MCP Tool: Get a single credit card by ID
     */
    @Tool(name = "getCard", description = "Get details for a single credit card by its ID")
    public Card getCard(@ToolParam(description = "The unique ID of the credit card to retrieve") Long id) {
        return cardService.getById(id);
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
            @ToolParam(description = "Date of birth in YYYY-MM-DD format") LocalDate birthday) {
        
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
            
            // Application accepted
            response.put("success", true);
            response.put("message", "Application submitted successfully for " + name + " " + surname + 
                        ". Your application is being reviewed and you will be contacted within 7-10 business days.");
            response.put("statusCode", 200);
            response.put("applicantName", name + " " + surname);
            response.put("salary", salary);
            response.put("applicationId", "APP-" + System.currentTimeMillis());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing application: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return response;
    }

    /**
     * MCP Tool: Get simplified card information showing only names and bonuses
     * This is a new capability that doesn't exist in the legacy API
     */
    @Tool(name = "getBonuses", description = "Get a simplified list showing only card names and their signup bonuses")
    public List<Map<String, String>> getBonuses() {
        List<Card> allCards = getCards();
        
        if (allCards == null) {
            return List.of();
        }
        
        return allCards.stream()
                .map(card -> {
                    Map<String, String> cardBonus = new HashMap<>();
                    cardBonus.put("cardName", card.getCardName());
                    cardBonus.put("bonus", card.getBonus());
                    return cardBonus;
                })
                .collect(Collectors.toList());
    }
} 