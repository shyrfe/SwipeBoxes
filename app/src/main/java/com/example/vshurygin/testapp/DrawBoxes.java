package com.example.vshurygin.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
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

    public int ScreenWidth = 0;
    public int ScreenHeight = 0;

    private CopyOnWriteArrayList<Box> mBoxStack = new CopyOnWriteArrayList<>();

    DrawBoxes(Context _context)
    {
        super(_context);
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

        private final int BACKGROUND_COLOR = Color.WHITE;

        private SurfaceHolder mSurfaceHolder;
        private boolean isRun = false;
        private CopyOnWriteArrayList<Box> mOutsideBoxPool;
        public CopyOnWriteArrayList<Box> mBoxPool = new CopyOnWriteArrayList<>();
        //private Vector<Box> mBoxPool = new Vector<>();



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
                                /*if (mBoxPool.get(i) != null)
                                {
                                    if (!(mOutsideBoxPool.get(i).equals(mBoxPool.get(i))))
                                    {
                                        mBoxPool.set(i,mOutsideBoxPool.get(i));
                                    }
                                }
                                else
                                {
                                    mBoxPool.add(mOutsideBoxPool.get(i));
                                    Log.d("DrawBoxes","Box added");
                                }*/
                                if (!(mOutsideBoxPool.get(i).equals(mBoxPool.get(i))))
                                {
                                    mBoxPool.set(i,mOutsideBoxPool.get(i));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            Log.d("DrawBoxes","StackIsEmpty");
                            e.printStackTrace();
                        }
                        finally
                        {
                            poolChange = false;
                        }

                    }
                    synchronized (mSurfaceHolder)
                    {
                        canvas.drawColor(BACKGROUND_COLOR);

                        paint.setColor(Color.BLACK);
                        paint.setStrokeWidth(1);

                        for (int i = 0; i < mBoxPool.size(); i++)
                        {
                            int x1 = mBoxPool.get(i).getX();
                            int y1 = mBoxPool.get(i).getY() ;
                            int x2 = mBoxPool.get(i).getX()+ mBoxPool.get(i).getWidth();
                            int y2 = mBoxPool.get(i).getY()+mBoxPool.get(i).getHeight();
                            canvas.drawRect(x1,y1,x2,y2,paint);
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
    }

}

