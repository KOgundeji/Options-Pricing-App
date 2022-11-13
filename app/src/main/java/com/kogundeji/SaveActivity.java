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

import com.kogundeji.Model.Option;
import com.kogundeji.Model.SaveAdapter;
import com.kogundeji.databinding.ActivitySaveBinding;

import java.util.ArrayList;

public class SaveActivity extends AppCompatActivity {
    private ArrayList<Option> optionlist;
    private ActivitySaveBinding bindSave;
    private SaveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        bindSave = DataBindingUtil.setContentView(this,R.layout.activity_save);
        optionlist = new ArrayList<>();

        setExampleData();
        setAdapter();
    }

    private void setAdapter() {
        adapter = new SaveAdapter(optionlist);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        bindSave.optionsList.setLayoutManager(manager);
        bindSave.optionsList.setAdapter(adapter);
        bindSave.optionsList.setItemAnimator(new DefaultItemAnimator());
    }

    private void setExampleData() {
        optionlist.add(new Option("ASTS",Option.CALL,
                6.31,25,80,4.5,"1/17/2024"));
        optionlist.add(new Option("MELI",Option.PUT,
                998,1200,60,4.5,"1/17/2024"));
        optionlist.add(new Option("SE",Option.PUT,
                49,62.5,60,4.5,"1/19/2025"));
        optionlist.add(new Option("NU",Option.PUT,
                4.29,7,67,4.5,"1/19/2025"));


//        optionlist.add(new Option("Call","ASTS"));
//        optionlist.add(new Option("Call","MELI"));
//        optionlist.add(new Option("Call","STNE"));
//        optionlist.add(new Option("Put","QUBT"));
//        optionlist.add(new Option("Call","AMZN"));
//        optionlist.add(new Option("Call","MSFT"));
//        optionlist.add(new Option("Put","TSLA"));
//        optionlist.add(new Option("Call","ARQQ"));
//        optionlist.add(new Option("Call","NU"));
//        optionlist.add(new Option("Put","ORGN"));
//        optionlist.add(new Option("Call","SE"));
//        optionlist.add(new Option("Put","NRGV"));
//        optionlist.add(new Option("Call","INTU"));
//        optionlist.add(new Option("Call","GMI"));
//        optionlist.add(new Option("Put","LC"));

    }

    public void addOptions(View view) {
        //goes to new screen
        Intent intent = new Intent(this,AddEdit_Screen.class);
        intent.putExtra("header","Add Option");
        intent.putExtra("button_name","Add");
        startActivity(intent);
    }
    public void deleteOptions(View view) {
        //removes, but asks for confirmation first
    }

    public void editOptions(View view) {
        //goes to same new screen as addOptions, but inputs existing values to start off

        int position = adapter.getSelectedPos();
        Log.d("Adapter Pos", String.valueOf(position));

        if (position >= 0) {
            Option selectedOption = optionlist.get(position);

            Intent intent = new Intent(this, AddEdit_Screen.class);
            intent.putExtra("header", "Edit " + selectedOption.getTicker_symbol() + " Option");
            intent.putExtra("button_name", "Update");
            intent.putExtra("current", selectedOption.getCurrent());
            intent.putExtra("strike", selectedOption.getStrike());
            intent.putExtra("volatility", selectedOption.getVolatility());
            intent.putExtra("rf", selectedOption.getRfRate());
            intent.putExtra("expiration", selectedOption.getExpiration());
            startActivity(intent);
        } else {
            Log.d("Adapter Pos", "Option selection failed!");
        }
    }

    public void openOptions(View view) {
        //opens in original main screen with existing values pre-loaded
    }

}