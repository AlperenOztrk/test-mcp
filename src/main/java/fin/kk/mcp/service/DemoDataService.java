package fin.kk.mcp.service;

import fin.kk.mcp.model.ApplicationStatus;
import fin.kk.mcp.model.CustomerProfile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing demo data including customers and applications
 */
@Service
public class DemoDataService {

    // In-memory storage for demo purposes
    private final Map<String, CustomerProfile> customers = new ConcurrentHashMap<>();
    private final Map<String, ApplicationStatus> applications = new ConcurrentHashMap<>();

    public DemoDataService() {
        initializeDemoData();
    }

    /**
     * Initialize demo data with realistic scenarios
     */
    private void initializeDemoData() {
        // --- DEMO CARD MAPPING: Realistic cards from Check24 API (cards.json) ---
        // Example cards (id, name):
        // 503247 - TF Mastercard Gold
        // 503186 - Barclays Visa
        // 503245 - Gebührenfrei Mastercard GOLD
        // 503262 - C24 Smart
        // 503248 - Hanseatic Bank GenialCard
        // 503249 - awa7 Visa Kreditkarte
        // 503250 - Deutschland-Kreditkarte Classic
        // 503251 - Consors Finanz Mastercard
        // 503252 - ICS Visa World Card
        // 503253 - DKB Visa Card
        // 503254 - Santander BestCard Basic
        // 503255 - comdirect Visa-Karte
        // 503256 - norisbank Mastercard Kreditkarte
        // 503257 - TARGOBANK Online-Classic-Karte
        // 503258 - PAYBACK Visa Flex+ Kreditkarte
        // 503259 - ADAC Kreditkarte
        // 503260 - Amazon.de Prime Visa Karte
        // 503261 - BMW Card
        // (See cards.json for more)

        // Create demo customers
        CustomerProfile johnDoe = createCustomer("CUST001", "John", "Doe", "john.doe@email.com");
        johnDoe.setPhoneNumber("+1-555-0123");
        johnDoe.setDateOfBirth(LocalDate.of(1985, 6, 15));
        johnDoe.setCustomerSegment("STANDARD");
        johnDoe.setPreferredContactMethod("EMAIL");

        CustomerProfile janeSmith = createCustomer("CUST002", "Jane", "Smith", "jane.smith@email.com");
        janeSmith.setPhoneNumber("+1-555-0456");
        janeSmith.setDateOfBirth(LocalDate.of(1990, 3, 22));
        janeSmith.setCustomerSegment("PREMIUM");
        janeSmith.setPreferredContactMethod("PHONE");

        CustomerProfile mikeJohnson = createCustomer("CUST003", "Mike", "Johnson", "mike.johnson@email.com");
        mikeJohnson.setPhoneNumber("+1-555-0789");
        mikeJohnson.setDateOfBirth(LocalDate.of(1982, 11, 8));
        mikeJohnson.setCustomerSegment("NEW");
        mikeJohnson.setPreferredContactMethod("SMS");

        CustomerProfile sarahWilson = createCustomer("CUST004", "Sarah", "Wilson", "sarah.wilson@email.com");
        sarahWilson.setPhoneNumber("+1-555-0321");
        sarahWilson.setDateOfBirth(LocalDate.of(1988, 9, 3));
        sarahWilson.setCustomerSegment("STANDARD");
        sarahWilson.setPreferredContactMethod("EMAIL");

        // --- DEMO APPLICATIONS WITH REALISTIC CARDS ---
        // John Doe - Recent application (TF Mastercard Gold)
        ApplicationStatus johnApp = createApplication("APP-001", johnDoe, 503247L, "TF Mastercard Gold", "UNDER_REVIEW");
        johnApp.setSubmissionDate(LocalDateTime.now().minusDays(3));
        johnApp.setProcessingDays(3);
        johnApp.setSalary(new BigDecimal("75000"));
        johnApp.setCreditScore(720);
        johnApp.setEstimatedDecisionDate(LocalDate.now().plusDays(4));
        johnApp.setStatusMessage("Your application is currently under review. We're verifying your information.");
        johnApp.setNextSteps("No action required. We'll contact you within 7 business days.");
        johnApp.setBirthday(johnDoe.getDateOfBirth());
        johnDoe.addApplication(johnApp);

        // Jane Smith - Old application (Barclays Visa)
        ApplicationStatus janeApp = createApplication("APP-002", janeSmith, 503186L, "Barclays Visa", "PENDING");
        janeApp.setSubmissionDate(LocalDateTime.now().minusDays(8));
        janeApp.setProcessingDays(8);
        janeApp.setSalary(new BigDecimal("95000"));
        janeApp.setCreditScore(780);
        janeApp.setEstimatedDecisionDate(LocalDate.now().plusDays(2));
        janeApp.setStatusMessage("Application pending final approval. Additional verification in progress.");
        janeApp.setNextSteps("We may contact you for additional documentation.");
        janeApp.setDocumentsRequired("Proof of income verification");
        janeApp.setBirthday(janeSmith.getDateOfBirth());
        janeSmith.addApplication(janeApp);

        // Mike Johnson - Approved application (Gebührenfrei Mastercard GOLD)
        ApplicationStatus mikeApp = createApplication("APP-003", mikeJohnson, 503245L, "Gebührenfrei Mastercard GOLD", "APPROVED");
        mikeApp.setSubmissionDate(LocalDateTime.now().minusDays(14));
        mikeApp.setProcessingDays(14);
        mikeApp.setSalary(new BigDecimal("68000"));
        mikeApp.setCreditScore(695);
        mikeApp.setEstimatedDecisionDate(LocalDate.now().minusDays(7));
        mikeApp.setStatusMessage("Congratulations! Your application has been approved.");
        mikeApp.setNextSteps("Your card will be shipped within 7-10 business days.");
        mikeApp.setBirthday(mikeJohnson.getDateOfBirth());
        mikeJohnson.addApplication(mikeApp);

        // Sarah Wilson - Multiple applications (C24 Smart, Hanseatic Bank GenialCard)
        ApplicationStatus sarahApp1 = createApplication("APP-004", sarahWilson, 503262L, "C24 Smart", "REJECTED");
        sarahApp1.setSubmissionDate(LocalDateTime.now().minusDays(30));
        sarahApp1.setProcessingDays(30);
        sarahApp1.setSalary(new BigDecimal("45000"));
        sarahApp1.setCreditScore(620);
        sarahApp1.setStatusMessage("Application was not approved due to credit requirements.");
        sarahApp1.setNextSteps("You may reapply after 6 months or consider other card options.");
        sarahApp1.setBirthday(sarahWilson.getDateOfBirth());
        sarahWilson.addApplication(sarahApp1);

        ApplicationStatus sarahApp2 = createApplication("APP-005", sarahWilson, 503248L, "Hanseatic Bank GenialCard", "UNDER_REVIEW");
        sarahApp2.setSubmissionDate(LocalDateTime.now().minusDays(5));
        sarahApp2.setProcessingDays(5);
        sarahApp2.setSalary(new BigDecimal("52000"));
        sarahApp2.setCreditScore(650);
        sarahApp2.setEstimatedDecisionDate(LocalDate.now().plusDays(3));
        sarahApp2.setStatusMessage("Your application is under review. Credit profile assessment in progress.");
        sarahApp2.setNextSteps("We'll notify you of our decision within 7 business days.");
        sarahApp2.setBirthday(sarahWilson.getDateOfBirth());
        sarahWilson.addApplication(sarahApp2);

        // Store customers and applications
        customers.put(johnDoe.getCustomerId(), johnDoe);
        customers.put(janeSmith.getCustomerId(), janeSmith);
        customers.put(mikeJohnson.getCustomerId(), mikeJohnson);
        customers.put(sarahWilson.getCustomerId(), sarahWilson);

        applications.put(johnApp.getApplicationId(), johnApp);
        applications.put(janeApp.getApplicationId(), janeApp);
        applications.put(mikeApp.getApplicationId(), mikeApp);
        applications.put(sarahApp1.getApplicationId(), sarahApp1);
        applications.put(sarahApp2.getApplicationId(), sarahApp2);
    }

