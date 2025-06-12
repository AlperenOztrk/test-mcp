package fin.kk.mcp.service;

import fin.kk.mcp.dto.Check24ApiResponse;
import fin.kk.mcp.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private static final String CHECK24_API_URL = "https://finanzen.check24.de/accounts/r/pas/result/CREDITCARD/NORMAL?deviceoutput=mobile&b2bid=50&cpid=checkbank&deviceOutput=mobile";

    private final RestTemplate restTemplate;

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

    @Autowired
    public CardService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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

    /**
     * Fetch credit cards from Check24 API and map to Card objects
     * @return List of cards from Check24 API
     */
    public List<Card> fetchCardsFromCheck24Api() {
        try {
            logger.info("Fetching credit cards from Check24 API: {}", CHECK24_API_URL);
            
            Check24ApiResponse response = restTemplate.getForObject(CHECK24_API_URL, Check24ApiResponse.class);
            
            if (response == null || response.getOffers() == null) {
                logger.warn("No offers received from Check24 API");
                return Collections.emptyList();
            }
            
            logger.info("Successfully fetched {} offers from Check24 API", response.getOffers().size());
            
            return response.getOffers().stream()
                    .map(this::mapOfferToCard)
                    .collect(Collectors.toList());
                    
        } catch (RestClientException e) {
            logger.error("Error fetching data from Check24 API: {}", e.getMessage(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Unexpected error while fetching cards from Check24 API: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Map Check24 API Offer to Card object
     * @param offer Check24 API offer
     * @return Mapped Card object
     */
    private Card mapOfferToCard(Check24ApiResponse.Offer offer) {
        Card card = new Card();
        
        if (offer.getProduct() != null) {
            Check24ApiResponse.Product product = offer.getProduct();
            
            // Basic information
            card.setId(product.getId());
            card.setCardName(product.getName());
            card.setBankName(product.getBank() != null ? product.getBank().getName() : "Unknown Bank");
            card.setBenefits(product.getBenefits());
            card.setDrawbacks(product.getDrawbacks());
            
            // Bonus information
            if (product.getC24OpeningBonus() != null) {
                card.setBonusValue(BigDecimal.valueOf(product.getC24OpeningBonus()));
                card.setSignupBonus(product.getC24OpeningBonus() + " € Bonus bei CHECK24");
            }
            
            // Product condition information
            if (product.getProductCondition() != null) {
                Check24ApiResponse.ProductCondition condition = product.getProductCondition();
                
                // Annual fee
                if (condition.getCost() != null) {
                    card.setAnnualFee(condition.getCost().getCost() != null ? 
                        BigDecimal.valueOf(condition.getCost().getCost()) : BigDecimal.ZERO);
                }
                
                // Interest rate
                if (condition.getDebitInterest() != null) {
                    card.setInterestRate(BigDecimal.valueOf(condition.getDebitInterest()));
                }
                
                // Card system and type
                card.setCardSystem(condition.getCardSystem());
                card.setCardType(condition.getCardType());
                card.setContactlessPayment(Boolean.TRUE.equals(condition.getContactlessPayment()));
                
                // Grades
                card.setPaymentGrade(condition.getPaymentGrade());
                card.setWithdrawGrade(condition.getWithdrawGrade());
                card.setInsuranceGrade(condition.getInsuranceGrade());
                
                // Payment features
                if (condition.getProductPayment() != null) {
                    Check24ApiResponse.ProductPayment payment = condition.getProductPayment();
                    card.setFreePaymentEuroZone(Boolean.TRUE.equals(payment.getFreeUseEuCurrency()));
                    card.setFreePaymentWorldwide(Boolean.TRUE.equals(payment.getFreeUseWorldCurrency()));
                    card.setFreeWithdrawEuroZone(Boolean.TRUE.equals(payment.getFreeWithdrawEu()));
                    card.setFreeWithdrawWorldwide(Boolean.TRUE.equals(payment.getFreeWithdrawWorld()));
                    
                    // Mobile payment support
                    if (payment.getMobilePaymentSystems() != null) {
                        card.setApplePaySupported(payment.getMobilePaymentSystems().contains("APPLE_PAY"));
                        card.setGooglePaySupported(payment.getMobilePaymentSystems().contains("GOOGLE_PAY"));
                    }
                }
                
                // Insurance features
                if (condition.getProductInsurance() != null) {
                    Check24ApiResponse.ProductInsurance insurance = condition.getProductInsurance();
                    card.setHealthInsuranceAbroad(Boolean.TRUE.equals(insurance.getHealthInsuranceAbroad()));
                    card.setAccidentInsuranceAbroad(Boolean.TRUE.equals(insurance.getAccidentInsuranceAbroad()));
                    card.setTravelInsurance(Boolean.TRUE.equals(insurance.getTravelCancelInsurance()));
                }
            }
            
            // Customer feedback
            if (product.getCustomerFeedbackSummary() != null) {
                Check24ApiResponse.CustomerFeedbackSummary feedback = product.getCustomerFeedbackSummary();
                if (feedback.getOverallStars() != null) {
                    card.setCustomerRating(BigDecimal.valueOf(feedback.getOverallStars()));
                }
                card.setCustomerFeedbackCount(feedback.getCountFeedbacks());
            }
            
            // Grade information
            if (product.getGrade() != null) {
                Check24ApiResponse.Grade grade = product.getGrade();
                card.setGradeDescription(grade.getGradeDescription());
            }
        }
        
        return card;
    }

    /**
     * Get all cards including both static and API data
     * @return Combined list of cards
     */
    public List<Card> getAllCards() {
        List<Card> apiCards = fetchCardsFromCheck24Api();
        List<Card> staticCards = findAll();
        
        // Add API cards first, then add static cards that don't have the same ID
        List<Card> combinedCards = apiCards.stream().collect(Collectors.toList());
        staticCards.stream()
                .filter(card -> apiCards.stream().noneMatch(apiCard -> apiCard.getId().equals(card.getId())))
                .forEach(combinedCards::add);
        
        return combinedCards;
    }
} 