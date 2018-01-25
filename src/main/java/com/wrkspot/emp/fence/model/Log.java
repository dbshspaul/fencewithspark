package com.wrkspot.emp.fence.model;

import java.util.Date;
import java.util.List;

public class Log {
	
	String timestamp;
	String _id;
	List<Beacon> beacons;
	
	
	
	
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public List<Beacon> getBeacons() {
		return beacons;
	}
	public void setBeacons(List<Beacon> beacons) {
		this.beacons = beacons;
	}
	
	

}
