package com.wrkspot.emp.fence.model.dto;

public class Fence {
	
	String uuid;
	
	String name;
	
	Integer noOfOccurrance;
	
	Integer median;
	
	

	String zone;

	
	
	public Integer getMedian() {
		return median;
	}

	public void setMedian(Integer median) {
		this.median = median;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNoOfOccurrance() {
		return noOfOccurrance;
	}

	public void setNoOfOccurrance(Integer noOfOccurrance) {
		this.noOfOccurrance = noOfOccurrance;
	}

	

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}
	
	
	
	
	

}
