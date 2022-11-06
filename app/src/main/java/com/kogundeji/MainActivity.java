package com.kogundeji;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingComponent;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.kogundeji.databinding.ActivityMainLayoutsBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainLayoutsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layouts);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main_layouts);

        binding.expiration.setEndIconOnClickListener(new View.OnClickListener() {
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
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                binding.expirationNum.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void calculate(View view) {
        binding.callPrice.setText(calc_call());
        binding.putPrice.setText(calc_put());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public void clear(View view) {
        binding.spotNum.setText("");
        binding.strikeNum.setText("");
        binding.volNum.setText("");
        binding.rfRateNum.setText("");
        binding.expirationNum.setText("");
    }

    public void saved(View view) {
        startActivity(new Intent(MainActivity.this,SaveActivity.class));
    }

    public String calc_call() {
        //use black-scholes model to calculate call option price
        //the delta_first_part part of the equation (e^-qt) is irrelevant because we assume dividends = 0. Equation always 1
        try {
            double spotD = Double.parseDouble(String.valueOf(binding.spotNum.getText()));
            double strikeD = Double.parseDouble(String.valueOf(binding.strikeNum.getText()));
            double rfD = Double.parseDouble(String.valueOf(binding.rfRateNum.getText())) / 100;
            double volD = Double.parseDouble(String.valueOf(binding.volNum.getText())) / 100;
            double timeD = Double.parseDouble(String.valueOf(binding.expirationNum.getText())) / 365;

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
            double spotD = Double.parseDouble(String.valueOf(binding.spotNum.getText()));
            double strikeD = Double.parseDouble(String.valueOf(binding.strikeNum.getText()));
            double rfD = Double.parseDouble(String.valueOf(binding.rfRateNum.getText())) / 100;
            double volD = Double.parseDouble(String.valueOf(binding.volNum.getText())) / 100;
            double timeD = Double.parseDouble(String.valueOf(binding.expirationNum.getText())) / 365;

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