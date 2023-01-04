package com.kogundeji;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.apache.commons.math3.distribution.NormalDistribution;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.kogundeji.databinding.ActivityMainBinding;
import com.kogundeji.model.Option;
import com.kogundeji.util.OptionUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bindMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindMain = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bindMain.expirationNum.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE); //created because Android Studio and XML are acting weird with DateTime inputtype

        setEndIconListener();
    }

    private void setEndIconListener() {
        bindMain.expiration.setEndIconOnClickListener(new View.OnClickListener() {
            boolean success;
            SimpleDateFormat simple;
            LocalDate local = null;
            int year, month, day;
            final Calendar c = Calendar.getInstance();

            @Override
            public void onClick(View view) {
                //try block to see if entered Date is an actual date and in the right format.
                try {
                    simple = new SimpleDateFormat(getString(R.string.preformatted_date));
                    Date date = simple.parse(bindMain.expirationNum.getText().toString());
                    local = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    success = true;
                } catch (ParseException e) {
                    success = false;
                }

                //starts DatePicker on entered date if there is a valid date already in EditText. Quality of Life feature
                if (success) {
                    year = local.getYear();
                    month = local.getMonthValue() - 1;
                    day = local.getDayOfMonth();
                } else {
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                }

                //created DatePicker for calendar icon at end of EditText "Expiration"
                //will take this date and calculate how many days from today to said date
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

    public void onCalculateButtonClick(View view) {
        //if day selected is in the future, run Black-Scholes option calculations

        String expiration = String.valueOf(bindMain.expirationNum.getText()).trim();
        double currentPrice = Double.parseDouble(String.valueOf(bindMain.spotNum.getText()).trim());
        double strikePrice = Double.parseDouble(String.valueOf(bindMain.strikeNum.getText()).trim());
        double riskFree = Double.parseDouble(String.valueOf(bindMain.rfRateNum.getText()).trim());
        double volatility = Double.parseDouble(String.valueOf(bindMain.volNum.getText()).trim());

        double daysRemaining = OptionUtil.getDaysRemaining(expiration);

        Option queriedOption = new Option(expiration, strikePrice, volatility, currentPrice, riskFree);

        if (daysRemaining >= 0) {

            String callPrice = OptionUtil.calculateCallPrice(this, queriedOption);
            String putPrice = OptionUtil.calculatePutPrice(queriedOption);

            bindMain.callPrice.setText(callPrice);
            bindMain.putPrice.setText(putPrice);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.expiration_error),
                    Toast.LENGTH_SHORT).show();
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //hide keyboard
    }

    public void onClearButtonClick(View view) {
        bindMain.spotNum.setText("");
        bindMain.strikeNum.setText("");
        bindMain.volNum.setText("");
        bindMain.rfRateNum.setText("");
        bindMain.expirationNum.setText("");
        bindMain.callPrice.setText("---");
        bindMain.putPrice.setText("---");
    }

    public void sendToExistingOptions(View view) {
        Intent intent = new Intent(MainActivity.this, ExistingOptionActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendToSavedList(View view) {
        Intent intent = new Intent(MainActivity.this, SavedListActivity.class);
        startActivity(intent);
    }

}