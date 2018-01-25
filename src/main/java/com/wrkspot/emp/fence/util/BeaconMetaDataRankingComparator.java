package com.wrkspot.emp.fence.util;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;

import java.util.Comparator;

/**
 * Created by murali on 7/25/17.
 */
public class BeaconMetaDataRankingComparator implements Comparator<BeaconMetaData> {
    @Override
    public int compare(BeaconMetaData o1, BeaconMetaData o2) {
        return  (int) (o1.getDistanceRank() - o2.getBeaconRank());
    }
}
