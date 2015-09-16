package com.quipmate2.features;

public class EventInfo {

	public String name; 
	public String imageUrl;
	public String birthdate;
	public boolean eventStatus;
	
	public EventInfo(String name, String imageUrl, String birthdate, boolean eventStatus){
		this.name = name;
		this.imageUrl = imageUrl;
		this.birthdate = birthdate;
		this.eventStatus = eventStatus;
	}
}
