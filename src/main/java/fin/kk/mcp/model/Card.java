package fin.kk.mcp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {
    private Long id;
    private String bankName;
    private String cardName;
    private BigDecimal annualFee;
    private String signupBonus;
    private BigDecimal interestRate;
    
    // Additional fields from Check24 API
    private String cardType; // CREDIT, DEBIT, PREPAID
    private String cardSystem; // VISA, MASTER, AMEX
    private List<String> benefits;
    private List<String> drawbacks;
    private BigDecimal bonusValue;
    private String bonusCondition;
    private boolean contactlessPayment;
    private boolean applePaySupported;
    private boolean googlePaySupported;
    private String paymentGrade; // WORLDWIDE, EURO_ZONE, etc.
    private String withdrawGrade; // NO_FREE_WITHDRAW, etc.
    private String insuranceGrade; // VERY_GOOD, NOT_AVAILABLE, etc.
    private BigDecimal customerRating;
    private Integer customerFeedbackCount;
    private String gradeDescription;
    
    // Payment related
    private boolean freePaymentEuroZone;
    private boolean freePaymentWorldwide;
    private boolean freeWithdrawEuroZone;
    private boolean freeWithdrawWorldwide;
    
    // Insurance related
    private boolean travelInsurance;
    private boolean healthInsuranceAbroad;
    private boolean accidentInsuranceAbroad;
    
    // Constructors
    public Card() {
    }

    public Card(Long id, String bankName, String cardName, BigDecimal annualFee, 
                String signupBonus, BigDecimal interestRate) {
        this.id = id;
        this.bankName = bankName;
        this.cardName = cardName;
        this.annualFee = annualFee;
        this.signupBonus = signupBonus;
        this.interestRate = interestRate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public String getSignupBonus() {
        return signupBonus;
    }

    public void setSignupBonus(String signupBonus) {
        this.signupBonus = signupBonus;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardSystem() {
        return cardSystem;
    }

    public void setCardSystem(String cardSystem) {
        this.cardSystem = cardSystem;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public List<String> getDrawbacks() {
        return drawbacks;
    }

    public void setDrawbacks(List<String> drawbacks) {
        this.drawbacks = drawbacks;
    }

    public BigDecimal getBonusValue() {
        return bonusValue;
    }

    public void setBonusValue(BigDecimal bonusValue) {
        this.bonusValue = bonusValue;
    }

    public String getBonusCondition() {
        return bonusCondition;
    }

    public void setBonusCondition(String bonusCondition) {
        this.bonusCondition = bonusCondition;
    }

    public boolean isContactlessPayment() {
        return contactlessPayment;
    }

    public void setContactlessPayment(boolean contactlessPayment) {
        this.contactlessPayment = contactlessPayment;
    }

    public boolean isApplePaySupported() {
        return applePaySupported;
    }

    public void setApplePaySupported(boolean applePaySupported) {
        this.applePaySupported = applePaySupported;
    }

    public boolean isGooglePaySupported() {
        return googlePaySupported;
    }

    public void setGooglePaySupported(boolean googlePaySupported) {
        this.googlePaySupported = googlePaySupported;
    }

    public String getPaymentGrade() {
        return paymentGrade;
    }

    public void setPaymentGrade(String paymentGrade) {
        this.paymentGrade = paymentGrade;
    }

    public String getWithdrawGrade() {
        return withdrawGrade;
    }

    public void setWithdrawGrade(String withdrawGrade) {
        this.withdrawGrade = withdrawGrade;
    }

    public String getInsuranceGrade() {
        return insuranceGrade;
    }

    public void setInsuranceGrade(String insuranceGrade) {
        this.insuranceGrade = insuranceGrade;
    }

    public BigDecimal getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(BigDecimal customerRating) {
        this.customerRating = customerRating;
    }

    public Integer getCustomerFeedbackCount() {
        return customerFeedbackCount;
    }

    public void setCustomerFeedbackCount(Integer customerFeedbackCount) {
        this.customerFeedbackCount = customerFeedbackCount;
    }

    public String getGradeDescription() {
        return gradeDescription;
    }

    public void setGradeDescription(String gradeDescription) {
        this.gradeDescription = gradeDescription;
    }

    public boolean isFreePaymentEuroZone() {
        return freePaymentEuroZone;
    }

    public void setFreePaymentEuroZone(boolean freePaymentEuroZone) {
        this.freePaymentEuroZone = freePaymentEuroZone;
    }

    public boolean isFreePaymentWorldwide() {
        return freePaymentWorldwide;
    }

    public void setFreePaymentWorldwide(boolean freePaymentWorldwide) {
        this.freePaymentWorldwide = freePaymentWorldwide;
    }

    public boolean isFreeWithdrawEuroZone() {
        return freeWithdrawEuroZone;
    }

    public void setFreeWithdrawEuroZone(boolean freeWithdrawEuroZone) {
        this.freeWithdrawEuroZone = freeWithdrawEuroZone;
    }

    public boolean isFreeWithdrawWorldwide() {
        return freeWithdrawWorldwide;
    }

    public void setFreeWithdrawWorldwide(boolean freeWithdrawWorldwide) {
        this.freeWithdrawWorldwide = freeWithdrawWorldwide;
    }

    public boolean isTravelInsurance() {
        return travelInsurance;
    }

    public void setTravelInsurance(boolean travelInsurance) {
        this.travelInsurance = travelInsurance;
    }

    public boolean isHealthInsuranceAbroad() {
        return healthInsuranceAbroad;
    }

    public void setHealthInsuranceAbroad(boolean healthInsuranceAbroad) {
        this.healthInsuranceAbroad = healthInsuranceAbroad;
    }

    public boolean isAccidentInsuranceAbroad() {
        return accidentInsuranceAbroad;
    }

    public void setAccidentInsuranceAbroad(boolean accidentInsuranceAbroad) {
        this.accidentInsuranceAbroad = accidentInsuranceAbroad;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", bankName='" + bankName + '\'' +
                ", cardName='" + cardName + '\'' +
                ", annualFee=" + annualFee +
                ", signupBonus='" + signupBonus + '\'' +
                ", interestRate=" + interestRate +
                ", cardType='" + cardType + '\'' +
                ", cardSystem='" + cardSystem + '\'' +
                ", bonusValue=" + bonusValue +
                ", customerRating=" + customerRating +
                ", gradeDescription='" + gradeDescription + '\'' +
                '}';
    }
} 