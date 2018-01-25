package com.wrkspot.emp.fence.model.iot;

/**
 * The class will store the adjacent information
 * Created by murali on 8/24/17.
 */

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import org.mongodb.morphia.annotations.Entity;

@Entity
public class AdjacentLocation {

    private long position;
    private String direction;
    private double distance;
    private BeaconMetaData beaconMetaData;

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public BeaconMetaData getBeaconMetaData() {
        return beaconMetaData;
    }

    public void setBeaconMetaData(BeaconMetaData beaconMetaData) {
        this.beaconMetaData = beaconMetaData;
    }
}
