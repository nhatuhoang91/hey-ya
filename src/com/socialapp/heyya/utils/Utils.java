package com.socialapp.heyya.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.text.format.Time;
import android.util.Log;

import com.quickblox.core.exception.QBResponseException;
import com.socialapp.heyya.App;

public class Utils {

	public static String calculateDiffTime(String start, String end){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		StringBuilder time= new StringBuilder();
		try {
			Date startDate = simpleDateFormat.parse(start);
			Date endDate = simpleDateFormat.parse(end);
			long diff = endDate.getTime() - startDate.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			Log.d("UTILS_CREATE_DIFF", "diff = "+diff);
			if(diffDays != 0){
				time.append(diffDays);
				if(diffDays == 1){
					time.append(" day ago");
				}else{
					time.append(" days ago");
				}
			}else
			{
				long diffHours = diff / (60 * 60 * 1000) % 24;
				if(diffHours!=0){
					time.append(diffHours);
					if(diffHours == 1){
						time.append(" hour ago");
					}else{
						time.append(" hours ago");
					}
				}else
				{
					long diffMinutes = diff / (60 * 1000) % 60;
					if(diffMinutes != 0){
						time.append(diffMinutes);
						if(diffMinutes == 1){
							time.append(" minute ago");
						}else{
							time.append(" minutes ago");
						}
					}else{
						long diffSeconds = diff / 1000 % 60;
						time.append(diffSeconds);
						if(diffSeconds == 1){
							time.append(" second ago");
						}else{
							time.append(" seconds ago");
						}
					}
				}
			}
			return time.toString();
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String calculateDiffTime(long diffTime){
		StringBuilder time = new StringBuilder();
		long diffDays = diffTime / (24 * 60 * 60 * 1000);
		if(diffDays != 0){
			time.append(diffDays);
			if(diffDays == 1){
				time.append(" day ago");
			}else{
				time.append(" days ago");
			}
		}else
		{
			long diffHours = diffTime / (60 * 60 * 1000) % 24;
			if(diffHours!=0){
				time.append(diffHours);
				if(diffHours == 1){
					time.append(" hour ago");
				}else{
					time.append(" hours ago");
				}
			}else
			{
				long diffMinutes = diffTime / (60 * 1000) % 60;
				if(diffMinutes != 0){
					time.append(diffMinutes);
					if(diffMinutes == 1){
						time.append(" minute ago");
					}else{
						time.append(" minutes ago");
					}
				}else{
					long diffSeconds = diffTime / 1000 % 60;
					time.append(diffSeconds);
					if(diffSeconds == 1){
						time.append(" second ago");
					}else{
						time.append(" seconds ago");
					}
				}
			}
		}
		return time.toString();
	}
	
	public static String createUniqueId(){
		String login = App.getInstance().getPrefsHelper().getString(PrefsHelper.PREF_USER_ID, null);
		StringBuilder uniqueId = new StringBuilder();
		uniqueId.append("m-");
		uniqueId.append(login);
		uniqueId.append(String.valueOf(System.currentTimeMillis()));
		return uniqueId.toString();
	}
	
	public static String createTime(Time time){
		StringBuilder timeFormated = new StringBuilder();
		timeFormated.append(time.hour)
		.append(":")
		.append(time.minute)
		.append(":")
		.append(time.second);
		
		return timeFormated.toString();
	}
	
	public static String createDate(Time time){
		StringBuilder timeFormated = new StringBuilder();
		timeFormated.append(time.month+1)
		.append("/")
		.append(time.monthDay)
		.append("/")
		.append(time.year);
		
		return timeFormated.toString();
	}
	public static boolean isTokenDestroyedError(QBResponseException e) {
        List<String> errors = e.getErrors();
        for (String error : errors) {
            if (Consts.TOKEN_REQUIRED_ERROR.equals(error)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExactError(QBResponseException e, String msgError) {
        Log.d(Utils.class.getSimpleName(), "");
        List<String> errors = e.getErrors();
        for (String error : errors) {
            Log.d(Utils.class.getSimpleName(), "error =" +error);
            if (error.contains(msgError)) {
                Log.d(Utils.class.getSimpleName(), error + " contains "+msgError);
                return true;
            }
        }
        return false;
    }
    
    public static void closeOutputStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                ErrorUtils.logError(e);
            }
        }
    }
}
