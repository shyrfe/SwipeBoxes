package com.example.vshurygin.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by vshurygin on 21.09.2016.
 */

public class DrawBoxes extends SurfaceView implements SurfaceHolder.Callback
{

    public DrawThread LocalDrawThread;
    public BoxController LocalBoxController;
    public Context LocalContext;

    public int ScreenWidth = 0;
    public int ScreenHeight = 0;

    private CopyOnWriteArrayList<Box> mBoxStack = new CopyOnWriteArrayList<>();

    DrawBoxes(Context _context)
    {
        super(_context);
        LocalContext = _context;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder _holder)
    {
        LocalDrawThread = new DrawThread(getHolder(),getResources(),mBoxStack);
        LocalDrawThread.setRunning(true);
        LocalDrawThread.start();
        ScreenWidth = this.getWidth();
        ScreenHeight = this.getHeight();
        Log.d("DrawBoxes","W: " + ScreenWidth + " H: "+ ScreenHeight);
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int _format, int _width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder _holder)
    {
        boolean retry = true;
        LocalDrawThread.setRunning(false);
        while(retry)
        {
            try
            {
                LocalDrawThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent _event)
    {
        if (LocalBoxController != null)
        {
            LocalBoxController.input(_event);
        }
        return true;
    }

    public void initBoxPool(CopyOnWriteArrayList _BoxPool)
    {
        mBoxStack = _BoxPool;
        if(LocalDrawThread != null)
        {
            LocalDrawThread.mBoxPool = _BoxPool;
        }
    }

//---------------------------------------------------------------------------------------------------------
    class DrawThread extends Thread
    {
        public boolean poolChange = false;
        public CopyOnWriteArrayList<Box> mBoxPool = new CopyOnWriteArrayList<>();


        private final int BACKGROUND_COLOR = Color.WHITE;

        private Bitmap mBoxPattern;
        private SurfaceHolder mSurfaceHolder;
        private boolean isRun = false;
        private CopyOnWriteArrayList<Box> mOutsideBoxPool;


        public DrawThread(SurfaceHolder _surfaceHolder, Resources _resources,CopyOnWriteArrayList _boxPool)
        {
            this.mSurfaceHolder = _surfaceHolder;
            mOutsideBoxPool = _boxPool;
            initBoxPool(_boxPool);
        }

        public void setRunning(boolean _run)
        {
            isRun = _run;
        }

        private void initBoxPool(CopyOnWriteArrayList _BoxPool)
        {
            try
            {
                mBoxPool.addAll(_BoxPool);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            Canvas canvas;
            Paint paint;

            while (isRun)
            {
                canvas = null;
                try
                {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    paint = new Paint();

                    if (poolChange)
                    {
                        try
                        {
                            for (int i = 0; i < mOutsideBoxPool.size(); i++)
                            {
                                if (!(mOutsideBoxPool.get(i).equals(mBoxPool.get(i))))
                                {
                                    mBoxPool.set(i,mOutsideBoxPool.get(i));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            Log.d("DrawBoxes","BoxPoolIsEmpty");
                            e.printStackTrace();
                        }
                        finally
                        {
                            poolChange = false;
                        }
                    }
                    synchronized (mSurfaceHolder)
                    {
                        try {
                            canvas.drawColor(BACKGROUND_COLOR);
                            paint.setStrokeWidth(1);
                        }
                        catch (Exception e)
                        {e.printStackTrace();}
                        for (int i = 0; i < mBoxPool.size(); i++)
                        {
                            try
                            {
                                if ((mBoxPool.get(i).getWidth() != 0) && (mBoxPool.get(i).getHeight() != 0)) {
                                    canvas.drawBitmap(parseBox(mBoxPool.get(i)), mBoxPool.get(i).getX(), mBoxPool.get(i).getY(), null);

                                    if (LocalBoxController != null)
                                    {
                                        LocalBoxController.animationUpdate();
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            //parseAndDrawBox(canvas,paint,mBoxPool.get(i));
                        }
                    }
                }
                finally
                {
                    if (canvas != null)
                    {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private Bitmap parseBox (Box _box)
        {
            int boxWidth = _box.getWidth();
            int boxHeight = _box.getHeight();

            Bitmap bm = Bitmap.createBitmap(boxWidth,boxHeight,Bitmap.Config.ARGB_8888);
            Canvas CanvasBoxPattern = new Canvas(bm);

            Paint _paint = new Paint();
            Path pathBox = new Path();
            pathBox.reset();

            _paint.setStyle(Paint.Style.FILL);
            _paint.setColor(_box.getColor());

            pathBox.addRect(0,0,boxWidth,boxHeight,Path.Direction.CW);
            CanvasBoxPattern.drawPath(pathBox,_paint);

            _paint.setStyle(Paint.Style.STROKE);
            _paint.setColor(Color.BLACK);

            int textSize;
            if (ScreenWidth < 500)
            {
                _paint.setStrokeWidth(3);
                textSize = 30;
            }
            else
            {
                textSize = 50;
                _paint.setStrokeWidth(5);
            }


            Path rectPath = new Path();
            rectPath.addRect(3,3,boxWidth-3,boxHeight-3,Path.Direction.CW);
            CanvasBoxPattern.drawPath(rectPath,_paint);

            _paint.setStrokeWidth(3);
            _paint.setStyle(Paint.Style.FILL);

            _paint.setTextSize(textSize);
            _paint.setTypeface(Typeface.MONOSPACE);
            if (_box.getNumber() != -1)
            {
                CanvasBoxPattern.drawText(String.valueOf(_box.getNumber()),_box.getWidth()/2 - textSize/4,_box.getHeight()/2 + textSize/4,_paint);
            }
            return bm;
        }

        private void parseAndDrawBox(Canvas _canvas, Paint _paint, Box _box)
        {

            int x1 = _box.getX();
            int y1 = _box.getY();
            int x2 = _box.getX() + _box.getWidth();
            int y2 = _box.getY() + _box.getHeight();

            int boxWidth = _box.getWidth();
            int boxHeight = _box.getHeight();

            Bitmap bm = Bitmap.createBitmap(boxWidth,boxHeight,Bitmap.Config.ARGB_8888);
            Canvas CanvasBoxPattern = new Canvas(bm);

            Path pathBox = new Path();
            pathBox.reset();

            _paint.setStyle(Paint.Style.FILL);
            _paint.setColor(_box.getColor());

            pathBox.addRect(0,0,boxWidth,boxHeight,Path.Direction.CW);
            CanvasBoxPattern.drawPath(pathBox,_paint);

            _paint.setStyle(Paint.Style.STROKE);
            _paint.setColor(Color.BLACK);
            _paint.setStrokeWidth(5);

            Path rectPath = new Path();
            rectPath.addRect(3,3,boxWidth-3,boxHeight-3,Path.Direction.CW);
            CanvasBoxPattern.drawPath(rectPath,_paint);

            _canvas.drawBitmap(bm,x1,y1,null);

            _paint.setStrokeWidth(3);
            _paint.setStyle(Paint.Style.FILL);

            int textSize;
            if (ScreenWidth < 500)
            {
                _paint.setStrokeWidth(3);
                textSize = 30;
            }
            else
            {
                textSize = 50;
                _paint.setStrokeWidth(5);
            }

            _paint.setTextSize(textSize);
            _paint.setTypeface(Typeface.MONOSPACE);
            if (_box.getNumber() != -1)
            {
                _canvas.drawText(String.valueOf(_box.getNumber()),x1+((x2-x1)/2) - textSize/4 ,y1+((y2-y1)/2)+ textSize/4,_paint);
            }

        }
    }

}

