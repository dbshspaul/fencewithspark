package com.wrkspot.emp.fence.locateMeAlgorithms.algorithm8;

import com.wrkspot.emp.fence.locateMeAlgorithms.AlgorithmList;
import com.wrkspot.emp.fence.locateMeAlgorithms.RealTimeLocationBuilder;
import com.wrkspot.emp.fence.locateMeAlgorithms.UserOccupancyService;
import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.realtimelocation.ErrorCheckIns;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeLocation;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeSiteConfig;
import com.wrkspot.emp.fence.mongoutil.MongoUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Algorithm 8 is being built after the initial deployment a Mammoth
 * The Algorithm will take into account the sightcount and make a decision based on the sight count .
 * The algorithm will first week out beacons with less than 15 sight  count
 * It will introduce a confidence factor based on sightings
 * With less then 40 sightings we will only with a low confidence the room and will try to get the floor and zone in high confidence
 * It will revert to Algorith 7 to make axes comparisons
 * <p>
 * Also try to find out missing checkins
 * <p>
 * Problems in Algorithm7
 * <p>
 * 1) Decisions made based on low sight count
 * 2) Not able to track missing checkin ids
 * <p>
 * Dependency
 * 1) Algorithm7 for axis based
 * 2) MongoTemplate
 * <p>
 * Steps
 * 1) Weed out beacons with less than 15 sight count
 * 2) Get the max count and look for 50% percentile
 * 3) Pick up axis with > 50%
 * 4) work with the axes as in Algorithm 7
 */

public class IOTDataHandlerAlg8 implements Serializable{

    private static final Logger log = LoggerFactory.getLogger(IOTDataHandlerAlg8.class);

    private IOTDataHandlerAlg8() {
    }

    private static class SingletonHelper {
        private static final IOTDataHandlerAlg8 INSTANCE = new IOTDataHandlerAlg8();
    }

    public static IOTDataHandlerAlg8 getInstance() {
        return SingletonHelper.INSTANCE;
    }


    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmm");
    private RealTimeLocationBuilder realTimeLocationBuilder = RealTimeLocationBuilder.getInstance();

    private UserOccupancyService userOccupancyService = UserOccupancyService.getInstance();

    private MongoUtil mongoTemplate = MongoUtil.getInstance();

    private LocateMeAlgorithm8 algorithm8;


    public void locateme(String checkId) {

        try {
            log.debug(" Going to get Beacons in Sight ");
            Map<String, Object> bInfo = realTimeLocationBuilder.getLineofSightMap(checkId);
            log.debug(" Got Beacons in Sight");

            List<BeaconMetaData> beaconMetaDataList = (List<BeaconMetaData>) bInfo.get("BEACONS");

            RealTimeSiteConfig siteConfig = realTimeLocationBuilder.getRealTimeSiteConfig((String) bInfo.get("SITEID"));

            Date logTime = (Date) bInfo.get("LOGTIME");

            DateTime sTime = new DateTime(logTime);

            long logTimemilli = Long.valueOf(sTime.toString(this.formatter));
            //long logTimemilli = sTime.getMillis();

            long sHour = sTime.getHourOfDay();
            long sMinute = sTime.getMinuteOfHour();


            RealTimeLocation realTimeLocation = null;


            realTimeLocation = algorithm8.locateMe(bInfo, siteConfig);


            realTimeLocation.setCheckIn(checkId);

            // Set Time

            realTimeLocation.setLogTime(sTime);
            realTimeLocation.setSlogTime(logTimemilli);
            realTimeLocation.setLogHour(sHour);
            realTimeLocation.setLogminute(sMinute);


            String siteID = (String) bInfo.get("SITEID");
            String employeeID = (String) bInfo.get("EMPLOYEEID");
            realTimeLocation.setEmployeeId(employeeID);
            realTimeLocation.setSiteID(siteID);


            realTimeLocation.setAppliedAlgorithm(AlgorithmList.ALGORITHM8.toString());

            try {

                realTimeLocationBuilder.saveRealTimeLocation(realTimeLocation);
            } catch (Exception e) {
                log.debug(" Unable to write to RealTime Location .. In Mongo");
                log.error(" Unable to write to Real time Locaton", e);
                log.error(" Attempting to Write Algrithm 8 ...");

                //Query  ar = new Query();
                //ar.addCriteria(Criteria.where("checkId").is(checkId));

                //ErrorCheckIns errorCheckIns1 = mongoTemplate.findOne(ar,ErrorCheckIns.class);
                //errorCheckIns = new ErrorCheckIns();
                //errorCheckIns1.setCheckId(realTimeLocation.getCheckIn());
                //errorCheckIns1.setErrorMessage(e.getMessage());
                ErrorCheckIns errorCheckIns1 = new ErrorCheckIns();
                errorCheckIns1.setCheckId(checkId);
                errorCheckIns1.setErrorMessage(" Unable to save Real Time Location");

                this.mongoTemplate.getMongoDatastore().save(errorCheckIns1);


            }

            try {
                //userOccupancyService.getPropertyInfo(realTimeLocation);
                log.debug(" Going to handle user Occupancy");
                log.debug((" Writing "));
                userOccupancyService.saveRealTimeLocation(realTimeLocation);
            } catch (Exception e) {
                ErrorCheckIns errorCheckIns = new ErrorCheckIns();
                errorCheckIns.setCheckId(checkId);
                errorCheckIns.setErrorMessage(" Unable to save User Occupancy");
                log.debug(" Unable to Save User Movements ");
                log.error(" Unable to Save User Movemments " + e);
            }

        } catch (Exception e) {
            log.error(" Error in Handling ALgorithm 8" + e.getMessage());
        }

        // Sort BeaconMetaData by Sightcount


    }
}
