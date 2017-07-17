package com.example.marketstocks;

/**
 * Created by Dhruv on 12-03-2017.
 */

public class Stock {
    private String name;
    private String Bid;
    private String symbol;
    private String Change_PercentChange;
    public Stock(String Bid, String symbol, String change, String name){
        this.Bid = Bid;
        this.symbol = symbol;
        this.Change_PercentChange = change;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBid() {
        return Bid;
    }

    public void Bid(String name) {
        this.Bid = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getChange() {
        return Change_PercentChange;
    }

    public void setChange(String change_PercentChange) {
        Change_PercentChange = change_PercentChange;
    }
}
