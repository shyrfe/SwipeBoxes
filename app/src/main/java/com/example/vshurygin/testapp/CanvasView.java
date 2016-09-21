package com.example.vshurygin.testapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vshurygin on 21.09.2016.
 */

public class CanvasView extends View
{
    public int width;
    public int height;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Context mContext;
    private Paint mPaint;
    private float mX,mY;

    CanvasView(Context _context, AttributeSet _attributeSet)
    {
        super(_context,_attributeSet);
        mContext = _context;

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4F);
    }

    @Override
    protected void onDraw(Canvas _canvas)
    {
        super.onDraw(_canvas);
        _canvas.drawPath(mPath,mPaint);
    }

    @Override
    protected void onSizeChanged(int _w, int _h, int _oldW, int _oldH)
    {
        super.onSizeChanged(_w, _h, _oldW, _oldH);

        mBitmap = Bitmap.createBitmap(_w,_h,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent _event)
    {
        float x = _event.getX();
        float y = _event.getY();

        switch (_event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x,y);
                mX = x;
                mY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x-mX);
                float dy = Math.abs(y-mY);
                if (dx >= 5 || dy >= 5)
                {
                    mPath.quadTo(mX,mY,(x+mX)/2,(y+mY)/2);
                    mX = x;
                    mY = y;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX,mY);
                break;
        }
        return true;
    }
}
