package com.wrkspot.emp.fence.model.iot;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import org.mongodb.morphia.annotations.Entity;

import java.util.List;

/**
 * Created by murali on 8/29/17.
 */
@Entity
public class IOTPlane {
    private String axes;
    /**
     * Axes, Plane, Top , Bottom
     */
    private Enum<IOTPlanType> iotPlanTypeEnum;

    private List<BeaconMetaData> beaconMetaDataList;

    private List<String> roomIdentifiers;

    private double planeDistance;


}
