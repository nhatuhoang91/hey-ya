package com.socialapp.heyya.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.Display;

public class ImageUtil {

	private static final String TEMP_FILE_NAME = "temp.png";

    private Activity activity;

    public ImageUtil(Activity activity) {
        this.activity = activity;
    }
	private enum ScalingLogic {
        CROP, FIT
    }
	public static ImageLoaderConfiguration getImageLoaderConfiguration(Context context) {
        final int MEMORY_CACHE_LIMIT = 2 * 1024 * 1024;
        final int MAX_IMAGE_WIDTH_FOR_MEMORY_CACHE = 600;
        final int MAX_IMAGE_HEIGHT_FOR_MEMORY_CACHE = 1200;

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(MAX_IMAGE_WIDTH_FOR_MEMORY_CACHE, MAX_IMAGE_HEIGHT_FOR_MEMORY_CACHE)
                .threadPriority(Thread.NORM_PRIORITY)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(
                        new UsingFreqLimitedMemoryCache(MEMORY_CACHE_LIMIT)).writeDebugLogs()
                .defaultDisplayImageOptions(Consts.UIL_DEFAULT_DISPLAY_OPTIONS)
                .denyCacheImageMultipleSizesInMemory().build();
        return imageLoaderConfiguration;
    }
	 public File getFileFromBitmap(Bitmap origBitmap) throws IOException {
	        int width = SizeUtil.dipToPixels(activity, 200);
	        int height = SizeUtil.dipToPixels(activity, 200);
	        Bitmap bitmap = createScaledBitmap(origBitmap, width, height, ScalingLogic.FIT);
	        byte[] bitmapData = getBytesBitmap(bitmap);
	        File tempFile = createFile(bitmapData);
	        return tempFile;
	 }
	 public static byte[] getBytesBitmap(Bitmap imageBitmap) {
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        imageBitmap.compress(Bitmap.CompressFormat.PNG, Consts.FULL_QUALITY, byteArrayOutputStream);
	        byte[] byteArray = byteArrayOutputStream.toByteArray();
	        Utils.closeOutputStream(byteArrayOutputStream);
	        return byteArray;
	 }
	
	 public File createFile(byte[] bitmapData) throws IOException {
	    File tempFile = new File(activity.getCacheDir(), TEMP_FILE_NAME);
	    tempFile.createNewFile();
	    FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
	    fileOutputStream.write(bitmapData);
	    Utils.closeOutputStream(fileOutputStream);
	    return tempFile;
	}
	public Bitmap getBitmap(Uri originalUri) {
        BitmapFactory.Options bitmapOptions = getBitmapOption();
        Bitmap selectedBitmap = null;
        try {
            ParcelFileDescriptor descriptor = activity.getContentResolver().openFileDescriptor(originalUri, "r");
            selectedBitmap = BitmapFactory.decodeFileDescriptor(descriptor.getFileDescriptor(), null, bitmapOptions);
        } catch (FileNotFoundException e) {
            ErrorUtils.showError(activity, e.getMessage());
        }
        return selectedBitmap;
    }
	private BitmapFactory.Options getBitmapOption() {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        //bitmapOptions.inJustDecodeBounds = true;
        bitmapOptions.inTempStorage = new byte[32 * 1024];
        return bitmapOptions;
    }
	public Bitmap createScaledBitmap(Bitmap unscaledBitmap) {
		//DisplayMetrics metrics = new DisplayMetrics();
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int displayWidth = size.x;
		int displayHeight = size.y;
        Bitmap scaledBitmap = createScaledBitmap(unscaledBitmap, displayWidth, displayHeight, ScalingLogic.FIT);

        return scaledBitmap;
    }
	 private Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,
	            ScalingLogic scalingLogic) {
	        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth,
	                dstHeight, scalingLogic);
	        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth,
	                dstHeight, scalingLogic);
	        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(scaledBitmap);
	        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
	        return scaledBitmap;
	    }
	 private Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
	            ScalingLogic scalingLogic) {
	        if (scalingLogic == ScalingLogic.CROP) {
	            final float srcAspect = (float) srcWidth / (float) srcHeight;
	            final float dstAspect = (float) dstWidth / (float) dstHeight;

	            if (srcAspect > dstAspect) {
	                final int srcRectWidth = (int) (srcHeight * dstAspect);
	                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
	                return new Rect(srcRectLeft, Consts.ZERO_INT_VALUE, srcRectLeft + srcRectWidth, srcHeight);
	            } else {
	                final int srcRectHeight = (int) (srcWidth / dstAspect);
	                final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
	                return new Rect(Consts.ZERO_INT_VALUE, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
	            }
	        } else {
	            return new Rect(Consts.ZERO_INT_VALUE, Consts.ZERO_INT_VALUE, srcWidth, srcHeight);
	        }
	    }
	 
	 public Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
	            ScalingLogic scalingLogic) {
	        if (scalingLogic == ScalingLogic.FIT) {
	            final float srcAspect = (float) srcWidth / (float) srcHeight;
	            final float dstAspect = (float) dstWidth / (float) dstHeight; // dsyWidth = dstHeight => 1

	            if (srcAspect > dstAspect) {
	            	//srcAspect > 1 => scrWidth > srcHeight
	                return new Rect(Consts.ZERO_INT_VALUE, Consts.ZERO_INT_VALUE, dstWidth,
	                        (int) (dstWidth / srcAspect));
	            } else {
	            	//srcAspect < 1 => scrWidth < srcHeight
	                return new Rect(Consts.ZERO_INT_VALUE, Consts.ZERO_INT_VALUE, (int) (dstHeight * srcAspect),
	                        dstHeight);
	            }
	        } else {
	            return new Rect(Consts.ZERO_INT_VALUE, Consts.ZERO_INT_VALUE, dstWidth, dstHeight);
	        }
	    }
}
