package com.socialapp.heyya.adapter;

import java.util.ArrayList;
import java.util.List;

import com.socialapp.heyya.ui.main.FriendFragment;
import com.socialapp.heyya.ui.main.HunterFragment;
import com.socialapp.heyya.ui.main.SearchFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabPageAdapter extends FragmentPagerAdapter{

	private final ArrayList<Fragment> listFragment = new ArrayList<>();
	private final ArrayList<String> listStringTile = new ArrayList<>();
	
	public TabPageAdapter(FragmentManager fm){
		super(fm);
	}
	
	@Override
	public Fragment getItem(int position) {
		if(position == 0){
			return FriendFragment.getInstance();
		}else
			if(position == 1){
				return HunterFragment.getInstance();
			}else
				if(position == 2){
					return SearchFragment.getInstance();
				}
		return null;
	}
	
	 public void addFrag(Fragment fragment, String title) {
         listFragment.add(fragment);
         listStringTile.add(title);
     }

	@Override
	public int getCount() {
		return 3;
	}
	
	 @Override
     public CharSequence getPageTitle(int position) {
		 if(position == 0){
				return "FRIEND";
			}else
				if(position == 1){
					return "HUNTER";
				}else
					if(position == 2){
						return "SEARCH";
					}
			return "";
     }

}
