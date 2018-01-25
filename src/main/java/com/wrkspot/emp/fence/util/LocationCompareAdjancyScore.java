package com.wrkspot.emp.fence.util;

import com.wrkspot.emp.fence.model.iot.Location;

import java.util.Comparator;

/**
 * Created by murali on 8/26/17.
 */
public class LocationCompareAdjancyScore implements Comparator<Location> {
    @Override
    public int compare(Location o1, Location o2) {
        return (int) (o1.getAdjacentScore() - o2.getAdjacentScore());
    }
}
