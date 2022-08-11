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

public class Adapter_SinglePhotoFolder extends RecyclerView.Adapter<Adapter_SinglePhotoFolder.ViewHolder> {

    Context context;
    ArrayList<Model_images> al_menu = new ArrayList<>();
    int int_position;


    public Adapter_SinglePhotoFolder(Context context, ArrayList<Model_images> al_menu, int int_position) {
        this.context = context;
        this.al_menu = al_menu;
        this.int_position = int_position;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_singlephoto_folder, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String selectedimage = al_menu.get(int_position).getAl_imagepath().get(position);
        // Toast.makeText(context,""+selectedimage,Toast.LENGTH_LONG).show();

        Glide.with(context).load("file://" + al_menu.get(int_position).getAl_imagepath().get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.iv_singlefolderimage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivitySelectImages.getSingleSelectedImage(selectedimage);
            }
        });

    }

    @Override
    public int getItemCount() {
        return al_menu.get(int_position).getAl_imagepath().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_singlefolderimage;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            iv_singlefolderimage = (ImageView) itemView.findViewById(R.id.iv_singlefolderimage);
        }
    }


}
