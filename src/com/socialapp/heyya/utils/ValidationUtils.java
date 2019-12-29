package com.socialapp.heyya.utils;

import java.util.ArrayList;
import java.util.List;

import com.quickblox.auth.QBAuth;
import com.quickblox.core.QBCallback;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.result.Result;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.R;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

public class ValidationUtils {
	private final String TAG = "ValidationUtils";
	private Context context;
    public ValidationUtils(Context context) {
        this.context = context;
    }
    
    public boolean isValidLoginEditText(EditText usernameEditText
    		, EditText passwordEditText, String username, String password){
    	if(isEmpty(username, usernameEditText) || isHasSpace(username, usernameEditText)){
    		return false;
    	}
    	if(isEmpty(password, passwordEditText) || isHasSpace(password, passwordEditText)){
    		return false;
    	}
    	return true;
    }
    
    public boolean isValidSignUpEdittext(ArrayList<EditText> editTexts){
    	String username = editTexts.get(0).getText().toString().trim();
    	String fullname = editTexts.get(1).getText().toString().trim();
    	String password = editTexts.get(2).getText().toString().trim();
    	String rePassword= editTexts.get(3).getText().toString().trim();
	
    	if(isEmpty(username, editTexts.get(0)) || isHasSpace(username, editTexts.get(0))
    			|| isInvalidLength(username, editTexts.get(0),8,25))
    	{
    		return false;
    	}
    	if(isEmpty(fullname, editTexts.get(1)) || isInvalidLength(fullname, editTexts.get(1),1,30))
    	{
    		return false;
    	}
    	if(isEmpty(password, editTexts.get(2)) || isHasSpace(password, editTexts.get(2))
    			|| isInvalidLength(password, editTexts.get(2),8,30))
    	{
    		return false;
    	}
    	if(isPasswordNotMatch(password, rePassword, editTexts.get(3)))
    	{
    		return false;
    	}
    	return true;
    }
    
    private boolean isEmpty(String text, EditText editText){
    	if(TextUtils.isEmpty(text))
    	{
    		editText.setError(context.getString(R.string.valid_edittext_not_empty));
    
    		return true;
    	}
    	return false;
    }
    private boolean isHasSpace(String text, EditText editText){
    	if(text.contains(" ")){
    		editText.setError(context.getString(R.string.valid_edittext_not_contain_white_space));
    		return true;
    	}
    	return false;
    }
    public boolean isInvalidLength(String text, EditText editText, int min, int max){
    	if(text.length()<min || text.length()>max){
    		editText.setError(context.getString(R.string.valid_edittext_length_invalid)+" "+min+" - "+max+" character(s)");
    		return true;
    	}
    	return false;
    }
    private boolean isPasswordNotMatch(String password, String rePassword, 
    		EditText editText){
    	if(!password.equals(rePassword))
    	{
    		editText.setError(context.getString(R.string.valid_edittext_password_error));
    		return true;
    	}
    	return false;
    }
}
