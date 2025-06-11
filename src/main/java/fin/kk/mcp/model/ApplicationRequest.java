package fin.kk.mcp.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ApplicationRequest {
    private String name;
    private String surname; 
    private BigDecimal salary;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    // Default constructor
    public ApplicationRequest() {}

    // Constructor with all fields
    public ApplicationRequest(String name, String surname, BigDecimal salary, LocalDate birthday) {
        this.name = name;
        this.surname = surname;
        this.salary = salary;
        this.birthday = birthday;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "ApplicationRequest{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", salary=" + salary +
                ", birthday=" + birthday +
                '}';
    }
} 