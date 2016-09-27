package com.example.vshurygin.testapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
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
    private BoxCellController mBoxCellController;
    private int ScreenWidth = 0;
    private int ScreenHeight = 0;
    private GestureDetector mGD;
    private ScaleGestureDetector mSGD;

    private boolean mForceFinished = true;
    private long mForce = 0;
    private long mLastTime = System.currentTimeMillis();


    private int mMinX;
    private int mMaxX;
    private int mMinY;
    private int mMaxY;

    private final int BOX_WIDTH_NUMBER = 3;
    private final int BOX_HEIGHT_NUMBER = 3;


    BoxController(DrawBoxes _drawBoxes)
    {
        mDrawBoxes = _drawBoxes;
        mSGD = new ScaleGestureDetector(_drawBoxes.LocalContext,new ScaleListener());
        mGD = new GestureDetector(_drawBoxes.LocalContext, new GestureListener());
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
        mGD.onTouchEvent(_event);
        mSGD.onTouchEvent(_event);

        if (_event.getAction() == MotionEvent.ACTION_UP)
        {
            //syncPositionWithReferenceCoord(BoxPool,mBoxCellController);
        }
    }

    public void animationUpdate()
    {
        //Log.d("Animation", "update");
        if (!mForceFinished)
        {

            if (mForce == 0)
            {
                mForceFinished = true;
                for (int i = 0; i < BoxPool.size(); i++)
                {
                    BoxPool.get(i).MoveLastTime = 0;
                }
            }
            else
            {
                long time = System.currentTimeMillis();
                long dTime = time - mLastTime;
                if (dTime >= 1000/60)
                {
                    MoveRight(3);
                    /*for (int i = 0; i < BoxPool.size(); i++)
                    {

                        *//*if (BoxPool.get(i).MoveLastTime == 0)
                        {
                            BoxPool.get(i).MoveLastTime = time;
                        }
                        time = System.currentTimeMillis();*//*
                        //long dTime = time - BoxPool.get(i).MoveLastTime;
                        int speed = 3;
                        float dt = (time - BoxPool.get(i).MoveLastTime)/1000;
                        BoxPool.get(i).setX(BoxPool.get(i).getX() + speed);
                        BoxPool.get(i).MoveLastTime = time;//(int)(dTime * 5));
                    }*/
                    mLastTime = time;
                    mForce = mForce - 1;
                }

            }

        }
    }

    private void slideClockwise()
    {
        for (int i = 0; i < BoxPool.size(); i++)
        {
            int x = BoxPool.get(i).getX();
            int y = BoxPool.get(i).getY();

            for (int j = 0; j < 10; j++)
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
    private void slideClockwise(int _force)
    {
            mForce = _force;
            mForceFinished = false;
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
        mBoxCellController = new BoxCellController(mReferenceCoord);
        poolChanged();
    }

    private void syncPositionWithReferenceCoord(CopyOnWriteArrayList<Box> _boxArray, BoxCellController _boxCellController)
    {
            int dx = Integer.MAX_VALUE;
            int dy = Integer.MAX_VALUE;
            int x = _boxArray.get(0).getX();
            int y = _boxArray.get(0).getY();
            int cellNumber = 0;

            for (int j = 0; j < _boxCellController.BoxCellArray.size(); j++)
            {
                if ((_boxArray.get(0).getX() == mMinX) || (_boxArray.get(0).getX() == mMaxX))
                {
                    if ( Math.abs(_boxArray.get(0).getY() - _boxCellController.BoxCellArray.get(j).getY()) < dy )
                    {
                            dy = Math.abs(_boxArray.get(0).getY() - _boxCellController.BoxCellArray.get(j).getY());
                            y = _boxCellController.BoxCellArray.get(j).getY();
                            cellNumber = j;
                    }
                }
                else if ((_boxArray.get(0).getY() == mMinY) || (_boxArray.get(0).getY() == mMaxY))
                {
                    if ( Math.abs(_boxArray.get(0).getX() - _boxCellController.BoxCellArray.get(j).getX()) < dx )
                    {
                            dx = Math.abs(_boxArray.get(0).getX() - _boxCellController.BoxCellArray.get(j).getX());
                            x = _boxCellController.BoxCellArray.get(j).getX();
                            cellNumber = j;
                    }
                }
            }

        BoxCellController.BoxCell localCell = _boxCellController.BoxCellArray.get(cellNumber);
        for (int i = 0; i < _boxArray.size();i++)
        {
            if (i == 4)
            {i++;}
            _boxArray.get(i).setX(localCell.getX());
            _boxArray.get(i).setY(localCell.getY());
            localCell = localCell.NextCell;

        }

    }

    private void MoveRight(int _step)
    {
        for (int i = 0; i < BoxPool.size(); i++)
        {
            int x = BoxPool.get(i).getX();
            int y = BoxPool.get(i).getY();


            if (x < mMaxX && y == mMinY)
            {
                if (BoxPool.get(i).getX() + _step >= mMaxX)
                {
                    BoxPool.get(i).setX(mMaxX);
                }
                else
                {
                    BoxPool.get(i).setX( BoxPool.get(i).getX() + _step);
                }

            }
            else if (x > mMinX && y == mMaxY)
            {
                if (BoxPool.get(i).getX() - _step <= mMinX)
                {
                    BoxPool.get(i).setX(mMinX);
                }
                else
                {
                    BoxPool.get(i).setX( BoxPool.get(i).getX() - _step);
                }

            }
            else if (x == mMaxX && y < mMaxY)
            {
                if (BoxPool.get(i).getY() + _step >= mMaxY)
                {
                    BoxPool.get(i).setY(mMaxY);
                }
                else
                {
                    BoxPool.get(i).setY( BoxPool.get(i).getY() + _step);
                }
            }
            else if (x == mMinX && y > mMinY)
            {
                if (BoxPool.get(i).getY() - _step <= mMinY)
                {
                    BoxPool.get(i).setY(mMinY);
                }
                else
                {
                    BoxPool.get(i).setY( BoxPool.get(i).getY() - _step);
                }

            }
            x = BoxPool.get(i).getX();
            y = BoxPool.get(i).getY();

        }
    }

    public class BoxCellController
    {
        ArrayList<BoxCell> BoxCellArray = new ArrayList<>();

        BoxCellController(int[] _coordMass)
        {
            for (int i = 0; i < (_coordMass.length/2); i++)
            {
                if (i == 0)
                {
                    //Log.d("ADD",""+_coordMass[i*2]+" " + _coordMass[i*2+1]);
                    BoxCellArray.add(new BoxCell(_coordMass[i*2],_coordMass[i*2+1]));
                }
                else if ( i == (_coordMass.length/2)-1)
                {
                    BoxCellArray.add(new BoxCell(_coordMass[i*2],_coordMass[i*2+1]));
                    BoxCellArray.get(i).NextCell = BoxCellArray.get(0);
                    BoxCellArray.get(0).LastCell = BoxCellArray.get(i);
                }
                else
                {
                    BoxCellArray.add(new BoxCell(_coordMass[i*2],_coordMass[i*2+1],BoxCellArray.get(i-1)));
                    BoxCellArray.get(i-1).NextCell = BoxCellArray.get(i);
                }
            }

            BoxCellArray.get(3).NextCell = BoxCellArray.get(0);
            BoxCellArray.get(0).LastCell = BoxCellArray.get(3);

            BoxCellArray.get(6).NextCell = BoxCellArray.get(3);
            BoxCellArray.get(3).LastCell = BoxCellArray.get(6);

            BoxCellArray.get(7).NextCell = BoxCellArray.get(6);
            BoxCellArray.get(6).LastCell = BoxCellArray.get(7);

            BoxCellArray.get(8).NextCell = BoxCellArray.get(7);
            BoxCellArray.get(7).LastCell = BoxCellArray.get(8);

            BoxCellArray.get(5).NextCell = BoxCellArray.get(8);
            BoxCellArray.get(8).LastCell = BoxCellArray.get(5);

            BoxCellArray.get(2).NextCell = BoxCellArray.get(5);
            BoxCellArray.get(5).LastCell = BoxCellArray.get(2);

        }

        public void refresh()
        {
            for (int i = 0; i < BoxCellArray.size(); i++)
            {
                BoxCellArray.get(i).isEmpty = true;
            }
        }

        public class BoxCell
        {
            public BoxCell NextCell;
            public BoxCell LastCell;

            Boolean isEmpty = true;
            private int mX;
            private int mY;

            BoxCell (int _x, int _y)
            {
                this(_x,_y,null,null);
            }
            BoxCell (int _x, int _y, BoxCell _lastCell)
            {
                this(_x,_y,_lastCell,null);
            }
            BoxCell(int _x, int _y, BoxCell _lastCell, BoxCell _nextCell)
            {
                mX = _x;
                mY = _y;

                LastCell = _lastCell;
                NextCell = _nextCell;
            }

            public int getX()
            {
                return mX;
            }
            public int getY()
            {
                return mY;
            }

        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public void onLongPress(MotionEvent _event)
        {
            slideClockwise(100);
            Log.d("Gesture", "LongPress");
        }
        @Override
        public boolean onScroll(MotionEvent _event1, MotionEvent _event2, float _distanceX, float _distanceY)
        {
            Log.d("Gesture", "Scroll");
            boolean result = false;

            try
            {
                float dX = _event2.getX() - _event1.getX();
                float dY = _event2.getY() - _event1.getY();

                if (Math.abs(dX) >  Math.abs(dY))
                {
                    if (dX > 0)
                    {
                        //rigthtX
                        if (_event2.getY() < ScreenHeight/2)
                        {
                            slideClockwise();
                        }
                        else
                        {
                            slideCounterclockwise();
                        }

                        //syncPositionWithReferenceCoord(BoxPool,mBoxCellController);
                    }
                    else
                    {
                        if (_event2.getY() > ScreenHeight/2)
                        {
                            slideCounterclockwise();
                        }
                        else
                        {
                            slideClockwise();
                        }
                        // syncPositionWithReferenceCoord(BoxPool,mBoxCellController);
                        //leftX
                    }
                    result = true;
                }
                else
                {
                    if (dY > 0)
                    {
                        if (_event2.getX() > ScreenWidth/2)
                        {
                            slideClockwise();
                        }
                        else
                        {
                            slideCounterclockwise();
                        }

                        // syncPositionWithReferenceCoord(BoxPool,mBoxCellController);
                        //BottomY
                    }
                    else
                    {
                        if (_event2.getX() < ScreenWidth/2)
                        {
                            slideCounterclockwise();
                        }
                        else
                        {
                            slideClockwise();
                        }
                        //syncPositionWithReferenceCoord(BoxPool,mBoxCellController);
                        //TopY
                    }
                    result = true;
                }
            }
            catch (Exception e)
            {e.printStackTrace();}

            //slideClockwise();
            return result;
        }
        @Override
        public boolean onFling (MotionEvent _event1, MotionEvent _event2, float _velocityX, float _velocityY)
        {
            Log.d("Gesture","Fling");
            final int SWIPE_THRESHOLD = 100;
            final int SWIPE_VELOCITY_THRESHOLD = 100;

            boolean result = false;
            //Log.d("Fling","X: "+_velocityX + " Y: " + _velocityY);
            try
            {
                float dX = _event2.getX() - _event1.getX();
                float dY = _event2.getY() - _event1.getY();

                if (Math.abs(dX) >  Math.abs(dY))
                {
                    if (Math.abs(dX) > SWIPE_THRESHOLD && Math.abs(_velocityX) > SWIPE_VELOCITY_THRESHOLD)
                    {
                        if (dX > 0)
                        {
                          //rigthtX
                            slideClockwise();
                            //syncPositionWithReferenceCoord(BoxPool,mBoxCellController);
                        }
                        else
                        {
                            slideCounterclockwise();
                           // syncPositionWithReferenceCoord(BoxPool,mBoxCellController);
                            //leftX
                        }
                        result = true;
                    }
                }
                else if (Math.abs(dY) > SWIPE_THRESHOLD && Math.abs(_velocityY) > SWIPE_VELOCITY_THRESHOLD)
                {
                    if (dY > 0)
                    {
                        slideClockwise();
                       // syncPositionWithReferenceCoord(BoxPool,mBoxCellController);
                        //BottomY
                    }
                    else
                    {
                        slideCounterclockwise();
                        //syncPositionWithReferenceCoord(BoxPool,mBoxCellController);

                        //TopY
                    }
                    result = true;
                }
            }
            catch (Exception e)
            {e.printStackTrace();}
            return result;
        }
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector _detector)
        {
            Log.d("scale", "detect");
            return true;
        }
    }
}

