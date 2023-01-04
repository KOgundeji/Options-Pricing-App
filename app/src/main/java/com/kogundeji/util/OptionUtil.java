package com.kogundeji.util;

import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import com.kogundeji.model.Option;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OptionUtil {
    //class of static methods to help with Option formatting and calculations
    //risk-free rate assumed to be 5% for existing options (for simplicity)
    public final static double riskFreeRate = 5;

    public static String getString(String stockTicker, double strikePrice, String expiration) {
        String fullTicker = "";
        try {
            SimpleDateFormat simpleDate = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat simpleDateforRequest = new SimpleDateFormat("MMddyy");
            String formattedDate = simpleDateforRequest.format(simpleDate.parse(expiration));
            fullTicker = stockTicker + "_" + formattedDate + "C" + getFormattedStrikeString(strikePrice);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fullTicker;
    }

    public static double getDaysRemaining(String expiration) {
        //calculates how many days between today and selected date
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String today = (month + 1) + "/" + day + "/" + year;

        try {
            SimpleDateFormat simple = new SimpleDateFormat("MM/dd/yyyy");
            Date today_date = simple.parse(today);
            Date future_date = simple.parse(expiration);

            long calc = future_date.getTime() - today_date.getTime();
            int difference = (int) (calc / (24 * 60 * 60 * 1000));
            return difference;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String calculateCallPrice(Context context, Option option) {
        //use black-scholes model to calculate call option price
        //the delta_first_part part of the equation (e^-qt) is irrelevant because we assume dividends = 0. Equation always 1

        //only need "context" for either Call or Put calculation, not both, since we're only using it to create a Toast
        try {
            double currentPrice = option.getCurrentPrice();
            double strikePrice = option.getStrike();
            double volatility = option.getVolatility() / 100;
            double riskFree = option.getRfRate() / 100;
            double yearsRemaining = OptionUtil.getDaysRemaining(option.getExpiration()) / 365;

            double delta_first_part = Math.log(currentPrice / strikePrice);
            double delta_second_part = yearsRemaining * (riskFree + Math.pow(volatility, 2) / 2);
            double delta_third_part = volatility * Math.pow(yearsRemaining, .5);


            double d1 = (delta_first_part + delta_second_part) / delta_third_part;
            double d2 = d1 - volatility * Math.pow(yearsRemaining, .5);

            NormalDistribution norm_dist_d1 = new NormalDistribution();
            NormalDistribution norm_dist_d2 = new NormalDistribution();
            double Nd1 = norm_dist_d1.cumulativeProbability(d1);
            double Nd2 = norm_dist_d2.cumulativeProbability(d2);

            double theAnswer = (Nd1 * currentPrice) - (Nd2 * strikePrice * 1 / Math.exp(riskFree * yearsRemaining));

            return String.format("$%.2f", theAnswer);
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Error. Please check inputs again.",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    public static String calculatePutPrice(Option option) {
        //use black-scholes model to calculate put option price
        //the delta_first_part part of the equation (e^-qt) is irrelevant because we assume dividends = 0. Equation always 1
        try {
            double currentPrice = option.getCurrentPrice();
            double strikePrice = option.getStrike();
            double volatility = option.getVolatility() / 100;
            double riskFree = option.getRfRate() / 100;
            double daysRemaining = OptionUtil.getDaysRemaining(option.getExpiration()) / 365;

            double delta_first_part = Math.log(currentPrice / strikePrice);
            double delta_second_part = daysRemaining * (riskFree + Math.pow(volatility, 2) / 2);
            double delta_third_part = volatility * Math.pow(daysRemaining, .5);


            double d1 = (delta_first_part + delta_second_part) / delta_third_part;
            double d2 = d1 - volatility * Math.pow(daysRemaining, .5);

            NormalDistribution norm_dist_d1_put = new NormalDistribution();
            NormalDistribution norm_dist_d2_put = new NormalDistribution();
            double N_neg_d1 = norm_dist_d1_put.cumulativeProbability(-d1);
            double N_neg_d2 = norm_dist_d2_put.cumulativeProbability(-d2);

            Double theAnswer = (N_neg_d2 * strikePrice * 1 / Math.exp(riskFree * daysRemaining)) - (N_neg_d1 * currentPrice);

            return String.format("$%.2f", theAnswer);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFormattedStrikeString(double strike) {
        //helper method to set the strike price formatting
        if ((int) strike == strike) {
            return String.format("%,.0f", strike);
        }
        return String.format("%,.1f", strike);
    }

    public static boolean isNetworkAvailable() {
        boolean availability = false;
        try {
            availability = InetAddress.getByName("www.google.com").isReachable(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availability;
    }


}
