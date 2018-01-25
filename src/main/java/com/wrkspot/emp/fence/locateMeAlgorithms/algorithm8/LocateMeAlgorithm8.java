package com.wrkspot.emp.fence.locateMeAlgorithms.algorithm8;

import com.wrkspot.emp.fence.locateMeAlgorithms.algorithm7.LocateMeAlgorithm7;
import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeLocation;
import com.wrkspot.emp.fence.model.realtimelocation.RealTimeSiteConfig;
import com.wrkspot.emp.fence.service.impl.RankingServiceImpl;
import com.wrkspot.emp.fence.util.BeaconMetaDataLineofSightComparator;
import com.wrkspot.emp.fence.util.FenceGlossaries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocateMeAlgorithm8 implements Serializable {

    private RankingServiceImpl rankingService=RankingServiceImpl.getInstance();

    private LocateMeAlgorithm7 locateMeAlgorithm7 = LocateMeAlgorithm7.getInstance();

    private static final Logger log = LoggerFactory.getLogger(LocateMeAlgorithm8.class);


    public RealTimeLocation locateMe(Map<String, Object> axisInfo, RealTimeSiteConfig realTimeSiteConfig) throws Exception {

        log.debug(" In Algorithem 8");
        List<BeaconMetaData> beaconMetaDataList = (List<BeaconMetaData>) axisInfo.get("BEACONS");

        beaconMetaDataList.sort(new BeaconMetaDataLineofSightComparator());

        // Discard < 15
        //  Look at the top
        //

        log.debug(" After Sorting");

        Map<String, Object> selectedMap = this.selecteBecaonMetaDataList(beaconMetaDataList);

        List<BeaconMetaData> beaconMetaDataList1 = (List<BeaconMetaData>) selectedMap.get("SELECTEDBEACONS");

        log.debug(" After Selected Beacons");


        if (FenceGlossaries.isObjectEmpty(beaconMetaDataList1)) {
            log.error(" Not in proximity of any beacon");
        }


        Map<String, List<BeaconMetaData>> selectAxisInfo = this.rankingService.buildAxesInfo(beaconMetaDataList1);

        //Build a Map<String,Object> and pass it to Algorithm 7
        // This is a temporary fix

        Map<String, Object> alg7Map = new HashMap<>();

//        for (Map.Entry<String, List<BeaconMetaData>> entry : selectAxisInfo.entrySet()) {
//
//            alg7Map.put(entry.getKey(),entry.getValue());
//        }

        alg7Map.put("AXISINFO", selectAxisInfo);


        RealTimeLocation realTimeLocation = this.locateMeAlgorithm7.locateMe(alg7Map, realTimeSiteConfig);


        return realTimeLocation;
    }

    public List<BeaconMetaData> getLineOfSightBeacons(List<BeaconMetaData> beaconMetaDatas, int highValue, int lowValue) throws Exception {
        List<BeaconMetaData> selectedBeaconMetaDatas = beaconMetaDatas.stream().filter(u -> (u.getSightingCount() >= lowValue && u.getSightingCount() < highValue)).collect(Collectors.toList());
        return selectedBeaconMetaDatas;
    }

    public Map<String, Object> selecteBecaonMetaDataList(List<BeaconMetaData> beaconMetaDatas) throws Exception {
        // Look for 48 and above
        List<BeaconMetaData> beaconMetaDataList = null;
        HashMap<String, Object> selecteList = new HashMap<>();
        beaconMetaDataList = this.getLineOfSightBeacons(beaconMetaDatas, 400, 48);
        selecteList.put("CONFIDENCE", "HIGH");
        selecteList.put("SELECTEDBEACONS", beaconMetaDataList);
        beaconMetaDataList = this.getLineOfSightBeacons(beaconMetaDatas, 48, 24);
        if (!FenceGlossaries.isObjectEmpty(beaconMetaDataList)) {
            return selecteList;
        }
        selecteList.put("CONFIDENCE", "MEDIUM");
        selecteList.put("SELECTEDBEACONS", beaconMetaDataList);
        if (!FenceGlossaries.isObjectEmpty(beaconMetaDataList)) {
            return selecteList;
        }
        beaconMetaDataList = this.getLineOfSightBeacons(beaconMetaDatas, 24, 15);

        selecteList.put("CONFIDENCE", "LOW");
        selecteList.put("SELECTEDBEACONS", beaconMetaDataList);

        return selecteList;
    }
}
