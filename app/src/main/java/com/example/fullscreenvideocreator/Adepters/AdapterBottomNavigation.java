package com.example.fullscreenvideocreator.Adepters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fullscreenvideocreator.Models.ModelBottomNavigation;
import com.example.fullscreenvideocreator.R;

import java.util.ArrayList;

public class AdapterBottomNavigation extends RecyclerView.Adapter<AdapterBottomNavigation.ViewHolder> {

    Context context;

    ArrayList<ModelBottomNavigation> modelBottomNavigations;
    public OnItemEditPhotoClickedListener onItemEditPhotoClickedListener;

    public void setOnItemEditPhotoClickedListener(OnItemEditPhotoClickedListener onItemEditPhotoClickedListener) {
        this.onItemEditPhotoClickedListener = onItemEditPhotoClickedListener;
    }

    public AdapterBottomNavigation(Context context, ArrayList<ModelBottomNavigation> modelBottomNavigations) {
        this.context = context;
        this.modelBottomNavigations = modelBottomNavigations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_bottom_navigation, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(modelBottomNavigations.get(position).getImage()).into(holder.iv_navimage);

        holder.tv_nav_name.setText(modelBottomNavigations.get(position).getNav_name());
        final ModelBottomNavigation modelBottomNavigation = modelBottomNavigations.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // FragmentEditPhoto.addListner(position);
                onItemEditPhotoClickedListener.onItemEditPhotoClicked(position);

            }
        });

    }


    @Override
    public int getItemCount() {
        return modelBottomNavigations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_navimage;
        TextView tv_nav_name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_navimage = itemView.findViewById(R.id.iv_navimage);
            tv_nav_name = itemView.findViewById(R.id.tv_nav_name);
        }
    }

    public interface OnItemEditPhotoClickedListener {
        void onItemEditPhotoClicked(int position);
    }


}
