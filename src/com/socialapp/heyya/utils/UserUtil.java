package com.socialapp.heyya.utils;

import com.quickblox.users.model.QBUser;
import com.socialapp.heyya.model.User;

public class UserUtil {

	public static User createUser(QBUser qbUser, String uri){
		User user = new User();
		user.setUserId(qbUser.getId().toString());
		user.setLogin(qbUser.getLogin());
		user.setFullname(qbUser.getFullName());
		user.setEmail(qbUser.getEmail());
		user.setAvtUri(uri);
		return user;
	}
}
