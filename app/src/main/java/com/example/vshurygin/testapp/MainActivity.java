package com.example.vshurygin.testapp;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    private DrawBoxes mDrawBoxes;
    private BoxController mBoxController;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mDrawBoxes = new DrawBoxes(this);
        setContentView(mDrawBoxes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mBoxController = new BoxController(mDrawBoxes);

    }
}
