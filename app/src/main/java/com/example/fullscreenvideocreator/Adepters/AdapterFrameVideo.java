package com.example.fullscreenvideocreator.Adepters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fullscreenvideocreator.Activities.DemoActivity;
import com.example.fullscreenvideocreator.Activities.DemoPresenter;
import com.example.fullscreenvideocreator.R;

public class AdapterFrameVideo extends RecyclerView.Adapter<AdapterFrameVideo.MyViewHolder> {

    Context context;
    private int[] drawable = new int[]{R.drawable.no_frame, R.drawable.frame_1, R.drawable.frame_2, R.drawable.frame_3, R.drawable.frame_5, R.drawable.frame_6, R.drawable.frame_7, R.drawable.frame_8, R.drawable.frame_9, R.drawable.frame_10,
            R.drawable.frame_11, R.drawable.frame_12, R.drawable.frame_13, R.drawable.frame_14, R.drawable.frame_15, R.drawable.frame_16, R.drawable.frame_17, R.drawable.frame_18, R.drawable.frame_19};
    private int mCheckIndex = 0;

    public AdapterFrameVideo(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_framelist, parent, false);

        return new MyViewHolder(view);
    }

    private int getItem(int pos) {
        return this.drawable[pos];
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Glide.with(context).load(getItem(position)).into(holder.iv_frame);

        holder.checkImg.setVisibility(mCheckIndex == position ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckIndex = position;
                DemoPresenter.addWaterMark(getItem(position));
                DemoActivity.adapterFrameVideo.notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return drawable.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_frame, checkImg;

        public MyViewHolder(View itemView) {
            super(itemView);

            iv_frame = itemView.findViewById(R.id.iv_framevideo);
            checkImg = itemView.findViewById(R.id.frame_check);
        }
    }

}
