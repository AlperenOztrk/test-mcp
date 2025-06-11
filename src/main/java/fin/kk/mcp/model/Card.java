package fin.kk.mcp.model;

import java.math.BigDecimal;

public class Card {
    private Long id;
    private String bankName;
    private String cardName;
    private BigDecimal yearlyFee;
    private String bonus;
    private BigDecimal interestRate;

    // Default constructor
    public Card() {}

    // Constructor with all fields
    public Card(Long id, String bankName, String cardName, BigDecimal yearlyFee, String bonus, BigDecimal interestRate) {
        this.id = id;
        this.bankName = bankName;
        this.cardName = cardName;
        this.yearlyFee = yearlyFee;
        this.bonus = bonus;
        this.interestRate = interestRate;
    }

    // Getters and setters
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

    public BigDecimal getYearlyFee() {
        return yearlyFee;
    }

    public void setYearlyFee(BigDecimal yearlyFee) {
        this.yearlyFee = yearlyFee;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", bankName='" + bankName + '\'' +
                ", cardName='" + cardName + '\'' +
                ", yearlyFee=" + yearlyFee +
                ", bonus='" + bonus + '\'' +
                ", interestRate=" + interestRate +
                '}';
    }
} 