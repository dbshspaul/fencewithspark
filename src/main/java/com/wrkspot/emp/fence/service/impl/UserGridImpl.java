package com.wrkspot.emp.fence.service.impl;

import com.wrkspot.emp.fence.FenceService;
import com.wrkspot.emp.fence.model.Beacon;
import com.wrkspot.emp.fence.model.EmployeeCheckins;
import com.wrkspot.emp.fence.model.IOTData;
import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.dto.LocateMe;
import com.wrkspot.emp.fence.util.FenceGlossaries;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class UserGridImpl implements Serializable{

    private static final Logger log = LoggerFactory.getLogger(UserGridImpl.class);

    private UserGridImpl() {
    }

    private static class SingletonHelper {
        private static final UserGridImpl INSTANCE = new UserGridImpl();
    }

    public static UserGridImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private FenceService fenceService = FenceService.getInstance();
    private RankingServiceImpl rankingService = RankingServiceImpl.getInstance();
    private PropertyServiceImpl propertyService = PropertyServiceImpl.getInstance();

    public Map<String, Object> findById(String id) {
        log.debug("Entered into findById method:");
        try {
            EmployeeCheckins empCheckIn = fenceService.retrieveCheckIn(id); // This to retrieve the checkin that was punched in by the mobile app
            if (empCheckIn != null) {
                log.info("Check In data time---->" + empCheckIn.getLogTime() + " Emp Id --> "
                        + empCheckIn.getEmployeeID() + " Site Id --> " + empCheckIn.getSiteID());
                LocateMe locateMe = new LocateMe();
                if (empCheckIn.getBeacons().isEmpty()) {
                    log.error(" No Beacons Found");
                    throw new Exception(" No Beacons found");
                }
                ;
                if (empCheckIn.getBeacons().size() > 0) {
                    // Review Comment Locate Me is unnecessary
                    locateMe.setBeacons(empCheckIn.getBeacons());
                }
                List<IOTData> visibleBeacons = new ArrayList<IOTData>();
                for (Beacon b : locateMe.getBeacons()) {
                    IOTData currentBeacon = new IOTData();
                    currentBeacon.setDistance(b.getDistance());
                    currentBeacon.setRssi(b.getRssi());
                    currentBeacon.setUuid(b.getUuid());
                    currentBeacon.setClientID(empCheckIn.getClientID());
                    currentBeacon.setCreatedAt(empCheckIn.getCreatedAt());
                    currentBeacon.setCreatedBy(empCheckIn.getCreatedBy());
                    currentBeacon.setEmployeeID(empCheckIn.getEmployeeID());
                    currentBeacon.setSiteID(empCheckIn.getSiteID());
                    currentBeacon.setLogTime(empCheckIn.getLogTime());

                    visibleBeacons.add(currentBeacon);
                }


                Map<String, Object> axisInfo = this.determineIOTFence(visibleBeacons);
                // Add Site ID
                // Add Client Id
                axisInfo.put("SITEID", empCheckIn.getSiteID());
                axisInfo.put("CLIENTID", empCheckIn.getClientID());
                axisInfo.put("EMPLOYEEID", empCheckIn.getEmployeeID());
                axisInfo.put("LOGTIME", empCheckIn.getLogTime());

                return axisInfo;

            } else {
                log.error("Unable to find checkin id" + id);
            }
        } catch (Exception e) {
            log.error("Failed" + e);
            log.error("Exception while retrieving user checkin " + id);
        }


        log.debug("Exiting from findById method:");
        return null;
    }


    /**
     * Method that processes the IOT data and retrieve the beacon information to rank them and arrive at a
     * conclusion on the user location within the premise
     *
     * @param visibleBeacons beacons that were visible
     */
    private Map<String, Object> determineIOTFence(List<IOTData> visibleBeacons) throws Exception {
        log.debug("Entered into receiveGeoFenceData method");
        HashMap<String, List<IOTData>> rankingMap = new HashMap();

        for (IOTData b : visibleBeacons) {
            List<IOTData> beaconsToRank = rankingMap.get(b.getUuid());
            if (FenceGlossaries.isObjectEmpty(beaconsToRank)) {
                beaconsToRank = new ArrayList<IOTData>();
                beaconsToRank.add(b);
                rankingMap.put(b.getUuid(), beaconsToRank);
            } else {
                beaconsToRank.add(b);
            }
        }
        List<BeaconMetaData> rankingContext = this.prepareContextForRanking(rankingMap);
        // Review comment This Loop is unneessary
        for (BeaconMetaData data : rankingContext) {
            log.debug("Identity " + data.getProperyIdentifer() + " Floor " + data.getFloorNumber() +
                    " RSSI " + data.getRssi() + " Count " + data.getSightingCount() + "" + data.getVariance());
        }

        this.rankingService.rankBeacons(rankingContext); //Initial ranking based on three parameters (RSSI, LINEofSight, Distance)
        for (BeaconMetaData data : rankingContext) {
            log.debug("Identity " + data.getProperyIdentifer() + " Variance " + data.getVariance()
                    + " Distance rank " + data.getDistanceRank() + " RSSI rank " + data.getSignalStrenthRank() + " No Of Occ "
                    + data.getLineOfSightRank());
        }

        this.rankingService.checkValidity(rankingContext);

        /**
         * Filter out beacons which do no show up more than 15 times
         */

        List<BeaconMetaData> inSightBeacons = rankingContext.stream().filter(u -> u.getSightingCount() >= 15).collect(Collectors.toList());


        Map<String, List<BeaconMetaData>> axisInfo = this.rankingService.buildAxesInfo(rankingContext);

        // build Return Map

        Map<String, Object> algMaps = new HashMap<String, Object>();

        algMaps.put("BEACONS", rankingContext);
        algMaps.put("AXISINFO", axisInfo);


        log.debug("Exiting from receiveGeoFenceData method");

        return algMaps;
    }


    /**
     * Method that prepares the context for ranking
     *
     * @param rankingMap
     */
    private List<BeaconMetaData> prepareContextForRanking(HashMap<String, List<IOTData>> rankingMap) {
        log.debug("Entered into prepareContextForRanking method: ");
        List<BeaconMetaData> bInfos = new ArrayList<>();

        for (Map.Entry<String, List<IOTData>> entry : rankingMap.entrySet()) {

            List<IOTData> iotData = entry.getValue();
            BeaconMetaData beaconInfo = new BeaconMetaData();
            beaconInfo.setBeaconUUID(entry.getKey());
            beaconInfo.setClientId(iotData.get(0).getClientID());
            beaconInfo.setEmpId(iotData.get(0).getEmployeeID());
            beaconInfo.setLogTime(iotData.get(0).getLogTime());
            beaconInfo.setSiteId(iotData.get(0).getSiteID());
            this.getDescriptiveStatistics(beaconInfo, iotData);
            this.getPropertyInfo(beaconInfo);
            // if (beaconInfo.getBeaconDistance() )
            // Avoid NAN on double. Happnes if only one beacon is sighted
            if (!Double.isNaN(beaconInfo.getBeaconDistance())) {
                log.debug(" Nan Deducted   .. BeaconUUID is " + beaconInfo.getBeaconUUID());
                bInfos.add(beaconInfo);
            }

        }
        log.debug("Exiting from prepareContextForRanking method ");
        return bInfos;
        //


    }

    /**
     * Method that retrieves property information
     *
     * @param beaconInfo
     */
    private void getPropertyInfo(BeaconMetaData beaconInfo) {
        log.debug("Entered into getPropertyInfo method: ");
        this.propertyService.updateAxisInfo(beaconInfo);
        log.debug("Exiting from getPropertyInfo method: ");
    }

    /**
     * Method to calcualte the percentile and get the median calculated for each beacon that
     * was visible to user's mobile
     *
     * @param beaconInfo
     * @param beaconTests
     */
    private void getDescriptiveStatistics(BeaconMetaData beaconInfo, List<IOTData> beaconTests) {
        log.debug("Entered into getDescriptiveStatistics method: ");
        // Start with RSSI
        int len = beaconTests.size();
        double[] rssis = new double[len];
        double[] distances = new double[len];
        int i = 0;
        for (IOTData bTest : beaconTests) {
            rssis[i] = bTest.getRssi();
            distances[i] = bTest.getDistance();
            i++;
        }
        DescriptiveStatistics desStatRssi = new DescriptiveStatistics(rssis);
        long rssi = (long) desStatRssi.getPercentile(50);
        log.debug("Variance -- " + beaconTests.get(0).getUuid() + " -- " + desStatRssi.getPopulationVariance());
        log.debug("Standard Deviation -- " + beaconTests.get(0).getUuid() + " -- " + desStatRssi.getStandardDeviation());
        DescriptiveStatistics desStatDistance = new DescriptiveStatistics(distances);
        double distance = desStatDistance.getPercentile(50);

        beaconInfo.setRssi(rssi);
        beaconInfo.setBeaconDistance(distance);
        beaconInfo.setStandardDeviation(desStatRssi.getStandardDeviation());
        beaconInfo.setVariance(desStatRssi.getVariance());
        beaconInfo.setSightingCount(len);

        log.debug("Exiting from getDescriptiveStatistics method: ");


    }
}
