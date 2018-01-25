package com.wrkspot.emp.fence.model.iot;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * The BeaconMetaData is Wrapped by a Location which has information about Adjacent and other information
 * <p>
 * Created by murali on 8/23/17.
 */
@Entity
public class Location {

    /**
     * The LocationType
     */
    private String locationType;
    /**
     * Locations to the Right
     */
    private List<AdjacentLocation> adjacentRight = new ArrayList<>();
    /**
     * Locations to the Left
     */
    private List<AdjacentLocation> adjacentLeft = new ArrayList<>();
    /**
     * Beacon under consideration for the location
     */
    private BeaconMetaData beaconMetaData;
    /**
     * Indictes if this location is the preferred location on the axes
     */
    private boolean selectedLocation;
    /**
     * Axis the Location belongs to
     */

    private AxisSelection axisSelection;

    /**
     * Location description
     */
    private String locationDescription;

    /**
     * Adjacent Score calculated by looking at left and right
     */
    private long adjacentScore;

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public List<AdjacentLocation> getAdjacentRight() {
        return adjacentRight;
    }

    public void setAdjacentRight(List<AdjacentLocation> adjacentRight) {
        this.adjacentRight = adjacentRight;
    }

    public List<AdjacentLocation> getAdjacentLeft() {
        return adjacentLeft;
    }

    public void setAdjacentLeft(List<AdjacentLocation> adjacentLeft) {
        this.adjacentLeft = adjacentLeft;
    }

    public BeaconMetaData getBeaconMetaData() {
        return beaconMetaData;
    }

    public void setBeaconMetaData(BeaconMetaData beaconMetaData) {
        this.beaconMetaData = beaconMetaData;
    }

    public boolean isSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(boolean selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public AxisSelection getAxisSelection() {
        return axisSelection;
    }

    public void setAxisSelection(AxisSelection axisSelection) {
        this.axisSelection = axisSelection;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public long getAdjacentScore() {
        return adjacentScore;
    }

    public void setAdjacentScore(long adjacentScore) {
        this.adjacentScore = adjacentScore;
    }
}
