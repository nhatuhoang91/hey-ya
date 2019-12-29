package com.socialapp.heyya.media;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;

public abstract class SoundResource<T> {
	protected T resource;
	protected Context context;
	public SoundResource(T resource, Context context){
		this.resource = resource;
		this.context = context;
	}
	public abstract void putResourceInPlayer(MediaPlayer mediaPlayer) throws
		IllegalArgumentException, IllegalStateException, IOException, SecurityException;
}
