package com.kogundeji;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
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
//        db.refreshTable(); //deletes tables and recreates it as a "reset"
//        setDBExample(); //populates with example data
        optionlist = db.getAllOptions(); //populates recycler view with Option instances from database

        setAdapter();
    }

    private void setAdapter() {
        adapter = new SaveAdapter(optionlist);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        bindSavedList.optionsList.setLayoutManager(manager);
        bindSavedList.optionsList.setAdapter(adapter);
        bindSavedList.optionsList.setItemAnimator(new DefaultItemAnimator());
    }

    public void deleteOptions(View view) {
        //deletes Option from database, but asks for confirmation first
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirmation))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
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
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        builder.create().show();
    }

    public void refreshScreen() {
        Intent i = new Intent(this, this.getClass());
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    public void openOptions(View view) {
        //opens in "MainActivity" and loads values into EditText slots
        int position = adapter.getSelectedPos();
        Option selectedOption = optionlist.get(position);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id",selectedOption.getId());
        startActivity(intent);
    }

    private void setDBExample() {
        //example data
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