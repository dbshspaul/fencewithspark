package com.wrkspot.emp.fence.locateMeAlgorithms;

import com.wrkspot.emp.fence.model.graph.EmployeeInfo;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeLocation;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeZoneInfo;
import com.wrkspot.emp.fence.model.userLocation.UserEmployee;
import com.wrkspot.emp.fence.model.userLocation.UserOccupancy;
import com.wrkspot.emp.fence.model.userLocation.UserProperty;
import com.wrkspot.emp.fence.mongoutil.MongoUtil;
import com.wrkspot.emp.fence.util.FenceGlossaries;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserOccupancyService  implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(UserOccupancyService.class);

    private UserOccupancyService() {
    }

    private static class SingletonHelper {
        private static final UserOccupancyService INSTANCE = new UserOccupancyService();
    }

    public static UserOccupancyService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private MongoUtil mongoUtil = MongoUtil.getInstance();

    public void saveRealTimeLocation(RealTimeLocation realTimeLocation) throws Exception {
        /**
         * Check if Employee Exists
         * Check if User Property Exists
         * Save Occupancy
         */

        UserEmployee propertyEmployee = this.getEmployeeInfo(realTimeLocation);
        List<UserProperty> propertyInfoList = this.getPropertyInfo(realTimeLocation);


        for (UserProperty propertyInfo : propertyInfoList) {
            UserOccupancy propertyOccupancy = new UserOccupancy();
            propertyOccupancy.setUserEmployee(propertyEmployee);
            propertyOccupancy.setUserProperty(propertyInfo);
            propertyOccupancy.setSiteID(realTimeLocation.getSiteID());

            propertyOccupancy.setLogTime(realTimeLocation.getSlogTime());
            propertyOccupancy.setsLogTime(realTimeLocation.getLogTime());
            propertyOccupancy.setStartHour(realTimeLocation.getLogHour());
            propertyOccupancy.setStartMinute(realTimeLocation.getLogminute());
            propertyOccupancy.setCheckIn(realTimeLocation.getCheckIn());


            log.debug(" Before saving  Property Occupancy ");

            //propertyOccupancyRepository.save(propertyOccupancy);
            mongoUtil.getMongoDatastore().save(propertyOccupancy);

        }
    }

    private UserProperty getSinglePropertyInfo(String roomNo, String siteID, String floorNo, String zone) throws Exception {
        Query<UserProperty> userQueryDS = mongoUtil.getMongoDatastore().createQuery(UserProperty.class);
        userQueryDS.field("siteID").equal(siteID)
                .field("roomNo").equal(roomNo)
                .field("floorNo").equal(floorNo)
                .field("zone").equal(zone);

        List<UserProperty> propertyInfos = userQueryDS.asList();

        if (FenceGlossaries.isObjectEmpty(propertyInfos)) {
            UserProperty propertyInfo = new UserProperty();
            propertyInfo.setRoomNo(roomNo);
            propertyInfo.setFloorNo(floorNo);
            propertyInfo.setSiteID(siteID);
            propertyInfo.setZone(zone);
            //  this.propertyInfoRepository.save(propertyInfo);
            mongoUtil.getMongoDatastore().save(propertyInfo);
            return propertyInfo;

        } else {
            if (propertyInfos.size() > 1) {
                log.error(" Property Info in Neo Corrupted .." + "room no : " + roomNo + "Floor No ..:" + floorNo + " Zone ..:" + zone);
                throw new Exception(" Property Info Corrupted for " + "room no : " + roomNo + "Floor No ..:" + floorNo + " Zone ..:" + zone);
            }

            UserProperty propertyInfo = propertyInfos.get(0);
            return propertyInfo;
        }
    }

    public UserEmployee getEmployeeInfo(RealTimeLocation realTimeLocation) throws Exception {

        EmployeeInfo employeeInfo = mongoUtil.getMongoDatastore().get(EmployeeInfo.class,new ObjectId(realTimeLocation.getEmployeeId()));

        Query<UserEmployee> datastoreQuery = mongoUtil.getMongoDatastore().createQuery(UserEmployee.class);
        datastoreQuery.field("siteID").equal(realTimeLocation.getSiteID())
                .field("employeeID").equal(realTimeLocation.getEmployeeId());
        List<UserEmployee> propertyEmployees = datastoreQuery.asList();

        UserEmployee propertyEmployee = null;

        if (FenceGlossaries.isObjectEmpty(propertyEmployees)) {
            propertyEmployee = new UserEmployee();
            propertyEmployee.setEmployeeID(realTimeLocation.getEmployeeId());
            propertyEmployee.setSiteID(realTimeLocation.getSiteID());

            propertyEmployee.setEmployeeID(realTimeLocation.getEmployeeId());
            if (!FenceGlossaries.isObjectEmpty(employeeInfo)) {
                propertyEmployee.setUserDept(employeeInfo.getDepartment());
                propertyEmployee.setFirstName(employeeInfo.getFirst_name());
                propertyEmployee.setLastName(employeeInfo.getLast_name());
                propertyEmployee.setUserShift(employeeInfo.getSchedule());
            }
            //propertyEmployeeRepository.save(propertyEmployee);
            mongoUtil.getMongoDatastore().save(propertyEmployee);

            return propertyEmployee;

        }

        if (propertyEmployees.size() > 1) {
            log.error(" Error in Graph Employee Database");
            throw new Exception(" Error in Grapch Database .. Duplicate Employee Inforation or Employee Id " + realTimeLocation.getEmployeeId());
        }

        propertyEmployee = propertyEmployees.get(0);
        return propertyEmployee;


    }

    private UserProperty handleRoom(RealTimeLocation realTimeLocation) throws Exception {

        String roomNo = realTimeLocation.getRealTimeRoomInfo().getRoomNo();
        String floorNo = realTimeLocation.getRealTimeRoomInfo().getFloorNumber();
        String zone = realTimeLocation.getRealTimeRoomInfo().getZone();
        String siteID = realTimeLocation.getSiteID();
        return this.getSinglePropertyInfo(roomNo, siteID, floorNo, zone);

    }

    private List<UserProperty> handleZone(RealTimeLocation realTimeLocation) throws Exception {
        List<RealTimeZoneInfo> zoneInfos = realTimeLocation.getRealTimeZoneInfos();
        String siteID = realTimeLocation.getSiteID();

        List<UserProperty> propertyInfoList = new ArrayList<>();

        for (RealTimeZoneInfo realTimeZoneInfo : zoneInfos) {
            // check if Zone and Room exists if not add it
            String roomNo = realTimeZoneInfo.getFromRoomInfo();
            String floorNo = realTimeZoneInfo.getFloorNo();
            String zone = realTimeZoneInfo.getZone();

            UserProperty propertyInfo = this.getSinglePropertyInfo(roomNo, siteID, floorNo, zone);
            propertyInfoList.add(propertyInfo);

        }

        return propertyInfoList;

    }

    public List<UserProperty> getPropertyInfo(RealTimeLocation realTimeLocation) throws Exception {
        /**
         * If we find Real Time Location roomInfo then it is inRoom
         * or Look for Zones  .
         */

        String siteID = realTimeLocation.getSiteID();

        List<UserProperty> propertyInfoList = new ArrayList<>();

        if (!FenceGlossaries.isObjectEmpty(realTimeLocation.getRealTimeRoomInfo())) {
            UserProperty propertyInfo = this.handleRoom(realTimeLocation);
            propertyInfoList.add(propertyInfo);

        } else {
            // handle zones
            propertyInfoList = this.handleZone(realTimeLocation);
        }


        return propertyInfoList;
    }


}
