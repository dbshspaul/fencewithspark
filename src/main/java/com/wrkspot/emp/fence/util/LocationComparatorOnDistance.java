package com.wrkspot.emp.fence.util;

import com.wrkspot.emp.fence.model.iot.Location;

import java.util.Comparator;

/**
 * Created by murali on 8/26/17.
 */
public class LocationComparatorOnDistance implements Comparator<Location> {
    @Override
    public int compare(Location o1, Location o2) {

        if (o1.getBeaconMetaData().getBeaconDistance() > o2.getBeaconMetaData().getBeaconDistance())
        {
            return 1;
        }
        if (o1.getBeaconMetaData().getBeaconDistance() < o2.getBeaconMetaData().getBeaconDistance())
        {
            return -1;
        }
        if (o1.getBeaconMetaData().getBeaconDistance() == o2.getBeaconMetaData().getBeaconDistance())
        {
            return 0;
        }

        //return (int) (o1.getBeaconMetaData().getBeaconDistance() - o2.getBeaconMetaData().getBeaconDistance());
        return 0;
    }
}
