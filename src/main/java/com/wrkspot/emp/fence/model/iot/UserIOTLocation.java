package com.wrkspot.emp.fence.model.iot;

import org.mongodb.morphia.annotations.Entity;

import java.util.List;

/**
 * Created by murali on 8/27/17.
 */
@Entity
public class UserIOTLocation {

    private Location probableLocation;
    private String confidence;
    private List<AxisSelection> axes;
    private List<AxisSelection> axisSelections;
    private String id;
    private String timeStamp;
    private String user;
    private String floorNumber;
    private String roomNumber;
    private String zone;
}
