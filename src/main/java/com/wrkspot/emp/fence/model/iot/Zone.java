package com.wrkspot.emp.fence.model.iot;

import org.mongodb.morphia.annotations.Entity;

import java.util.List;

/**
 * Zone identies the are area outside the rooms
 * Zone could be a combination of rooms
 * Created by murali on 8/25/17.
 */
@Entity
public class Zone {
    private String descirption;
    /**
     * List of locations
     */
    private List<Location> locationList;
    /**
     * Axes on which the Zone is described
     */
    private AxisSelection axisSelection;

    private String zoneDescription;

    public String getDescirption() {
        return descirption;
    }

    public void setDescirption(String descirption) {
        this.descirption = descirption;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public AxisSelection getAxisSelection() {
        return axisSelection;
    }

    public void setAxisSelection(AxisSelection axisSelection) {
        this.axisSelection = axisSelection;
    }

    public String getZoneDescription() {
        return zoneDescription;
    }

    public void setZoneDescription(String zoneDescription) {
        this.zoneDescription = zoneDescription;
    }
}
