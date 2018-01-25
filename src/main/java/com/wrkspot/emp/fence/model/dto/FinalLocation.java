package com.wrkspot.emp.fence.model.dto;

import org.mongodb.morphia.annotations.Entity;

import java.util.Date;
import java.util.List;

/**
 * Created by prakash on 7/12/17.
 *
 * @copyrights WRKSPOT Corp
 */
@Entity
public class FinalLocation {


    private String employeeIdentifier;
    private String propertyIdentifier;
    private double percentageConfidence;
    private List<LocateMeAxis> locateMeAxes;
    private Date createdDate;

    public String getEmployeeIdentifier() {
        return employeeIdentifier;
    }

    public void setEmployeeIdentifier(String employeeIdentifier) {
        this.employeeIdentifier = employeeIdentifier;
    }

    public String getPropertyIdentifier() {
        return propertyIdentifier;
    }

    public void setPropertyIdentifier(String propertyIdentifier) {
        this.propertyIdentifier = propertyIdentifier;
    }

    public double getPercentageConfidence() {
        return percentageConfidence;
    }

    public void setPercentageConfidence(double percentageConfidence) {
        this.percentageConfidence = percentageConfidence;
    }

    public List<LocateMeAxis> getLocateMeAxes() {
        return locateMeAxes;
    }

    public void setLocateMeAxes(List<LocateMeAxis> locateMeAxes) {
        this.locateMeAxes = locateMeAxes;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
