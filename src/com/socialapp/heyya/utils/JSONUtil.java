package com.socialapp.heyya.utils;

import org.json.JSONObject;

import com.socialapp.heyya.model.MessageObject;

public class JSONUtil {

	public static String createJSONMessage(MessageObject message)throws Exception{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sender", message.getSenderId());
		jsonObject.put("fullname", message.getSenderFullname());
		jsonObject.put("lat", message.getLat());
		jsonObject.put("lon", message.getLon());
		jsonObject.put("message", message.getMessage());
		jsonObject.put("time", message.getTime());
		jsonObject.put("date", message.getDate());
		return jsonObject.toString();
	}
}
