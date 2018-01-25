package com.wrkspot.emp.fence.service.impl;

import com.wrkspot.emp.fence.model.Beacon;
import com.wrkspot.emp.fence.model.dto.Fence;
import com.wrkspot.emp.fence.model.dto.LocateMe;
import com.wrkspot.emp.fence.model.dto.UserLocation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public class LocationServiceImpl implements Serializable{

    private LocationServiceImpl() {
    }

    private static class SingletonHelper {
        private static final LocationServiceImpl INSTANCE = new LocationServiceImpl();
    }

    public static LocationServiceImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);


    public String locateMe(LocateMe userCheckin) throws Exception {
        log.debug("Entered into locateMe method:");
        LocateMe locateUser = userCheckin;
        List<Beacon> visibleBeacons = null;
        String userLocation = "Unable to locate, try again!";
        UserLocation yourLocation;
        List<Fence> reducedFenceInfo = new ArrayList<Fence>();
        List<Fence> reducedFenceInfoByMedian = new ArrayList<Fence>();
        List<Fence> reducedFenceInfoByOccurance = new ArrayList<Fence>();
        try {
            if (locateUser.getBeacons().size() > 0) {
                visibleBeacons = locateUser.getBeacons();
                Collections.sort(visibleBeacons, (o1, o2) -> o1.getRssi().compareTo(o2.getRssi()));
                Map<String, List<Beacon>> uniqueVisibleBeacons = new HashMap<String, List<Beacon>>();
                for (Beacon cur : visibleBeacons) {
                    List<Beacon> beaconAttributes = uniqueVisibleBeacons.get(cur.getUuid());
                    if (beaconAttributes != null) {
                        beaconAttributes.add(cur);
                    } else {
                        List<Beacon> beacons = new ArrayList<Beacon>();
                        uniqueVisibleBeacons.put(cur.getUuid(), beacons);
                    }

                }
                // Sorting each beacon ranges
                Iterator beacons = uniqueVisibleBeacons.entrySet().iterator();
                while (beacons.hasNext()) {
                    Entry<String, List<Beacon>> fenceBeacons = (Entry<String, List<Beacon>>) beacons.next();
                    // sort this list
                    String uuid = fenceBeacons.getKey();
                    List<Beacon> bea = fenceBeacons.getValue();
                    if (bea.size() > 1) { // Discarding the beacon if it occurs just once during 60 seconds of ranging
                        Collections.sort(bea, (o1, o2) -> o1.getRssi().compareTo(o2.getRssi()));
                        Fence userFence = traceuserLocation(uuid, bea);
                        reducedFenceInfoByMedian.add(userFence);
                    }
                }

                reducedFenceInfoByOccurance = reducedFenceInfoByMedian;

                Collections.sort(reducedFenceInfoByMedian, (o1, o2) -> o1.getMedian().compareTo(o2.getMedian()));
                Collections.sort(reducedFenceInfoByOccurance, (o1, o2) -> o1.getNoOfOccurrance().compareTo(o2.getNoOfOccurrance()));

                Collections.reverse(reducedFenceInfoByMedian);

                /*
                 * 1. Low RSSI and high number of hits then that the user
                 * location 2.
                 */

                /*
                 * Collections.sort(reducedFenceInfo, new
                 * UserLocationChainedComparator( new OccuranceComparator(),new
                 * MedianComparator()
                 *
                 * ));
                 */
                for (Fence f : reducedFenceInfoByMedian) {
                    System.out.println(f.getName() + " --> " + f.getUuid() + " --> " + f.getMedian() + " --> "
                            + f.getNoOfOccurrance());
                }
                System.out.println("--------------------------");
                for (Fence f : reducedFenceInfoByOccurance) {
                    System.out.println(f.getName() + " --> " + f.getUuid() + " --> " + f.getMedian() + " --> "
                            + f.getNoOfOccurrance());
                }
                String highestRecordedBeacon = "Loc1";
                String closestRecordedBeacon = "Loc2";
                int beaconSize = reducedFenceInfoByMedian.size();
                if (beaconSize > 1) {
                    determine:
                    {
                        for (Fence aReducedFenceInfoByMedian : reducedFenceInfoByMedian) {

                            for (int j = 0; j < beaconSize; j++) {
                                if ((aReducedFenceInfoByMedian.getUuid()
                                        .equals(reducedFenceInfoByOccurance.get(j).getUuid()))) {
                                    highestRecordedBeacon = aReducedFenceInfoByMedian.getUuid();
                                    closestRecordedBeacon = aReducedFenceInfoByMedian.getUuid();
                                    break determine;
                                } else if (aReducedFenceInfoByMedian.getMedian() > reducedFenceInfoByOccurance
                                        .get(j).getMedian()) {

                                    closestRecordedBeacon = aReducedFenceInfoByMedian.getUuid();
                                    return closestRecordedBeacon;
                                }

                            }
                        }
                    }
                    return closestRecordedBeacon;
                } else {
                    closestRecordedBeacon = reducedFenceInfoByMedian.get(0).getUuid();
                    return closestRecordedBeacon;
                }

            } else {

                log.debug("Exiting from locateMe method");
                return null;
            }
        } catch (Exception e) {
            log.error("Exiting from locateMe method");
        }
        return null;

    }

    /**
     * Method to trace user location based on the RSSI received from each beacon
     * If beacon was visible just one time, then the median is the same value.
     * If beacon was visible twice then the average is considered as median value.
     * If beacon was visible more than twice then the median is calculated apache commons library
     * For each beacon this calculation is performed and median is calculated. Lowest median value represents
     * user's closest proximity and also in future floor plan also will attribute to user location
     * determination.
     *
     * @param UUID
     * @param b
     * @return
     */
    private Fence traceuserLocation(String UUID, List<Beacon> b) {
        // Calculate RSSI MEDIAN & no of uuid occurance
        Fence usrLocBsdCurBecn = new Fence();
        String beaconName = "";
        int beaconSize = b.size();
        System.out.println("Beacon UUID == " + UUID + " Size == " + beaconSize);
        try {
            if (beaconSize > 2) {
                usrLocBsdCurBecn.setNoOfOccurrance(beaconSize);
                double[] rssiValues = new double[beaconSize];
                double[] distanceValues = new double[beaconSize];
                int i = 0;
                for (Beacon bb : b) {
                    rssiValues[i] = bb.getRssi();
                    //beaconName = bb.getName();
                    distanceValues[i] = bb.getDistance();
                    i++;
                }
                DescriptiveStatistics stats = new DescriptiveStatistics(rssiValues);
                Integer median = (int) stats.getPercentile(50);
                DescriptiveStatistics distanceStats = new DescriptiveStatistics(distanceValues);
                Double dist = distanceStats.getPercentile(50);
                System.out.println("-- Median for " + UUID + "  =  " + median + " -- beacon name is = " + beaconName
                        + " -- No of occurance" + b.size() + "--  Distance Median" + dist);
                usrLocBsdCurBecn.setMedian(median);
                usrLocBsdCurBecn.setUuid(UUID);
                usrLocBsdCurBecn.setName(beaconName);
                return usrLocBsdCurBecn;

            } else if (beaconSize == 2) {
                usrLocBsdCurBecn.setMedian((b.get(0).getRssi() + b.get(1).getRssi()) / 2);
                usrLocBsdCurBecn.setUuid(UUID);
                //usrLocBsdCurBecn.setName(b.get(0).getName());
                usrLocBsdCurBecn.setNoOfOccurrance(beaconSize);

                return usrLocBsdCurBecn;
            } else {
                usrLocBsdCurBecn.setMedian((b.get(0).getRssi()));
                usrLocBsdCurBecn.setUuid(UUID);
                //usrLocBsdCurBecn.setName(b.get(0).getName());
                usrLocBsdCurBecn.setNoOfOccurrance(beaconSize);
                return usrLocBsdCurBecn;

            }
        } catch (Exception e) {
            log.error("Exception while calculating median for --" + UUID);
            log.error("Exception  --" + e);

        }
        return null;
    }

}