    /**
     * Find customer by name (first + last name)
     */
    public CustomerProfile findCustomerByName(String fullName) {
        return customers.values().stream()
                .filter(customer -> customer.getFullName().equalsIgnoreCase(fullName.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find customer by first name (for partial matches)
     */
    public List<CustomerProfile> findCustomersByFirstName(String firstName) {
        return customers.values().stream()
                .filter(customer -> customer.getFirstName().equalsIgnoreCase(firstName.trim()))
                .toList();
    }

    /**
     * Get all applications for a customer
     */
    public List<ApplicationStatus> getCustomerApplications(String customerName) {
        CustomerProfile customer = findCustomerByName(customerName);
        if (customer != null) {
            return customer.getApplications();
        }
        
        // Try partial name match
        String[] parts = customerName.trim().split("\\s+");
        if (parts.length > 0) {
            List<CustomerProfile> matches = findCustomersByFirstName(parts[0]);
            if (!matches.isEmpty()) {
                return matches.get(0).getApplications();
            }
        }
        
        return Collections.emptyList();
    }

    /**
     * Get application by ID
     */
    public ApplicationStatus getApplicationById(String applicationId) {
        return applications.get(applicationId);
    }

    /**
     * Get all applications
     */
    public List<ApplicationStatus> getAllApplications() {
        return new ArrayList<>(applications.values());
    }

    /**
     * Get applications that might need proactive attention (old applications)
     */
    public List<ApplicationStatus> getApplicationsNeedingAttention() {
        return applications.values().stream()
                .filter(app -> app.isOldApplication() && 
                             ("PENDING".equals(app.getStatus()) || "UNDER_REVIEW".equals(app.getStatus())))
                .toList();
    }

    /**
     * Cancel an application
     */
    public Map<String, Object> cancelApplication(String applicationId) {
        Map<String, Object> result = new HashMap<>();
        ApplicationStatus application = applications.get(applicationId);
        
        if (application == null) {
            result.put("success", false);
            result.put("message", "Application not found: " + applicationId);
            return result;
        }
        
        if ("APPROVED".equals(application.getStatus()) || "REJECTED".equals(application.getStatus())) {
            result.put("success", false);
            result.put("message", "Cannot cancel application that is already " + application.getStatus().toLowerCase());
            return result;
        }
        
        application.setStatus("CANCELLED");
        application.setStatusMessage("Application cancelled by customer request.");
        application.setNextSteps("No further action required. You may apply for a new card anytime.");
        
        result.put("success", true);
        result.put("message", "Application " + applicationId + " has been successfully cancelled.");
        result.put("applicationId", applicationId);
        result.put("customerName", application.getCustomerName());
        
        return result;
    }

    /**
     * Estimate approval time for an application
     */
    public Map<String, Object> estimateApprovalTime(String applicationId) {
        Map<String, Object> result = new HashMap<>();
        ApplicationStatus application = applications.get(applicationId);
        
        if (application == null) {
            result.put("success", false);
            result.put("message", "Application not found: " + applicationId);
            return result;
        }
        
        int daysProcessed = application.getProcessingDays() != null ? application.getProcessingDays() : 0;
        int estimatedDaysRemaining;
        String status = application.getStatus();
        
        switch (status) {
            case "PENDING":
                estimatedDaysRemaining = Math.max(1, 7 - daysProcessed);
                break;
            case "UNDER_REVIEW":
                estimatedDaysRemaining = Math.max(1, 5 - daysProcessed);
                break;
            case "APPROVED":
                estimatedDaysRemaining = 0;
                break;
            case "REJECTED":
            case "CANCELLED":
                estimatedDaysRemaining = 0;
                break;
            default:
                estimatedDaysRemaining = 3;
        }
        
        result.put("success", true);
        result.put("applicationId", applicationId);
        result.put("currentStatus", status);
        result.put("daysProcessed", daysProcessed);
        result.put("estimatedDaysRemaining", estimatedDaysRemaining);
        result.put("estimatedCompletionDate", LocalDate.now().plusDays(estimatedDaysRemaining));
        
        if (estimatedDaysRemaining == 0) {
            result.put("message", "Application processing is complete.");
        } else {
            result.put("message", "Estimated " + estimatedDaysRemaining + " days remaining for decision.");
        }
        
        return result;
    }

    /**
     * Get customers with old applications (for proactive outreach)
     */
    public List<CustomerProfile> getCustomersWithOldApplications() {
        return customers.values().stream()
                .filter(CustomerProfile::hasOldApplications)
                .toList();
    }

    /**
     * Add a new application (when someone applies through the chat)
     */
    public ApplicationStatus addApplication(String customerName, Long cardId, String cardName, BigDecimal salary, LocalDate birthday) {
        // Find or create customer
        CustomerProfile customer = findCustomerByName(customerName);
        if (customer == null) {
            String[] parts = customerName.trim().split("\\s+");
            String firstName = parts[0];
            String lastName = parts.length > 1 ? parts[parts.length - 1] : "";
            String customerId = "CUST" + String.format("%03d", customers.size() + 1);
            customer = createCustomer(customerId, firstName, lastName, firstName.toLowerCase() + "." + lastName.toLowerCase() + "@email.com");
            customer.setDateOfBirth(birthday);
            customer.setCustomerSegment("NEW");
            customers.put(customer.getCustomerId(), customer);
        }
        
        // Create new application
        String applicationId = "APP-" + String.format("%03d", applications.size() + 1);
        ApplicationStatus application = createApplication(applicationId, customer, cardId, cardName, "PENDING");
        application.setSalary(salary);
        application.setBirthday(birthday);
        application.setEstimatedDecisionDate(LocalDate.now().plusDays(7));
        application.setStatusMessage("Application submitted successfully. Initial review in progress.");
        application.setNextSteps("We'll contact you within 7-10 business days with a decision.");
        application.setProcessingDays(0);
        
        // Simulate credit score based on salary
        int creditScore = calculateEstimatedCreditScore(salary);
        application.setCreditScore(creditScore);
        
        customer.addApplication(application);
        applications.put(application.getApplicationId(), application);
        
        return application;
    }

    // Helper methods
    private CustomerProfile createCustomer(String customerId, String firstName, String lastName, String email) {
        CustomerProfile customer = new CustomerProfile(customerId, firstName, lastName, email);
        customer.setRegistrationDate(LocalDateTime.now().minusDays((int) (Math.random() * 365)));
        customer.setLastLoginDate(LocalDateTime.now().minusDays((int) (Math.random() * 30)));
        return customer;
    }

    private ApplicationStatus createApplication(String applicationId, CustomerProfile customer, Long cardId, String cardName, String status) {
        ApplicationStatus application = new ApplicationStatus(applicationId, customer.getFullName(), cardId, cardName, status);
        return application;
    }

    private int calculateEstimatedCreditScore(BigDecimal salary) {
        // Simple estimation based on salary
        double salaryDouble = salary.doubleValue();
        if (salaryDouble >= 100000) return 750 + (int) (Math.random() * 50);
        if (salaryDouble >= 75000) return 700 + (int) (Math.random() * 50);
        if (salaryDouble >= 50000) return 650 + (int) (Math.random() * 50);
        return 600 + (int) (Math.random() * 50);
    }

    // Getters for external access
    public Collection<CustomerProfile> getAllCustomers() {
        return customers.values();
    }

    public int getTotalCustomers() {
        return customers.size();
    }

    public int getTotalApplications() {
        return applications.size();
    }
} 