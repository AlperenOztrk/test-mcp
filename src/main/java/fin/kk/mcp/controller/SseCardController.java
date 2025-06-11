package fin.kk.mcp.controller;

import fin.kk.mcp.model.ApplicationRequest;
import fin.kk.mcp.model.Card;
import fin.kk.mcp.service.CardService;
import fin.kk.mcp.service.McpCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Demo Controller for Web Interface and REST API
 * 
 * This controller provides:
 * - Web interface at / for demonstration
 * - REST API endpoints at /api/* for testing
 * - SSE streams for real-time updates
 * 
 * Note: This is separate from the MCP server endpoints (/sse and /mcp/message)
 * which are automatically configured by Spring AI MCP Server.
 */
@Controller
@RequestMapping
public class SseCardController {

    @Autowired
    private CardService cardService;
    
    @Autowired
    private McpCardService mcpCardService;

    private final List<SseEmitter> cardEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> applicationEmitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    /**
     * Home page that displays the SSE demo interface
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("cards", cardService.findAll());
        return "index";
    }

    /**
     * REST API endpoint to get all cards
     */
    @GetMapping("/api/cards")
    @ResponseBody
    public ResponseEntity<List<Card>> getCards() {
        List<Card> cards = cardService.findAll();
        return ResponseEntity.ok(cards);
    }

    /**
     * REST API endpoint to get a single card by ID
     */
    @GetMapping("/api/cards/{id}")
    @ResponseBody
    public ResponseEntity<Card> getCard(@PathVariable Long id) {
        Card card = cardService.getById(id);
        return card != null ? ResponseEntity.ok(card) : ResponseEntity.notFound().build();
    }

    /**
     * SSE endpoint for streaming card updates
     */
    @GetMapping(value = "/api/cards/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter streamCards() {
        SseEmitter emitter = new SseEmitter(300_000L); // 5 minutes timeout
        cardEmitters.add(emitter);

        emitter.onCompletion(() -> cardEmitters.remove(emitter));
        emitter.onTimeout(() -> cardEmitters.remove(emitter));
        emitter.onError((ex) -> cardEmitters.remove(emitter));

        // Send initial data
        try {
            List<Card> cards = cardService.findAll();
            emitter.send(SseEmitter.event()
                    .name("initial-cards")
                    .data(cards));
        } catch (IOException e) {
            emitter.completeWithError(e);
            return emitter;
        }

        // Schedule periodic updates with simulated interest rate changes
        executor.scheduleAtFixedRate(() -> {
            try {
                List<Card> cards = cardService.findAll();
                // Simulate slight interest rate fluctuations for demo purposes
                cards.forEach(card -> {
                    double variation = (Math.random() - 0.5) * 0.5; // ±0.25% variation
                    BigDecimal newRate = card.getInterestRate().add(BigDecimal.valueOf(variation));
                    if (newRate.compareTo(BigDecimal.valueOf(10.0)) < 0) {
                        newRate = BigDecimal.valueOf(10.0);
                    }
                    if (newRate.compareTo(BigDecimal.valueOf(25.0)) > 0) {
                        newRate = BigDecimal.valueOf(25.0);
                    }
                    card.setInterestRate(newRate.setScale(2, java.math.RoundingMode.HALF_UP));
                });
                
                cardEmitters.forEach(sse -> {
                    try {
                        sse.send(SseEmitter.event()
                                .name("card-update")
                                .data(Map.of(
                                    "timestamp", LocalTime.now().toString(),
                                    "cards", cards,
                                    "message", "Interest rates updated"
                                )));
                    } catch (IOException e) {
                        cardEmitters.remove(sse);
                    }
                });
            } catch (Exception e) {
                // Log error but continue
                System.err.println("Error sending card updates: " + e.getMessage());
            }
        }, 10, 30, TimeUnit.SECONDS); // Send updates every 30 seconds

        return emitter;
    }

    /**
     * REST API endpoint to submit credit card application
     */
    @PostMapping("/api/applications")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitApplication(@RequestBody ApplicationRequest request) {
        Map<String, Object> result = mcpCardService.submitApplication(
                request.getName(),
                request.getSurname(),
                request.getSalary(),
                request.getBirthday()
        );

        // Notify all application stream listeners
        applicationEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("application-submitted")
                        .data(Map.of(
                            "timestamp", LocalTime.now().toString(),
                            "application", result,
                            "applicantName", request.getName() + " " + request.getSurname()
                        )));
            } catch (IOException e) {
                applicationEmitters.remove(emitter);
            }
        });

        return ResponseEntity.ok(result);
    }

    /**
     * SSE endpoint for streaming application status updates
     */
    @GetMapping(value = "/api/applications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter streamApplications() {
        SseEmitter emitter = new SseEmitter(300_000L); // 5 minutes timeout
        applicationEmitters.add(emitter);

        emitter.onCompletion(() -> applicationEmitters.remove(emitter));
        emitter.onTimeout(() -> applicationEmitters.remove(emitter));
        emitter.onError((ex) -> applicationEmitters.remove(emitter));

        // Send welcome message
        try {
            emitter.send(SseEmitter.event()
                    .name("connection-established")
                    .data(Map.of(
                        "message", "Connected to application status stream",
                        "timestamp", LocalTime.now().toString()
                    )));
        } catch (IOException e) {
            emitter.completeWithError(e);
            return emitter;
        }

        // Send periodic status updates
        executor.scheduleAtFixedRate(() -> {
            try {
                applicationEmitters.forEach(sse -> {
                    try {
                        // Simulate application processing updates
                        String[] statuses = {
                            "Applications being processed...",
                            "Credit checks in progress...",
                            "Identity verification running...",
                            "Risk assessment completed...",
                            "Approval decisions pending..."
                        };
                        String randomStatus = statuses[(int) (Math.random() * statuses.length)];
                        
                        sse.send(SseEmitter.event()
                                .name("processing-update")
                                .data(Map.of(
                                    "timestamp", LocalTime.now().toString(),
                                    "message", randomStatus,
                                    "activeApplications", (int) (Math.random() * 50) + 10
                                )));
                    } catch (IOException e) {
                        applicationEmitters.remove(sse);
                    }
                });
            } catch (Exception e) {
                System.err.println("Error sending application updates: " + e.getMessage());
            }
        }, 15, 45, TimeUnit.SECONDS); // Send updates every 45 seconds

        return emitter;
    }

    /**
     * Get bonuses endpoint
     */
    @GetMapping("/api/bonuses")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getBonuses() {
        List<Map<String, String>> bonuses = mcpCardService.getBonuses();
        return ResponseEntity.ok(bonuses);
    }
} 