package com.socialapp.heyya.concurrency;

import java.io.File;
import java.io.IOException;

import com.socialapp.heyya.utils.ImageUtil;

import android.graphics.Bitmap;

public class ReceiveFileFromBitmapTask extends BaseAsyncTask{

	private ReceiveFileListener receiveFileListener;

    public ReceiveFileFromBitmapTask(ReceiveFileListener receiveFileListener) {
        this.receiveFileListener = receiveFileListener;
    }
	@Override
	public void onResult(Object result) {
		// TODO Auto-generated method stub
		 if (result instanceof File) {
	            receiveFileListener.onCachedImageFileReceived((File) result);
		 }
	}

	@Override
	public void onException(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object performInBackground(Object[] params) throws Exception {
		File imageFile;
        String absolutePath;
        ImageUtil imageUtils = (ImageUtil) params[0];
        Bitmap bitmap = (Bitmap) params[1];

        try{
        	imageFile = imageUtils.getFileFromBitmap(bitmap);
        	return imageFile;
        } catch (IOException e) {
            onException(e);
        }

        return null;
	}

	public interface ReceiveFileListener {

        public void onCachedImageFileReceived(File imageFile);
    }
}
