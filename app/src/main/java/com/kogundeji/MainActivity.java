package com.kogundeji;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.apache.commons.math3.distribution.NormalDistribution;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import com.kogundeji.databinding.ActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bindMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindMain = DataBindingUtil.setContentView(this,R.layout.activity_main);

        bindMain.save.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,SaveActivity.class);
            startActivity(intent);
        });

        bindMain.expiration.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int exp_year,
                                                  int exp_month, int exp_day) {

                                String date = (exp_month + 1) + "/" + exp_day + "/" + exp_year;

                                bindMain.expirationNum.setText(date);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public int getDays() {

        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String today = (month + 1) + "/" + day + "/" + year;
        String future = bindMain.expirationNum.getText().toString().trim();

        try {
            SimpleDateFormat simple = new SimpleDateFormat("MM/dd/yyyy");
            Date today_date = simple.parse(today);
            Date future_date = simple.parse(future);

            long calc = future_date.getTime() - today_date.getTime();
            int difference = (int) (calc / (24*60*60*1000));
            Log.d("Testing expiration", "days: " + difference);
            return difference;

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Testing expiration", "Parse Exception");
        }
        return -1;
    }

    public void calculate(View view) {

        if (getDays() >= 0) {
            bindMain.callPrice.setText(calc_call());
            bindMain.putPrice.setText(calc_put());
            Log.d("Testing days", "Positive # of days");
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

    }

    public void clear(View view) {
        bindMain.spotNum.setText("");
        bindMain.strikeNum.setText("");
        bindMain.volNum.setText("");
        bindMain.rfRateNum.setText("");
        bindMain.expirationNum.setText("");
    }

    public String calc_call() {
        //use black-scholes model to calculate call option price
        //the delta_first_part part of the equation (e^-qt) is irrelevant because we assume dividends = 0. Equation always 1
        try {
            double spotD = Double.parseDouble(String.valueOf(bindMain.spotNum.getText()));
            double strikeD = Double.parseDouble(String.valueOf(bindMain.strikeNum.getText()));
            double rfD = Double.parseDouble(String.valueOf(bindMain.rfRateNum.getText())) / 100;
            double volD = Double.parseDouble(String.valueOf(bindMain.volNum.getText())) / 100;
            double timeD = (double) getDays() / 365;

            double delta_first_part = Math.log(spotD / strikeD);
            double delta_second_part = timeD * (rfD + Math.pow(volD, 2) / 2);
            double delta_third_part = volD * Math.pow(timeD, .5);


            double d1 = (delta_first_part + delta_second_part) / delta_third_part;
            double d2 = d1 - volD * Math.pow(timeD, .5);

            NormalDistribution norm_dist_d1 = new NormalDistribution();
            NormalDistribution norm_dist_d2 = new NormalDistribution();
            double Nd1 = norm_dist_d1.cumulativeProbability(d1);
            double Nd2 = norm_dist_d2.cumulativeProbability(d2);

            double theAnswer = (Nd1 * spotD) - (Nd2 * strikeD * 1 / Math.exp(rfD * timeD));

            return String.format("$%.2f", theAnswer);
        } catch (NumberFormatException e) {
            Log.d("Number Format: Call", "Number Format Exception!");
            e.printStackTrace();
        }

        return null;

    }

    private String calc_put() {
        //use black-scholes model to calculate call option price
        //the delta_first_part part of the equation (e^-qt) is irrelevant because we assume dividends = 0. Equation always 1
        try {
            double spotD = Double.parseDouble(String.valueOf(bindMain.spotNum.getText()));
            double strikeD = Double.parseDouble(String.valueOf(bindMain.strikeNum.getText()));
            double rfD = Double.parseDouble(String.valueOf(bindMain.rfRateNum.getText())) / 100;
            double volD = Double.parseDouble(String.valueOf(bindMain.volNum.getText())) / 100;
            double timeD = (double) getDays() / 365;

            double delta_first_part = Math.log(spotD / strikeD);
            double delta_second_part = timeD * (rfD + Math.pow(volD, 2) / 2);
            double delta_third_part = volD * Math.pow(timeD, .5);


            double d1 = (delta_first_part + delta_second_part) / delta_third_part;
            double d2 = d1 - volD * Math.pow(timeD, .5);

            NormalDistribution norm_dist_d1_put = new NormalDistribution();
            NormalDistribution norm_dist_d2_put = new NormalDistribution();
            double N_neg_d1 = norm_dist_d1_put.cumulativeProbability(-d1);
            double N_neg_d2 = norm_dist_d2_put.cumulativeProbability(-d2);

            Double theAnswer = (N_neg_d2 * strikeD * 1 / Math.exp(rfD * timeD)) - (N_neg_d1 * spotD);

            return String.format("$%.2f", theAnswer);
        } catch (NumberFormatException e) {
            Log.d("Number Format: Put", "Number Format Exception!");
            e.printStackTrace();
        }
        return null;
    }
}