package com.wrkspot.emp.fence.model.realtimelocation;

import org.mongodb.morphia.annotations.Entity;

/**
 * Created by murali on 9/3/17.
 */
@Entity
public class RealTimeZoneRelationship {
    private String primaryBeacon;
    private String secondaryBeacon;
    private String relationship;
    private String primaryAxis;
    private String secondaryAxis;
    private long primaryFloor;
    private long secondaryFloor;

}
