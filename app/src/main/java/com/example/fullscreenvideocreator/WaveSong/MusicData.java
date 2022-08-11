package com.example.fullscreenvideocreator.WaveSong;


import com.example.fullscreenvideocreator.R;

public class MusicData {
    public long track_Id;
    public String track_Title;
    public String track_data;
    public String track_displayName;
    public long track_duration;
    public int[] resID ={R.raw.fluteonmyway};

    public MusicData(){

    }
    public MusicData(String track_data, String track_displayName) {
        this.track_data = track_data;
        this.track_displayName = track_displayName;
    }

    // public ArrayList<String> id=
    public int[] getResID() {
        return resID;
    }

    public void setResID(int[] resID) {
        this.resID = resID;
    }

    public long getTrack_Id() {
        return this.track_Id;
    }

    public void setTrack_Id(long track_Id) {
        this.track_Id = track_Id;
    }

    public long getTrack_duration() {
        return this.track_duration;
    }

    public void setTrack_duration(long track_duration) {
        this.track_duration = track_duration;
    }

    public String getTrack_Title() {
        return this.track_Title;
    }

    public void setTrack_Title(String track_Title) {
        this.track_Title = track_Title;
    }

    public String getTrack_data() {
        return this.track_data;
    }

    public void setTrack_data(String track_data) {
        this.track_data = track_data;
    }

    public String getTrack_displayName() {
        return this.track_displayName;
    }

    public void setTrack_displayName(String track_displayName) {
        this.track_displayName = track_displayName;
    }
}
