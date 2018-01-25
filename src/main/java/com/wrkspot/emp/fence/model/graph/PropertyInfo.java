package com.wrkspot.emp.fence.model.graph;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class PropertyInfo {

    private Long id;
    private String roomNo;
    private String zone;

    private String floorNumber;
    private String siteID;
    // indicates if the Node represents ZOne or ZOneroom only

}
