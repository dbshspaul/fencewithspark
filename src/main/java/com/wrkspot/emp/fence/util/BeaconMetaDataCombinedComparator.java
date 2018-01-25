package com.wrkspot.emp.fence.util;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;

import java.util.Comparator;

/**
 * Created by murali on 7/27/17.
 */
public class BeaconMetaDataCombinedComparator  implements Comparator<BeaconMetaData> {

    /**
     *  Sorting will be based on sighting
     * @param o1
     * @param o2
     * @return
     */
    public int compare(BeaconMetaData o1, BeaconMetaData o2) {

        double o1Rank = o1.getLineOfSightRank();
        double o1Distace = o1.getBeaconDistance();
        double o1tot = o1Rank + o1Distace;

        double o2Rank = o2.getLineOfSightRank();
        double o2Distace = o2.getBeaconDistance();

        double o2tot = o2Rank +o2Distace;

        if ((o1tot - o2tot) > 0)
        {
            return 1;
        }

        if (o1tot - o2tot == 0)
        {
            return 0;
        }

        if (o1tot - o2tot < 0 )
        {
            return -1;
        }

       return 0;

    }
}


