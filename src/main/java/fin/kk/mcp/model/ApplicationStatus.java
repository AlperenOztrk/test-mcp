package fin.kk.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing a credit card application status
 */
public class ApplicationStatus {
    
    @JsonProperty("applicationId")
    private String applicationId;
    
    @JsonProperty("customerName")
    private String customerName;
    
    @JsonProperty("cardId")
    private Long cardId;
    
    @JsonProperty("cardName")
    private String cardName;
    
    @JsonProperty("status")
    private String status; // PENDING, UNDER_REVIEW, APPROVED, REJECTED, CANCELLED
    
    @JsonProperty("submissionDate")
    private LocalDateTime submissionDate;
    
    @JsonProperty("lastUpdated")
    private LocalDateTime lastUpdated;
    
    @JsonProperty("estimatedDecisionDate")
    private LocalDate estimatedDecisionDate;
    
    @JsonProperty("salary")
    private BigDecimal salary;
    
    @JsonProperty("creditScore")
    private Integer creditScore;
    
    @JsonProperty("statusMessage")
    private String statusMessage;
    
    @JsonProperty("nextSteps")
    private String nextSteps;
    
    @JsonProperty("documentsRequired")
    private String documentsRequired;
    
    @JsonProperty("processingDays")
    private Integer processingDays;
    
    @JsonProperty("birthday")
    private LocalDate birthday;
    
    public ApplicationStatus() {
        this.submissionDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }
    
    public ApplicationStatus(String applicationId, String customerName, Long cardId, String cardName, String status) {
        this();
        this.applicationId = applicationId;
        this.customerName = customerName;
        this.cardId = cardId;
        this.cardName = cardName;
        this.status = status;
    }
    
    // Getters and setters
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public Long getCardId() {
        return cardId;
    }
    
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
    
    public String getCardName() {
        return cardName;
    }
    
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public LocalDate getEstimatedDecisionDate() {
        return estimatedDecisionDate;
    }
    
    public void setEstimatedDecisionDate(LocalDate estimatedDecisionDate) {
        this.estimatedDecisionDate = estimatedDecisionDate;
    }
    
    public BigDecimal getSalary() {
        return salary;
    }
    
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    
    public Integer getCreditScore() {
        return creditScore;
    }
    
    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }
    
    public String getStatusMessage() {
        return statusMessage;
    }
    
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
    
    public String getNextSteps() {
        return nextSteps;
    }
    
    public void setNextSteps(String nextSteps) {
        this.nextSteps = nextSteps;
    }
    
    public String getDocumentsRequired() {
        return documentsRequired;
    }
    
    public void setDocumentsRequired(String documentsRequired) {
        this.documentsRequired = documentsRequired;
    }
    
    public Integer getProcessingDays() {
        return processingDays;
    }
    
    public void setProcessingDays(Integer processingDays) {
        this.processingDays = processingDays;
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }
    
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    /**
     * Helper method to check if application is old (more than 7 days)
     */
    public boolean isOldApplication() {
        return processingDays != null && processingDays > 7;
    }
    
    /**
     * Helper method to get days since submission
     */
    public long getDaysSinceSubmission() {
        return java.time.temporal.ChronoUnit.DAYS.between(submissionDate.toLocalDate(), LocalDate.now());
    }
} 