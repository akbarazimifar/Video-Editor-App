package com.example.fullscreenvideocreator.Models;

public class ModelBottomNavigation {

    int image;
    String nav_name;
    public int VALUE;

    ModelBottomNavigation(int VALUE) {
        this.VALUE = VALUE;
    }
    public ModelBottomNavigation(int image, String nav_name) {
        this.image = image;
        this.nav_name = nav_name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNav_name() {
        return nav_name;
    }

    public void setNav_name(String nav_name) {
        this.nav_name = nav_name;
    }
}
