package com.example.fullscreenvideocreator.widget;

import com.hw.photomovie.PhotoMovieFactory;

/**
 * Created by huangwei on 2018/9/9.
 */
public class TransferItem {

    public TransferItem(int imgRes, String name, PhotoMovieFactory.PhotoMovieType type) {
        this.imgRes = imgRes;
        this.name = name;
        this.type = type;
    }

    public int imgRes;
    public String name;
    public PhotoMovieFactory.PhotoMovieType type;
}
