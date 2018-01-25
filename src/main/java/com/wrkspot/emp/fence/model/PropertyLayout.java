package com.wrkspot.emp.fence.model;


import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.LinkedList;
import java.util.Map;

@Entity
public class PropertyLayout {


    @Id
    String _id;
    private long floorNumber;
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
    private String siteID;
    private String zone;


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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(long floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getPropertyWing() {
        return propertyWing;
    }

    public void setPropertyWing(String propertyWing) {
        this.propertyWing = propertyWing;
    }

    public String getPropertyIdentifier() {
        return propertyIdentifier;
    }

    public void setPropertyIdentifier(String propertyIdentifier) {
        this.propertyIdentifier = propertyIdentifier;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getAxis() {
        return axis;
    }

    public void setAxis(String axis) {
        this.axis = axis;
    }

    public Map<String, String> getIntersections() {
        return intersections;
    }

    public void setIntersections(Map<String, String> intersections) {
        this.intersections = intersections;
    }

    public long getAxisPos() {
        return axisPos;
    }

    public void setAxisPos(long axisPos) {
        this.axisPos = axisPos;
    }

    public String getPropertyTop() {
        return propertyTop;
    }

    public void setPropertyTop(String propertyTop) {
        this.propertyTop = propertyTop;
    }

    public String getPropertyFront() {
        return propertyFront;
    }

    public void setPropertyFront(String propertyFront) {
        this.propertyFront = propertyFront;
    }

    public String getPropertydown() {
        return propertydown;
    }

    public void setPropertydown(String propertydown) {
        this.propertydown = propertydown;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public LinkedList<String> getAdjacentRightRooms() {
        return adjacentRightRooms;
    }

    public void setAdjacentRightRooms(LinkedList<String> adjacentRightRooms) {
        this.adjacentRightRooms = adjacentRightRooms;
    }

    public LinkedList<String> getAdjacentLeftRooms() {
        return adjacentLeftRooms;
    }

    public void setAdjacentLeftRooms(LinkedList<String> adjacentLeftRooms) {
        this.adjacentLeftRooms = adjacentLeftRooms;
    }

    public LinkedList<String> getFrontRightRooms() {
        return frontRightRooms;
    }

    public void setFrontRightRooms(LinkedList<String> frontRightRooms) {
        this.frontRightRooms = frontRightRooms;
    }

    public LinkedList<String> getFrontLeftRooms() {
        return frontLeftRooms;
    }

    public void setFrontLeftRooms(LinkedList<String> frontLeftRooms) {
        this.frontLeftRooms = frontLeftRooms;
    }

    public LinkedList<String> getBackRightRooms() {
        return backRightRooms;
    }

    public void setBackRightRooms(LinkedList<String> backRightRooms) {
        this.backRightRooms = backRightRooms;
    }

    public LinkedList<String> getBackLeftRooms() {
        return backLeftRooms;
    }

    public void setBackLeftRooms(LinkedList<String> backLeftRooms) {
        this.backLeftRooms = backLeftRooms;
    }

    public LinkedList<String> getTopRightRooms() {
        return topRightRooms;
    }

    public void setTopRightRooms(LinkedList<String> topRightRooms) {
        this.topRightRooms = topRightRooms;
    }

    public LinkedList<String> getTopLeftRooms() {
        return topLeftRooms;
    }

    public void setTopLeftRooms(LinkedList<String> topLeftRooms) {
        this.topLeftRooms = topLeftRooms;
    }

    public LinkedList<String> getFrontRooms() {
        return frontRooms;
    }

    public void setFrontRooms(LinkedList<String> frontRooms) {
        this.frontRooms = frontRooms;
    }

    public LinkedList<String> getBackRooms() {
        return backRooms;
    }

    public void setBackRooms(LinkedList<String> backRooms) {
        this.backRooms = backRooms;
    }

    public LinkedList<String> getTopRooms() {
        return topRooms;
    }

    public void setTopRooms(LinkedList<String> topRooms) {
        this.topRooms = topRooms;
    }
}
