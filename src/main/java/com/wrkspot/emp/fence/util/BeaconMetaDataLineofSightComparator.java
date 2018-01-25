package com.wrkspot.emp.fence.util;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;

import java.util.Comparator;

/**
 * Created by murali on 8/23/17.
 */
public class BeaconMetaDataLineofSightComparator implements Comparator<BeaconMetaData> {

    @Override
    public int compare(BeaconMetaData o1, BeaconMetaData o2) {
       // return (int)  (o1.getLineOfSightRank() - o2.getLineOfSightRank());
        if (o1.getSightingCount() > o2.getSightingCount())
        {
            return -1;
        }
        if (o1.getSightingCount() == o2.getSightingCount())
        {
            return 0;
        }
        if (o1.getSightingCount() < o2.getSightingCount())
        {
            return 1;
        }

        return  0;
    }
}
