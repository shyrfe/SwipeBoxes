package com.example.vshurygin.testapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * Created by vshurygin on 21.09.2016.
 */

public class BoxController
{
    DrawBoxes mDrawBoxes;
    BoxController(Activity _activity)
    {
        mDrawBoxes = new DrawBoxes(_activity);
    }
}

