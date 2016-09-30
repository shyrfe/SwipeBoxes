package com.example.vshurygin.testapp;

/**
 * Created by vshurygin on 28.09.2016.
 */

public class MovingBoxMapPoint
    {
        public int x;
        public int y;

        public MovingBoxMapPoint NextPoint;
        public MovingBoxMapPoint LastPoint;

        MovingBoxMapPoint (int _x,int _y, MovingBoxMapPoint _nextPoint, MovingBoxMapPoint _lastPoint)
        {
            x = _x;
            y = _y;

            NextPoint = _nextPoint;
            LastPoint = _lastPoint;
        }
        @Override
        public boolean equals (Object _obj)
        {
            MovingBoxMapPoint _objPoint = (MovingBoxMapPoint)_obj;
            if ((_objPoint.x == this.x)&&(_objPoint.y == this.y))
            {
                return true;
            }
            return false;
        }
    }

