package com.wrkspot.emp.fence.model;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;

import java.util.List;
import java.util.Map;

/**
 * Created by murali on 8/21/17.
 * This class will hold the results for axes processing
 * and will be convereted into a Neo4J object
 */
public class IOTFence {

    private String iotID;
    private Map<String,List<BeaconMetaData>>  axesInfo;
    private Map<String, List<BeaconMetaData>> axesProximity;
    private Map<String,List<BeaconMetaData>> axesRecommendation;
}
