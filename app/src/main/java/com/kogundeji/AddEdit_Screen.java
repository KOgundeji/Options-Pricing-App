package com.kogundeji;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.kogundeji.databinding.ActivityAddEditScreenBinding;

public class AddEdit_Screen extends AppCompatActivity {

    ActivityAddEditScreenBinding bindAddEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_screen);
        bindAddEdit = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_screen);

        getScreenDetails();
        getOptionDetails();
    }

    public void getScreenDetails() {
        if (getIntent().getStringExtra("header") != null) {
            bindAddEdit.headerAddEdit.setText(getIntent().getStringExtra("header"));
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
        if(bindAddEdit.AddEditButton.getText() == "Add") {

        } else {

        }
    }
}