package com.wrkspot.emp.fence.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Queue;

/**
 * @author prakash
 */
@Entity
public class HKCache {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    String empId;

    Queue<String> userLocations;

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Queue<String> getUserLocations() {
        return userLocations;
    }

    public void setUserLocations(Queue<String> userLocations) {
        this.userLocations = userLocations;
    }


}
