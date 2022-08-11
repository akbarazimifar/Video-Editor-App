package com.example.fullscreenvideocreator.Adepters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fullscreenvideocreator.Models.ModelFont;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.Utills.Constance;

import java.util.ArrayList;

public class AdapterFont extends RecyclerView.Adapter<AdapterFont.MyViewHolder> {

    private ArrayList<ModelFont> list = new ArrayList<>();

    private Context context;
    private EventListener mEventListener;

    public AdapterFont(Context context, ArrayList<ModelFont> list_model) {
        this.context = context;
        this.list = list_model;
    }

    public interface EventListener {
        void onItemViewClicked(int position);
    }

    private void onItemViewClicked(int position) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(position);
        }
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView font_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            font_name = (TextView) itemView.findViewById(R.id.tv_font_name);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_font, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.font_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClicked(position);
            }
        });
        Constance.FONT_STYLE = list.get(position).getFont_name();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), Constance.FONT_STYLE.toLowerCase() + ".ttf");
        holder.font_name.setText("Hello");
        holder.font_name.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
