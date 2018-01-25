package com.wrkspot.emp.fence.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * @author prakash
 */
@Entity
public class UserLocationData {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    String empId;

    List<String> userLocations;


    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public List<String> getUserLocations() {
        return userLocations;
    }

    public void setUserLocations(List<String> userLocations) {
        this.userLocations = userLocations;
    }

}
