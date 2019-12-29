package com.socialapp.heyya.model;

import android.net.Uri;

public class User {
 
	private String user_Id;
	private String login;
	private String fullname;
	private String email;
	private String avt_Uri;
	
	public User(){
		
	}
	public User(String user_Id, String login, String fullname,
			String email, String phone, String avt_Uri){
		this.user_Id = user_Id;
		this.login = login;
		this.fullname = fullname;
		this.email = email;
		this.avt_Uri = avt_Uri;
	}
	public String getUserId(){
		return this.user_Id;
	}
	public void setUserId(String user_Id){
		this.user_Id = user_Id;
	}
	
	public String getLogin(){
		return this.login;
	}
	public void setLogin(String login){
		this.login = login;
	}
	
	public String getFullname(){
		return this.fullname;
	}
	public void setFullname(String fullname){
		this.fullname = fullname;
	}
	
	public String getEmail(){
		return this.email;
	}
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getAvtUri(){
		return this.avt_Uri;
	}
	public void setAvtUri(String avt_Uri){
		this.avt_Uri = avt_Uri;
	}
}
