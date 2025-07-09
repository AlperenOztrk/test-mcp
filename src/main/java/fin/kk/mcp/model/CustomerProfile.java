package fin.kk.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a customer profile with application history
 */
public class CustomerProfile {
    
    @JsonProperty("customerId")
    private String customerId;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    
    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;
    
    @JsonProperty("registrationDate")
    private LocalDateTime registrationDate;
    
    @JsonProperty("lastLoginDate")
    private LocalDateTime lastLoginDate;
    
    @JsonProperty("applications")
    private List<ApplicationStatus> applications;
    
    @JsonProperty("preferredContactMethod")
    private String preferredContactMethod;
    
    @JsonProperty("customerSegment")
    private String customerSegment; // PREMIUM, STANDARD, NEW
    
    @JsonProperty("notes")
    private String notes;
    
    public CustomerProfile() {
        this.applications = new ArrayList<>();
        this.registrationDate = LocalDateTime.now();
        this.lastLoginDate = LocalDateTime.now();
    }
    
    public CustomerProfile(String customerId, String firstName, String lastName, String email) {
        this();
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    /**
     * Get full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Add an application to this customer's history
     */
    public void addApplication(ApplicationStatus application) {
        this.applications.add(application);
    }
    
    /**
     * Get active applications (not cancelled or rejected)
     */
    public List<ApplicationStatus> getActiveApplications() {
        return applications.stream()
                .filter(app -> !"CANCELLED".equals(app.getStatus()) && !"REJECTED".equals(app.getStatus()))
                .toList();
    }
    
    /**
     * Get pending applications
     */
    public List<ApplicationStatus> getPendingApplications() {
        return applications.stream()
                .filter(app -> "PENDING".equals(app.getStatus()) || "UNDER_REVIEW".equals(app.getStatus()))
                .toList();
    }
    
    /**
     * Get most recent application
     */
    public ApplicationStatus getMostRecentApplication() {
        return applications.stream()
                .max((a1, a2) -> a1.getSubmissionDate().compareTo(a2.getSubmissionDate()))
                .orElse(null);
    }
    
    /**
     * Check if customer has any old applications (>7 days)
     */
    public boolean hasOldApplications() {
        return applications.stream()
                .anyMatch(ApplicationStatus::isOldApplication);
    }
    
    // Getters and setters
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    
    public List<ApplicationStatus> getApplications() {
        return applications;
    }
    
    public void setApplications(List<ApplicationStatus> applications) {
        this.applications = applications;
    }
    
    public String getPreferredContactMethod() {
        return preferredContactMethod;
    }
    
    public void setPreferredContactMethod(String preferredContactMethod) {
        this.preferredContactMethod = preferredContactMethod;
    }
    
    public String getCustomerSegment() {
        return customerSegment;
    }
    
    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 