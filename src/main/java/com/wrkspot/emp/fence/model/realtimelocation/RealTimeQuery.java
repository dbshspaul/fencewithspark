package com.wrkspot.emp.fence.model.realtimelocation;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class RealTimeQuery {
    private String checkinID;
    private String employeeID;
    private String startDate;
    private String endDate;


}
