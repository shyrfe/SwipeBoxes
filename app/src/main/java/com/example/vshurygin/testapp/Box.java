package com.example.vshurygin.testapp;

import android.graphics.Color;

import java.util.Objects;

/**
 * Created by vshurygin on 21.09.2016.
 */

public class Box
{
    private int mY;
    private int mX;
    private int mWidth;
    private int mHeight;
    private int mColor;
    private int mNumber;
    //public long MoveLastTime = 0;
    public int Force = 0;

    public Box ()
    {
        this(0,0,0,0,Color.BLACK,-1);
    }
    public Box (int _x, int _y)
    {
        this(_x,_y,0,0,Color.BLACK,-1);
    }
    public Box (int _x, int _y, int _width, int _height)
    {
        this(_x,_y,_width,_height, Color.BLACK,-1);
    }
    public Box (int _x, int _y, int _width, int _height, int _color)
    {
        this(_x,_y,_width,_height,_color,-1);
    }
    public Box (int _x, int _y, int _width, int _height, int _color, int _number)
    {
        mX = _x;
        mY = _y;
        mWidth = _width;
        mHeight = _height;
        mColor = _color;
        mNumber = _number;
    }

    public void setY(int _value)
    {
        mY = _value;
    }
    public void setX(int _value)
    {
        mX = _value;
    }
    public void setWidth(int _value)
    {
        mWidth = _value;
    }
    public void setHeight(int _value)
    {
        mHeight = _value;
    }
    public void setColor(int _color)
    {
        mColor = _color;
    }
    public void setNumber(int _value)
    {
        mNumber = _value;
    }

    public int getY()
    {
        return mY;
    }
    public int getX()
    {
        return mX;
    }
    public int getWidth()
    {
        return mWidth;
    }
    public int getHeight()
    {
        return mHeight;
    }
    public int getColor()
    {
        return mColor;
    }
    public int getNumber()
    {
        return mNumber;
    }

    @Override
    public boolean equals(Object _object)
    {
        if (getClass() != _object.getClass())
        {
            return false;
        }

        Box other = (Box)_object;
        if((mX != other.mX)||(mY != other.mY))
        {
            return false;
        }

        if ((mWidth != other.mWidth)||(mHeight != other.mHeight))
        {
            return false;
        }

        if (mColor != other.mColor)
        {
            return false;
        }

        if (mNumber != other.mNumber)
        {
            return false;
        }

        return true;
    }

}
