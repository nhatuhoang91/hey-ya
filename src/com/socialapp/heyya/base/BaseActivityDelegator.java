package com.socialapp.heyya.base;

import android.content.Context;

public abstract class BaseActivityDelegator {
	
	private Context context;

    public BaseActivityDelegator(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
