package com.kogundeji.util;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kogundeji.model.Option;
import com.kogundeji.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.MyViewHolder> {

    private ArrayList<Option> optionList;
    private int selectedPos = RecyclerView.NO_POSITION;
    private int previouslySelected = RecyclerView.NO_POSITION - 1;
    private int selectedBackground = Color.rgb(71,179,98);

    public SaveAdapter(ArrayList<Option> optionList) {
        this.optionList = optionList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_list_options,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPos == position);
        holder.itemView.setBackgroundColor(selectedPos == position ?
                selectedBackground : Color.TRANSPARENT);

        SimpleDateFormat simple = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat simple2 = new SimpleDateFormat("MMM dd yyyy");

        String ticker = optionList.get(position).getTicker_symbol();
        double strike = optionList.get(position).getStrike();

        try {
            Date get_date = simple.parse(optionList.get(position).getExpiration());
            String reformatted_date = simple2.format(get_date);
            String option_view = ticker + " $" + getStrikeString(strike) + " " + reformatted_date;
            holder.textView.setText(option_view);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        if (position % 2 == 0) {
//            holder.cardView.setBackgroundResource(R.color.cardView_background_dark);
//        } else {
//            holder.cardView.setBackgroundResource(R.color.cardView_background_light);
//        }
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public String getStrikeString(double strike) {
        if ((int) strike == strike) {
            return String.format("%,.0f",strike);
        }
        return String.format("%,.1f",strike);

    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_options);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //to make sure only 1 list item is selected at a time
            //and allows you to make the same selection multiple times
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            if (previouslySelected == selectedPos) {
                selectedPos = RecyclerView.NO_POSITION;
                previouslySelected = RecyclerView.NO_POSITION - 1;
            } else {
                previouslySelected = selectedPos;
            }
            notifyItemChanged(selectedPos);
        }
    }
}
