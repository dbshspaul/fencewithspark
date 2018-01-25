package com.wrkspot.emp.fence.locateMeAlgorithms;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.iot.AdjacentLocation;
import com.wrkspot.emp.fence.model.iot.AxisSelection;
import com.wrkspot.emp.fence.model.iot.Location;
import com.wrkspot.emp.fence.model.iot.Zone;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeSiteConfig;
import com.wrkspot.emp.fence.util.BeaconMetaDataCombinedComparator;
import com.wrkspot.emp.fence.util.FenceGlossaries;
import com.wrkspot.emp.fence.util.LocationComparatorOnDistance;
import com.wrkspot.emp.fence.util.LocationCompareAdjancyScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by murali on 8/19/17.
 * Algorithm will take care of Axes and relationship with axes
 * The method will return a location
 * It will write to Neo4J database
 * Algorithm1, Algorithm2, Algorithm3 are handled here
 */
public class AxisSelectionService  implements Serializable {

    private AxisSelectionService() {
    }

    private static class SingletonHelper {
        private static final AxisSelectionService INSTANCE = new AxisSelectionService();
    }

    public static AxisSelectionService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public static final String INLINEOFSIGHT = "InLineOfSight";
    public static final String NOTINLINEOFSIGHT = "NotInLineOfSight";
    public static final String INPROXIMITY = "InProximity";
    public static final String NOTINPROXIMITY = "NotInProximity";
    public static final int LISTSIZE = 5;
    public static final int LINEOFSIGHT = 2;
    public static final double PROXIMITYDISTANCE = 3.5;
    public static final int DISTANCEDIFF = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(AxisSelectionService.class);


    public List<AxisSelection> performAxesSelection(Map<String, List<BeaconMetaData>> rankingContext) throws Exception {
        /**
         * Check if Sights are greater than a given number
         * And get those beacons
         * Loop thro the context
         * Axis Selection is for each axes
         */

        LOGGER.info(" Received Beacon Meta Data");
        List<AxisSelection> axisSelectionList = new ArrayList();
        LOGGER.info("Looping through Beacons based on Axes ");
        for (Map.Entry<String, List<BeaconMetaData>> entry : rankingContext.entrySet()) {

            LOGGER.info(" Processing Axes " + entry.getKey());

            AxisSelection axisSelection = new AxisSelection();
            axisSelection.setAxes(entry.getKey());
            axisSelection.setBeaconMetaDataList(entry.getValue());
            this.determineAxesQuality(axisSelection);
            this.determineLocatonZone(axisSelection);
            this.updateAxesLocationScore(axisSelection);
            //this.resolveAxesDecision(axisSelection);

            axisSelectionList.add(axisSelection);

        }


        return axisSelectionList;
    }

    public void updateAxesLocationScore(AxisSelection axisSelection) throws Exception {
        Location location = axisSelection.getZoneLocations().stream().max((x, y) -> Long.compare(x.getAdjacentScore(), y.getAdjacentScore())).get();

        axisSelection.setAdjacentScore(location.getAdjacentScore());


    }

    public void resolveAxesDecision(AxisSelection axisSelection) throws Exception {
        if (checkIfConflict(axisSelection)) {
            // Resolve Conflict
            // Check Adjacent scores
            // Left should be MIN
            // Right should be Max
            // get Location with Max Score

            Location location = axisSelection.getZoneLocations().stream().max((x, y) -> Long.compare(x.getAdjacentScore(), y.getAdjacentScore())).get();
            // Now Filter and see if there are more of the same value
            // If more found resolve conflict

            long adjScore = location.getAdjacentScore();

            List<Location> conflicts = axisSelection.getZoneLocations().stream().filter(s -> s.getAdjacentScore() == adjScore).collect(Collectors.toList());


            // Only Zone can be determined

            this.resolveAxesLocationConflictdistance(axisSelection, conflicts);

        }
    }

    public void resolveAxesLocationConflictdistance(AxisSelection axisSelection, List<Location> conflictLocations) throws Exception {
        // Sort this based on Position
        // Handle Two Beacon Theory here
        // Sorting should be on the distance

        conflictLocations.sort(new LocationComparatorOnDistance());

        // Check of Adjancency

        long lprevLoc = -1;
        Location finalLocation = conflictLocations.get(0);
        for (Location loc2 : conflictLocations) {
            if (!(lprevLoc == -1)) {
                // Check Adjacent
                long currpos = loc2.getBeaconMetaData().getAxisPos();
                if (currpos == lprevLoc) {
                    // This is the two beacon per room situtation
                    return;
                }
                if (currpos < lprevLoc) {
                    finalLocation = loc2;
                }
            }

            lprevLoc = loc2.getBeaconMetaData().getAxisPos();

        }

        axisSelection.setFinalLocation(finalLocation);


    }


