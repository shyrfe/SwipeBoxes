package com.example.vshurygin.testapp;

/**
 * Created by vshurygin on 21.09.2016.
 */

public class Box
{
    private int mY;
    private int mX;
    private int mWidth;
    private int mHeight;
    private String mColor;
    private int mNumber;

    public Box ()
    {
         new Box (0,0,0,0,"",0);
    }
    public Box (int _x, int _y)
    {
        new Box (_x,_y,0,0,"",0);
    }
    public Box (int _x, int _y, int _width, int _height)
    {
        new Box (_x,_y,_width,_height,"",0);
    }
    public Box (int _x, int _y, int _width, int _height, String _color)
    {
        new Box (_x,_y,_width,_height,_color,0);
    }
    public Box (int _x, int _y, int _width, int _height, String _color, int _number)
    {
        new Box (_x,_y,_width,_height,_color,_number);
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
    public void setColor(String _color)
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
    public String getColor()
    {
        return mColor;
    }
    public int getNumber()
    {
        return mNumber;
    }

}
