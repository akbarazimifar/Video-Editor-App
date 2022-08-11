package com.example.fullscreenvideocreator.UtillsNew;

import android.app.Activity;

import com.example.fullscreenvideocreator.widget.FilterItem;
import com.example.fullscreenvideocreator.widget.TransferItem;
import com.hw.photomovie.render.GLTextureView;

import java.util.List;

/**
 * Created by huangwei on 2018/9/9.
 */
public interface IDemoView {
    public  GLTextureView getGLView();
    void setFilters(List<FilterItem> filters);
    Activity getActivity();

    void setTransfers(List<TransferItem> items);
}
