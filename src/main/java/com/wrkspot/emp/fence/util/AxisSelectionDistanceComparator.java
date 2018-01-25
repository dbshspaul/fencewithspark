package com.wrkspot.emp.fence.util;

import com.wrkspot.emp.fence.model.iot.AxisSelection;

import java.util.Comparator;

/**
 * Created by murali on 8/27/17.
 */
public class AxisSelectionDistanceComparator implements Comparator<AxisSelection> {

    public int compare(AxisSelection o1, AxisSelection o2) {
        //return (int) (o1.getAxisDistance() - o2.getAxisDistance());

        if (o1.getAxisDistance() > o2.getAxisDistance())
        {
            return 1;
        }
        if (o1.getAxisDistance() == o2.getAxisDistance())
        {
            return 0;
        }
        if (o1.getAxisDistance() < o2.getAxisDistance())
        {
            return -1;
        }

        return 0;
    }
}
