package com.wrkspot.emp.fence.model;

import org.mongodb.morphia.annotations.Entity;

/**
 * @author prakash
 */
@Entity
public class Beacon {


    Double distance;
    Integer rssi;
    String uuid;
    String _id;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
