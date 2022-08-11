package com.example.fullscreenvideocreator.Activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.example.fullscreenvideocreator.Fragments.FragmentEditPhoto;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.Utills.Constance;

public class ActivityImageEditor extends FragmentActivity {

    Context context;
    String editimagepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);
        context=ActivityImageEditor.this;
        editimagepath=getIntent().getExtras().getString("editimagepath");
        Log.d("gggg","editimagepath : "+editimagepath);

        Constance.editimagepath=editimagepath;

        Log.d("gggg","editimagepath : "+Constance.editimagepath);
        Display display = getWindow ().getWindowManager ().getDefaultDisplay ();
        Constance.screenHeight = display.getHeight ();
        Constance.screenWidth = display.getWidth ();
        // Toast.makeText(context,""+Constance.screenHeight+ ":"+ Constance.screenWidth, Toast.LENGTH_LONG).show();
        addFragment(new FragmentEditPhoto());
    }
    private void addFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(R.id.rootMain, fragment);
        ft.commit();
    }

    public void addFragmentToStack(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(R.id.rootMain, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentEditPhoto.hideLoading();
        super.onBackPressed();
    }
}
