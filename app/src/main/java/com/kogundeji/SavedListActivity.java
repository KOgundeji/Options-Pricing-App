package com.kogundeji;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kogundeji.database.DatabaseHandler;
import com.kogundeji.databinding.ActivitySavedlistBinding;
import com.kogundeji.model.Option;
import com.kogundeji.util.SaveAdapter;

import java.util.ArrayList;

public class SavedListActivity extends AppCompatActivity {
    private ArrayList<Option> optionlist;
    private ActivitySavedlistBinding bindSavedList;
    private SaveAdapter adapter;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedlist);

        bindSavedList = DataBindingUtil.setContentView(this,R.layout.activity_savedlist);
        db = new DatabaseHandler(this);
//        db.refreshTable();
//        setDBExample();
        optionlist = db.getAllOptions();

        setAdapter();
    }

    private void setAdapter() {
        adapter = new SaveAdapter(optionlist);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        bindSavedList.optionsList.setLayoutManager(manager);
        bindSavedList.optionsList.setAdapter(adapter);
        bindSavedList.optionsList.setItemAnimator(new DefaultItemAnimator());
    }

//    public void addOptions(View view) {
//        //goes to new screen
//        Intent intent = new Intent(this,AddEdit_Screen.class);
//        intent.putExtra("page_type","Add");
//        intent.putExtra("button_name","Add");
//        startActivity(intent);
//    }
    public void deleteOptions(View view) {
        //removes, but asks for confirmation first
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int position = adapter.getSelectedPos();
                        Option selectedOption = optionlist.get(position);
                        db.deleteOption(selectedOption.getId());
                        Toast.makeText(SavedListActivity.this,
                                selectedOption.getTicker_symbol() + " Option deleted",
                                Toast.LENGTH_SHORT).show();
                        refreshScreen();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        builder.create().show();
    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this?")
                .setPositiveButton("I'm sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int position = adapter.getSelectedPos();
                        Option selectedOption = optionlist.get(position);
                        db.deleteOption(selectedOption.getId());
                        refreshScreen();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }

    public void refreshScreen() {
        Intent i = new Intent(this, this.getClass());
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

//    public void editOptions(View view) {
//        //goes to same new screen as addOptions, but inputs existing values to start off
//
//        int position = adapter.getSelectedPos();
//        Log.d("Adapter Pos", String.valueOf(position));
//
//        if (position >= 0) {
//            Option selectedOption = optionlist.get(position);
//
//            Intent intent = new Intent(this, AddEdit_Screen.class);
//            intent.putExtra("page_type","Edit");
//            intent.putExtra("button_name", "Update");
//            intent.putExtra("symbol",selectedOption.getTicker_symbol());
//            intent.putExtra("current", selectedOption.getCurrent());
//            intent.putExtra("strike", selectedOption.getStrike());
//            intent.putExtra("volatility", selectedOption.getVolatility());
//            intent.putExtra("rf", selectedOption.getRfRate());
//            intent.putExtra("expiration", selectedOption.getExpiration());
//            intent.putExtra("id",selectedOption.getId());
//            startActivity(intent);
//        } else {
//            Log.d("Adapter Pos", "Option selection failed!");
//        }
//    }

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