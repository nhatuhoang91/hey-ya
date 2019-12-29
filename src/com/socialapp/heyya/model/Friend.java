package com.socialapp.heyya.model;

import android.net.Uri;

public class Friend {

	private String friend_Id;
	private String full_Name;
	private String avt_Uri;
	private Integer is_Ask_Friend;
	private Integer is_Requested_Friend;
	private Integer is_Online;
	
	public Friend(){
		
	}
	public Friend(String friendId, String fullName,String email, String phone
			,String avtUri, Integer isAsk, Integer isRequest, Integer isOnline){
		friend_Id = friendId;
		full_Name = fullName;
		avt_Uri = avtUri;
		is_Ask_Friend = isAsk;
		is_Requested_Friend = isRequest;
		is_Online = isOnline;
	}
	
	public void setFriendId(String id){
		friend_Id = id;
	}
	public String getFriendId(){
		return friend_Id;
	}
	
	public void setFullName(String fullName){
		full_Name = fullName;
	}
	public String getFullName(){
		return full_Name;
	}
	
	public void setUri(String avtUri){
		avt_Uri = avtUri;
	}
	public String getUri(){
		return avt_Uri;
	}
	
	public void setIsAskFriend(Integer isAskFriend){
		is_Ask_Friend = isAskFriend;
	}
	public Integer getIsAskFriend(){
		return is_Ask_Friend;
	}
	
	public void setIsRequestedFriend(Integer isRequestedFriend){
		is_Requested_Friend = isRequestedFriend;
	}
	public Integer getIsRequestedFriend(){
		return is_Requested_Friend;
	}
	
	public void setIsOnline(Integer isOnline){
		is_Online = isOnline;
	}
	public Integer getIsOnline(){
		return is_Online;
	}
}
