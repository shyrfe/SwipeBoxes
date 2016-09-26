package com.example.vshurygin.testapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
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

    private int mMinX;
    private int mMaxX;
    private int mMinY;
    private int mMaxY;

    private final int BOX_WIDTH_NUMBER = 3;
    private final int BOX_HEIGHT_NUMBER = 3;

    private int mi = 0;

    BoxController(DrawBoxes _drawBoxes)
    {
        mDrawBoxes = _drawBoxes;
        mDrawBoxes.initBoxPool(BoxPool);
        mDrawBoxes.LocalBoxController = this;

        new Thread(){
            @Override
            public void run()
            {
                while ((mDrawBoxes.ScreenWidth == 0) && (mDrawBoxes.ScreenHeight == 0))
                {

                }
                ScreenWidth = mDrawBoxes.ScreenWidth;
                ScreenHeight = mDrawBoxes.ScreenHeight;
                boxsInit();
            }
        }.start();
    }

    public void input (MotionEvent _event)
    {

        switch (_event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
               /* Log.d("Input","Down");
                Log.d("Input","X: " + _event.getX());
                Log.d("Input","Y: " + _event.getY());
                BoxPool.get(1).setX(Math.abs((int)_event.getX()));
                BoxPool.get(1).setY(Math.abs((int)_event.getY()));*/
                /*if (mi == 0 )
                {
                    for (int i = 0; i < BoxPool.size(); i++)
                    {
                        BoxPool.get(i).setX(BoxPool.get(i).getX() + 100);
                    }
                    mi = 1;
                    Log.d("INPUT","mi = 1");
                }
                else if (mi == 1)
                {
                    for (int i = 0; i < BoxPool.size(); i++)
                    {
                        BoxPool.get(i).setX(BoxPool.get(i).getX() - 100);
                    }
                    mi = 0;
                    Log.d("INPUT","mi = 0");
                }*/
                for (int i = 0; i < BoxPool.size(); i++)
                {
                    int x = BoxPool.get(i).getX();
                    int y = BoxPool.get(i).getY();

                    //Log.d("Input","" + mMinX + " " + mMinY + " " + mMaxX + " " + mMaxY);
                    for (int j = 0; j < 100; j++)
                    {
                        if (x < mMaxX && y == mMinY)
                        {
                            BoxPool.get(i).setX( BoxPool.get(i).getX() + 1);
                        }
                        else if (x > mMinX && y == mMaxY)
                        {
                            BoxPool.get(i).setX( BoxPool.get(i).getX() - 1);
                        }
                        else if (x == mMaxX && y < mMaxY)
                        {
                            BoxPool.get(i).setY( BoxPool.get(i).getY() + 1);
                        }
                        else if (x == mMinX && y > mMinY)
                        {
                            BoxPool.get(i).setY( BoxPool.get(i).getY() - 1);
                        }

                        x = BoxPool.get(i).getX();
                        y = BoxPool.get(i).getY();
                    }

                }

                break;
            case MotionEvent.ACTION_UP:
                Log.d("Input","Up");
                break;
            case MotionEvent.ACTION_SCROLL:
                if (mi < 300)
                {
                    mi++;
                    BoxPool.get(0).setX(mi);
                }
                else
                {
                    mi = 0;
                }
                break;
        }
    }

    private void poolChanged()
    {
        if (mDrawBoxes.LocalDrawThread != null)
        {
            mDrawBoxes.initBoxPool(BoxPool);
            mDrawBoxes.LocalDrawThread.poolChange = true;
        }
    }

    private void boxsInit()
    {
        final int PADDING = 4;
        int padding_step_w = PADDING;
        int padding_step_h = PADDING;
        final int TOTAL_PADDING_WIDTH = PADDING * (BOX_WIDTH_NUMBER+1);
        final int TOTAL_PADDING_HEIGHT = PADDING * (BOX_HEIGHT_NUMBER+1);


        //размер первого отступа
        int first_width_margin = (ScreenWidth - (Math.abs((ScreenWidth - TOTAL_PADDING_WIDTH) / BOX_WIDTH_NUMBER)*BOX_WIDTH_NUMBER))/2;
        int first_height_margin = (ScreenHeight - (Math.abs((ScreenHeight -TOTAL_PADDING_HEIGHT) / BOX_HEIGHT_NUMBER)* BOX_HEIGHT_NUMBER))/2;

        int boxsSpaceWidth = (ScreenWidth - (first_width_margin*2) - TOTAL_PADDING_WIDTH);
        int boxsSpaceHeight = (ScreenHeight- (first_height_margin * 2) - TOTAL_PADDING_HEIGHT);

        int box_width = Math.abs(boxsSpaceWidth / BOX_WIDTH_NUMBER);//ширина box'а
        int box_height = Math.abs(boxsSpaceHeight / BOX_HEIGHT_NUMBER);//высота box'а



        for (int i = 0; i < BOX_HEIGHT_NUMBER; i++)
        {
            for (int j = 0; j < BOX_WIDTH_NUMBER; j++)
            {
                if (i == 0 && j == 0)
                {
                    mMinX = j*(box_width+padding_step_w) + (padding_step_w + first_width_margin);
                    mMinY = i*(box_height+padding_step_h) + (padding_step_h+first_height_margin);
                }
                if (i == BOX_HEIGHT_NUMBER - 1 && j == BOX_WIDTH_NUMBER - 1)
                {
                    mMaxX = j*(box_width+padding_step_w) + (padding_step_w + first_width_margin);
                    mMaxY = i*(box_height+padding_step_h) + (padding_step_h+first_height_margin);
                }

                if(i == 1 && j == 1)
                {
                    BoxPool.add(new Box (
                            j*(box_width+padding_step_w) + (padding_step_w + first_width_margin)
                            ,i*(box_height+padding_step_h) + (padding_step_h+first_height_margin)
                            ,box_width,box_height
                            ,Color.RED));
                }
                else
                {
                    BoxPool.add(new Box (
                            j*(box_width+padding_step_w) + (padding_step_w + first_width_margin)
                            ,i*(box_height+padding_step_h) + (padding_step_h+first_height_margin)
                            ,box_width,box_height
                            ,Color.parseColor("#BDE0EB")
                            ,1));
                }


            }
        }
        poolChanged();

    }


}

