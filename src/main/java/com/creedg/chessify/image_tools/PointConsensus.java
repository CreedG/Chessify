package com.creedg.chessify.image_tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Creed on 2/1/2017.
 */

//Group points together for processing

public class PointConsensus {

    int groupSizeX = 0;
    int groupSizeY = 0;

    private ArrayList<PointGroup> groups;

    public PointConsensus(int sx, int sy) {
        groupSizeX = sx;
        groupSizeY = sy;

        groups = new ArrayList<PointGroup>();
    }

    public void add(int x, int y) {
        boolean placed = false;
        for (PointGroup group : groups) {
            //add it to that group
            if (Math.abs(x-group.avgX) < groupSizeX && Math.abs(y-group.avgY) < groupSizeY) {
                group.addPoint(x,y);
                placed = true;
                break;
            }
        }

        if (placed == false) {
            PointGroup newGroup = new PointGroup();
            newGroup.addPoint(x, y);
            groups.add(newGroup);
        }
    }

    //Sort the groups on size
    public void sortGroups() {
        Collections.sort(groups, new Comparator<PointGroup>() {
            public int compare(PointGroup o1, PointGroup o2) {
                return o1.numPoints-o2.numPoints;
            }
        });
    }

    public void thresholdGroups(int thresh) {
        for (int i = groups.size()-1; i >= 0; i--){
            if (groups.get(i).numPoints < thresh) {
                groups.remove(i);
            }
        }
    }

    public ArrayList<PointGroup> getGroups() {
        thresholdGroups(5);
        sortGroups();
        return groups;
    }

}
