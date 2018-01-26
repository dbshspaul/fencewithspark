package com.wrkspot.emp.fence;

import com.wrkspot.emp.fence.model.*;
import com.wrkspot.emp.fence.model.dto.FinalLocation;
import com.wrkspot.emp.fence.model.dto.LocateMe;
import com.wrkspot.emp.fence.mongoutil.MongoUtil;
import com.wrkspot.emp.fence.service.impl.HouseKeepingServiceImpl;
import com.wrkspot.emp.fence.service.impl.LocationServiceImpl;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FenceService implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FenceService.class);

    private FenceService() {
    }

    private static class SingletonHelper {
        private static final FenceService INSTANCE = new FenceService();
    }

    public static FenceService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private MongoUtil mongoUtil = MongoUtil.getInstance();
    private HouseKeepingServiceImpl houseKeepingService = HouseKeepingServiceImpl.getInstance();
    private LocationServiceImpl locationService = LocationServiceImpl.getInstance();


    public EmployeeCheckins findById(String id) {

        LOGGER.info("Inside Service" + id);
        EmployeeCheckins empCheckIn = retrieveCheckIn(id);
        if (empCheckIn != null) {
            LocateMe locateMe = new LocateMe();
            if (empCheckIn.getBeacons().size() > 0) {
                locateMe.setBeacons(empCheckIn.getBeacons());
            }

            try {
                String closestRoomUUID = locationService.locateMe(locateMe);
                LOGGER.info("closestRoomUUID" + closestRoomUUID);
                if (closestRoomUUID != null) {
                    HotelFloorPlan userCurrentRoom = retrieveRoomByUUID(closestRoomUUID);
                    if (userCurrentRoom != null) {
                        UserFence userlocation = prepareLocationInfo(empCheckIn, userCurrentRoom);
                        saveUserLocation(userlocation);
                        //See where user spent his last 5 minutes
                        cacheUserLocation(empCheckIn, userCurrentRoom);
                    }

                }
            } catch (Exception e) {
                LOGGER.error("Exception" + e);
            }
        }
        return null;
    }

    /**
     * Method to retrieve checkin
     *
     * @param fenceId check in id fro the kafka queue
     * @return EmployeeCheckins checkin data from the mobile app
     */
    public EmployeeCheckins retrieveCheckIn(String fenceId) {
        LOGGER.info("Entered into retrieveCheckIn in DAO:");
        LOGGER.info(" Fence ID is ...." + fenceId);

        EmployeeCheckins currrentFence = null;
        try {
            fenceId = fenceId.trim();
            currrentFence = mongoUtil.getMongoDatastore().get(EmployeeCheckins.class,new ObjectId(fenceId));
            if (currrentFence != null) {
                LOGGER.info("Check In data time---->" + currrentFence.getLogTime() + " Emp Id --> "
                        + currrentFence.getEmployeeID() + " Site Id --> " + currrentFence.getSiteID());
            }
            return currrentFence;
        } catch (Exception e) {
            LOGGER.error("Exception" + e);
            LOGGER.error("Failed retrieving check in id" + fenceId);


        }
        LOGGER.info("Exiting from retrieveCheckIn in DAO");
        return currrentFence;

    }

    /**
     * Method
     *
     * @param roomIdentifier
     * @return
     */
    public PropertyLayout retrieveProperty(String roomIdentifier) {
        LOGGER.debug("Entered into retrieveProperty method: ");
        PropertyLayout thisRoom = null;
        try {
            Query<PropertyLayout> userQueryDS = mongoUtil.getMongoDatastore().createQuery(PropertyLayout.class);
            userQueryDS.field("propertyIdentifier").equal(roomIdentifier);
            thisRoom = userQueryDS.get();
            return thisRoom;
        } catch (Exception e) {
            LOGGER.error("Unable to locate room for identifier" + roomIdentifier);
        }
        LOGGER.debug("Entered into retrieveProperty method: ");
        return thisRoom;
    }

    /**
     * Method to get the property info
     *
     * @param siteId siteId
     * @return PropertyLayout PropertyLayout
     */
    public List<PropertyLayout> retrivePropertyInfo(String siteId, String axisName) {
        LOGGER.debug("Entered into retrivePropertyInfo method: ");
        List<PropertyLayout> siteLayOut = null;
        try {
            Query<PropertyLayout> userQueryDS = mongoUtil.getMongoDatastore().createQuery(PropertyLayout.class);
            userQueryDS.field("siteID").equal(siteId).field("axis").equal(axisName);
            siteLayOut = userQueryDS.asList();
            if (siteLayOut != null) {
                return siteLayOut;
            }
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve the layout for site id == " + siteId);
        }
        LOGGER.debug("Exiting from retrivePropertyInfo method: ");
        return null;
    }


    /**
     * Method to savePredictedLocated
     *
     * @param finalLocation finalLocation
     */
    public void savePredictedLocated(FinalLocation finalLocation) {
        LOGGER.debug("Entered into savePredictedLocated method: ");
        try {
            mongoUtil.getMongoDatastore().save(finalLocation, "PredictedLocation");
        } catch (Exception e) {
            LOGGER.error("Unable to save the predicted location");
        }
        LOGGER.debug("Entered into savePredictedLocated method: ");

    }

    /**
     * Method to saveUserMovements2
     *
     * @param userMovements userMovements
     */
    public void saveUserMovements2(UserFence2 userMovements) {
        {
            LOGGER.debug("Entered into saveUserMovements2 method: ");
            try {
                mongoUtil.getMongoDatastore().save(userMovements);
            } catch (Exception e) {
                LOGGER.error("Unable to save the predicted location");
            }
            LOGGER.debug("Entered into saveUserMovements2 method: ");

        }
    }

    /**
     * Method to retrieve the room details by room uuid
     */
    public HotelFloorPlan retrieveRoomByUUID(String uuid) {
        LOGGER.info("Entered into retrieveRoomByUUID in DAO:");
        HotelFloorPlan hotelRoom = null;
        try {
            Query<HotelFloorPlan> userQueryDS = mongoUtil.getMongoDatastore().createQuery(HotelFloorPlan.class);
            userQueryDS.field("uuid").equal(uuid);
            LOGGER.debug("UUID is : Retrieve Hotel Room  :" + uuid);
            hotelRoom = userQueryDS.get();
            LOGGER.info("hotelRoom is---->" + hotelRoom);
            return hotelRoom;

        } catch (Exception e) {
            LOGGER.error("Error in retrieving the hotel room" + e);
        }
        LOGGER.info("Exiting from retrieveRoomByUUID in DAO");
        return hotelRoom;

    }

    /**
     * Method to retrieve the room layout info from uuid
     */
    public PropertyLayout retrieveRoomLayout(String hotelRoomIdentifier, String siteID) {
        LOGGER.info("Entered into retrieveRoomLayout in DAO:");
        PropertyLayout propertyLayout = null;
        try {
            Query<PropertyLayout> userQueryDS = mongoUtil.getMongoDatastore().createQuery(PropertyLayout.class);
            userQueryDS.field("propertyIdentifier").equal(hotelRoomIdentifier);
            propertyLayout = userQueryDS.get();
            LOGGER.info("hotelRoom is---->" + propertyLayout);
            return propertyLayout;

        } catch (Exception e) {
            LOGGER.error("Error in retrieving the hotel room Layout" + e);
        }
        LOGGER.info("Exiting from retrieveRoomLayout in DAO");
        return propertyLayout;

    }

    public void saveUserLocation(UserFence userFence) {
        LOGGER.info("Entered into saveUserLocation in DAO");
        try {
            mongoUtil.getMongoDatastore().save(userFence);
        } catch (Exception e) {
            LOGGER.error("Exception " + e);

        }
        LOGGER.info("Exiting from saveUserLocation in DAO");

    }

    public void cacheUserLocation(EmployeeCheckins empCheckIn, HotelFloorPlan userCurrentRoom) {
        LOGGER.info("Entered into cacheUserLocation:");
        UserLocationData userCache = getUserLocation(empCheckIn.getEmployeeID());
        List<String> usersLastKnownLocations = null;
        if (userCache != null) {
            usersLastKnownLocations = userCache.getUserLocations();
        }

        if (usersLastKnownLocations == null) {
            LOGGER.info("Creating first known location in cache for employee id -- " + empCheckIn.getEmployeeID());

            // Create first record on the redis cache
            UserLocationData ul = new UserLocationData();
            ul.setEmpId(empCheckIn.getEmployeeID());
            List<String> userLocations = new ArrayList<String>();
            userLocations.add(userCurrentRoom.getRoom_identifier());
            ul.setUserLocations(userLocations);
            saveUserKnowLocation(ul);
            saveHouseKeepingInfo(empCheckIn, userCurrentRoom, false);
        } else {
            if (usersLastKnownLocations.get(0).equals(userCurrentRoom.getRoom_identifier())) {
                saveHouseKeepingInfo(empCheckIn, userCurrentRoom, true);
            } else {
                usersLastKnownLocations.remove(0);
                usersLastKnownLocations.add(userCurrentRoom.getRoom_identifier());
                userCache.setUserLocations(usersLastKnownLocations);
                saveUserKnowLocation(userCache);
                saveHouseKeepingInfo(empCheckIn, userCurrentRoom, false);

            }
        }

        LOGGER.info("Exiting from cacheUserLocation:");

    }

    private UserLocationData getUserLocation(String employeeID) {
        LOGGER.info("Entered into getUserLocation method");
        List<String> prevLocations = null;
        try {
            UserLocationData cacheVal = houseKeepingService.getById(employeeID);
            if (cacheVal != null) {

                return cacheVal;
            }
        } catch (Exception e) {
            LOGGER.error("Exception while reading cache" + e);
        }
        LOGGER.info("Exiting from getUserLocation method");

        return null;
    }

    private void saveUserKnowLocation(UserLocationData userLocationData) {
        LOGGER.info("Entered into saveUserKnowLocation method:");
        houseKeepingService.saveOrUpdate(userLocationData);
        LOGGER.info("Exiting from saveUserKnowLocation method:");
    }

    private void saveHouseKeepingInfo(EmployeeCheckins empCheckIn, HotelFloorPlan userCurrentRoom, Boolean existing) {
        // See if the record already exists
        LOGGER.info("Entered into saveHouseKeepingInfo method:");
        if (existing) {
            HouseKeeping hkdata = retrieveHKbyID(empCheckIn.getEmployeeID(),
                    userCurrentRoom.getRoom_identifier(), empCheckIn.getLogTime());
            if (hkdata != null) {
                DateTime dt = new DateTime(hkdata.getEnd());
                DateTime newDateis = dt.plusMinutes(1);
                hkdata.setEnd(newDateis);
                saveHouseKeeping(hkdata);
            } else {
                // This is when the user stopped at a location and again the user started at the same location after some time
                HouseKeeping nhkdata = new HouseKeeping();
                nhkdata.setEmployeeID(empCheckIn.getEmployeeID());
                nhkdata.setRoom_identifier(userCurrentRoom.getRoom_identifier());
                nhkdata.setFloor(userCurrentRoom.getFloor());
                nhkdata.setWing(userCurrentRoom.getWing());
                nhkdata.setSiteID(userCurrentRoom.getSiteID());
                nhkdata.setClientID(empCheckIn.getClientID());
                Date logTime = empCheckIn.getLogTime();
                DateTime toTime = new DateTime(logTime);
                nhkdata.setEnd(toTime);
                DateTime fromTime = toTime.minusMinutes(1);
                nhkdata.setStart(fromTime);
                saveHouseKeeping(nhkdata);
            }

        } else {
            HouseKeeping hkdata = new HouseKeeping();
            hkdata.setEmployeeID(empCheckIn.getEmployeeID());
            hkdata.setRoom_identifier(userCurrentRoom.getRoom_identifier());
            hkdata.setFloor(userCurrentRoom.getFloor());
            hkdata.setWing(userCurrentRoom.getWing());
            hkdata.setSiteID(userCurrentRoom.getSiteID());
            Date logTime = empCheckIn.getLogTime();
            DateTime toTime = new DateTime(logTime);
            hkdata.setEnd(toTime);
            DateTime fromTime = toTime.minusMinutes(1);
            hkdata.setStart(fromTime);
            saveHouseKeeping(hkdata);
        }
        LOGGER.info("Exiting from saveHouseKeepingInfo method:");
    }

    public HouseKeeping retrieveHKbyID(String empId, String roomId, Date logTime) {
        LOGGER.info("Entered into retrieveHKbyID in DAO:");
        HouseKeeping houseKeeping = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(logTime); // reset hour, minutes, seconds and millis
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date criteriaTime = logTime;
        DateTime lastKnownTime = new DateTime(criteriaTime);
        DateTime prevEndTime = lastKnownTime.minusMinutes(2);
        try {
            Query<HouseKeeping> userQueryDS = mongoUtil.getMongoDatastore().createQuery(HouseKeeping.class);
            userQueryDS.field("employeeID").equal(empId).field("room_identifier").equal(roomId).field("end").greaterThanOrEq(prevEndTime);
            List<HouseKeeping> loggedHKData = userQueryDS.asList();
            LOGGER.info("loggedHKData is " + loggedHKData + "loggedHKData size" + loggedHKData.size());
            if (loggedHKData != null && loggedHKData.size() > 0) {
                houseKeeping = loggedHKData.get(0);
                LOGGER.info(
                        "houseKeeping End time ---->" + houseKeeping.getEnd() + "lastKnownTime is (-2)  " + prevEndTime);
            }

            return houseKeeping;

        } catch (Exception e) {
            LOGGER.error("Error in retrieving retrieveHKbyID" + e);
        }
        LOGGER.info("Exiting from retrieveHKbyID in DAO");
        return houseKeeping;

    }

    public void saveHouseKeeping(HouseKeeping hkData) {
        LOGGER.info("Entered into saveHouseKeeping in DAO");
        try {
            mongoUtil.getMongoDatastore().save(hkData);
        } catch (Exception e) {
            LOGGER.error("Exception " + e);

        }
        LOGGER.info("Exiting from saveHouseKeeping in DAO");

    }

    private UserFence prepareLocationInfo(EmployeeCheckins empCheckIn, HotelFloorPlan userCurrentRoom) {
        LOGGER.info("Entered into prepareLocationInfo:");
        UserFence userLocation = new UserFence();
        userLocation.setCreatedAt(new Date());
        userLocation.setCreatedBy("FenceEngine");
        userLocation.setEmployeeID(empCheckIn.getEmployeeID());
        userLocation.setFloor(userCurrentRoom.getFloor());
        userLocation.setName(userCurrentRoom.getName());
        userLocation.setRoom_identifier(userCurrentRoom.getRoom_identifier());
        userLocation.setSiteID(userCurrentRoom.getSiteID());
        userLocation.setClientID(empCheckIn.getClientID());
        Date userLoggedTime = empCheckIn.getLogTime();
        /* Removing seconds and milliseconds attributes from timestamp
         * to avoid repeated calculations when user starts and stops at the same minute.
         * MongoDb collections already configured to have unique index created for employee id
         * and timestamp attribute.*/
        Calendar cal = Calendar.getInstance();
        cal.setTime(userLoggedTime);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        userLocation.setTimestamp(cal.getTime());
        userLocation.setUuid(userCurrentRoom.getUuid());
        userLocation.setWing(userCurrentRoom.getWing());
        LOGGER.info("Exiting from prepareLocationInfo:");
        return userLocation;
    }
}
