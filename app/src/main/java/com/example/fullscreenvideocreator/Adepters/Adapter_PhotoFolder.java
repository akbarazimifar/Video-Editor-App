package com.example.fullscreenvideocreator.Adepters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fullscreenvideocreator.Activities.ActivitySelectImages;
import com.example.fullscreenvideocreator.Models.Model_images;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.Utills.Constance;

import java.util.ArrayList;

public class Adapter_PhotoFolder extends RecyclerView.Adapter<Adapter_PhotoFolder.ViewHolder> {

    Context context;
    ArrayList<Model_images> al_menu = new ArrayList<>();
    private int mCheckIndex = 0;

    public Adapter_PhotoFolder(Context context, ArrayList<Model_images> al_menu) {
        this.context = context;
        this.al_menu = al_menu;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_photosfolder, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_foldern.setText(al_menu.get(position).getStr_folder());
        //Toast.makeText(context, "" + al_menu.get(position).getStr_folder(), Toast.LENGTH_LONG).show();
        Glide.with(context).load("file://" + al_menu.get(position).getAl_imagepath().get(0))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.iv_image);
        holder.folder_check.setVisibility(mCheckIndex == position ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckIndex = position;
                Constance.Folderposition = position;
                ActivitySelectImages.setSingleFolderData();
                ActivitySelectImages.adapter_photoFolder.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return al_menu.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_foldern;
        LinearLayout folder_check;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            tv_foldern = itemView.findViewById(R.id.tv_folder);
            folder_check = itemView.findViewById(R.id.folder_check);
            iv_image = itemView.findViewById(R.id.iv_image);

        }
    }
}