    public void resolveAxes(AxisSelection axisSelection) throws Exception {
        // If Final Location not resolved . It is a zone

        if (FenceGlossaries.isObjectEmpty(axisSelection.getFinalLocation())) {
            // Zone it
            Zone zone = new Zone();
            zone.setLocationList(axisSelection.getZoneLocations());
            String zoneLocation = " The Location is between ";
            for (Location loc : axisSelection.getZoneLocations()) {
                zoneLocation = zoneLocation + loc.getBeaconMetaData().getProperyIdentifer();
                zoneLocation = zoneLocation + "   ";
            }

            axisSelection.setZone(zone);

        }


    }

    public void resolveAxisConflictAdjancy(List<Location> conflictLocations, AxisSelection axisSelection) throws Exception {
        // Sort by Adjancy Score
        conflictLocations.sort(new LocationCompareAdjancyScore());

        Location finalLocation = conflictLocations.stream().max((x, y) -> Long.compare(x.getAdjacentScore(), y.getAdjacentScore())).get();

        // See if more than one locaton available or same adjancy score

        List<Location> moreLocations = conflictLocations.stream().filter((x -> x.getAdjacentScore() == finalLocation.getAdjacentScore())).collect(Collectors.toList());

        if (!FenceGlossaries.isObjectEmpty(moreLocations)) {
            // Needs distance resolution
            axisSelection.setFinalLocation(null);

        } else {
            axisSelection.setFinalLocation(finalLocation);
        }

    }

    /**
     * Checks if the resolution has more than one locatins
     *
     * @param axisSelection
     * @return
     * @throws Exception
     */

    public boolean checkIfConflict(AxisSelection axisSelection) throws Exception {
        if (axisSelection.getZoneLocations().size() > 1) {
            return true;
        }
        return false;
    }

    /**
     * Determines the Zone
     * The closest beacon is named as the location
     * Any beacon with distance with limited variance is also place in the AxesLocation locations
     *
     * @param axisSelection
     * @throws Exception
     */

    private void determineLocatonZone(AxisSelection axisSelection) throws Exception {
        // Sort by the comibned factor 
        // The Key part in the Algorithm is sorting
        LOGGER.info(" Sorting the collection ...");
        LOGGER.info(" Using BeaconMetaDataCombinedComparator");
        axisSelection.getBeaconMetaDataList().sort(new BeaconMetaDataCombinedComparator());
        List<BeaconMetaData> selectedList = axisSelection.getBeaconMetaDataList().subList(0, axisSelection.getBeaconMetaDataList().size());
        // Distance to the closest beacon
        double axisDistance = selectedList.get(0).getBeaconDistance();
        LOGGER.info(" Setting Axes Distance as = " + axisDistance);
        axisSelection.setAxisDistance(axisDistance);

        for (BeaconMetaData beaconMetaData : selectedList) {

            if (FenceGlossaries.isObjectEmpty(axisSelection.getZoneLocations())) {
                // Algorithm 1 is being applied . the closest beacon is considered as the location 

                LOGGER.info(" Algorithm 1 Pick the Closest beacon for the Axes ");

                this.createIOTLocation(axisSelection, beaconMetaData);
            } else {
                LOGGER.info(" Start Algorithm 2 ");
                this.resolveDistance(axisSelection, beaconMetaData);
                this.determineLocoationAdjacentScore(axisSelection);
            }

        }

    }

    /**
     * The Method adds the BeacanMetaData as the locaton to the AxisSelection
     *
     * @param axisSelection
     * @param beaconMetaData
     * @throws Exception
     */
    public void createIOTLocation(AxisSelection axisSelection, BeaconMetaData beaconMetaData) throws Exception {
        Location location = new Location();
        location.setBeaconMetaData(beaconMetaData);
        location.setSelectedLocation(true);
        location.setLocationDescription("The Location is .." + beaconMetaData.getProperyIdentifer());
        List<Location> locations = new ArrayList();
        locations.add(location);
        location.setSelectedLocation(true);
        axisSelection.setZoneLocations(locations);

    }

