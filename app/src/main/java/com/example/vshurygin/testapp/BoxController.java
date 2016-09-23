package com.example.vshurygin.testapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by vshurygin on 21.09.2016.
 */

public class BoxController
{
    private DrawBoxes mDrawBoxes;
    public CopyOnWriteArrayList<Box> BoxPool = new CopyOnWriteArrayList<>();

    private int ScreenWidth = 0;
    private int ScreenHeight = 0;

    private final int BOX_WIDTH_NUMBER = 3;
    private final int BOX_HEIGHT_NUMBER = 3;

    BoxController(DrawBoxes _drawBoxes)
    {
        mDrawBoxes = _drawBoxes;
        mDrawBoxes.initBoxPool(BoxPool);

        new Thread(){
            @Override
            public void run()
            {
                while ((mDrawBoxes.ScreenWidth == 0) && (mDrawBoxes.ScreenWidth == 0))
                {}
                ScreenWidth = mDrawBoxes.ScreenWidth;
                ScreenHeight = mDrawBoxes.ScreenHeight;
                boxInit();
            }
        }.start();

        /*Box mBox = new Box(100,100,300,300);
        Box mBox2 = new Box(100,400,350,450);
        BoxPool.add(mBox);
        BoxPool.add(mBox2);*/



        /*Box mBox3 = new Box(400,500,450,600);
        BoxPool.set(1,mBox3);
        poolChanged();*/



        /*Box mBox = new Box(100,100,300,300);
        Box mBox2 = new Box(100,400,350,450);
        mDrawBoxes.DrawBox(mBox);
        mDrawBoxes.DrawBox(mBox2);*/
        /*int h = mDrawBoxes.getHeight();
        int w = mDrawBoxes.getWidth();
        Log.d("BoxController","W:"+w+" H:"+h);*/
    }

    private void poolChanged()
    {
        if (mDrawBoxes.LocalDrawThread != null)
        {
            mDrawBoxes.initBoxPool(BoxPool);
            mDrawBoxes.LocalDrawThread.poolChange = true;
        }
    }
    private void boxInit()
    {
        //размер первого отступа
        int first_width_margin = (ScreenWidth - (Math.abs((ScreenWidth - 16) / BOX_WIDTH_NUMBER)*BOX_WIDTH_NUMBER))/2;
        int first_height_margin = (ScreenHeight - (Math.abs((ScreenHeight -16) / BOX_HEIGHT_NUMBER)* BOX_HEIGHT_NUMBER))/2;

        //int first_width_margin = (ScreenWidth - (ScreenWidth - 16 - (Math.abs((ScreenWidth - 16) / BOX_WIDTH_NUMBER)*BOX_WIDTH_NUMBER)))/2;
        //int first_height_margin = (ScreenHeight - (ScreenHeight - 16 -(Math.abs((ScreenHeight -16) / BOX_HEIGHT_NUMBER) * BOX_HEIGHT_NUMBER)))/2;

        Log.d ("f_m","w: " + first_width_margin + "h: " + first_height_margin);

        int newScreenWidth = (ScreenWidth - (first_width_margin*2) - 16);
        int newScreenHeight = (ScreenHeight- (first_height_margin * 2) - 16);

        Log.d("BoxController","W: "+newScreenWidth / BOX_WIDTH_NUMBER);
        int bw = Math.abs(newScreenWidth / BOX_WIDTH_NUMBER);//ширина box'а
        Log.d("BoxController","H: "+newScreenHeight / BOX_HEIGHT_NUMBER);
        int bh = Math.abs(newScreenHeight / BOX_HEIGHT_NUMBER);//высота box'а

        int margin_step_w = 4;
        int margin_step_h = 4;

        //Box mBox1 = new Box(1,1,bw,bh);


        for (int i = 0; i < BOX_HEIGHT_NUMBER; i++)
        {
            for (int j = 0; j < BOX_WIDTH_NUMBER; j++)
            {
                if (i == 0 && j == 0)
                {
                    BoxPool.add(new Box( margin_step_w + first_width_margin, margin_step_h + first_height_margin ,bw, bh));
                }
                else if (j == 0 )
                {
                    BoxPool.add(new Box( margin_step_w + first_width_margin , i * ( bh + margin_step_h ) + (margin_step_h+first_height_margin) , bw , bh));
                }
                else if ( i == 0)
                {
                    BoxPool.add(new Box ( j* (bw + margin_step_w) + (margin_step_w + first_width_margin), margin_step_h + first_height_margin,bw,bh));
                }
                else
                {
                    BoxPool.add(new Box (j*(bw+margin_step_w) + (margin_step_w + first_width_margin) , i*(bh+margin_step_h) + (margin_step_h+first_height_margin),bw,bh));
                }
                /*if (j == 0 )
                {
                    BoxPool.add(new Box (j*bw + first_width_margin+margin_step_w,i*bh+margin_step_h, bw - margin_step_w,bh - margin_step_h) );//переделать что-бы блоки находились ровно друг от друга!
                }
                else
                {
                    BoxPool.add(new Box (j*bw+margin_step_w + first_width_margin,i*bh+margin_step_h, bw - margin_step_w,bh - margin_step_h) );
                    Log.d("BoXWH:",""+(j*bw+margin_step_w)+" " + (i*bh+margin_step_h) + " " + ((j*bw) + bw) + " " + ((i*bh)+bh));
                }*/


            }
        }

        //Box mBox1 = new Box(100,100,200,200);


        poolChanged();

    }
}

