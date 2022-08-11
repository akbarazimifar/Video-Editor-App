package com.example.fullscreenvideocreator.Adepters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.fullscreenvideocreator.Activities.ActivityMyVideo;
import com.example.fullscreenvideocreator.Activities.ActivityVideoPreview;
import com.example.fullscreenvideocreator.R;

import java.io.File;
import java.util.ArrayList;


public class AdapterMyVideoFileList extends RecyclerView.Adapter<AdapterMyVideoFileList.ViewHolder> {
    private Context context;
    private ArrayList<File> fileArrayList;

    public AdapterMyVideoFileList(Context context, ArrayList<File> files) {
        this.context = context;
        this.fileArrayList = files;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_myvideofilelist, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMyVideoFileList.ViewHolder viewHolder, final int position) {
        File fileItem = fileArrayList.get(position);
        final String urlpath = fileItem.getAbsolutePath();
        final String filename = fileItem.getName();


        //  Toast.makeText(context,""+urlpath,Toast.LENGTH_LONG).show();

        viewHolder.tv_fileName.setText(filename);
        try {
            String extension = fileItem.getName().substring(fileItem.getName().lastIndexOf("."));
            if (extension.equals(".mp4")) {
                viewHolder.iv_play.setVisibility(View.VISIBLE);
            } else {
                viewHolder.iv_play.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(fileItem.getPath())
                    .into(viewHolder.iv_fileimage);
        } catch (Exception ex) {
        }
        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileItem.delete();
                fileArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, fileArrayList.size());
                if (fileArrayList.size() == 0) {
                    ActivityMyVideo.rl_videonotfound.setVisibility(View.VISIBLE);
                }

            }
        });
        viewHolder.iv_sharefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareVideo(context, urlpath);
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent inNext = new Intent(context, ActivityVideoPreview.class);
                inNext.putExtra("Video_Path", urlpath);
                context.startActivity(inNext);
                ActivityMyVideo.showInterstitial();
            }
        });


    }

    public static void shareVideo(Context context, String filePath) {
        Uri mainUri = Uri.parse(filePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/mp4");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Application not found to open this file", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_play, iv_fileimage, iv_delete, iv_sharefile;
        TextView tv_fileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fileName = itemView.findViewById(R.id.tv_fileName);
            iv_play = itemView.findViewById(R.id.iv_play);
            iv_fileimage = itemView.findViewById(R.id.iv_fileimage);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            iv_sharefile = itemView.findViewById(R.id.iv_sharefile);


        }
    }


}