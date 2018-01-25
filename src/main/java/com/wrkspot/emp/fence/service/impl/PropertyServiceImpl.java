package com.wrkspot.emp.fence.service.impl;

import com.wrkspot.emp.fence.FenceService;
import com.wrkspot.emp.fence.model.HotelFloorPlan;
import com.wrkspot.emp.fence.model.PropertyLayout;
import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class PropertyServiceImpl implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);

    private PropertyServiceImpl() {
    }

    private static class SingletonHelper {
        private static final PropertyServiceImpl INSTANCE = new PropertyServiceImpl();
    }

    public static PropertyServiceImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private FenceService fenceDao=FenceService.getInstance();


    /**
     * Method that retrieves the hotel room and its axis position
     *
     * @param beaconData beacon data
     */
    public void updateAxisInfo(BeaconMetaData beaconData) {
        log.debug("Entered into updateAxisInfo method ");
        PropertyLayout roomLayout = null;
        // @TODO: 6/30/17 Instead of retrieving room identifier from database, this needs to updated to Redis Cache as soon as it becomes available
        try {
            HotelFloorPlan roomInfoFromDeployment = fenceDao.retrieveRoomByUUID(beaconData.getBeaconUUID());
            String roomIdentifier = roomInfoFromDeployment.getRoom_identifier();

            if (roomIdentifier != null) {
                //TODO  Need the Site ID  and a find one is used
                // BUG BUG BUG
                String siteID = beaconData.getSiteId();
                roomLayout = fenceDao.retrieveRoomLayout(roomIdentifier, siteID
                );
            }
            if (roomLayout != null) {
                beaconData.setAxis(roomLayout.getAxis());
                beaconData.setAxisPos(roomLayout.getAxisPos());
                beaconData.setBelowRoom(roomLayout.getPropertydown());
                beaconData.setFloorNumber(roomLayout.getFloorNumber());
                beaconData.setFloorWing(roomLayout.getPropertyWing());
                beaconData.setTopRoom(roomLayout.getPropertyTop());
                beaconData.setFrontRoom(roomLayout.getPropertyFront());
                beaconData.setBelowRoom(roomLayout.getPropertydown());
                beaconData.setIntersections(roomLayout.getIntersections());
                beaconData.setProperyIdentifer(roomIdentifier);
                beaconData.setZone(roomLayout.getZone());

                // TODO: 6/30/17 Need to add other properties of the room
            } else {
                log.error("Unable to retrieve room layout for identifier " + roomIdentifier);
            }


        } catch (Exception e) {
            log.error("Exception occurred while retrieving  ");
        }

        log.debug("Exiting from updateAxisInfo method ");


    }
}
