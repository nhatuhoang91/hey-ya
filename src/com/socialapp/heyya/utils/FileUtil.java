package com.socialapp.heyya.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.res.AssetManager;
import android.util.Log;

public class FileUtil {
/*	public static String readConfig(AssetManager mgr, String path, String key) {
    String contents = "";
    InputStream is = null;
    BufferedReader reader = null;
    try {
        
        is = mgr.open(path);
        reader = new BufferedReader(new InputStreamReader(is));
        //contents = reader.readLine();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if(line.contains(key)){
            	int i = line.indexOf(":");
            	contents = line.substring(i+1);
            	break;
            }
        }
    } catch (final Exception e) {
        e.printStackTrace();
    } finally {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ignored) {
            	Log.e("FRIEND_UTIL", "Error : "+ignored.toString());
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ignored) {
            	Log.e("FRIEND_UTIL", "Error : "+ignored.toString());
            }
        }
    }
    return contents;
}*/
}