    /**
     * This method comares the distance between the selected location and the current becaon .
     * If the distance is less than 0.2 m then the current beacon is also considered as a possible location
     *
     * @param axisSelection
     * @param beaconMetaData
     * @throws Exception
     */
    public void resolveDistance(AxisSelection axisSelection, BeaconMetaData beaconMetaData) throws Exception {

        LOGGER.info(" Resolve Distances .....Begin Algorithm 2 ");

        boolean chkPossibleLocation = this.checkIfPossibleLocation(axisSelection, beaconMetaData);
        if (chkPossibleLocation) {
            this.createNewLocation(axisSelection, beaconMetaData);
        } else {
            // Get the Adjacent scores for the beacon Metadata
            for (Location loc2 : axisSelection.getZoneLocations()) {
                this.calucateAdjacentScore(loc2, beaconMetaData);
            }
        }

    }

    /**
     * Creates the Location Wrapper which will set on the Axes . Each Axes will have a location
     *
     * @param axisSelection
     * @param beaconMetaData
     * @throws Exception
     */

    public void createNewLocation(AxisSelection axisSelection, BeaconMetaData beaconMetaData) throws Exception {

        LOGGER.debug(" Creating a new Location");

        Location possibleLoc = new Location();
        possibleLoc.setBeaconMetaData(beaconMetaData);
        possibleLoc.setLocationDescription(" The Possile Location is .." + beaconMetaData.getProperyIdentifer());

        LOGGER.debug(" Updating the possible values for the new Location..");

        // Seems Fishy

        for (Location locAxis : axisSelection.getZoneLocations()) {
            this.calucateAdjacentScore(possibleLoc, locAxis.getBeaconMetaData());
        }

        if (FenceGlossaries.isObjectEmpty(axisSelection.getZoneLocations())) {
            List<Location> locations = new ArrayList();
            locations.add(possibleLoc);
            axisSelection.setZoneLocations(locations);
        } else {
            axisSelection.getZoneLocations().add(possibleLoc);
        }

        //Adjacent Location information need only from ZoneLocations


    }

    /**
     * The method checks the distance of the person from the beacon. If the difference is less than 0.2 meters the beacon is added as a possible location.
     *
     * @param axisSelection
     * @param beaconMetaData
     * @throws Exception
     */

    public boolean checkIfPossibleLocation(AxisSelection axisSelection, BeaconMetaData beaconMetaData) throws Exception {
        // Get the Selected Location
        Location loc = this.getSelectedLocation(axisSelection);

        LOGGER.info(" Current Algorithm 1 Favorite is " + loc.getBeaconMetaData().getBeaconIdentifier());

        double selLocation = loc.getBeaconMetaData().getBeaconDistance();
        double beaconDistance = beaconMetaData.getBeaconDistance();

        double distanceDiff = selLocation - beaconDistance;
        distanceDiff = Math.abs(distanceDiff);

        if (distanceDiff <= 0.2) {
            LOGGER.info(" This is a possible Location");
            return true;

        }

        LOGGER.info(" This is not a possible Location");

        return false;

    }

    public boolean checkIfPossibleLocationWithPrecision(AxisSelection axisSelection, BeaconMetaData beaconMetaData, RealTimeSiteConfig siteConfig) throws Exception {
        /**
         *  Checks if the Primary Location is with 3.5 meters
         */

        // Get the Selected Location
        // Location loc = this.getSelectedLocation(axisSelection);

        double proximitDistance = siteConfig.getProximityDistance();

        //  Bug Fix double primaryDistance = loc.getBeaconMetaData().getBeaconDistance();
        double primaryDistance = axisSelection.getAxisDistance();
        double beaconDistance = beaconMetaData.getBeaconDistance();

        if (primaryDistance <= proximitDistance) {
            if (beaconDistance <= primaryDistance) {
                return true;
            }
        }


        return false;

    }

    /**
     * Returns the Location selected based on the Algorithm1
     *
     * @param axisSelection
     * @return
     * @throws Exception
     */
    private Location getSelectedLocation(AxisSelection axisSelection) throws Exception {

        return (Location) axisSelection.getZoneLocations().stream().filter(Location::isSelectedLocation).findFirst().orElse(null);
    }

    /**
     * This method calculates the Adjacent Score of the beacon.
     * if the position difference is postion positive it is converted into a negative ( meaning the beacon is in the right of the beacon in consideration )
     * The beacon selections are made from the SelectedBeacon and Possible Locations
     *
     * @param location
     * @param currentBeaconMetaData
     * @throws Exception
     */

    public void calucateAdjacentScore(Location location, BeaconMetaData currentBeaconMetaData) throws Exception {
        // First perform calculation for Selected Beacon
        // Loop through

        LOGGER.info(" Calculating Adjancent Score ");

        this.adjancentCalculation(location, currentBeaconMetaData);

        // Loop through Zones and get the location score


    }

