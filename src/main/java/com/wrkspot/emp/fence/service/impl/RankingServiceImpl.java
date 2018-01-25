package com.wrkspot.emp.fence.service.impl;

import com.wrkspot.emp.fence.FenceService;
import com.wrkspot.emp.fence.model.IOTData;
import com.wrkspot.emp.fence.model.PropertyLayout;
import com.wrkspot.emp.fence.model.UserFence2;
import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.dto.FinalLocation;
import com.wrkspot.emp.fence.model.dto.LocateMeAxis;
import com.wrkspot.emp.fence.model.dto.UserGrid;
import com.wrkspot.emp.fence.util.FenceGlossaries;
import com.wrkspot.emp.fence.util.FinalScoreComparator;
import com.wrkspot.emp.fence.util.LocateMeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


public class RankingServiceImpl implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(RankingServiceImpl.class);

    private RankingServiceImpl() {
    }

    private static class SingletonHelper {
        private static final RankingServiceImpl INSTANCE = new RankingServiceImpl();
    }

    public static RankingServiceImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    FenceService fenceDao= FenceService.getInstance();

    /**
     * Method that ranks the beacon based on the v2 of the Ranking algorithm
     *
     * @param beaconInfoList list thats received from the users checkin
     */
    public void rankBeacons(List<BeaconMetaData> beaconInfoList) {
        log.debug("Entered into rankBeacons method:");
        rankByLineOfSight(beaconInfoList);
        rankByDistance(beaconInfoList);
        rankBySignalStrength(beaconInfoList);
        log.debug("Exiting from rankBeacons method");

    }

    /**
     * Method that ranks the beacon based on the signal strength
     *
     * @param beaconInfoList beaconInfoList
     */
    private void rankBySignalStrength(List<BeaconMetaData> beaconInfoList) {
        log.debug("Entered into rankBySignalStrength method: ");
        beaconInfoList.forEach(beaconInfo -> {
            if (beaconInfo.getRssi() >= -78) {
                beaconInfo.setSignalStrenthRank(3);
            } else if (beaconInfo.getRssi() < -78 && beaconInfo.getRssi() >= -95) {
                beaconInfo.setSignalStrenthRank(2);
            } else {
                beaconInfo.setSignalStrenthRank(1);
            }
        });
        log.debug("Exiting from rankBySignalStrength method ");

    }

    /**
     * Method that ranks the beacon based on the distance
     *
     * @param beaconInfoList beaconInfoList
     */
    private void rankByDistance(List<BeaconMetaData> beaconInfoList) {
        log.debug("Entered into rankByDistance method: ");
        beaconInfoList.forEach(beaconInfo -> {
            if (beaconInfo.getBeaconDistance() <= 1.5) {
                beaconInfo.setDistanceRank(3);
            } else if (beaconInfo.getBeaconDistance() > 1.5 && beaconInfo.getBeaconDistance() < 2.5) {
                beaconInfo.setDistanceRank(2);
            } else {
                beaconInfo.setDistanceRank(1);
            }

        });

        log.debug("Exiting from rankByDistance method ");
    }

    /**
     * Method that ranks the beacon based on number of times the beacon was visible
     *
     * @param beaconInfoList beaconInfoList
     */
    private void rankByLineOfSight(List<BeaconMetaData> beaconInfoList) {
        log.debug("Entered into rankByLineOfSight method: ");

        beaconInfoList.forEach(beaconInfo -> {
            if (beaconInfo.getSightingCount() <= 20) {
                beaconInfo.setLineOfSightRank(0);
            } else if (beaconInfo.getSightingCount() > 20 && beaconInfo.getSightingCount() <= 40) {
                beaconInfo.setLineOfSightRank(1);
            } else if (beaconInfo.getSightingCount() > 40 && beaconInfo.getSightingCount() <= 54) {
                beaconInfo.setLineOfSightRank(2);
            } else {
                beaconInfo.setLineOfSightRank(3);
            }


        });

        log.debug("Exiting from rankByLineOfSight method ");
    }


    /**
     * Method to check various attributes on the beacon list
     *
     * @param rankingContext ranking Context
     */
    void checkValidity(List<BeaconMetaData> rankingContext) throws Exception {
        log.debug("Entered into checkValidity method");

        List<BeaconMetaData> beaconInfos = rankingContext.stream().filter(ab -> ((FenceGlossaries.isObjectEmpty(ab.getAxis()) || FenceGlossaries.isObjectEmpty(ab.getAxisPos()) || FenceGlossaries.isObjectEmpty(ab.getBeaconRank()) || FenceGlossaries.isObjectEmpty(ab.getProperyIdentifer())))).collect(Collectors.toList());

        if (beaconInfos.size() > 0) {
            log.error(" Issues with beacon Info . Information not found ");
            log.error(beaconInfos.toString());
            throw new Exception(" Cannot Proceed as Meta data not correctly set");
            //throw new Exception(); //@Todo need to throw a new exception that initiates debugging
        }

        log.debug("Entered into checkValidity method");


    }

    /**
     * Method to build axis for each beacon that is visible to the user
     *
     * @param rankingContext rankingContext
     * @return axisBeaconMap axisBeaconMap
     */
    public Map<String, Map<String, List<BeaconMetaData>>> buildAxesonRating(List<BeaconMetaData> rankingContext) {
        log.debug("Entered into method buildAxes: ");
        HashMap<String, Map<String, List<BeaconMetaData>>> axisBeaconMap = new HashMap();
        rankingContext.stream().

                forEach(
                        ab ->

                        {
                            HashMap axisMap = (HashMap) axisBeaconMap.get(ab.getBeaconRating());
                            if (FenceGlossaries.isObjectEmpty(axisMap)) {
                                // put rating and add axis
                                axisMap = new HashMap();
                                List<BeaconMetaData> axisList = new ArrayList<BeaconMetaData>();
                                axisList.add(ab);
                                axisMap.put(ab.getAxis(), axisList);
                                axisBeaconMap.put(ab.getBeaconRating(), axisMap);
                            } else {
                                // Rating is available
                                // get Axis information
                                //axisBeaconMap.entrySet().stream().filter(ta ->(ta.getKey() == ab.getAxis())).;
                                List axisInfo = (List) axisMap.get(ab.getAxis());
                                if (FenceGlossaries.isObjectEmpty(axisInfo)) {
                                    // Create Axis
                                    List<BeaconMetaData> axisList1 = new ArrayList<BeaconMetaData>();
                                    axisList1.add(ab);

                                    axisMap.put(ab.getAxis(), axisList1);

                                } else {
                                    axisInfo.add(ab);
                                }

                            }
                        }
                );
        log.debug("Exiting from method buildAxes: ");
        return axisBeaconMap;

    }


    /**
     * Returns the Axis and List of BeaconMetaData
     * @param rankingContext
     * @return
     * @throws Exception
     */


    public Map<String, List<BeaconMetaData>> buildAxesInfo(List<BeaconMetaData> rankingContext) throws Exception

    {
     //   Map<String, Book> result = books.stream() .collect(Collectors.toMap(book -> book.getISBN, book -> book));

       // Read more: http://javarevisited.blogspot.com/2016/04/10-examples-of-converting-list-to-map.html#ixzz4qNYYsny3
       // Bug Java8 Streams does not like duplicate keys
        //Map alg3Metadata = rankingContext.stream().collect(Collectors.toMap(beacon -> beacon.getAxis(),beacon->beacon));

        HashMap<String,List<BeaconMetaData>> alg3Metadata = new HashMap<>();

        /**
         * Added by Murali to feed out beacons not sighted for more than 15 times
         */

        for(BeaconMetaData beaconMetaData : rankingContext)
        {
            List<BeaconMetaData> beaconMetaDataList = alg3Metadata.get(beaconMetaData.getAxis());

            if (FenceGlossaries.isObjectEmpty(beaconMetaDataList))
            {
                List<BeaconMetaData> bList = new ArrayList<>();
                bList.add(beaconMetaData);
                alg3Metadata.put(beaconMetaData.getAxis(),bList);
            }
            else
            {
                beaconMetaDataList.add(beaconMetaData);
            }
        }

        return alg3Metadata;
    }

    public List<LocateMeAxis> processAxes(Map<String, Map<String, List<BeaconMetaData>>> processMap, List<BeaconMetaData> originalBeaconList) {

        log.debug("Entered into processAxes method: ");
        // Assess Quality of Hight
        //   Map highMap(<String,List<BeaconInfo>>) = (Map) processMap.get("HIGH");

        //HashMap<String,List<BeaconInfo>> highMap  = (HashMap<String, List<BeaconInfo>>) processMap.get("HIGH");

        // for (Map.Entry<String, List<BeaconInfo>> entry : highMap.entrySet()) {

        List<LocateMeAxis> locateMeAxes = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<BeaconMetaData>>> entryMap : processMap.entrySet()) {
            String axisType = entryMap.getKey();

            Map<String, List<BeaconMetaData>> entryMaps = entryMap.getValue();
            for (Map.Entry<String, List<BeaconMetaData>> entry : entryMaps.entrySet()) {
                LocateMeAxis locateMeAxis = new LocateMeAxis();
                locateMeAxis.setAxisRanking(axisType);
                this.setAxisConfidence(locateMeAxis);


                locateMeAxis.setAxisName(entry.getKey());

                List<BeaconMetaData> listBeaconInfos = entry.getValue();
                this.getAxisMatchCount(locateMeAxis, entry.getKey(), listBeaconInfos);

                // Sort by Ranking

                Collections.sort(listBeaconInfos, Comparator.comparing(BeaconMetaData::getBeaconRank));

                //listBeaconInfos.stream().forEach(ab -> {
                for (BeaconMetaData ab : listBeaconInfos) {

                    UserGrid locMe = new UserGrid();
                    locMe.setPropertyIdentifier(ab.getProperyIdentifer());
                    locMe.setFloorNumber((ab.getFloorNumber()));

                    this.setConfidence(locMe, ab);
                    this.handleProximity(locMe, ab, listBeaconInfos);
                    this.handleOppositeAndBackwardRooms(locMe, ab, originalBeaconList);
                    this.generateLocateMeScore(locMe);

                    if (FenceGlossaries.isObjectEmpty(locateMeAxis.getLocateMes())) {
                        List<UserGrid> listLocateMe = new ArrayList<UserGrid>();

                        listLocateMe.add(locMe);
                        locateMeAxis.setLocateMes(listLocateMe);

                    } else {
                        locateMeAxis.getLocateMes().add(locMe);
                    }

                }


                this.generateLocatemAxisScore(locateMeAxis);
                locateMeAxes.add(locateMeAxis);

            }

        }
        log.debug("Exiting from processAxes method: ");
        return locateMeAxes;


    }

    /**
     * Method to generate Locate Axis Score
     *
     * @param locateMeAxis locateMeAxis
     */
    private void generateLocatemAxisScore(LocateMeAxis locateMeAxis) {

        UserGrid locatememax1 = (UserGrid) locateMeAxis.getLocateMes().stream().max(new LocateMeComparator()).get();
        long finalscore = (locateMeAxis.getConfidence() + locatememax1.getFinalScore());
        locateMeAxis.setFinalScore(finalscore);


    }

    /**
     * Method that calc
     *
     * @param locMe
     */
    private void generateLocateMeScore(UserGrid locMe) {
        locMe.setFinalScore(locMe.getConfidence() + locMe.getAdjacentConfidence());
    }

    /**
     * Handle handleOppositeAndBackwardRooms
     *
     * @param locateMe
     * @param beaconInfo
     * @param originalBeaconInfos
     */
    private void handleOppositeAndBackwardRooms(UserGrid locateMe, BeaconMetaData beaconInfo, List<BeaconMetaData> originalBeaconInfos) {

        long bonusPoints = 3;
        // Look for Front and Top
        // Get property identifier and look for Top and front and down rooms
        log.debug("handleFrontandback --> " + beaconInfo.getProperyIdentifer());
        PropertyLayout property = fenceDao.retrieveProperty(beaconInfo.getProperyIdentifer());


        if (property != null) {

            String frontProperty = property.getPropertyFront();

            BeaconMetaData beaconInfo1 = originalBeaconInfos.stream().filter(ab -> (frontProperty.equals(ab.getProperyIdentifer()))).findFirst().orElse(null);

            if (FenceGlossaries.isObjectEmpty(beaconInfo1)) {
                bonusPoints = bonusPoints - 1;
            }

            String topProperty = property.getPropertyTop();

            beaconInfo1 = originalBeaconInfos.stream().filter(ab -> (topProperty.equals(ab.getProperyIdentifer()))).findFirst().orElse(null);

            if (FenceGlossaries.isObjectEmpty(beaconInfo1)) {
                bonusPoints = bonusPoints - 1;
            }

        }


        //@TODO add bottom property

//       beaconInfo1 = originalBeaconInfos.stream().filter(ab -> (StringUtils.equals(topProperty,ab.getProperyIdentifer()))).findFirst().orElse(null);
//
//       if (FenceGlossaries.isObjectEmpty(beaconInfo1))
//       {
//           bonusPoints = bonusPoints - 1;
//       }


    }

    private void handleProximity(UserGrid locateMe, BeaconMetaData beaconInfo, List<BeaconMetaData> beaconInfos) {


        long adjacentConfidence = 3;

        long beaconPos = beaconInfo.getAxisPos();

        if (beaconPos == 1) {
            // Start with One
            adjacentConfidence = 1;
            long chkPosition1 = beaconPos + 1;


            BeaconMetaData adjacentBeacon = beaconInfos.stream().filter(ab -> (ab.getAxisPos() == chkPosition1)).findAny().orElse(null);

            if (FenceGlossaries.isObjectEmpty(adjacentBeacon)) {
                // reduce confidence by 1
                adjacentConfidence = adjacentConfidence - 1;
            }

            locateMe.setAdjacentConfidence(adjacentConfidence);

        } else {

            long chkPosition = beaconPos - 1;
            BeaconMetaData adjacentBeacon = beaconInfos.stream().filter(ab -> (ab.getAxisPos() == chkPosition)).findAny().orElse(null);

            if (FenceGlossaries.isObjectEmpty(adjacentBeacon)) {
                // reduce confidence by 1
                adjacentConfidence = adjacentConfidence - 1;

            }

            long chkPosition1 = beaconPos + 1;


            adjacentBeacon = beaconInfos.stream().filter(ab -> (ab.getAxisPos() == chkPosition1)).findAny().orElse(null);

            if (FenceGlossaries.isObjectEmpty(adjacentBeacon)) {
                // reduce confidence by 1
                adjacentConfidence = adjacentConfidence - 1;
            }

            locateMe.setAdjacentConfidence(adjacentConfidence);
        }


    }

    /**
     * Method to set the confidence
     *
     * @param locateme locateme
     * @param bInfo    bInfo
     */
    private void setConfidence(UserGrid locateme, BeaconMetaData bInfo) {
        log.debug("Entered into setConfidence method: ");

        long beaconRank = bInfo.getSignalStrenthRank() + bInfo.getDistanceRank();
        long sigthtRank = bInfo.getLineOfSightRank(); // @Todo Include this if the ranking is not better


        if (beaconRank == 6) {
            locateme.setConfidence(5);
        } else if (beaconRank == 5) {
            locateme.setConfidence(4);
        } else if (beaconRank == 4) {
            locateme.setConfidence(3);
        } else if (beaconRank == 3) {
            locateme.setConfidence(2);
        } else {
            locateme.setConfidence(1);
        }

        log.debug("Entered into setConfidence method: ");

    }

    private void setAxisConfidence(LocateMeAxis locateMeAxis) {


        if (locateMeAxis.getAxisRanking().equals("HIGH")) {
            locateMeAxis.setConfidence(3);
        }
        if (locateMeAxis.getAxisRanking().equals("MEDIUM")) {
            locateMeAxis.setConfidence(2);
        }
        if (locateMeAxis.getAxisRanking().equals("LOW")) {
            locateMeAxis.setConfidence(1);
        }


    }

    /**
     * This method gives the count of beacons visible in the axis . It is a score of the axis . More beacons visible means more
     * better location based
     *
     * @param beaconInfoList
     */
    public void getAxisMatchCount(LocateMeAxis locateMeAxis, String axisName, List<BeaconMetaData> beaconInfoList) {
        // Get the PropertyRelationship and see how may counts are matching
        long axisCount = 0;
        List<PropertyLayout> propRels = fenceDao.retrivePropertyInfo(beaconInfoList.get(0).getSiteId(), axisName);
        for (PropertyLayout propRel : propRels) {
            long axisPosition = propRel.getAxisPos();
            BeaconMetaData bKinfo = beaconInfoList.stream().filter(ab -> (ab.getAxisPos() == axisPosition)).findFirst().orElse(null);
            if (FenceGlossaries.isObjectEmpty(bKinfo)) {
                axisCount++;
            }
        }
        locateMeAxis.setAxisMatchCount(axisCount);
    }

    /**
     * Method to makeFinalJudgement  method
     *
     * @param locateMeAxes   locateMeAxes
     * @param visibleBeacons visibleBeacons
     */
    public void makeFinalJudgement(List<LocateMeAxis> locateMeAxes, List<IOTData> visibleBeacons) {


        log.debug("Entered into makeFinalJudgement method: ");
        LocateMeAxis finalAxis = (LocateMeAxis) locateMeAxes.stream().max(new FinalScoreComparator()).get();
        // get best location in axes
        //@TODO handle conflict
        UserGrid locateMe = (UserGrid) finalAxis.getLocateMes().stream().max(new LocateMeComparator()).get();
        List<UserGrid> finalLocations = finalAxis.getLocateMes();
        long predictedFloorNumber = locateMe.getFloorNumber();

        Map<Long, Long> counted = finalLocations.stream()
                .collect(Collectors.groupingBy(UserGrid::getFloorNumber, Collectors.counting()));

        Long floorConf = counted.get(predictedFloorNumber);
        System.out.println("Floor confidence " + floorConf);

        FinalLocation finalLocation = new FinalLocation();
        finalLocation.setPropertyIdentifier(locateMe.getPropertyIdentifier());
        List<LocateMeAxis> locateMeAxes1 = new ArrayList<>();
        locateMeAxes1.addAll(locateMeAxes);
        finalLocation.setLocateMeAxes(locateMeAxes1);
        finalLocation.setCreatedDate(new Date());
        double finalConfidence = (((double) finalAxis.getFinalScore() / 11) * 100);
        finalLocation.setPercentageConfidence(finalConfidence);
        this.fenceDao.savePredictedLocated(finalLocation);


        UserFence2 userMovements = new UserFence2();
        userMovements.setClientID("");
        userMovements.setSiteID(visibleBeacons.get(0).getSiteID());
        userMovements.setEmployeeID(visibleBeacons.get(0).getEmployeeID());
        userMovements.setTimestamp(visibleBeacons.get(0).getLogTime());
        userMovements.setConfidencePercentage(finalLocation.getPercentageConfidence());
        userMovements.setRoom_identifier(finalLocation.getPropertyIdentifier());
        this.fenceDao.saveUserMovements2(userMovements);
        log.debug("Exiting from makeFinalJudgement method: ");


    }

    /**
     *  Sorts the Axes, the High Low rating based axes is removed
     * @param rankingContext
     * @return
     * @throws Exception
     */
    public Map<String,List<BeaconMetaData>> buildAxeswithoutRating(List<BeaconMetaData> rankingContext) throws Exception
    {
            HashMap<String,List<BeaconMetaData>>  axesMap = new HashMap();

        for(BeaconMetaData metaData : rankingContext)
        {
            String axes = metaData.getAxis();

            List<BeaconMetaData> beaconMetaDatas = axesMap.get(axes);

            if (FenceGlossaries.isObjectEmpty(beaconMetaDatas))
            {
                List<BeaconMetaData> b1List = new ArrayList<BeaconMetaData>();
                b1List.add(metaData);
                axesMap.put(axes,b1List);
            }
            else
            {
                beaconMetaDatas.add(metaData);
            }
        }

        return axesMap;
    }



}