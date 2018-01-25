package com.wrkspot.emp.fence.model.dto;

import com.wrkspot.emp.fence.model.Beacon;
import org.mongodb.morphia.annotations.Entity;

import java.util.List;

@Entity
public class LocateMe {

    private List<Beacon> beacons;
    private List<BeaconMetaData> beaconMetaDataList;

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }
}
