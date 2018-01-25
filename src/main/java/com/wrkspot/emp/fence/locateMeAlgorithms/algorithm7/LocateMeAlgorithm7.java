package com.wrkspot.emp.fence.locateMeAlgorithms.algorithm7;

import com.wrkspot.emp.fence.locateMeAlgorithms.RealTimeLocationBuilder;
import com.wrkspot.emp.fence.locateMeAlgorithms.ZAxisResolutionService;
import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.iot.AxisSelection;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeLocation;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeSiteConfig;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeZoneInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Algorithm takes into account the Z Axis . The Algorithm is an extension of Algorithm6 . The Algorithm takes 10 closest beacons
 * And starts building relationship and if the relationship is on th Z Axis it will Place it in a zone .
 */
public class LocateMeAlgorithm7 implements Serializable {

    private LocateMeAlgorithm7() {
    }

    private static class SingletonHelper {
        private static final LocateMeAlgorithm7 INSTANCE = new LocateMeAlgorithm7();
    }

    public static LocateMeAlgorithm7 getInstance() {
        return SingletonHelper.INSTANCE;
    }


    private ZAxisResolutionService zAxisResolutionService = ZAxisResolutionService.getInstance();
    private static final Logger log = LoggerFactory.getLogger(LocateMeAlgorithm7.class);

    /**
     * Load the Beacons and if the beacons are in the range of 3 to 3.5 meters  then a room determination needs to be made
     * If the closets beacon in a front desk or a common area that then is laid out
     */
    public RealTimeLocation locateMe(Map<String, Object> beaconInfo, RealTimeSiteConfig siteConfig) throws Exception {

        RealTimeLocation realTimeLocation = new RealTimeLocation();
        log.debug(" Going to Start Applying Algorithm 7");

        Map<String, List<BeaconMetaData>> axisDetails = (Map<String, List<BeaconMetaData>>) beaconInfo.get("AXISINFO");

        log.debug(" Going to Perform Axis Selection");
        List<AxisSelection> axisSelectionList = zAxisResolutionService.performAxisSelection(axisDetails, siteConfig);
        log.info(" Got the Axes Information ");

        realTimeLocation.setAxisSelections(axisSelectionList);


        log.debug(" Going to get Axis by distance which are revelant");
        List<AxisSelection> distanceSortedList = zAxisResolutionService.selectAxesbyDistance(axisSelectionList, siteConfig);
        log.debug(" Got Distance based on distance");


        List<AxisSelection> finallist = this.zAxisResolutionService.applyAlgorithm7(distanceSortedList, siteConfig);
        realTimeLocation.setRealTimeZoneInfos(new ArrayList<RealTimeZoneInfo>());

        realTimeLocation.setFinallist(finallist);

        for (AxisSelection axisSelection1 : finallist) {
            this.zAxisResolutionService.handleFinalList(realTimeLocation, axisSelection1);
        }

        return realTimeLocation;
    }


}
