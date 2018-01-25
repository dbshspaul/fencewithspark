package com.wrkspot.emp.fence.util;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;

import java.util.Comparator;

/**
 * Created by murali on 7/25/17.
 */
public class BeaconMetaDataDistanceComparator implements Comparator<BeaconMetaData>{
    @Override
    public int compare(BeaconMetaData o1, BeaconMetaData o2) {

        if (o1.getBeaconDistance() > o2.getBeaconDistance())
        {
            return 1;
        }
        else if ( o1.getBeaconDistance() == o2.getBeaconDistance())
        {
            return 0;
        }
        else
        {
            return -1;
        }

    }
}
