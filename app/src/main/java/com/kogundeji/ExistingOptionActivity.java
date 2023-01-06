package com.kogundeji;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kogundeji.database.DatabaseHandler;
import com.kogundeji.databinding.ActivityExistingoptionBinding;
import com.kogundeji.util.OptionUtil;
import com.kogundeji.model.Option;
import com.kogundeji.internet.GetOptionDataService;
import com.kogundeji.internet.RetrofitInstance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExistingOptionActivity extends AppCompatActivity {

    private ActivityExistingoptionBinding bind;
    private DatabaseHandler db;
    private Context activityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existingoption);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_existingoption);
        db = new DatabaseHandler(this);
        activityContext = this;

        bind.existingExpirationNum.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE); //created because Android Studio and XML are acting weird with DateTime inputtype

        setEndIconListener();

        if (getIntent().getIntExtra("id", 0) > 0) {
            populateOptionInfo(); //populates with Option data from "SavedListActivity" after it is opened
        }
    }

    private void setEndIconListener() {
        bind.existingExpiration.setEndIconOnClickListener(new View.OnClickListener() {
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
                    Date date = simple.parse(bind.existingExpirationNum.getText().toString());
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
                        ExistingOptionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int exp_year,
                                                  int exp_month, int exp_day) {

                                String date = (exp_month + 1) + "/" + exp_day + "/" + exp_year;
                                bind.existingExpirationNum.setText(date);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void onSaveButtonClick_Existing(View view) {
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
                        Option editedOption = new Option();

                        editedOption.setTicker_symbol(String.valueOf(bind.existingNameString.getText()));
                        editedOption.setCurrentPrice(Double.parseDouble(String.valueOf(bind.existingSpotNum.getText()).trim()));
                        editedOption.setStrike(Double.parseDouble(String.valueOf(bind.existingStrikeNum.getText()).trim()));
                        editedOption.setVolatility(Double.parseDouble(String.valueOf(bind.existingVolNum.getText()).trim()));
                        editedOption.setRfRate(Double.parseDouble(String.valueOf(bind.existingRfRateNum.getText()).trim()));
                        editedOption.setExpiration(String.valueOf(bind.existingExpirationNum.getText()).trim());
                        db.addOption(editedOption);

                        Toast.makeText(activityContext, editedOption.getTicker_symbol() + " Option saved",
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
                            + OptionUtil.getFormattedStrikeString(optionlist.get(i).getStrike())
                            + " " + reformatted_date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            builder.setTitle("Overwrite existing Option");
            final int[] selectedItem = {-1};
            builder.setSingleChoiceItems(str_option, selectedItem[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    selectedItem[0] = i;
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Option editedOption = new Option();
                    editedOption.setId(selectedItem[0] + 1);
                    editedOption.setTicker_symbol(optionlist.get(selectedItem[0]).getTicker_symbol());
                    editedOption.setStrike(Double.parseDouble(String.valueOf(bind.existingStrikeNum.getText()).trim()));
                    editedOption.setExpiration(String.valueOf(bind.existingExpirationNum.getText()).trim());
                    db.updateOption(editedOption);
                    Toast.makeText(ExistingOptionActivity.this,
                            editedOption.getTicker_symbol() + " Option updated",
                            Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();
        } else {
            Toast.makeText(ExistingOptionActivity.this, getString(R.string.no_existing_options),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void populateOptionInfo() {
        //sets info of Option opened from "SavedListActivity"
        db = new DatabaseHandler(this);
        Option openedOption = db.getOption(getIntent().getIntExtra("id", 0));

        bind.existingNameString.setText(openedOption.getTicker_symbol());
        bind.existingStrikeNum.setText(String.valueOf(openedOption.getStrike()));
        bind.existingExpirationNum.setText(openedOption.getExpiration());
    }

    public void onClearButtonClick_Existing(View view) {
        bind.existingNameString.setText("");
        bind.existingSpotNum.setText("");
        bind.existingStrikeNum.setText("");
        bind.existingVolNum.setText("");
        bind.existingRfRateNum.setText("");
        bind.existingExpirationNum.setText("");
        bind.existingCallPrice.setText("---");
        bind.existingPutPrice.setText("---");
        bind.existingSpotPrice.setVisibility(View.INVISIBLE);
        bind.existingVolatility.setVisibility(View.INVISIBLE);
        bind.existingRfRate.setVisibility(View.INVISIBLE);
    }

    public void onCalculateButtonClick_Existing(View view) {
        //method takes option data from TD Ameritrade website and inputs it in to app

        boolean anyInputEmpty = String.valueOf(bind.existingNameString.getText()).isEmpty() ||
                String.valueOf(bind.existingExpirationNum.getText()).isEmpty() ||
                String.valueOf(bind.existingStrikeNum.getText()).isEmpty();

        if (!anyInputEmpty) {
            Option queriedOption = new Option();
            final String[] descriptionCheck = {""};

            String ticker = String.valueOf(bind.existingNameString.getText()).trim();
            String expiration = String.valueOf(bind.existingExpirationNum.getText()).trim();
            double strikePrice = Double.parseDouble(String.valueOf(bind.existingStrikeNum.getText()).trim());

            queriedOption.setTicker_symbol(ticker);
            queriedOption.setExpiration(expiration);
            queriedOption.setStrike(strikePrice);
            queriedOption.setRfRate(OptionUtil.riskFreeRate);

            double daysRemaining = OptionUtil.getDaysRemaining(String.valueOf(bind.existingExpirationNum.getText()).trim());

            //if day selected is in the future, run Black-Scholes option calculations
            if (daysRemaining >= 0) {

                Map<String, String> parameters = new HashMap<>();
                String optionTicker = OptionUtil.getString(ticker, strikePrice, expiration);
                parameters.put("symbol", optionTicker);
                parameters.put("apikey", OptionUtil.apikey);

                GetOptionDataService service = RetrofitInstance.getRetrofitInstance().create(GetOptionDataService.class);

                Call<JsonElement> call = service.getOptionData(parameters);

                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                        assert response.body() != null;

                        JsonObject jObj = response.body().getAsJsonObject().get(optionTicker).getAsJsonObject();
                        double parsedCurrentPrice = Double.parseDouble(jObj.get("underlyingPrice").toString().trim());
                        double parsedVolatility = Double.parseDouble(jObj.get("volatility").toString());
                        double parsedStrike = Double.parseDouble(jObj.get("strikePrice").toString().trim());
                        String parsedTicker = jObj.get("underlying").toString().replaceAll("\"", "");
                        descriptionCheck[0] = jObj.get("description").toString().replaceAll("\"", "");

                        if (!descriptionCheck[0].equals("Symbol not found")) {
                            assert (parsedTicker.equals(ticker));
                            assert (parsedStrike == strikePrice);

                            queriedOption.setCurrentPrice(parsedCurrentPrice);
                            queriedOption.setVolatility(parsedVolatility);

                            String callPrice = OptionUtil.calculateCallPrice(queriedOption, activityContext);
                            String putPrice = OptionUtil.calculatePutPrice(queriedOption);

                            bind.existingCallPrice.setText(callPrice);
                            bind.existingPutPrice.setText(putPrice);
                            bind.existingSpotNum.setText(String.valueOf(queriedOption.getCurrentPrice()));
                            bind.existingVolNum.setText(String.valueOf(queriedOption.getVolatility()));
                            bind.existingRfRateNum.setText(String.valueOf(OptionUtil.riskFreeRate));
                            bind.existingSpotPrice.setVisibility(View.VISIBLE);
                            bind.existingVolatility.setVisibility(View.VISIBLE);
                            bind.existingRfRate.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(activityContext, "Option entered does not exist. Please try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        Log.d("JSONRetrofit", "Failure!");
                    }
                });

            } else {
                Toast.makeText(ExistingOptionActivity.this, getString(R.string.expiration_error),
                        Toast.LENGTH_SHORT).show();
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //hide keyboard
        } else {
            Toast.makeText(this, "Please enter a value for every input",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void sendToCalculator(View view) {
        Intent intent = new Intent(ExistingOptionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendToSavedList(View view) {
        Intent intent = new Intent(ExistingOptionActivity.this, SavedListActivity.class);
        startActivity(intent);
    }
}
