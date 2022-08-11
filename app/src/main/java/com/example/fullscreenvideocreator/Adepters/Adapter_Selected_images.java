package com.example.fullscreenvideocreator.Adepters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fullscreenvideocreator.Activities.ActivitySelectImages;
import com.example.fullscreenvideocreator.Models.Model_images;
import com.example.fullscreenvideocreator.R;

import java.util.ArrayList;

public class Adapter_Selected_images extends RecyclerView.Adapter<Adapter_Selected_images.ViewHolder> {

    Context context;
    ArrayList<Model_images> al_menu = new ArrayList<>();


    public Adapter_Selected_images(Context context, ArrayList<Model_images> al_menu) {
        this.context = context;
        this.al_menu = al_menu;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_single_seletedimage, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Glide.with(context).load("file://" + al_menu.get(position).getSingleimagepath())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.iv_single_selectedimage);

        holder.iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivitySelectImages.removeItemOnrecyclerview(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return al_menu.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_single_selectedimage, iv_cancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_single_selectedimage = (ImageView) itemView.findViewById(R.id.iv_single_selectedimage);
            iv_cancel = (ImageView) itemView.findViewById(R.id.iv_cancel);

        }
    }


}
