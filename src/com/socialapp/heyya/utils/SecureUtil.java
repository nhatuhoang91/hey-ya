package com.socialapp.heyya.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.util.Base64;

public class SecureUtil {

	private static final String UTF8 = "utf-8";
	private static final char[] METLAM = "dungaohelpdoi".toCharArray();
	private static final byte[] salt={100,21,72,37,18,22,76,19};
	
	private static final char[] HLVT = "huyntrinleviet".toCharArray();
	private static final byte[] salthlvt={90,22,72,57,88,55,70,49};
	 
	protected String encrypt( String value ) {

	      try {
	          final byte[] bytes = value!=null ? value.getBytes(UTF8) : new byte[0];
	          SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
	          SecretKey key = keyFactory.generateSecret(new PBEKeySpec(METLAM));
	          Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
	          PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
	          pbeCipher.init(Cipher.ENCRYPT_MODE, key,pbeParameterSpec);
	          return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP),UTF8);
	      } catch( Exception e ) {
	          throw new RuntimeException(e);
	      }
	  }

	  protected String decrypt(String value){
	      try {
	          final byte[] bytes = value!=null ? Base64.decode(value,Base64.DEFAULT) : new byte[0];
	          SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
	          SecretKey key = keyFactory.generateSecret(new PBEKeySpec(METLAM));
	          Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
	          PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
	          pbeCipher.init(Cipher.DECRYPT_MODE, key,pbeParameterSpec);
	          return new String(pbeCipher.doFinal(bytes),UTF8);

	      } catch( Exception e) {
	          throw new RuntimeException(e);
	      }
	  }
	  public static String encryptUsernameAndPassword(String value) {
	      try {
	          final byte[] bytes = value!=null ? value.getBytes(UTF8) : new byte[0];
	          SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
	          SecretKey key = keyFactory.generateSecret(new PBEKeySpec(HLVT));
	          Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
	          PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salthlvt, 100);
	          pbeCipher.init(Cipher.ENCRYPT_MODE, key,pbeParameterSpec);
	          return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP),UTF8);
	      } catch( Exception e ) {
	          throw new RuntimeException(e);
	      }
	  }
	  
}
