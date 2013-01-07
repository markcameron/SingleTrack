package com.android.mcameron.singletrack;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
	 private static final String APP_PREFERENCES = "SINGLETRACK_PREFERENCES";

	 private SharedPreferences appSharedPrefs;
	 private SharedPreferences.Editor prefsEditor;
	 
	 Globals globals = new Globals();
	 
	 public AppPreferences(Context context){
		 this.appSharedPrefs = context.getSharedPreferences(APP_PREFERENCES, Activity.MODE_PRIVATE);
		 this.prefsEditor = appSharedPrefs.edit();
	 }
	 
	 public int getLevelState(String levelPack, String levelNumber) {
		 return appSharedPrefs.getInt(levelPack + levelNumber, globals.LEVEL_UNSET);
	 }
	 
	 public void setLevelState(String levelPack, String levelNumber, int levelState) {
		 prefsEditor.putInt(levelPack + levelNumber, levelState).commit();
	 }
	 
	 public void clear() {
		 prefsEditor.clear().commit();
	 }
}
