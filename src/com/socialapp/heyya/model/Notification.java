package com.socialapp.heyya.model;

import com.socialapp.heyya.service.QBServiceConsts;

public class Notification {

	private String message_Id;
	private String sender_Id;
	private Integer is_Sender;
	private Integer is_Read;
	private Integer status;
	private Double lat;
	private Double lon;
	private String message;
	private String time;
	private String date;
	
	public Notification(){
		
	}
	
	public void setMessageId(String messageId){
		message_Id = messageId;
	}
	public String getMessageId(){
		return message_Id;
	}
	
	public void setSenderId(String id){
		sender_Id = id;
	}
	public String getSenderId(){
		return sender_Id;
	}
	
	public void setIsSender(Integer isSender){
		is_Sender = isSender;
	}
	public Integer getIsSender(){
		return is_Sender;
	}
	
	public void setIsRead(Integer isRead){
		is_Read = isRead;
	}
	public Integer getIsRead(){
		return is_Read;
	}
	
	public void setStatus(Integer status){
		this.status = status;
	}
	public Integer getStatus(){
		return status;
	}
	
	public void setLat(Double lat){
		this.lat = lat;
	}
	public Double getLat(){
		return lat;
	}
	
	public void setLon(Double lon){
		this.lon = lon;
	}
	public Double getLon(){
		return lon;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	public String getMessage(){
		return message;
	}
	
	public void setTime(String time){
		this.time = time;
	}
	public String getTime(){
		return time;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	public String getDate(){
		return date;
	}
}
