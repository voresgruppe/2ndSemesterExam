package dk.vores.be;

import java.time.LocalDate;

public class DataExample {
    private LocalDate date;
    private int stockBeginningOfDay;
    private double unitsProduced;
    private double unitsSold;

    public DataExample(LocalDate date, int stockBeginningOfDay, double unitsProduced, double unitsSold) {
        this.date = date;
        this.stockBeginningOfDay = stockBeginningOfDay;
        this.unitsProduced = unitsProduced;
        this.unitsSold = unitsSold;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getStockBeginningOfDay() {
        return stockBeginningOfDay;
    }

    public void setStockBeginningOfDay(int stockBeginningOfDay) {
        this.stockBeginningOfDay = stockBeginningOfDay;
    }

    public double getUnitsProduced() {
        return unitsProduced;
    }

    public void setUnitsProduced(double unitsProduced) {
        this.unitsProduced = unitsProduced;
    }

    public double getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(double unitsSold) {
        this.unitsSold = unitsSold;
    }
}
