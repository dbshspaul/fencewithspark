package com.wrkspot.emp.fence.util;

import com.wrkspot.emp.fence.model.dto.LocateMeAxis;

import java.util.Comparator;

/**
 * Created by prakash on 7/12/17.
 *
 * @copyrights WRKSPOT Corp
 */
public class FinalScoreComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {

        LocateMeAxis obj1 = (LocateMeAxis) o1;
        LocateMeAxis obj2 = (LocateMeAxis) o2;

        return (int) (obj1.getFinalScore() - obj2.getFinalScore());

    }
}
