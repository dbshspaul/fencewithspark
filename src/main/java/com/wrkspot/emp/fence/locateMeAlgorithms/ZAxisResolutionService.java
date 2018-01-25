package com.wrkspot.emp.fence.locateMeAlgorithms;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.iot.AxisSelection;
import com.wrkspot.emp.fence.model.iot.Location;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeLocation;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeSiteConfig;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeZoneInfo;
import com.wrkspot.emp.fence.util.AxisSelectionDistanceComparator;
import com.wrkspot.emp.fence.util.BeaconMetaDataCombinedComparator;
import com.wrkspot.emp.fence.util.FenceGlossaries;
import com.wrkspot.emp.fence.util.LocationComparatorOnDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Service will resolve the Z Axis . In case of close proximity the the service
 *
 */

public class ZAxisResolutionService  implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ZAxisResolutionService.class);

    private ZAxisResolutionService() {
    }

    private static class SingletonHelper {
        private static final ZAxisResolutionService INSTANCE = new ZAxisResolutionService();
    }

    public static ZAxisResolutionService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private RealTimeLocationBuilder realTimeLocationBuilder=RealTimeLocationBuilder.getInstance();
    private AxisSelectionService axisSelectionService=AxisSelectionService.getInstance();


    public List<AxisSelection> performAxisSelection(Map<String,List<BeaconMetaData>> rankingContext, RealTimeSiteConfig siteConfig) throws Exception
    {

        log.debug(" Received Beacon Meta Data in Z Axis Service");
        List<AxisSelection> axisSelectionList = new ArrayList();
        log.debug("Looping through Beacons based on Axes in Z Axis Service ");

        List<AxisSelection> axisSelections = new ArrayList<>();
        for (Map.Entry<String, List<BeaconMetaData>> entry : rankingContext.entrySet()) {


            log.debug(" Processing Axis " + entry.getKey());
              log.debug(" Going to build Preferred Location Which Is Algorithm one");
             AxisSelection axisSelection =  realTimeLocationBuilder.buildPreferredLocation(entry.getKey(),entry.getValue(), siteConfig);

             if (! FenceGlossaries.isObjectEmpty(axisSelection.getBeaconMetaDataList())) {

                 if (axisSelection.getBeaconMetaDataList().size() > 1) {
                     /**
                      * Start Processing the Axis
                      */

                     List<BeaconMetaData> selectedList = axisSelection.getBeaconMetaDataList().subList(1, axisSelection.getSelectionCount());

                     for (BeaconMetaData beaconMetaData : selectedList) {
                         log.debug(" Processing Beacons for Axis " + entry.getKey());
                         log.debug(" Processing Beacon " + beaconMetaData.getBeaconIdentifier());
                         log.debug(" Beacon in Room .." + beaconMetaData.getProperyIdentifer());
                         // check if new location needs to be created

                         boolean chkNew = axisSelectionService.checkIfPossibleLocationWithPrecision(axisSelection, beaconMetaData, siteConfig);

                         if (chkNew) {


                             axisSelectionService.createNewLocation(axisSelection, beaconMetaData);
                         } else {
                             // bug if Axis has no locations
                             List<Location> zoneLocations = axisSelection.getZoneLocations();


                             if (!FenceGlossaries.isObjectEmpty(axisSelection.getZoneLocations())) {
                                 for (Location loc2 : axisSelection.getZoneLocations()) {
                                     axisSelectionService.calucateAdjacentScore(loc2, beaconMetaData);
                                 }
                             }
                         }
                         log.debug(" Processing done for Beacon " + beaconMetaData.getBeaconIdentifier());
                     }
                     axisSelectionService.determineLocoationAdjacentScore(axisSelection);
                     axisSelections.add(axisSelection);
                 } else {
                     axisSelections.add(axisSelection);
                 }

                 log.debug(" Completed Processing for Axis " + entry.getKey());
                 this.determineAxesLocationCeilingDeployment(axisSelection, siteConfig);
             }

        }




            return axisSelections;
    }

    // This method gives a broad selection of Axes . May be removed
    public List<AxisSelection> selectAxesbyDistance(List<AxisSelection> selections, RealTimeSiteConfig siteConfig) throws Exception
    {
        /**
         * Resolve based on distance
         * Pick the axes based on distance
         */

        // Sort the axes by distance
        // Start resolving conflicts
        //

        log.debug(" In Select Axes by Distance ");

        selections.sort(new AxisSelectionDistanceComparator());

            double lprevDistance = -9;

            List<AxisSelection> selectedaxes = new ArrayList<>();

            for ( AxisSelection axisSelection : selections)
            {
                double currDistance = axisSelection.getAxisDistance();
                if (lprevDistance == -9)
                {
                    lprevDistance = axisSelection.getAxisDistance();
                }
                else
                {
                    double diff = currDistance - lprevDistance;

                    if ( diff > siteConfig.getAxisDistanceComp())
                    {
                        break;
                    }

                }
                selectedaxes.add(axisSelection);

                lprevDistance = axisSelection.getAxisDistance();
            }

            log.debug(" Axes Selected ");
            return selectedaxes;
        }



        public void determineAxesLocationCeilingDeployment(AxisSelection axisSelection,  RealTimeSiteConfig siteConfig) throws Exception
        {

            if (! FenceGlossaries.isObjectEmpty(axisSelection.getZoneLocations())) {
                if (axisSelection.getZoneLocations().size() > 1) {
                    // It is a zone situation
                    // The is a room zone location logic
                    // This cannot be left like this. Needs to be handled
                    axisSelection.getZoneLocations().sort(new LocationComparatorOnDistance());

                    double lprevDistance = axisSelection.getZoneLocations().get(0).getBeaconMetaData().getBeaconDistance();
                    double lprevPosition = axisSelection.getZoneLocations().get(0).getBeaconMetaData().getAxisPos();

                    Location finalLocation = axisSelection.getZoneLocations().get(0);


                    List<Location> finalLocationList = new ArrayList<>();


                    for (Location loc1 : axisSelection.getZoneLocations().subList(1, axisSelection.getZoneLocations().size())) {
                        double ldiff = java.lang.Math.abs(lprevDistance - loc1.getBeaconMetaData().getBeaconDistance());

                        if (ldiff > siteConfig.getAxesRoomProximity()) {
                            break;
                        }

                        finalLocationList.add(loc1);


                    }

                    axisSelection.setFinalLocation(finalLocation);
                    axisSelection.setFinalZoneLocations(finalLocationList);

                } else {
                    axisSelection.setFinalLocation(axisSelection.getZoneLocations().get(0));

                }

            }

        }

        // Name does both Z Axis Resolution and required resolution
        public boolean checkifZaxisresolutionrequired(AxisSelection primary , AxisSelection secondary, RealTimeSiteConfig siteConfig) throws Exception
        {

            log.debug(" In the Perform Resolution Z Axis ");
            double ldiff = java.lang.Math.abs(primary.getAxisDistance() - secondary.getAxisDistance());
            if ( !(primary.getAxisFloor()==secondary.getAxisFloor()) )
            {


                  if ( ldiff < siteConfig.getzAxisDistance() )
                  {
                      return true;
                  }
            }
            else
            {
                // Same Floor
                if ( ldiff < siteConfig.getSameFloorComp())
                {
                    return true;
                }
            }

            return false;
        }




        public List<AxisSelection> applyAlgorithm7(List<AxisSelection>  axisSelectionList,  RealTimeSiteConfig siteConfig) throws Exception
        {
            // Sort axes based on distance

            log.debug(" In Apply Algorithm 7 Method");
            log.debug(" Going to resolve for axes and a fine list");

            axisSelectionList.sort(new AxisSelectionDistanceComparator());

            List<AxisSelection> finalList = new ArrayList();

            AxisSelection primary = axisSelectionList.get(0);

            finalList.add(primary);

            if (axisSelectionList.size() > 1 )

            {
                for (AxisSelection axisSelection : axisSelectionList.subList(1, axisSelectionList.size())) {
                    // Check Curent in proximity


                    if (primary.getProximityScore() == 2 && axisSelection.getProximityScore() == 2) {
                        // Both in Proximity
                        // Check if z Axis needs to be applied
                        this.applyProximityZAxis(primary, axisSelection, finalList);

                        finalList.sort(new AxisSelectionDistanceComparator());

                        primary = finalList.get(0);

                    } else {


                       // if (primary.getProximityScore() == 2 && axisSelection.getProximityScore() != 1) {

                        if (primary.getProximityScore() == 2 && axisSelection.getProximityScore() == 1) {
                            // Stop
                            break;


                        }
                        // check if Secondary needs to be considered
                        boolean chk = this.checkifZaxisresolutionrequired(primary, axisSelection, siteConfig);
                        if (chk) {
                            finalList.add(axisSelection);
                        }
                    }


                }
            }

            return  finalList;

        }


        // Watch for Bugs in the method

        public void applyProximityZAxis(AxisSelection primary, AxisSelection secondary, List<AxisSelection> axes) throws Exception
        {

            log.debug(" Apply Proximity for X Asis when axes are im Proximity");
            if( ! (primary.getAxisFloor()== secondary.getAxisFloor()))
            {
                log.debug(" Z Axis Detected");
                long pFloor = Long.valueOf(primary.getAxisFloor());

                long sFloor = Long.valueOf(secondary.getAxisFloor());

                log.debug(" Primary Floor is " + pFloor);
                log.debug(" Secondary Floor is " + sFloor
                );
                if (sFloor > pFloor)
                {
                    log.debug(" Primary Secondary Swap is happening ");
                    // remove Primry and put Secondary
                    // Need to implement a line concept
                    axes.remove(primary);
                    axes.add(secondary);
                }
            }
            else
            {

                log.debug(" This situtation should not occur ");
                axes.add(secondary);
            }
        }

        public void  makeFinalDecision(RealTimeLocation realTimeLocation, List<AxisSelection> selectedList, RealTimeSiteConfig siteConfig) throws  Exception
        {
            /**
             * Final Decision should bring Floor Confidence , Room Confidence , Premise Confidence
             */
            /**
             * Two Sceanrios more than one axis , or single axis
             */

            selectedList.sort(new AxisSelectionDistanceComparator());
            List<AxisSelection> finalAxisSelection = new ArrayList<>();
            finalAxisSelection.add(selectedList.get(0));

            if (selectedList.size() > 1)
            {
                double lprevDistance = selectedList.get(0).getAxisDistance();

                for ( AxisSelection axisSelection : selectedList.subList(1,selectedList.size()))
                {
                    double ldiff = java.lang.Math.abs(lprevDistance - axisSelection.getAxisDistance());

                    if ( ldiff > siteConfig.getAxisFinalComp())
                    {
                        break;
                    }

                    finalAxisSelection.add(axisSelection);

                }
            }



        }

        public void  handleFinalList(RealTimeLocation realTimeLocation, AxisSelection axisSelection) throws Exception
        {
            if (! FenceGlossaries.isObjectEmpty(axisSelection.getFinalLocation()))
            {
                // in Proximity
                log.debug(" Axis has Final Location Set ");
                if (! FenceGlossaries.isObjectEmpty(realTimeLocation.getRealTimeRoomInfo()))
                {
                    log.debug(" Room already Set . Error Condition");

                    this.handleRoomLocationAlreadyeSet(realTimeLocation,axisSelection);
                }
                else
                {
                    log.debug(" Create Room based on Location ");
                    this.realTimeLocationBuilder.buildRealTimeRoomInfo(realTimeLocation,axisSelection.getFinalLocation().getBeaconMetaData());
                }
            }
            if(! FenceGlossaries.isObjectEmpty(axisSelection.getZoneLocations()))
            {
                log.debug(" Zone Locations found .. ");
                this.handleAxesAddLocationsAsZone(realTimeLocation,axisSelection);
            }

            if (FenceGlossaries.isObjectEmpty(axisSelection.getZoneLocations()))
            {
                // Not in Proximity
                //
                this.handleZoneCondition(realTimeLocation,axisSelection);

            }
        }

        public void handleRoomLocationAlreadyeSet(RealTimeLocation realTimeLocation , AxisSelection axisSelection) throws Exception
        {
            log.debug(" Calculation has not worked ");
            log.error(" Calculation has not worked .. Adding this as a zone .. Revisit Calculation");
            realTimeLocation.setAlgorithmError(true);
            realTimeLocation.setAlgorithmErrorComments(" Two proximity axis detected. Please reviewith Algorithm with Check idn " + realTimeLocation.getCheckIn());
            log.debug(" Error Condition .. Please create Zone");

            RealTimeZoneInfo zoneInfo = new RealTimeZoneInfo();
            zoneInfo.setFromRoomInfo(axisSelection.getFinalLocation().getBeaconMetaData().getProperyIdentifer());
            zoneInfo.setZone(axisSelection.getFinalLocation().getBeaconMetaData().getZone());
            realTimeLocation.getRealTimeZoneInfos().add(zoneInfo);



        }

        public void handleAxesAddLocationsAsZone(RealTimeLocation realTimeLocation, AxisSelection axisSelection) throws Exception
        {
            for(Location loc2 : axisSelection.getZoneLocations())
            {
                RealTimeZoneInfo zoneInfo = new RealTimeZoneInfo();
                zoneInfo.setFromRoomInfo(loc2.getBeaconMetaData().getProperyIdentifer());

                realTimeLocation.getRealTimeZoneInfos().add(zoneInfo);
            }
        }

        public void handleZoneCondition(RealTimeLocation realtimeLocation, AxisSelection axisSelection) throws Exception

        {
            // Sort the beacaons and add two beacons as Zone
            axisSelection.getBeaconMetaDataList().sort(new BeaconMetaDataCombinedComparator());

            int i = 0;
            for( BeaconMetaData beaconMetaData : axisSelection.getBeaconMetaDataList())
            {
                if ( i > 3)
                {
                    break;
                }
                this.realTimeLocationBuilder.buidlRoomInfoforZone(realtimeLocation,beaconMetaData);
               i ++ ;
            }

        }

        public List<AxisSelection> getAxisinLineofSight(List<AxisSelection> selectedAxises) throws Exception
        {
            List<AxisSelection> inlineOfSight = selectedAxises.stream().filter(ux -> org.apache.commons.lang3.StringUtils.equals(ux.getLineOfSightStatus(),AxisSelectionService.INLINEOFSIGHT)).collect(Collectors.toList());


              return inlineOfSight;
        }

        public List<AxisSelection> getAxisoutofSight(List<AxisSelection> selectedAxes) throws Exception
        {
            List<AxisSelection> notinlineOfSight = selectedAxes.stream().filter(ux -> org.apache.commons.lang3.StringUtils.equals(ux.getLineOfSightStatus(),AxisSelectionService.NOTINLINEOFSIGHT)).collect(Collectors.toList());


            return notinlineOfSight;

        }

        public List<AxisSelection> handleCorridorcase(List<AxisSelection> notiSight)
        {

            /**
             * Sort the axes or just pass the closest axes. Reading is bad what is the point in dwelllingon this
             */

            notiSight.sort(new AxisSelectionDistanceComparator());


            return null;
        }




    }



