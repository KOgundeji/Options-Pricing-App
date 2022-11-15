package com.kogundeji;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kogundeji.database.DatabaseHandler;
import com.kogundeji.databinding.ActivityAddEditScreenBinding;
import com.kogundeji.model.Option;

public class AddEdit_Screen extends AppCompatActivity {

    ActivityAddEditScreenBinding bindAddEdit;
    DatabaseHandler db;
    String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_screen);
        bindAddEdit = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_screen);
        db = new DatabaseHandler(this);

        getScreenDetails();
        getOptionDetails();
    }

    public void getScreenDetails() {
        if (getIntent().getStringExtra("page_type").equals("Edit")) {
            symbol = getIntent().getStringExtra("symbol");
            String editMessage = "Editing " + symbol + " Option";
            bindAddEdit.headerAddEdit.setText(editMessage);
        } else if (getIntent().getStringExtra("page_type").equals("Add")) {
            String addMessage = "Adding Option";
            bindAddEdit.headerAddEdit.setText(addMessage);
        } else {
            bindAddEdit.headerAddEdit.setText("Error: Unknown Action");
        }

        if (getIntent().getStringExtra("button_name") != null) {
            bindAddEdit.AddEditButton.setText(getIntent().getStringExtra("button_name"));
        } else {
            bindAddEdit.AddEditButton.setText("Unknown");
            bindAddEdit.AddEditButton.setEnabled(false);
        }
    }

    private void getOptionDetails() {
        double current = getIntent().getDoubleExtra("current", 0);
        bindAddEdit.spotNumAddEdit.setText(String.valueOf(current));

        double strike = getIntent().getDoubleExtra("strike", 0);
        bindAddEdit.strikeNumAddEdit.setText(String.valueOf(strike));

        double vol = getIntent().getDoubleExtra("volatility", 0);
        bindAddEdit.volNumAddEdit.setText(String.valueOf(vol));

        double rf = getIntent().getDoubleExtra("rf", 0);
        bindAddEdit.rfRateNumAddEdit.setText(String.valueOf(rf));

        bindAddEdit.expirationNumAddEdit.setText(getIntent().getStringExtra("expiration"));
    }

    public void AddEdit(View view) {
        Option editedOption = new Option();
        editedOption.setId(getIntent().getIntExtra("id",0));
        editedOption.setCurrent(Double.parseDouble(String.valueOf(bindAddEdit.spotNumAddEdit.getText()).trim()));
        editedOption.setCurrent(Double.parseDouble(String.valueOf(bindAddEdit.spotNumAddEdit.getText()).trim()));
        editedOption.setVolatility(Double.parseDouble(String.valueOf(bindAddEdit.volNumAddEdit.getText()).trim()));
        editedOption.setRfRate(Double.parseDouble(String.valueOf(bindAddEdit.rfRateNumAddEdit.getText()).trim()));
        editedOption.setExpiration(String.valueOf(bindAddEdit.expirationNumAddEdit.getText()).trim());

        if(bindAddEdit.AddEditButton.getText() == "Add") {
            db.addOption(editedOption);
        } else if (bindAddEdit.AddEditButton.getText() == "Edit") {
            db.updateOption(editedOption);
        }
        Intent intent = new Intent(this, SaveActivity.class);
        startActivity(intent);
        finish();
    }
}