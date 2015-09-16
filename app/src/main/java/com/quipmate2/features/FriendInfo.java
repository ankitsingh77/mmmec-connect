package com.quipmate2.features;



public class FriendInfo {
	private String id;
	private String name;
	private String imageURL;
	
	public FriendInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public FriendInfo(String id,String name, String imageURL) {
		super();
		this.id=id;
		this.name = name;
		this.imageURL = imageURL;
	}
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
		System.out.println(name);
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
		System.out.println(imageURL);
	}
}
