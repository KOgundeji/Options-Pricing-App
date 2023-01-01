package com.kogundeji;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.apache.commons.math3.distribution.NormalDistribution;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.kogundeji.database.DatabaseHandler;
import com.kogundeji.databinding.ActivityMainBinding;
import com.kogundeji.model.Option;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bindMain;
    private DatabaseHandler db;
    final int[] selectedItem = {-1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindMain = DataBindingUtil.setContentView(this, R.layout.activity_main);
        db = new DatabaseHandler(this);
        bindMain.expirationNum.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE); //created because Android Studio and XML are acting weird with DateTime inputtype

        setEndIconListener();

        if (getIntent().getIntExtra("id", 0) > 0) {
            populateOptionInfo(); //populates with Option data from "SavedListActivity" after it is opened
        }
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
                    month = local.getMonthValue() -1;
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

    public void calculate(View view) {
        //if day selected is in the future, run Black-Scholes option calculations
        if (getDays() >= 0) {
            bindMain.callPrice.setText(calc_call());
            bindMain.putPrice.setText(calc_put());
        } else {
            Toast.makeText(MainActivity.this,getString(R.string.expiration_error),
                    Toast.LENGTH_SHORT).show();
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //hide keyboard
    }

    public void clear(View view) {
        bindMain.spotNum.setText("");
        bindMain.strikeNum.setText("");
        bindMain.volNum.setText("");
        bindMain.rfRateNum.setText("");
        bindMain.expirationNum.setText("");
    }

    public void save(View view) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        //first step to saving option. Save over an existing option, or create a new save?

        builder.setMessage(getString(R.string.new_or_existing_save))
                .setPositiveButton(getString(R.string.existing_option), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createDialogOptionList(); //creates the option list to choose which option to overwrite
                    }
                })
                .setNegativeButton(getString(R.string.new_option), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Creates another dialog to ask for ticker name for new option.
                        //This is the last piece of data needed to create an "Option" instance for DB entry
                        getTickerName();
                    }
                });
        builder.create().show();
    }

    public void getTickerName() {
        //Creates another dialog to ask for ticker name for new option.
        //This is the last piece of data needed to create an "Option" instance for DB entry
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.ticker_handler, null);
        EditText ticker = (EditText) view.findViewById(R.id.ticker);

        builder.setView(view);
        builder.setMessage(getString(R.string.enter_ticker))
                .setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Option editedOption = new Option();

                        editedOption.setTicker_symbol(String.valueOf(ticker.getText()));
                        editedOption.setCurrent(Double.parseDouble(String.valueOf(bindMain.spotNum.getText()).trim()));
                        editedOption.setStrike(Double.parseDouble(String.valueOf(bindMain.strikeNum.getText()).trim()));
                        editedOption.setVolatility(Double.parseDouble(String.valueOf(bindMain.volNum.getText()).trim()));
                        editedOption.setRfRate(Double.parseDouble(String.valueOf(bindMain.rfRateNum.getText()).trim()));
                        editedOption.setExpiration(String.valueOf(bindMain.expirationNum.getText()).trim());
                        db.addOption(editedOption);
                        Toast.makeText(MainActivity.this,
                                editedOption.getTicker_symbol() + " Option saved",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create().show();
    }

    public void createDialogOptionList() {
        //creates the option list to choose which option to overwrite
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ArrayList<Option> optionlist = db.getAllOptions();

        if (optionlist.size() > 0) {
            String[] str_option = new String[optionlist.size()];

            SimpleDateFormat simple = new SimpleDateFormat(getString(R.string.preformatted_date));
            SimpleDateFormat simple2 = new SimpleDateFormat(getString(R.string.formatted_date));

            for (int i = 0; i < optionlist.size(); i++) {
                try {
                    Date get_date = simple.parse(optionlist.get(i).getExpiration());
                    String reformatted_date = simple2.format(get_date);
                    str_option[i] = optionlist.get(i).getTicker_symbol() + " $"
                            + getStrikeString(optionlist.get(i).getStrike())
                            + " " + reformatted_date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            builder.setTitle("Overwrite existing Option");
            builder.setSingleChoiceItems(str_option, selectedItem[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    selectedItem[0] = i;
                }
            });
            builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Option editedOption = new Option();
                    editedOption.setId(selectedItem[0] + 1);
                    editedOption.setTicker_symbol(optionlist.get(selectedItem[0]).getTicker_symbol());
                    editedOption.setCurrent(Double.parseDouble(String.valueOf(bindMain.spotNum.getText()).trim()));
                    editedOption.setStrike(Double.parseDouble(String.valueOf(bindMain.strikeNum.getText()).trim()));
                    editedOption.setVolatility(Double.parseDouble(String.valueOf(bindMain.volNum.getText()).trim()));
                    editedOption.setRfRate(Double.parseDouble(String.valueOf(bindMain.rfRateNum.getText()).trim()));
                    editedOption.setExpiration(String.valueOf(bindMain.expirationNum.getText()).trim());
                    db.updateOption(editedOption);
                    Toast.makeText(MainActivity.this,
                            editedOption.getTicker_symbol() + " Option updated",
                            Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();
        } else {
            Toast.makeText(MainActivity.this,getString(R.string.no_existing_options),
                    Toast.LENGTH_LONG).show();
        }
    }

    public String getStrikeString(double strike) {
        //helper method to set the strike price formatting
        if ((int) strike == strike) {
            return String.format("%,.0f", strike);
        }
        return String.format("%,.1f", strike);

    }

    public void sendToSavedList(View view) {
        Intent intent = new Intent(MainActivity.this, SavedListActivity.class);
        startActivity(intent);
    }

    public String calc_call() {
        //use black-scholes model to calculate call option price
        //the delta_first_part part of the equation (e^-qt) is irrelevant because we assume dividends = 0. Equation always 1
        try {
            double spotD = Double.parseDouble(String.valueOf(bindMain.spotNum.getText()).trim());
            double strikeD = Double.parseDouble(String.valueOf(bindMain.strikeNum.getText()).trim());
            double rfD = Double.parseDouble(String.valueOf(bindMain.rfRateNum.getText()).trim()) / 100;
            double volD = Double.parseDouble(String.valueOf(bindMain.volNum.getText()).trim()) / 100;
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
            Toast.makeText(MainActivity.this,getString(R.string.input_error),
                    Toast.LENGTH_SHORT).show();
            Log.d("Number Format: Call", "Number Format Exception!");
            e.printStackTrace();
        }
        return null;
    }

    public String calc_put() {
        //use black-scholes model to calculate put option price
        //the delta_first_part part of the equation (e^-qt) is irrelevant because we assume dividends = 0. Equation always 1
        try {
            double spotD = Double.parseDouble(String.valueOf(bindMain.spotNum.getText()).trim());
            double strikeD = Double.parseDouble(String.valueOf(bindMain.strikeNum.getText()).trim());
            double rfD = Double.parseDouble(String.valueOf(bindMain.rfRateNum.getText()).trim()) / 100;
            double volD = Double.parseDouble(String.valueOf(bindMain.volNum.getText()).trim()) / 100;
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

    public void populateOptionInfo() {
        //sets info of Option opened from "SavedListActivity"
        db = new DatabaseHandler(this);
        Option openedOption = db.getOption(getIntent().getIntExtra("id", 0));

        bindMain.spotNum.setText(String.valueOf(openedOption.getCurrent()));
        bindMain.strikeNum.setText(String.valueOf(openedOption.getStrike()));
        bindMain.volNum.setText(String.valueOf(openedOption.getVolatility()));
        bindMain.rfRateNum.setText(String.valueOf(openedOption.getRfRate()));
        bindMain.expirationNum.setText(openedOption.getExpiration());
    }

    public int getDays() {
        //calculates how many days between today and selected date
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
            int difference = (int) (calc / (24 * 60 * 60 * 1000));
            return difference;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Testing expiration", "Parse Exception");
        }
        return -1;
    }
}