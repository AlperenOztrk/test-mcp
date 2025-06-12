package fin.kk.mcp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Check24ApiResponse {
    
    @JsonProperty("meta")
    private Meta meta;
    
    @JsonProperty("offers")
    private List<Offer> offers;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        @JsonProperty("unfilteredCount")
        private Integer unfilteredCount;
        
        @JsonProperty("filteredCount")
        private Integer filteredCount;

        public Integer getUnfilteredCount() {
            return unfilteredCount;
        }

        public void setUnfilteredCount(Integer unfilteredCount) {
            this.unfilteredCount = unfilteredCount;
        }

        public Integer getFilteredCount() {
            return filteredCount;
        }

        public void setFilteredCount(Integer filteredCount) {
            this.filteredCount = filteredCount;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Offer {
        @JsonProperty("product")
        private Product product;
        
        @JsonProperty("position")
        private Integer position;
        
        @JsonProperty("applicationUrl")
        private String applicationUrl;

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public Integer getPosition() {
            return position;
        }

        public void setPosition(Integer position) {
            this.position = position;
        }

        public String getApplicationUrl() {
            return applicationUrl;
        }

        public void setApplicationUrl(String applicationUrl) {
            this.applicationUrl = applicationUrl;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("bank")
        private Bank bank;
        
        @JsonProperty("benefits")
        private List<String> benefits;
        
        @JsonProperty("drawbacks")
        private List<String> drawbacks;
        
        @JsonProperty("logoUrl")
        private String logoUrl;
        
        @JsonProperty("c24OpeningBonus")
        private Double c24OpeningBonus;
        
        @JsonProperty("productCondition")
        private ProductCondition productCondition;
        
        @JsonProperty("customerFeedbackSummary")
        private CustomerFeedbackSummary customerFeedbackSummary;
        
        @JsonProperty("grade")
        private Grade grade;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Bank getBank() {
            return bank;
        }

        public void setBank(Bank bank) {
            this.bank = bank;
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

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public Double getC24OpeningBonus() {
            return c24OpeningBonus;
        }

        public void setC24OpeningBonus(Double c24OpeningBonus) {
            this.c24OpeningBonus = c24OpeningBonus;
        }

        public ProductCondition getProductCondition() {
            return productCondition;
        }

        public void setProductCondition(ProductCondition productCondition) {
            this.productCondition = productCondition;
        }

        public CustomerFeedbackSummary getCustomerFeedbackSummary() {
            return customerFeedbackSummary;
        }

        public void setCustomerFeedbackSummary(CustomerFeedbackSummary customerFeedbackSummary) {
            this.customerFeedbackSummary = customerFeedbackSummary;
        }

        public Grade getGrade() {
            return grade;
        }

        public void setGrade(Grade grade) {
            this.grade = grade;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bank {
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("name")
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductCondition {
        @JsonProperty("cost")
        private Cost cost;
        
        @JsonProperty("cardSystem")
        private String cardSystem;
        
        @JsonProperty("cardType")
        private String cardType;
        
        @JsonProperty("productPayment")
        private ProductPayment productPayment;
        
        @JsonProperty("productInsurance")
        private ProductInsurance productInsurance;
        
        @JsonProperty("debitInterest")
        private Double debitInterest;
        
        @JsonProperty("contactlessPayment")
        private Boolean contactlessPayment;
        
        @JsonProperty("paymentGrade")
        private String paymentGrade;
        
        @JsonProperty("withdrawGrade")
        private String withdrawGrade;
        
        @JsonProperty("insuranceGrade")
        private String insuranceGrade;

        public Cost getCost() {
            return cost;
        }

        public void setCost(Cost cost) {
            this.cost = cost;
        }

        public String getCardSystem() {
            return cardSystem;
        }

        public void setCardSystem(String cardSystem) {
            this.cardSystem = cardSystem;
        }

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public ProductPayment getProductPayment() {
            return productPayment;
        }

        public void setProductPayment(ProductPayment productPayment) {
            this.productPayment = productPayment;
        }

        public ProductInsurance getProductInsurance() {
            return productInsurance;
        }

        public void setProductInsurance(ProductInsurance productInsurance) {
            this.productInsurance = productInsurance;
        }

        public Double getDebitInterest() {
            return debitInterest;
        }

        public void setDebitInterest(Double debitInterest) {
            this.debitInterest = debitInterest;
        }

        public Boolean getContactlessPayment() {
            return contactlessPayment;
        }

        public void setContactlessPayment(Boolean contactlessPayment) {
            this.contactlessPayment = contactlessPayment;
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
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cost {
        @JsonProperty("cost")
        private Double cost;
        
        @JsonProperty("costSecondYear")
        private Double costSecondYear;

        public Double getCost() {
            return cost;
        }

        public void setCost(Double cost) {
            this.cost = cost;
        }

        public Double getCostSecondYear() {
            return costSecondYear;
        }

        public void setCostSecondYear(Double costSecondYear) {
            this.costSecondYear = costSecondYear;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductPayment {
        @JsonProperty("freeUseEuCurrency")
        private Boolean freeUseEuCurrency;
        
        @JsonProperty("freeUseWorldCurrency")
        private Boolean freeUseWorldCurrency;
        
        @JsonProperty("freeWithdrawEu")
        private Boolean freeWithdrawEu;
        
        @JsonProperty("freeWithdrawWorld")
        private Boolean freeWithdrawWorld;
        
        @JsonProperty("mobilePaymentSystems")
        private List<String> mobilePaymentSystems;

        public Boolean getFreeUseEuCurrency() {
            return freeUseEuCurrency;
        }

        public void setFreeUseEuCurrency(Boolean freeUseEuCurrency) {
            this.freeUseEuCurrency = freeUseEuCurrency;
        }

        public Boolean getFreeUseWorldCurrency() {
            return freeUseWorldCurrency;
        }

        public void setFreeUseWorldCurrency(Boolean freeUseWorldCurrency) {
            this.freeUseWorldCurrency = freeUseWorldCurrency;
        }

        public Boolean getFreeWithdrawEu() {
            return freeWithdrawEu;
        }

        public void setFreeWithdrawEu(Boolean freeWithdrawEu) {
            this.freeWithdrawEu = freeWithdrawEu;
        }

        public Boolean getFreeWithdrawWorld() {
            return freeWithdrawWorld;
        }

        public void setFreeWithdrawWorld(Boolean freeWithdrawWorld) {
            this.freeWithdrawWorld = freeWithdrawWorld;
        }

        public List<String> getMobilePaymentSystems() {
            return mobilePaymentSystems;
        }

        public void setMobilePaymentSystems(List<String> mobilePaymentSystems) {
            this.mobilePaymentSystems = mobilePaymentSystems;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductInsurance {
        @JsonProperty("healthInsuranceAbroad")
        private Boolean healthInsuranceAbroad;
        
        @JsonProperty("accidentInsuranceAbroad")
        private Boolean accidentInsuranceAbroad;
        
        @JsonProperty("travelCancelInsurance")
        private Boolean travelCancelInsurance;

        public Boolean getHealthInsuranceAbroad() {
            return healthInsuranceAbroad;
        }

        public void setHealthInsuranceAbroad(Boolean healthInsuranceAbroad) {
            this.healthInsuranceAbroad = healthInsuranceAbroad;
        }

        public Boolean getAccidentInsuranceAbroad() {
            return accidentInsuranceAbroad;
        }

        public void setAccidentInsuranceAbroad(Boolean accidentInsuranceAbroad) {
            this.accidentInsuranceAbroad = accidentInsuranceAbroad;
        }

        public Boolean getTravelCancelInsurance() {
            return travelCancelInsurance;
        }

        public void setTravelCancelInsurance(Boolean travelCancelInsurance) {
            this.travelCancelInsurance = travelCancelInsurance;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomerFeedbackSummary {
        @JsonProperty("overallStars")
        private Double overallStars;
        
        @JsonProperty("countFeedbacks")
        private Integer countFeedbacks;

        public Double getOverallStars() {
            return overallStars;
        }

        public void setOverallStars(Double overallStars) {
            this.overallStars = overallStars;
        }

        public Integer getCountFeedbacks() {
            return countFeedbacks;
        }

        public void setCountFeedbacks(Integer countFeedbacks) {
            this.countFeedbacks = countFeedbacks;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Grade {
        @JsonProperty("exactGrade")
        private Double exactGrade;
        
        @JsonProperty("gradeDescription")
        private String gradeDescription;

        public Double getExactGrade() {
            return exactGrade;
        }

        public void setExactGrade(Double exactGrade) {
            this.exactGrade = exactGrade;
        }

        public String getGradeDescription() {
            return gradeDescription;
        }

        public void setGradeDescription(String gradeDescription) {
            this.gradeDescription = gradeDescription;
        }
    }
} 