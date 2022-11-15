package com.kogundeji;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kogundeji.database.DatabaseHandler;
import com.kogundeji.model.Option;
import com.kogundeji.util.SaveAdapter;
import com.kogundeji.databinding.ActivitySaveBinding;

import java.util.ArrayList;
import java.util.List;

public class SaveActivity extends AppCompatActivity {
    private ArrayList<Option> optionlist;
    private ActivitySaveBinding bindSave;
    private SaveAdapter adapter;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        bindSave = DataBindingUtil.setContentView(this,R.layout.activity_save);
        db = new DatabaseHandler(this);
        db.refreshTable();
        setDBExample();
        optionlist = db.getAllOptions();

        for (Option option:optionlist) {
            Log.d("Options Testing", "ticker: " +option.getTicker_symbol() +
                                               " id: " + option.getId());
        }

        setAdapter();
    }

    private void setAdapter() {
        adapter = new SaveAdapter(optionlist);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        bindSave.optionsList.setLayoutManager(manager);
        bindSave.optionsList.setAdapter(adapter);
        bindSave.optionsList.setItemAnimator(new DefaultItemAnimator());
    }

    public void addOptions(View view) {
        //goes to new screen
        Intent intent = new Intent(this,AddEdit_Screen.class);
        intent.putExtra("page_type","Add");
        intent.putExtra("button_name","Add");
        startActivity(intent);
    }
    public void deleteOptions(View view) {
        //removes, but asks for confirmation first
        int position = adapter.getSelectedPos();
        Option selectedOption = optionlist.get(position);

        db.deleteOption(selectedOption.getId());
        refreshScreen();
    }

    public void refreshScreen() {
        Intent i = new Intent(this, this.getClass());
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    public void editOptions(View view) {
        //goes to same new screen as addOptions, but inputs existing values to start off

        int position = adapter.getSelectedPos();
        Log.d("Adapter Pos", String.valueOf(position));

        if (position >= 0) {
            Option selectedOption = optionlist.get(position);

            Intent intent = new Intent(this, AddEdit_Screen.class);
            intent.putExtra("page_type","Edit");
            intent.putExtra("button_name", "Update");
            intent.putExtra("symbol",selectedOption.getTicker_symbol());
            intent.putExtra("current", selectedOption.getCurrent());
            intent.putExtra("strike", selectedOption.getStrike());
            intent.putExtra("volatility", selectedOption.getVolatility());
            intent.putExtra("rf", selectedOption.getRfRate());
            intent.putExtra("expiration", selectedOption.getExpiration());
            intent.putExtra("id",selectedOption.getId());
            startActivity(intent);
        } else {
            Log.d("Adapter Pos", "Option selection failed!");
        }
    }

    public void openOptions(View view) {
        //opens in original main screen with existing values pre-loaded
        int position = adapter.getSelectedPos();
        Option selectedOption = optionlist.get(position);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id",selectedOption.getId());
        startActivity(intent);
    }


    private void setDBExample() {

        db.addOption(new Option("ASTS",
                6.31,25,80,4.5,"1/17/2024"));
        db.addOption(new Option("MELI",
                998,1200,60,4.5,"1/17/2024"));
        db.addOption(new Option("SE",
                49,62.5,60,4.5,"1/19/2025"));
        db.addOption(new Option("NU",
                4.29,7,67,4.5,"1/19/2025"));
        db.addOption(new Option("STNE",
                11.05,20,60,4.5,"1/17/2024"));
        db.addOption(new Option("ARQQ",
                8.52,12.5,110,4.5,"11/19/2023"));
        db.addOption(new Option("NRGV",
                3.18,7,85,4.5,"1/19/2025"));
        db.addOption(new Option("ORGN",
                5.82,7.5,60,4.5,"1/17/2024"));
        db.addOption(new Option("QUBT",
                3.12,5,60,4.5,"1/19/2025"));
        db.addOption(new Option("JMIA",
                3,2.5,67,4.5,"1/19/2025"));

    }

}