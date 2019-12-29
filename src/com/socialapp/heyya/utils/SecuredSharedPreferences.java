package com.socialapp.heyya.utils;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.qpid.management.common.sasl.UserPasswordCallbackHandler;
import org.jivesoftware.smack.util.Base64Encoder;
import org.xbill.DNS.utils.base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Base64;


public class SecuredSharedPreferences implements SharedPreferences{
	private static final String UTF8 = "utf-8";
	 private static final char[] METLAM = "dungaohelpdoi".toCharArray();
	 
	 private SharedPreferences sharedPreferences;
	 private SecureUtil secureUtil;
	 private byte[] salt={100,21,72,37,18,22,76,19};
	
	 public SecuredSharedPreferences(Context context, String prefName){
		 this.sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		 secureUtil = new SecureUtil();
	 }
	 
	 @Override
	public Map<String, ?> getAll() {
		// TODO Auto-generated method stub
		return this.sharedPreferences.getAll();
	}

	@Override
	public String getString(String key, String defValue) {
		// TODO Auto-generated method stub
		final String v =this.sharedPreferences.getString(key, null);
		return v!=null?decrypt(v):defValue;
	}

	@Override
	public Set<String> getStringSet(String key, Set<String> defValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInt(String key, int defValue) {
		// TODO Auto-generated method stub
		final String v = this.sharedPreferences.getString(key, null);
		return v!=null?Integer.parseInt(decrypt(v)):defValue;
	}

	@Override
	public long getLong(String key, long defValue) {
		// TODO Auto-generated method stub
		final String v = this.sharedPreferences.getString(key, null);
		return v!=null?Long.parseLong(decrypt(v)):defValue;
	}

	@Override
	public float getFloat(String key, float defValue) {
		// TODO Auto-generated method stub
		final String v = this.sharedPreferences.getString(key, null);
		return v!=null?Float.parseFloat(decrypt(v)):defValue;
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		// TODO Auto-generated method stub
		final String v = this.sharedPreferences.getString(key, null);
		return v!=null?Boolean.parseBoolean(decrypt(v)):defValue;
	}

	@Override
	public boolean contains(String key) {
		// TODO Auto-generated method stub
		return this.sharedPreferences.contains(key);
	}

	@Override
	public Editor edit() {
		// TODO Auto-generated method stub
		return new Editor();
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		// TODO Auto-generated method stub
		this.sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		// TODO Auto-generated method stub
		this.sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
	}
	
	public class Editor implements SharedPreferences.Editor{
		private SharedPreferences.Editor mEditor;
		public Editor(){
			mEditor = SecuredSharedPreferences.this.sharedPreferences.edit();
		}
		@Override
		public Editor putString(String key,
				String value) {
			// TODO Auto-generated method stub
			mEditor.putString(key, encrypt(value));
			return this;
		}

		@Override
		public Editor putInt(String key,
				int value) {
			mEditor.putString(key, encrypt(Integer.toString(value)));
			return this;
		}

		@Override
		public Editor putLong(String key,
				long value) {
			mEditor.putString(key, encrypt(Long.toString(value)));
			return this;
		}

		@Override
		public Editor putFloat(String key,
				float value) {
			mEditor.putString(key, encrypt(Float.toString(value)));
			return this;
		}

		@Override
		public Editor putBoolean(String key,
				boolean value) {
			mEditor.putString(key, encrypt(Boolean.toString(value)));
			return this;
		}

		@Override
		public Editor remove(String key) {
			// TODO Auto-generated method stub
			mEditor.remove(key);
			return this;
		}

		@Override
		public Editor clear() {
			mEditor.clear();
			return this;
		}

		@Override
		public boolean commit() {
			return mEditor.commit();
		}

		@Override
		public void apply() {
			// TODO Auto-generated method stub
			mEditor.apply();
		}
		@Override
		public android.content.SharedPreferences.Editor putStringSet(
				String key, Set<String> values) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	protected String encrypt( String value ) {
		return secureUtil.encrypt(value);
	}

	protected String decrypt(String value){
		return secureUtil.decrypt(value);
	}
}
