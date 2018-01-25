package com.wrkspot.emp.fence.model.graph;

import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

@Entity
public class PropertyOccupancy {

    @Id
    private Long id;
    private long logTime;

    private List<String> zoneRooms;
    private DateTime sLogTime;
    private long startHour;
    private long startMinute;


    private PropertyEmployee propertyEmployee;
    private PropertyInfo propertyInfo;

}
