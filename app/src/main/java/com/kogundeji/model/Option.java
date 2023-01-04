package com.kogundeji.model;

public class Option {

    int id;
    String expiration, ticker_symbol;
    double strike, rfRate, volatility, currentPrice;

    public Option() {
    }

    public Option(String expiration, double strike, double volatility, double currentPrice, double rfRate) {
        this.expiration = expiration;
        this.strike = strike;
        this.volatility = volatility;
        this.currentPrice = currentPrice;
        this.rfRate = rfRate;
    }

    @Override
    public String toString() {
        return "Option{" +
                "id=" + id +
                ", expiration='" + expiration + '\'' +
                ", strike=" + strike +
                ", rfRate=" + rfRate +
                ", ticker_symbol='" + ticker_symbol + '\'' +
                ", volatility=" + volatility +
                ", current=" + currentPrice +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicker_symbol() {
        return ticker_symbol;
    }

    public void setTicker_symbol(String ticker_symbol) {
        this.ticker_symbol = ticker_symbol;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getStrike() {
        return strike;
    }

    public void setStrike(double strike) {
        this.strike = strike;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public double getRfRate() {
        return rfRate;
    }

    public void setRfRate(double rfRate) {
        this.rfRate = rfRate;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}
