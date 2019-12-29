package com.socialapp.heyya.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.provider.Settings;
import android.util.Log;

public class MediaPlayerManager {

	private static final String TAG = MediaPlayerManager.class.getSimpleName();
	public static final String DEFAULT_RINGTONE = "defaultRingtone.ogg";
	
	private MediaPlayer mediaPlayer;
	private Context context;
	boolean isPlaying;
	private int originalVolume;
	
	public MediaPlayerManager(Context context){
		this.context = context;
	}
	
	public void playAssetSound(String resource, boolean looping){
		AssetsSoundResource assetsSoundResource = new AssetsSoundResource(resource, context);
		playSound(assetsSoundResource, looping, true);
	}
	
	public void playDefaultRingtone(){
		UriSoundResource uriSoundResource = new UriSoundResource(Settings.System.DEFAULT_RINGTONE_URI, context);
		playSound(uriSoundResource, true, true);
	}
	
	public void setMaxVolume(){
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
	}
	
	public void returnOriginalVolume(){
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
	}
	
	public boolean isPlaying(){
		return isPlaying;
	}
	
	public void stop(){
		if(isPlaying){
			if(mediaPlayer!=null && mediaPlayer.isPlaying())
				mediaPlayer.stop();
			release();
		}
		isPlaying = false;
	}
	
	private void release(){
		if(mediaPlayer!=null){
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	private void playDefaultSound(){
		AssetsSoundResource assetsSoundResource = new AssetsSoundResource(DEFAULT_RINGTONE, context);
		playSound(assetsSoundResource, true, false);
	}
	
	private void playSound(SoundResource soundResource, boolean looping, boolean catchException){
		stop();
		mediaPlayer = new MediaPlayer();
		try{
			mediaPlayer.setLooping(looping);
			soundResource.putResourceInPlayer(mediaPlayer);
			mediaPlayer.prepare();
			mediaPlayer.start();
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					stop();
				}
			});
			isPlaying = true;
		}catch(Exception e){
			if(!catchException){
				Log.e(TAG, "Error : "+e.toString());
			}else{
				playDefaultSound();
			}
		}
	}
}