    /**
     * Calucates the adjacent score for the axes locations
     *
     * @param axisSelection
     * @throws Exception
     */
    public void determineLocoationAdjacentScore(AxisSelection axisSelection) throws Exception {
        // Loop throught the axisSelection
        // Set the LocationAdjacentScore
        // Take the left and multiple by -1 and add to the right which will be negative
        // Will be used in resolution conflict
        // Right - Minimum
        // Left - Maximum
        AdjacentLocation adjRight = null;
        AdjacentLocation adjLeft = null;

        if (!FenceGlossaries.isObjectEmpty(axisSelection.getZoneLocations())) {

            for (Location loc : axisSelection.getZoneLocations()) {
                // Do right
                if (!FenceGlossaries.isObjectEmpty(loc.getAdjacentRight())) {
                    adjRight = loc.getAdjacentRight().stream().min((x, y) -> Long.compare(x.getPosition(), y.getPosition())).get();
                }

                if (!FenceGlossaries.isObjectEmpty(loc.getAdjacentLeft())) {

                    adjLeft = loc.getAdjacentLeft().stream().max((x, y) -> Long.compare(x.getPosition(), y.getPosition())).get();
                }

                long rightScore = 0;
                long leftScore = 0;
                long adjScore = 0;

                if (!FenceGlossaries.isObjectEmpty(adjRight)) {
                    rightScore = adjRight.getPosition();
                    rightScore = -1 * rightScore;
                }
                if (!FenceGlossaries.isObjectEmpty(adjLeft)) {
                    leftScore = adjLeft.getPosition();
                }

                adjScore = rightScore + leftScore;

                loc.setAdjacentScore(adjScore);


            }
        }
    }

    /**
     * I Wish we were doing functional programming
     *
     * @param selected
     * @param current
     * @return
     * @throws Exception
     */
    private void adjancentCalculation(Location selected, BeaconMetaData current) throws Exception {
        long selectedPos = selected.getBeaconMetaData().getAxisPos();
        long currentPos = current.getAxisPos();

        LOGGER.info(" Selected Position is  : " + selectedPos);
        LOGGER.info(" Current position is  : " + currentPos);

        long sPos = selectedPos - currentPos;

        LOGGER.info(" The position difference is : " + sPos);

        AdjacentLocation adjLocation = new AdjacentLocation();
        adjLocation.setDistance(current.getBeaconDistance());
        adjLocation.setPosition(sPos);
        adjLocation.setBeaconMetaData(current);

        if (sPos >= 0) {

            LOGGER.info(" Beacon Located to the Right");

            adjLocation.setDirection("RIGHT");

            selected.getAdjacentRight().add(adjLocation);


        } else {
            LOGGER.info(" Beacon Located to the Left");
            adjLocation.setDirection("LEFT");
            selected.getAdjacentLeft().add(adjLocation);
        }
    }

    public void determineAxesQuality(AxisSelection axisSelection) throws Exception {

        LOGGER.info(" Determine the Quality of reading wrt to the Axes");
        List<BeaconMetaData> proximitBeacons = axisSelection.getBeaconMetaDataList().stream().filter(s -> s.getLineOfSightRank() >= AxisSelectionService.LINEOFSIGHT)
                .collect(Collectors.toList());
        if (FenceGlossaries.isObjectEmpty(proximitBeacons)) {
            // No proximity beacons


            LOGGER.info(" The beacons in this axes were not in sight");
            LOGGER.info(" Setting the status as NOTINLINEOFSIGHT");
            axisSelection.setLineOfSightStatus(AxisSelectionService.NOTINLINEOFSIGHT);
            axisSelection.setLineofSightScore(1);

        } else {
            LOGGER.info(" The Beacons in this axis where  seen a lot");
            LOGGER.info(" Setting Axis Status to INLINEOFSIGHT");
            axisSelection.setLineOfSightStatus(AxisSelectionService.INLINEOFSIGHT);
            axisSelection.setLineofSightScore(2);
            // sort the beacons based on distance
            // Work with ZOne

        }

        proximitBeacons = null;

        LOGGER.info(" Going to check for Proximity");
        proximitBeacons = axisSelection.getBeaconMetaDataList().stream().filter(s -> s.getBeaconDistance() <= AxisSelectionService.PROXIMITYDISTANCE).collect(Collectors.toList());

        if (FenceGlossaries.isObjectEmpty(proximitBeacons)) {
            LOGGER.info(" Not in proximity not 3m close to any of the beacons");
            axisSelection.setProximityStatus(AxisSelectionService.NOTINPROXIMITY);
            axisSelection.setProximityScore(1);
        } else {
            LOGGER.info(" Some beacons are closer ");
            axisSelection.setProximityStatus(AxisSelectionService.INPROXIMITY);
            axisSelection.setProximityScore(2);
        }


    }


}
