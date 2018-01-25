package com.wrkspot.emp.fence.locateMeAlgorithms.algorithm7;

import com.wrkspot.emp.fence.locateMeAlgorithms.*;
import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.iot.AxisSelection;
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
import java.util.Map;

/**
 * Algorithm7 Takes into account the Z Axis.
 *
 * The Algorirhtm takes the 10 closest beacons and finds relationship between them like Algorithm5
 * Determines the relationship based on Axes.
 * This algorithm only supports ceiling based deployment only.
 * The SiteConfig plays a very important role in this Algorithm
 * The flow of the Algorithm is as follows
 * Sort the sighted beacons are sorted by line of sight count and distance
 * The beacons are then placed in their axes
 * the axes are sorted by distance .
 *
 * The closest axes is picked and check if there are any proximity beacons
 * Any beacon within 3.5 meters is considered a proximity beacon
 * Due to placement in the ceiling . In a in room situtation there will be 3 beacons which show up as proximity.
 * In a first swipe Axes withing 2.5 metrers of overlap is considered.
 * In a second swipe 1.5 meters of overlsap is considered
 * In this step teh Z Axis resolution is done. If a vertical axis is visible in the range the vertical axis is picked
 * Final list of Axes are selected
 * The Final list should ideally one axes.
 * If more than one is got we drop into a zone
 *
 * Zone is a range of rooms the person is predicted to be in
 *
 * This is the most comprehensive Algorithm for Ceiling based deployment
 *
 * Known Issues
 *   The Algorithm will be totally suprised to see more than one axes in the final roound
 *   The Algorritm will put proximity rooms into a zone as the readings may be inconsistent
 * Testing
 * The Algorithm will be tested with Brent Mamooth
 */

public class IOTDataHandlerAlg7 implements Serializable{


    private static final Logger LOG = LoggerFactory.getLogger(IOTDataHandlerAlg7.class);

    private IOTDataHandlerAlg7() {
    }

    private static class SingletonHelper {
        private static final IOTDataHandlerAlg7 INSTANCE = new IOTDataHandlerAlg7();
    }

    public static IOTDataHandlerAlg7 getInstance() {
        return SingletonHelper.INSTANCE;
    }


    private static org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmm");
    private RealTimeLocationBuilder realTimeLocationBuilder = RealTimeLocationBuilder.getInstance();
    private MongoUtil mongoTemplate= MongoUtil.getInstance();
    private LocateMeAlgorithm7 algorithm7;



    public void locateMe(String checkId)
    {
        LOG.info(" Starting Algorithm 7... ");

        try {


            LOG.debug(" Going to get Beacons in Sight ");
            Map<String,Object> bInfo = realTimeLocationBuilder.getLineofSightMap(checkId);
            LOG.debug(" Got Beacons in Sight");

            RealTimeSiteConfig siteConfig = realTimeLocationBuilder.getRealTimeSiteConfig((String) bInfo.get("SITEID"));

            Date logTime = (Date) bInfo.get("LOGTIME");

            DateTime sTime = new DateTime(logTime);





            long logTimemilli = Long.valueOf(sTime.toString(this.formatter));
            //long logTimemilli = sTime.getMillis();

            long sHour = sTime.getHourOfDay();
            long sMinute = sTime.getMinuteOfHour();



            LOG.debug(" Log Time ..... " + logTime);
            LOG.debug(" S Time is  ..." + sTime);
            LOG.debug(" Hour of the Day" + sHour);
            LOG.debug(" Minute of the Hour" + sMinute);

            /**
             * Fix for constant jumping
             * If no beacons have been sighted for more than 15 times than we will revert to Algorithm 1
             * When the axes are built it will only include beacons which are sighted more than 15 times.
             * The Fix for constant Jumping will be done later
             */

            RealTimeLocation realTimeLocation = null;

                 realTimeLocation = this.algorithm7.locateMe(bInfo,siteConfig);



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

            realTimeLocation.setAppliedAlgorithm(AlgorithmList.ALGORITHM7.toString());

            try {

              realTimeLocationBuilder.saveRealTimeLocation(realTimeLocation);
            }
            catch(Exception e)
            {
                LOG.debug(" Unable to write to RealTime Location .. In Mongo");
                LOG.error(" Unable to write to Real time Locaton" , e);

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

        }
        catch(Exception e)
        {
            ErrorCheckIns errorCheckIns = new ErrorCheckIns();
            errorCheckIns.setCheckId(checkId)
            ;
            errorCheckIns.setErrorMessage(" Failed in Algorithm 7");
            e.printStackTrace();
            LOG.error(" Failed in Algorithm 7", e.getMessage());
        }
    }

}
