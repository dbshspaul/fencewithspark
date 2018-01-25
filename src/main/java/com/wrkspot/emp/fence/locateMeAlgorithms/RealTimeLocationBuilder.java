package com.wrkspot.emp.fence.locateMeAlgorithms;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.iot.AxisSelection;
import com.wrkspot.emp.fence.model.iot.RoomConfidence;
import com.wrkspot.emp.fence.model.realtimelocation.*;
import com.wrkspot.emp.fence.mongoutil.MongoUtil;
import com.wrkspot.emp.fence.service.impl.UserGridImpl;
import com.wrkspot.emp.fence.util.BeaconMetaDataDistanceComparator;
import com.wrkspot.emp.fence.util.FenceGlossaries;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class RealTimeLocationBuilder implements Serializable{
    private static final Logger log = LoggerFactory.getLogger(RealTimeLocationBuilder.class);

    private RealTimeLocationBuilder() {
    }

    private static class SingletonHelper {
        private static final RealTimeLocationBuilder INSTANCE = new RealTimeLocationBuilder();
    }

    public static RealTimeLocationBuilder getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private UserGridImpl userGrid = UserGridImpl.getInstance();
    private MongoUtil mongoUtil = MongoUtil.getInstance();
    private AxisSelectionService axisSelectionService = AxisSelectionService.getInstance();


    public RealTimeSiteConfig getRealTimeSiteConfig(String siteID) throws Exception {

        Query<RealTimeSiteConfig> userQueryDS = mongoUtil.getMongoDatastore().createQuery(RealTimeSiteConfig.class);
        userQueryDS.field("siteID").equal(siteID);
        List<RealTimeSiteConfig> siteConfigs = userQueryDS.asList();

        if (FenceGlossaries.isObjectEmpty(siteConfigs)) {
            log.error(" Site Config not Set" + siteID);
            throw new Exception(" Site Configs not set for Site ID" + siteID);
        }

        if (siteConfigs.size() > 1) {
            log.error(" Error in Site Config" + siteID);


            throw new Exception("  DuplicateSite Config found " + siteID);
        }

        return siteConfigs.get(0);


    }

    public Map<String, Object> getLineofSightMap(String checkId) throws Exception {
        log.info(" Starting appplication of Algorithm 6 ");
        log.info(" Algorithm goes by pulling up layout");
        log.debug(" Incoming Id..." + checkId);
        Map<String, Object> beaconInfo = userGrid.findById(checkId);

        if (FenceGlossaries.isObjectEmpty(beaconInfo)) {
            ErrorCheckIns errorCheckIns = new ErrorCheckIns();
            errorCheckIns.setCheckId(checkId);
            errorCheckIns.setErrorMessage(" Beacon Info Not Found");
            log.error(" No Beacons in Sight for Check Id " + checkId);
            throw new Exception(" No Beacons in Sight");
        }

        return beaconInfo;

    }

    public void saveRealTimeLocation(RealTimeLocation realTimeLocation) throws Exception {
        log.debug(" Before Saving RealTimeLocation");
        mongoUtil.getMongoDatastore().save(realTimeLocation, "RealTimeLocationInfo");
    }

    public AxisSelection buildPreferredLocation(String axisName, List<BeaconMetaData> beaconMetaData, RealTimeSiteConfig siteConfig) throws Exception {

        log.debug(" In the Build Preferred Location");

        AxisSelection axisSelection = new AxisSelection();
        axisSelection.setAxes(axisName);
        // Get only Beacons which are sighted more than
        List<BeaconMetaData> sightedBeacons = beaconMetaData.stream().filter(u -> u.getSightingCount() >= 15).collect(Collectors.toList());
        if (!FenceGlossaries.isObjectEmpty(sightedBeacons)) {
            axisSelection.setBeaconMetaDataList(sightedBeacons);
            log.debug(" Going to Set Axis Properties ");

            this.setAxisProperties(axisSelection, siteConfig);

        }

        return axisSelection;
    }

    public void setAxisProperties(AxisSelection axisSelection, RealTimeSiteConfig siteConfig) throws Exception {
        String axisName = axisSelection.getAxes();

        String axisFloor = axisName.substring(0, 1);
        String axisGivenName = axisName.substring(1, 2);
        String axisNumber = axisName.substring(2, 3);

        log.debug(" Axis Floor is " + axisFloor);
        log.debug(" Axis Name is " + axisGivenName);
        log.debug(" Axis Number is " + axisNumber);


        // Commented by Murali to accomodate Algorithm 8
        // axisSelection.getBeaconMetaDataList().sort(new BeaconMetaDataCombinedComparator());
        // Distance to the closest beacon

        axisSelection.getBeaconMetaDataList().sort(new BeaconMetaDataDistanceComparator());
        double axisDistance = axisSelection.getBeaconMetaDataList().get(0).getBeaconDistance();
        log.debug(" Setting Axes Distance as = " + axisDistance);
        axisSelection.setAxisDistance(axisDistance);
        axisSelection.setAxisName(axisGivenName);
        axisSelection.setAxisNumber(axisNumber);
        axisSelection.setAxisFloor(axisFloor);


        BeaconMetaData beaconMetaData = axisSelection.getBeaconMetaDataList().get(0);

        // Bug Fix Not needed
        //axisSelectionService.createIOTLocation(axisSelection,beaconMetaData);

        axisSelectionService.determineAxesQuality(axisSelection);

        int selectionCount = axisSelection.getBeaconMetaDataList().size();

        if (axisSelection.getBeaconMetaDataList().size() > siteConfig.getSelectCount()) {
            selectionCount = (int) siteConfig.getSelectCount();
        }

        axisSelection.setSelectionCount(selectionCount);

        log.debug(" Axis Properties Set");

        log.debug(" Add Location ..");

        if (axisDistance <= siteConfig.getProximityDistance()) {
            this.axisSelectionService.createIOTLocation(axisSelection, axisSelection.getBeaconMetaDataList().get(0));
        }
    }

    public void buildRealTimeRoomInfo(RealTimeLocation realTimeLocation, BeaconMetaData beaconMetaData) throws Exception {
        log.info(" Received Beacon Meta Data : " + beaconMetaData.toString());

        RealTimeRoomInfo realTimeRoomInfo = new RealTimeRoomInfo();

        realTimeRoomInfo.setRoomNo(beaconMetaData.getProperyIdentifer());
        realTimeRoomInfo.setFloorNumber(Long.toString(beaconMetaData.getFloorNumber()));
        realTimeRoomInfo.setConfidenceLevel(this.getRoomConfidence(beaconMetaData).toString());
        realTimeRoomInfo.setZone(beaconMetaData.getZone());

        realTimeLocation.setRealTimeRoomInfo(realTimeRoomInfo);
    }

    public RoomConfidence getRoomConfidence(BeaconMetaData beaconMetaData) throws Exception {
        log.info(" Buiding Room Confidence");
        if (beaconMetaData.getBeaconDistance() <= 3) {
            return RoomConfidence.HIGH;
        } else {
            return RoomConfidence.LOW;
        }
    }

    public void buidlRoomInfoforZone(RealTimeLocation realTimeLocation, BeaconMetaData beaconMetaData) throws Exception {

        RealTimeRoomInfo realTimeRoomInfo = new RealTimeRoomInfo();

        realTimeRoomInfo.setRoomNo(beaconMetaData.getProperyIdentifer());
        realTimeRoomInfo.setFloorNumber(Long.toString(beaconMetaData.getFloorNumber()));
        realTimeRoomInfo.setConfidenceLevel(this.getRoomConfidence(beaconMetaData).toString());
        realTimeRoomInfo.setZone(beaconMetaData.getZone());
        // Add it to the proposed collection

        if (FenceGlossaries.isObjectEmpty(realTimeLocation.getDeterminedRooms())) {
            List<RealTimeRoomInfo> roomInfos = new ArrayList();
            roomInfos.add(realTimeRoomInfo);
            realTimeLocation.setDeterminedRooms(roomInfos);

        } else {
            realTimeLocation.getDeterminedRooms().add(realTimeRoomInfo);
        }

        RealTimeFloorInfo realTimeFloorInfo = new RealTimeFloorInfo();
        realTimeFloorInfo.setFloorNumber(Long.toString(beaconMetaData.getFloorNumber()));
        realTimeFloorInfo.setConfidenceLevel(this.getRoomConfidence(beaconMetaData).toString());

        if (FenceGlossaries.isObjectEmpty(realTimeLocation.getDeterminedFloors())) {
            List<RealTimeFloorInfo> floorInfos = new ArrayList();
            floorInfos.add(realTimeFloorInfo);
            realTimeLocation.setDeterminedFloors(floorInfos);

        } else {
            realTimeLocation.getDeterminedFloors().add(realTimeFloorInfo);
        }


    }
}
