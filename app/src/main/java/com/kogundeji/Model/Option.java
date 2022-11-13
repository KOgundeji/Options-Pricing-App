package com.kogundeji.Model;

public class Option {
    public static final int CALL = 0;
    public static final int PUT = 1;

    String ticker_symbol, expiration;
    int optionType;
    double current, strike, volatility, rfRate;

    public Option(String ticker_symbol, int optionType,
                  double current, double strike, double volatility, double rfRate, String expiration) {
        this.ticker_symbol = ticker_symbol;
        this.optionType = optionType;
        this.current = current;
        this.strike = strike;
        this.volatility = volatility;
        this.rfRate = rfRate;
        this.expiration = expiration;
    }

    public String getTicker_symbol() {
        return ticker_symbol;
    }

    public void setTicker_symbol(String ticker_symbol) {
        this.ticker_symbol = ticker_symbol;
    }

    public int getOptionType() {
        return optionType;
    }

    public String getOptionType_Letter() {
        if (optionType == 0) {
            return "C";
        }
        return "P";
    }

    public void setOptionType(int optionType) {
        this.optionType = optionType;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
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
