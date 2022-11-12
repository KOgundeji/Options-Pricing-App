package com.kogundeji;

import android.os.Bundle;

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
        SaveAdapter adapter = new SaveAdapter(optionlist);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        bindSave.optionsList.setLayoutManager(manager);
        bindSave.optionsList.setAdapter(adapter);
        bindSave.optionsList.setItemAnimator(new DefaultItemAnimator());

    }

    private void setExampleData() {

        optionlist.add(new Option("ASTS",Option.CALL,
                6.31,25,80,4.5,"1/17/2024"));
        optionlist.add(new Option("ASTS",Option.CALL,
                6.31,12.5,80,4.5,"1/17/2024"));
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
}