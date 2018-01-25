package com.wrkspot.emp.fence.model.realtimelocation;

import org.mongodb.morphia.annotations.Entity;

/**
 * Created by murali on 9/1/17.
 */
@Entity
public class RealTimePlotInfo {

    private String xAxisBeacon;
    private String yAxisBeacon;
    private String xAxis;
    private String yAxis;
    private double xDistance;
    private double yDistance;


}
