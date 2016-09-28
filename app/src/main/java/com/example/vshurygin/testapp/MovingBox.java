package com.example.vshurygin.testapp;

/**
 * Created by vshurygin on 28.09.2016.
 */

public class MovingBox
{
    public MovingBox NextBox;
    public MovingBox LastBox;

    public int LastXReferenceCoord;
    public int LastYReferenceCoord;

    public int NextXReferenceCoord;
    public int NextYReferenceCoord;

    public MovingBoxMapPoint ThisPoint;
    public MovingBoxMapPoint NextPoint;

    public Box LocalBox;

    MovingBox(Box _box)
    {
        LocalBox = _box;
    }
    MovingBox(Box _box, int _lastXRefCoord, int _lastYRefCoord)
    {
        LocalBox = _box;
        LastXReferenceCoord = _lastXRefCoord;
        LastYReferenceCoord = _lastYRefCoord;
    }

    MovingBox(Box _box, int _lastXRefCoord, int _lastYRefCoord, MovingBox _lastBox, MovingBox _nextBox)
    {
        LocalBox = _box;
        LastXReferenceCoord = _lastXRefCoord;
        LastYReferenceCoord = _lastYRefCoord;
        LastBox = _lastBox;
        NextBox = _nextBox;
    }

    public void setX(int _coord)
    {
        LocalBox.setX(_coord);
    }
    public void setY(int _coord)
    {
        LocalBox.setY(_coord);
    }
    public int getX()
    {
        return LocalBox.getX();
    }
    public int getY()
    {
        return LocalBox.getY();
    }

}
