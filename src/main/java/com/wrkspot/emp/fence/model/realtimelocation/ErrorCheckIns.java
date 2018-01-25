package com.wrkspot.emp.fence.model.realtimelocation;


import org.mongodb.morphia.annotations.Entity;

@Entity
public class ErrorCheckIns {

    private String checkId;
    private String errorMessage;
    private String status;

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
