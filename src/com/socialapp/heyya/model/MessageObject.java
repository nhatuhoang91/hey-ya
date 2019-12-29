package com.socialapp.heyya.model;

public class MessageObject {

	private String sender_Id;
	private String sender_Fullname;
	private double lat;
	private double lon;
	private String message;
	private String time;
	private String date;
	
	public MessageObject(){
		
	}
	public String getSenderId(){
		return sender_Id;
	}
	public void setSenderId(String sender_Id){
		this.sender_Id = sender_Id;
	}
	
	public String getSenderFullname(){
		return sender_Fullname;
	}
	public void setSenderFullname(String sender_Fullname){
		this.sender_Fullname = sender_Fullname;
	}
	
	public double getLat(){
		return lat;
	}
	public void setLat(double lat){
		this.lat = lat;
	}
	
	public double getLon(){
		return lon;
	}
	public void setLon(double lon){
		this.lon = lon;
	}
	
	public String getMessage(){
		return message;
	}
	public void setMessage(String message){
		this.message = message;
	}
	
	public String getTime(){
		return time;
	}
	public void setTime(String time){
		this.time = time;
	}
	
	public String getDate(){
		return date;
	}
	public void setDate(String date){
		this.date = date;
	}
}
