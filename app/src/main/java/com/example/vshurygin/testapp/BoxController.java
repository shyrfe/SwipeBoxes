package com.example.vshurygin.testapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.lang.reflect.Array;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by vshurygin on 21.09.2016.
 */

public class BoxController
{

    public CopyOnWriteArrayList<Box> BoxPool = new CopyOnWriteArrayList<>();

    private int[] mReferenceCoord;

    private DrawBoxes mDrawBoxes;
    private int ScreenWidth = 0;
    private int ScreenHeight = 0;

    private int mMinX;
    private int mMaxX;
    private int mMinY;
    private int mMaxY;

    private final int BOX_WIDTH_NUMBER = 3;
    private final int BOX_HEIGHT_NUMBER = 3;


    BoxController(DrawBoxes _drawBoxes)
    {
        mDrawBoxes = _drawBoxes;
        mDrawBoxes.initBoxPool(BoxPool);
        mDrawBoxes.LocalBoxController = this;

        new Thread(){
            @Override
            public void run()
            {
                while (true)
                {
                    if ((mDrawBoxes.ScreenWidth != 0) && (mDrawBoxes.ScreenHeight != 0))
                    {
                        ScreenWidth = mDrawBoxes.ScreenWidth;
                        ScreenHeight = mDrawBoxes.ScreenHeight;
                        break;
                    }
                }
                boxsInit();
            }
        }.start();
    }

    private float mStartMotionX = 0;
    private float mEndMotionX = 0;
    private float mStartMotionY = 0;
    private float mEndMotionY = 0;

    public void input(MotionEvent _event)
    {
        switch (_event.getAction())
        {
            case MotionEvent.ACTION_CANCEL:

                //Log.d("Input","Cancel");
                if ((mEndMotionX - mStartMotionX) > 0)
                {
                    slideClockwise();
                }
                else
                {
                    slideCounterclockwise();
                }
                break;

            case MotionEvent.ACTION_UP:

                Log.d("Input","Up");
                if ((mEndMotionY - mStartMotionY) < (mEndMotionX - mStartMotionX))
                {
                    if ((mEndMotionX - mStartMotionX) > 0)
                    {
                        slideClockwise();
                    }
                    else
                    {
                        slideCounterclockwise();
                    }
                }
                else
                {
                    if ((mEndMotionY - mStartMotionY) > 0)
                    {
                        slideClockwise();
                    }
                    else
                    {
                        slideCounterclockwise();
                    }
                }
                syncPositionWithReferenceCoord(BoxPool,mReferenceCoord);
                break;

            case MotionEvent.ACTION_DOWN:

                Log.d("Input","Down");
                mStartMotionX = _event.getX();
                mStartMotionY = _event.getY();
                break;

            case MotionEvent.ACTION_MOVE:

                Log.d("Input","Move");
                mEndMotionX = _event.getX();
                mEndMotionY = _event.getY();

                if ((mEndMotionY - mStartMotionY) < (mEndMotionX - mStartMotionX))
                {
                    if ((mEndMotionX - mStartMotionX) > 0)
                    {
                        slideClockwise();
                    }
                    else
                    {
                        slideCounterclockwise();
                    }
                }
                else
                {
                    if ((mEndMotionY - mStartMotionY) > 0)
                    {
                        slideClockwise();
                    }
                    else
                    {
                        slideCounterclockwise();
                    }
                }
                break;
        }
    }

