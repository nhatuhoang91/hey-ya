package com.socialapp.heyya.media;

import java.io.IOException;

import com.socialapp.heyya.utils.Consts;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class AssetsSoundResource extends SoundResource<String>{

	public AssetsSoundResource(String resource, Context context) {
		super(resource, context);
	}

	@Override
	public void putResourceInPlayer(MediaPlayer mediaPlayer)
			throws IllegalArgumentException, IllegalStateException,
			IOException, SecurityException {
		AssetFileDescriptor afd = context.getAssets().openFd(Consts.ASSET_SOUND_PATH+resource);
		mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
	}

}
