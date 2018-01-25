package com.wrkspot.emp.fence.util;

import com.wrkspot.emp.fence.model.dto.UserGrid;

import java.util.Comparator;

/**
 * Created by prakash on 7/12/17.
 *
 * @copyrights WRKSPOT Corp
 */
public class LocateMeComparator implements Comparator {


    @Override
    public int compare(Object o1, Object o2) {

        UserGrid obj1 = (UserGrid) o1;
        UserGrid obj2 = (UserGrid) o2;

        return (int) (obj1.getFinalScore() - obj2.getFinalScore());
    }
}
