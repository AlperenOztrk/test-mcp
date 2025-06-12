package fin.kk.mcp.controller;

import fin.kk.mcp.model.Card;
import fin.kk.mcp.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/cards")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Get all static cards
     */
    @GetMapping("/static")
    public ResponseEntity<List<Card>> getStaticCards() {
        List<Card> cards = cardService.findAll();
        return ResponseEntity.ok(cards);
    }

    /**
     * Get cards from Check24 API
     */
    @GetMapping("/check24")
    public ResponseEntity<List<Card>> getCheck24Cards() {
        List<Card> cards = cardService.fetchCardsFromCheck24Api();
        return ResponseEntity.ok(cards);
    }

    /**
     * Get all cards (combined static and API data)
     */
    @GetMapping("/all")
    public ResponseEntity<List<Card>> getAllCards() {
        List<Card> cards = cardService.getAllCards();
        return ResponseEntity.ok(cards);
    }

    /**
     * Get card by ID from combined data sources
     */
    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable Long id) {
        // First try to get from API cards, then fall back to static cards
        List<Card> allCards = cardService.getAllCards();
        Card card = allCards.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (card != null) {
            return ResponseEntity.ok(card);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Test endpoint to verify MCP service is working with Check24 API data only
     */
    @GetMapping("/mcp-test")
    public ResponseEntity<Map<String, Object>> testMcpService() {
        Map<String, Object> result = new HashMap<>();
        result.put("check24Cards", cardService.fetchCardsFromCheck24Api().size());
        result.put("mcpCardsOnly", "MCP now uses only Check24 API cards");
        return ResponseEntity.ok(result);
    }
} 