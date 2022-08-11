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
import com.example.fullscreenvideocreator.Activities.ActivityListSelectedPhotos;
import com.example.fullscreenvideocreator.Models.Model_images;
import com.example.fullscreenvideocreator.R;

import java.util.ArrayList;

public class Adapter_ListSelectedPhotos extends RecyclerView.Adapter<Adapter_ListSelectedPhotos.ViewHolder> {

    Context context;
    ArrayList<Model_images> al_menu = new ArrayList<>();

    public Adapter_ListSelectedPhotos(Context context, ArrayList<Model_images> al_menu) {
        this.context = context;
        this.al_menu = al_menu;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_list_selectedphotos, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String editimagepath = al_menu.get(position).getSingleimagepath();
        Glide.with(context).load("file://" + al_menu.get(position).getSingleimagepath())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.iv_listselectedimage);


        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityListSelectedPhotos.removeItemOnrecyclerview(position);

            }
        });
        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityListSelectedPhotos.EditItemOnrecyclerview(position, editimagepath);
            }
        });

    }


    @Override
    public int getItemCount() {
        return al_menu.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_edit, iv_delete, iv_listselectedimage;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
            iv_edit = (ImageView) itemView.findViewById(R.id.iv_edit);
            iv_listselectedimage = (ImageView) itemView.findViewById(R.id.iv_listselectedimage);
        }
    }


}
