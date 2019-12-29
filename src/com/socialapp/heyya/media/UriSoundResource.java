package com.socialapp.heyya.media;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class UriSoundResource extends SoundResource<Uri>{

	public UriSoundResource(Uri resource, Context context) {
		super(resource, context);
	}

	@Override
	public void putResourceInPlayer(MediaPlayer mediaPlayer)
			throws IllegalArgumentException, IllegalStateException,
			IOException, SecurityException {
		// TODO Auto-generated method stub
		mediaPlayer.setDataSource(context, resource);
	}

}
