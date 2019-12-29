package com.socialapp.heyya.concurrency;

import java.io.File;
import java.io.IOException;

import com.socialapp.heyya.utils.ErrorUtils;
import com.socialapp.heyya.utils.ImageUtil;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class ReceiveUriScaledBitmapTask extends AsyncTask<Object, Uri, Uri>{

	private ReceiveUriScaledBitmapListener receiveUriScaledBitmapListener;

	public ReceiveUriScaledBitmapTask(ReceiveUriScaledBitmapListener receiveUriScaledBitmapListener) {      
		 this.receiveUriScaledBitmapListener = receiveUriScaledBitmapListener;
	}
	@Override
	protected Uri doInBackground(Object[] params) {
		 ImageUtil imageUtil = (ImageUtil) params[0];
	        Uri originalUri = (Uri) params[1];

	       File bitmapFile = null;
	       Uri outputUri = null;
	        Log.d("RECEIVE_URI_TASK", "originalUri = "+originalUri.toString());
	        Bitmap bitmap = imageUtil.getBitmap(originalUri);
	        if(bitmap == null)
	        Log.d("RECEIVE_URI_TASK", "bitmap empty");
	        Bitmap scaledBitmap = imageUtil.createScaledBitmap(bitmap);

	       try {
	            bitmapFile = imageUtil.getFileFromBitmap(scaledBitmap);
	        	//bitmapFile = imageUtil.getFileFromBitmap(bitmap);
	        } catch (IOException error) {
	            ErrorUtils.logError(error);
	        }

	        if (bitmapFile != null) {
	            outputUri = Uri.fromFile(bitmapFile);
	        }
			
	        //return bitmap;
	        return outputUri;
	}
	@Override
    protected void onPostExecute(Uri uri) {
        receiveUriScaledBitmapListener.onUriScaledBitmapReceived(uri);
    }
	public interface ReceiveUriScaledBitmapListener {

        public void onUriScaledBitmapReceived(Uri uri);
    }
}
