package com.wrkspot.emp.fence.model.graph;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class PropertyEmployee {

    @Id
    private Long id;
    private String employeeID;
    private String firstName;
    private String lastName;
    private String dept;
    private String shift;

    private String siteID;
}
