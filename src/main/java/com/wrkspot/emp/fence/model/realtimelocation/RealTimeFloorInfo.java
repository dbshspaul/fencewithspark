package com.wrkspot.emp.fence.model.realtimelocation;

import org.mongodb.morphia.annotations.Entity;


/**
 * Created by murali on 9/1/17.
 */
//@NodeEntity
@Entity
public class RealTimeFloorInfo {
    // @GraphId
    private Long id;
    private String floorNumber;
    private String confidenceLevel;
    private String zone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(String confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
