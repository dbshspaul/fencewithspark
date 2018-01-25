package com.wrkspot.emp.fence.model;

import org.mongodb.morphia.annotations.Entity;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by prakash on 7/12/17.
 * <p>
 * copyrights WRKSPOT Corp
 */
@Entity
public class PropertyRelationship {


    private String floorNumber;
    private String propertyWing;
    private String propertyIdentifier;
    private String propertyType;
    private String axis;
    private Map<String, String> intersections;
    private long axisPos;
    // These three properties  will be filled by the system as it tracks the
    private String propertyTop;
    private String propertyFront;
    private String propertydown;
    private String siteId;
    private String orgId;
    private String createdBy;
    private String updatedBy;
    private String loadName;


    // Collections from here
    private LinkedList<String> adjacentRightRooms;
    private LinkedList<String> adjacentLeftRooms;
    private LinkedList<String> frontRightRooms;
    private LinkedList<String> frontLeftRooms;
    private LinkedList<String> backRightRooms;
    private LinkedList<String> backLeftRooms;
    private LinkedList<String> topRightRooms;
    private LinkedList<String> topLeftRooms;
    private LinkedList<String> frontRooms;
    private LinkedList<String> backRooms;
    private LinkedList<String> topRooms;


}
