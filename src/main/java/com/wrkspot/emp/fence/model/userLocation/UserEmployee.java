package com.wrkspot.emp.fence.model.userLocation;

import org.mongodb.morphia.annotations.Entity;

@Entity
public class UserEmployee {

    private String employeeID;
    private String siteID;
    private String firstName;
    private String lastName;
    private String userDept;
    private String userShift;

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserDept() {
        return userDept;
    }

    public void setUserDept(String userDept) {
        this.userDept = userDept;
    }

    public String getUserShift() {
        return userShift;
    }

    public void setUserShift(String userShift) {
        this.userShift = userShift;
    }
}
