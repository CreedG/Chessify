package com.creedg.chessify.image_tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Creed on 2/1/2017.
 */

//Group points together for processing

public class PointGroup  {
    int avgX;
    int avgY;
    int numPoints;

    ArrayList<Integer> pointsX;
    ArrayList<Integer> pointsY;

    public PointGroup() {
        avgX = 0;
        avgY = 0;
        numPoints = 0;
        pointsX = new ArrayList<Integer>();
        pointsY = new ArrayList<Integer>();
    }

    public void addPoint(int x, int y) {
        avgX = (avgX*numPoints+x) / (numPoints+1);
        avgY = (avgY*numPoints+y) / (numPoints+1);
        numPoints += 1;
        pointsX.add(x);
        pointsY.add(y);
    }

    //get the line described by the points
    public Line getLine() {
        LinearRegression lr = new LinearRegression(buildDoubleArray(pointsX), buildDoubleArray(pointsY));
        Line l = new Line(lr.slope(), lr.intercept());
        return l;
    }

    private double[] buildDoubleArray(List<Integer> integers) {
        double[] arr = new double[integers.size()];
        int i = 0;
        for (Integer n : integers) {
            arr[i++] = n;
        }
        return arr;
    }

}
