package com.kogundeji.Model;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kogundeji.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.MyViewHolder> {
    private ArrayList<Option> optionList;

    public SaveAdapter(ArrayList<Option> optionList) {
        this.optionList = optionList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_list_options,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SimpleDateFormat simple = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat simple2 = new SimpleDateFormat("MMM dd yyyy");

        String ticker = optionList.get(position).getTicker_symbol();
        String type = optionList.get(position).getOptionType_Letter();
        double strike = optionList.get(position).getStrike();

        try {
            Date get_date = simple.parse(optionList.get(position).getExpiration());
            String reformatted_date = simple2.format(get_date);
            String option_view = ticker + " " + getStrikeString(strike) + "" + type + " " + reformatted_date;
            Log.d("Date Testing", "date: " + get_date + " date2: " + reformatted_date);
            holder.textView.setText(option_view);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (position % 2 == 0) {
            holder.cardView.setCardBackgroundColor(Color.LTGRAY);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public String getStrikeString(double strike) {
        if ((int) strike == strike) {
            return String.format("%,.0f",strike);
        }
        return String.format("%,.1f",strike);

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_options);
            cardView = itemView.findViewById(R.id.card_view_item);
        }
    }


}
