package com.example.vshurygin.testapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.widget.Toast;

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
    public ArrayList<MovingBox> MovingBoxPool = new ArrayList<>();
    public ArrayList<MovingBoxMapPoint> MovingBoxMap = new ArrayList<>();

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
    private long mForceStep;


    private int mMinX;
    private int mMaxX;
    private int mMinY;
    private int mMaxY;

    private final int BOX_WIDTH_NUMBER = 3;
    private final int BOX_HEIGHT_NUMBER = 3;

    private int mBoxXDistance;
    private int mBoxYDistance;

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

        /*if (_event.getAction() == MotionEvent.ACTION_UP)
        {
            //syncPositionWithReferenceCoord(BoxPool,mBoxCellController);
        }*/
    }

    public void animationUpdate()
    {
        //Log.d("Animation", "update");
        if (mLastTime == 0)
        {
            //MoveRight();
            mLastTime = System.currentTimeMillis();
        }

        long time = System.currentTimeMillis();
        long dTime = (time - mLastTime) / 1000;


        if (dTime >= 1/60)
        {
            //syncPositionWithRightReferenceCoord();
            //syncPositionWithRightReferenceCoord();
            //syncPositionWithLeftReferenceCoord();
            //syncPositionWithLeftReferenceCoord();
            //MoveRight();
            //MoveRight(3);
            if (mForce == 0)
            {
                //syncPositionWithLeftReferenceCoord();
                //syncPositionWithLeftReferenceCoord();
                mForceFinished = true;
            }
            else
            {
                if (Math.abs(mForce) < 3*mForceStep && Math.abs(mForce) > 2*mForceStep)
                {
                    if (mForce > 0)
                    {
                        for (int i = 0; i < 6; i++)
                        {
                            Move(1);
                        }
                    }
                    else if (mForce < 0)
                    {
                        for (int i = 0; i < 6; i++)
                        {
                            Move(-1);
                        }
                    }
                }
                else if (Math.abs(mForce) < 2*mForceStep && Math.abs(mForce) > mForceStep)
                {
                    if (mForce > 0)
                    {
                        for (int i = 0; i < 3; i++)
                        {
                            Move(1);
                        }

                    }
                    else if (mForce < 0)
                    {
                        for (int i = 0; i < 3; i++)
                        {
                            Move(-1);
                        }
                    }
                }
                else if (Math.abs(mForce) < mForceStep)
                {
                    if (mForce > 0)
                    {
                        Move(1);
                    }
                    else if (mForce < 0)
                    {
                        Move(-1);
                    }
                }
                //MoveLeft(1);
                if (mForce > 0)
                {
                    mForce = mForce - 1;
                }
                else if (mForce < 0)
                {
                    mForce = mForce + 1;
                }
            }
            mLastTime = time;
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
        movingBoxPoolInit(BoxPool);
        poolChanged();
    }
    private void movingBoxPoolInit(CopyOnWriteArrayList<Box> _boxPool)
    {
        for (int i = 0; i < _boxPool.size(); i++)
        {
            if (i == 0)
            {
                MovingBoxPool.add(new MovingBox(_boxPool.get(i),
                                                _boxPool.get(i).getX(),
                                                _boxPool.get(i).getY(),null,null));
            }
            else if ( i == (_boxPool.size()-1))
            {
                MovingBoxPool.add(new MovingBox(_boxPool.get(i),
                                            _boxPool.get(i).getX(),
                                            _boxPool.get(i).getY(),
                                            MovingBoxPool.get(i-1),
                                            MovingBoxPool.get(0)));
                MovingBoxPool.get(0).LastBox = MovingBoxPool.get(i);
            }
            else
            {
                MovingBoxPool.add(new MovingBox(_boxPool.get(i),
                        _boxPool.get(i).getX(),
                        _boxPool.get(i).getY(),
                        MovingBoxPool.get(i-1),
                        null));
                MovingBoxPool.get(i-1).NextBox = MovingBoxPool.get(i);
            }
        }

        MovingBoxPool.get(3).NextBox = MovingBoxPool.get(0);
        MovingBoxPool.get(0).LastBox = MovingBoxPool.get(3);

        MovingBoxPool.get(6).NextBox = MovingBoxPool.get(3);
        MovingBoxPool.get(3).LastBox = MovingBoxPool.get(6);

        MovingBoxPool.get(7).NextBox = MovingBoxPool.get(6);
        MovingBoxPool.get(6).LastBox = MovingBoxPool.get(7);

        MovingBoxPool.get(8).NextBox = MovingBoxPool.get(7);
        MovingBoxPool.get(7).LastBox = MovingBoxPool.get(8);

        MovingBoxPool.get(5).NextBox = MovingBoxPool.get(8);
        MovingBoxPool.get(8).LastBox = MovingBoxPool.get(5);

        MovingBoxPool.get(2).NextBox = MovingBoxPool.get(5);
        MovingBoxPool.get(5).LastBox = MovingBoxPool.get(2);

        for (int i = 0; i < MovingBoxPool.size(); i++)
        {
            MovingBoxPool.get(i).ThisPoint = new MovingBoxMapPoint(MovingBoxPool.get(i).getX(),MovingBoxPool.get(i).getY(),null,null);
        }

        for (int i = 0; i < MovingBoxPool.size(); i++)
        {
            MovingBoxPool.get(i).ThisPoint.LastPoint = MovingBoxPool.get(i).LastBox.ThisPoint;
            MovingBoxPool.get(i).ThisPoint.NextPoint = MovingBoxPool.get(i).NextBox.ThisPoint;
            MovingBoxPool.get(i).NextPoint = MovingBoxPool.get(i).NextBox.ThisPoint;
        }

        mBoxXDistance = MovingBoxPool.get(0).NextBox.getX() - MovingBoxPool.get(0).getX();
        mBoxYDistance = MovingBoxPool.get(0).LastBox.getY() - MovingBoxPool.get(0).getY();
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

    private Box findBox (int _x, int _y)
    {
        for (int i = 0; i < BoxPool.size();i++)
        {
            Box localBox = BoxPool.get(i);
            if ((localBox.getX() <= _x) && (_x <= (localBox.getX()+localBox.getWidth())) && (localBox.getY() <= _y) && (_y <= (localBox.getY() + localBox.getHeight())))
            {
                return localBox;
            }
        }
        return null;
    }

    private void MoveWithForce(int _force)
    {
        mForce = _force;
        mForceStep = Math.abs(mForce) / 3;
        mForceFinished = false;
    }
    private void Move(int _direction)
    {
        if (_direction > 0)
        {
            MoveRight();
        }
        else if (_direction < 0)
        {
            MoveLeft();
        }

    }
    private void MoveRight()
    {
        int step = 1;
        for (int i = 0; i < BoxPool.size(); i++)
        {
            int x = BoxPool.get(i).getX();
            int y = BoxPool.get(i).getY();


            if (x < mMaxX && y == mMinY)
            {
                BoxPool.get(i).setX( BoxPool.get(i).getX() + step);
            }
            else if (x > mMinX && y == mMaxY)
            {
                BoxPool.get(i).setX( BoxPool.get(i).getX() - step);
            }
            else if (x == mMaxX && y < mMaxY)
            {
                BoxPool.get(i).setY( BoxPool.get(i).getY() + step);
            }
            else if (x == mMinX && y > mMinY)
            {
                BoxPool.get(i).setY( BoxPool.get(i).getY() - step);
            }
        }
    }
    private void MoveLeft ()
    {
        int step = 1;

        for (int i = 0; i < BoxPool.size(); i++)
        {
            int x = BoxPool.get(i).getX();
            int y = BoxPool.get(i).getY();

                if (x == mMinX && y < mMaxY)
                {
                    BoxPool.get(i).setY( BoxPool.get(i).getY() + step);
                }
                else if (x == mMaxX && y > mMinY)
                {
                    BoxPool.get(i).setY( BoxPool.get(i).getY() - step);
                }
                else if (x < mMaxX && y == mMaxY)
                {
                    BoxPool.get(i).setX( BoxPool.get(i).getX() + step);
                }
                else if (x > mMinX && y == mMinY)
                {
                    BoxPool.get(i).setX( BoxPool.get(i).getX() - step);
                }
        }
    }

    private void syncPositionWithRightReferenceCoord()
    {
        int _step = 1;

        for (int i = 0; i < MovingBoxPool.size(); i++)
        {
            int x = MovingBoxPool.get(i).getX();
            int y = MovingBoxPool.get(i).getY();

            if (x < mMaxX && y == mMinY)
            {
                if (MovingBoxPool.get(i).getY() == MovingBoxPool.get(i).NextBox.getY())
                {
                    if ((MovingBoxPool.get(i).NextBox.getX() - MovingBoxPool.get(i).getX()) > mBoxXDistance)
                    {
                        while ((MovingBoxPool.get(i).NextBox.getX() - MovingBoxPool.get(i).getX()) > mBoxXDistance)
                        {
                            MovingBoxPool.get(i).setX( MovingBoxPool.get(i).getX() + _step);
                        }
                    }
                }
                else
                {
                    MovingBoxPool.get(i).setX( MovingBoxPool.get(i).getX() + _step);
                }

            }
            else if (x > mMinX && y == mMaxY)
            {
                if (MovingBoxPool.get(i).getY() == MovingBoxPool.get(i).NextBox.getY())
                {
                    if ((MovingBoxPool.get(i).getX() - MovingBoxPool.get(i).NextBox.getX()) > mBoxXDistance)
                    {
                        while ((MovingBoxPool.get(i).getX() - MovingBoxPool.get(i).NextBox.getX()) > mBoxXDistance)
                        {
                            MovingBoxPool.get(i).setX( MovingBoxPool.get(i).getX() - _step);
                        }
                    }
                }
                else
                {
                    MovingBoxPool.get(i).setX( MovingBoxPool.get(i).getX() - _step);
                }

            }
            else if (x == mMaxX && y < mMaxY)
            {
                if (MovingBoxPool.get(i).getX() == MovingBoxPool.get(i).NextBox.getX())
                {
                    if ((MovingBoxPool.get(i).NextBox.getY() - MovingBoxPool.get(i).getY()) > mBoxYDistance)
                    {
                        while ((MovingBoxPool.get(i).NextBox.getY() - MovingBoxPool.get(i).getY()) > mBoxYDistance)
                        {
                            MovingBoxPool.get(i).setY(MovingBoxPool.get(i).getY() + _step);
                        }
                    }
                }
                else
                {
                    MovingBoxPool.get(i).setY( MovingBoxPool.get(i).getY() + _step );
                }

            }
            else if (x == mMinX && y > mMinY)
            {
                if (MovingBoxPool.get(i).getX() == MovingBoxPool.get(i).NextBox.getX())
                {
                    if (( MovingBoxPool.get(i).getY() - MovingBoxPool.get(i).NextBox.getY()) > mBoxYDistance)
                    {
                        while (( MovingBoxPool.get(i).getY() - MovingBoxPool.get(i).NextBox.getY()) > mBoxYDistance)
                        {
                            MovingBoxPool.get(i).setY(MovingBoxPool.get(i).getY() - _step);
                        }
                    }
                }
                else
                {
                    MovingBoxPool.get(i).setY( MovingBoxPool.get(i).getY() - _step );
                }
            }
        }
    }

    private void syncPositionWithLeftReferenceCoord()
    {
        int _step = 1;

        for (int i = 0; i < MovingBoxPool.size(); i++)
        {
            int x = MovingBoxPool.get(i).getX();
            int y = MovingBoxPool.get(i).getY();

            if (x == mMinX && y < mMaxY)
            {
                if (MovingBoxPool.get(i).getX() == MovingBoxPool.get(i).LastBox.getX())
                {
                    if ((MovingBoxPool.get(i).LastBox.getY() - MovingBoxPool.get(i).getY()) > mBoxYDistance)
                    {
                        while ((MovingBoxPool.get(i).LastBox.getY() - MovingBoxPool.get(i).getY()) > mBoxYDistance)
                        {
                            MovingBoxPool.get(i).setY( MovingBoxPool.get(i).getY() + _step);
                        }
                    }
                }
                else
                {
                    MovingBoxPool.get(i).setY( MovingBoxPool.get(i).getY() + _step);
                }
            }
            else if (x < mMaxX && y == mMaxY)
            {
                if(MovingBoxPool.get(i).getY() == MovingBoxPool.get(i).LastBox.getY())
                {
                    if ((MovingBoxPool.get(i).LastBox.getX() - MovingBoxPool.get(i).getX()) > mBoxXDistance)
                    {
                        while ((MovingBoxPool.get(i).LastBox.getX() - MovingBoxPool.get(i).getX()) > mBoxXDistance)
                        {
                            MovingBoxPool.get(i).setX( MovingBoxPool.get(i).getX() + _step);
                        }
                    }
                }
                else
                {
                    MovingBoxPool.get(i).setX(MovingBoxPool.get(i).getX() + _step);
                }

            }
            else if (x == mMaxX && y > mMinY)
            {
                if (MovingBoxPool.get(i).getX() == MovingBoxPool.get(i).LastBox.getX())
                {
                    if ((MovingBoxPool.get(i).getY() - MovingBoxPool.get(i).LastBox.getY()) > mBoxYDistance)
                    {
                        while ((MovingBoxPool.get(i).getY() - MovingBoxPool.get(i).LastBox.getY()) > mBoxYDistance)
                        {
                            MovingBoxPool.get(i).setY( MovingBoxPool.get(i).getY() - _step);
                        }
                    }
                }
                else
                {
                    MovingBoxPool.get(i).setY( MovingBoxPool.get(i).getY() - _step);
                }

            }
            else if (x > mMinX && y == mMinY)
            {
                if(MovingBoxPool.get(i).getY() == MovingBoxPool.get(i).LastBox.getY())
                {
                    if ((MovingBoxPool.get(i).getX() - MovingBoxPool.get(i).LastBox.getX()) > mBoxXDistance)
                    {
                        while ((MovingBoxPool.get(i).getX() - MovingBoxPool.get(i).LastBox.getX()) > mBoxXDistance)
                        {
                            MovingBoxPool.get(i).setX( MovingBoxPool.get(i).getX() - _step);
                        }
                    }
                }
                else
                {
                    MovingBoxPool.get(i).setX( MovingBoxPool.get(i).getX() - _step);
                }
            }
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
            //slideClockwise(100);
            Box localBox = findBox((int)_event.getX(),(int)_event.getY());
            if (localBox != null)
            {
                Toast.makeText(mDrawBoxes.LocalContext,""+localBox.getNumber(),Toast.LENGTH_SHORT).show();
                Log.d("Gesture", "LongPress start #"+localBox.getNumber());
            }
            else
            {
                Toast.makeText(mDrawBoxes.LocalContext,"Box not found!",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public boolean onScroll(MotionEvent _event1, MotionEvent _event2, float _distanceX, float _distanceY)
        {
            //Log.d("Gesture", "Scroll");
            boolean result = false;

            try
            {
                float dX = _event2.getX() - _event1.getX();
                float dY = _event2.getY() - _event1.getY();

                if (Math.abs(dX) >  Math.abs(dY))
                {
                    if (dX > 0)
                    {
                        //LEFT TO RIGHT
                        if (_event2.getY() < ScreenHeight/2)
                        {
                            MoveWithForce(1000);
                            //Move(1);
                        }
                        else
                        {
                            MoveWithForce(-1000);
                            //Move(-1);
                        }

                    }
                    else
                    {
                        //RIGHT TO LEFT
                        if (_event2.getY() > ScreenHeight/2)
                        {
                            MoveWithForce(1000);
                            //Move(1);
                        }
                        else
                        {
                            MoveWithForce(-1000);
                            //Move(-1);
                        }
                    }
                    result = true;
                }
                else
                {

                    if (dY > 0)
                    {
                        //TOP TO BOTTOM
                        if (_event2.getX() > ScreenWidth/2)
                        {
                            MoveWithForce(1000);
                            //Move(1);
                        }
                        else
                        {
                            MoveWithForce(-1000);
                            //Move(-1);
                        }
                    }
                    else
                    {
                        //BOTTOM TO TOP
                        if (_event2.getX() < ScreenWidth/2)
                        {
                            MoveWithForce(1000);
                            //Move(1);

                        }
                        else
                        {
                            MoveWithForce(-1000);
                            //Move(-1);
                        }
                    }
                    result = true;
                }
            }
            catch (Exception e)
            {e.printStackTrace();}

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

