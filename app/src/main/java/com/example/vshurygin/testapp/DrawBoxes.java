package com.example.vshurygin.testapp;

import android.app.Activity;

/**
 * Created by vshurygin on 21.09.2016.
 */

public class DrawBoxes
{
    private Activity mLocalActivity;
    private CanvasView mLocalCanvasView;

    DrawBoxes(Activity _activity)
    {
        mLocalActivity = _activity;
        mLocalCanvasView = (CanvasView)mLocalActivity.findViewById(R.id.draw_box_surface);

    }
}
