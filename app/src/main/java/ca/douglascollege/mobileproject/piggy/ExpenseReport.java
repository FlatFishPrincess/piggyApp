package ca.douglascollege.mobileproject.piggy;

public class ExpenseReport {

    public Double value;
    public String name;

    // constructor
    ExpenseReport(){

    }

    ExpenseReport(String name, double value ){
        this.value = value;
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
