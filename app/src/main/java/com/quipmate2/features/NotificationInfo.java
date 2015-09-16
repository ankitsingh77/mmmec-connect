package com.quipmate2.features;

public class NotificationInfo {

	public String postType;
	public FriendInfo postBy;
	public String actionType;
	public FriendInfo actionBy;
	public String time;
	
	public NotificationInfo(String actionType,FriendInfo actionBy,FriendInfo postBy, String time) {
		// TODO Auto-generated constructor stub
		//this.postType = postType;
		this.actionType = actionType;
		this.actionBy = actionBy;
		this.postBy = postBy;
		this.time = time;
	}
}