    private void slideClockwise()
    {
        for (int i = 0; i < BoxPool.size(); i++)
        {
            int x = BoxPool.get(i).getX();
            int y = BoxPool.get(i).getY();

            for (int j = 0; j < 30; j++)
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
    }

    private void slideCounterclockwise()
    {
        for (int i = 0; i < BoxPool.size(); i++)
        {
            int x = BoxPool.get(i).getX();
            int y = BoxPool.get(i).getY();

            for (int j = 0; j < 30; j++)
            {
                if (x == mMinX && y < mMaxY)
                {
                    BoxPool.get(i).setY( BoxPool.get(i).getY() + 1);
                }
                else if (x == mMaxX && y > mMinY)
                {
                    BoxPool.get(i).setY( BoxPool.get(i).getY() - 1);
                }
                else if (x < mMaxX && y == mMaxY)
                {
                    BoxPool.get(i).setX( BoxPool.get(i).getX() + 1);
                }
                else if (x > mMinX && y == mMinY)
                {
                    BoxPool.get(i).setX( BoxPool.get(i).getX() - 1);
                }

                x = BoxPool.get(i).getX();
                y = BoxPool.get(i).getY();
            }
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

        mReferenceCoord = new int[(BOX_WIDTH_NUMBER * BOX_HEIGHT_NUMBER)*2];

        //размер первого отступа
        int first_width_margin = (ScreenWidth - (Math.abs((ScreenWidth - TOTAL_PADDING_WIDTH) / BOX_WIDTH_NUMBER)*BOX_WIDTH_NUMBER))/2;
        int first_height_margin = (ScreenHeight - (Math.abs((ScreenHeight -TOTAL_PADDING_HEIGHT) / BOX_HEIGHT_NUMBER)* BOX_HEIGHT_NUMBER))/2;

        int boxsSpaceWidth = (ScreenWidth - (first_width_margin*2) - TOTAL_PADDING_WIDTH);
        int boxsSpaceHeight = (ScreenHeight- (first_height_margin * 2) - TOTAL_PADDING_HEIGHT);

        int box_width = Math.abs(boxsSpaceWidth / BOX_WIDTH_NUMBER);//ширина box'а
        int box_height = Math.abs(boxsSpaceHeight / BOX_HEIGHT_NUMBER);//высота box'а

        int box_number = 1;

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
                            ,box_number));
                    box_number++;
                }
                mReferenceCoord[((i*BOX_HEIGHT_NUMBER + j)*2)] = j*(box_width+padding_step_w) + (padding_step_w + first_width_margin);
                mReferenceCoord[((i*BOX_HEIGHT_NUMBER + j)*2) + 1] = i*(box_height+padding_step_h) + (padding_step_h+first_height_margin);
            }
        }
        poolChanged();
    }

    private void syncPositionWithReferenceCoord(CopyOnWriteArrayList<Box> _boxArray, int[] _coordMass)
    {
        /*for (int i = 0; i <  (_coordMass.length/2); i++)
        {
            Log.d("ReferenceCoord","X: "+_coordMass[i*2]+" ");
            Log.d("ReferenceCoord","Y: "+_coordMass[i*2+1]+" ");
        }*/

        for (int i = 0; i < _boxArray.size(); i++)
        {
            int dx = Integer.MAX_VALUE;
            int dy = Integer.MAX_VALUE;
            int x = _boxArray.get(i).getX();
            int y = _boxArray.get(i).getY();

            for (int j = 0; j < (_coordMass.length/2); j++)
            {
                if ((_boxArray.get(i).getX() == mMinX) || (_boxArray.get(i).getX() == mMaxX))
                {
                    if ( Math.abs(_boxArray.get(i).getY() - _coordMass[(j*2)+1]) < dy )
                    {
                        dy = Math.abs(_boxArray.get(i).getY() - _coordMass[(j*2)+1]);
                        y = _coordMass[(j*2)+1];
                    }
                }
                else if ((_boxArray.get(i).getY() == mMinY) || (_boxArray.get(i).getY() == mMaxY))
                {
                    if ( Math.abs(_boxArray.get(i).getX() - _coordMass[j*2]) < dx )
                    {
                        dx = Math.abs(_boxArray.get(i).getX() - _coordMass[j*2]);
                        x = _coordMass[j*2];
                    }
                }
            }
            _boxArray.get(i).setX(x);
            _boxArray.get(i).setY(y);
        }
    }
}

