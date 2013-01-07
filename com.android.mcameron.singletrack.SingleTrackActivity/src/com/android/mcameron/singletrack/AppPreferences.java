package com.android.mcameron.singletrack;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
	private static final String APP_PREFERENCES = "SINGLETRACK_PREFERENCES";

	private SharedPreferences appSharedPrefs;
	private SharedPreferences.Editor prefsEditor;
	 
	public AppPreferences(Context context){
		this.appSharedPrefs = context.getSharedPreferences(APP_PREFERENCES, Activity.MODE_PRIVATE);
		this.prefsEditor = appSharedPrefs.edit();
	}
	 
	public int getLevelState(String levelPack, String levelNumber) {
		return appSharedPrefs.getInt(levelPack + levelNumber, Globals.LEVEL_UNSET);
	}
	 
	public void setLevelState(String levelPack, String levelNumber, int levelState) {
		prefsEditor.putInt(levelPack + levelNumber, levelState).commit();
	}
	 
	public void clear() {
		prefsEditor.clear().commit();
	}
	 
	public int getHighestUnlockedLevel(String levelPack) {
		
		for (Map.Entry<String,?> entry : appSharedPrefs.getAll().entrySet()) {
			
		}
		 
		return appSharedPrefs.getInt(levelPack, Globals.LEVEL_UNSET);
	}
}
