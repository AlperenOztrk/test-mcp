package fin.kk.mcp.service;

import fin.kk.mcp.model.Card;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    // Static in-memory data for demonstration
    private static final List<Card> CARDS = Arrays.asList(
            new Card(1L, "Chase Bank", "Chase Sapphire Preferred", 
                    new BigDecimal("95.00"), "60,000 points after spending $4,000 in first 3 months", 
                    new BigDecimal("15.99")),
            new Card(2L, "American Express", "Platinum Card", 
                    new BigDecimal("695.00"), "100,000 points after spending $6,000 in first 6 months", 
                    new BigDecimal("19.99")),
            new Card(3L, "Capital One", "Venture X", 
                    new BigDecimal("395.00"), "75,000 miles after spending $4,000 in first 3 months", 
                    new BigDecimal("16.99"))
    );

    /**
     * Find all cards
     * @return List of all cards
     */
    public List<Card> findAll() {
        return CARDS;
    }

    /**
     * Find card by ID
     * @param id Card ID
     * @return Optional containing the card if found, empty otherwise
     */
    public Optional<Card> findById(Long id) {
        return CARDS.stream()
                .filter(card -> card.getId().equals(id))
                .findFirst();
    }

    /**
     * Get card by ID, returns null if not found
     * @param id Card ID
     * @return Card if found, null otherwise
     */
    public Card getById(Long id) {
        return findById(id).orElse(null);
    }
} 