package com.android.mcameron.singletrack;

import android.app.Application;

public class Globals extends Application {
	  private String currentLevel;
	  public static final byte LEVEL_UNSET = -1;
	  public static final byte LEVEL_DISABLED = 0;
	  public static final byte LEVEL_ENABLED = 1;
	  public static final byte LEVEL_SOLVED = 1;
	 
	  public void setCurrentLevel(String levelID){
		  currentLevel = levelID;
	  }
	  
	  public String getCurrentLevel(){
		  return currentLevel;
	  }
	  
	  public String getNextLevel() {
		  setNextLevel();
		  return getCurrentLevel();
	  }
	  
	  public void setNextLevel() {
		  int currentLevel = Integer.parseInt(this.currentLevel);
		  setCurrentLevel(Integer.toString(++currentLevel));
	  }
}
