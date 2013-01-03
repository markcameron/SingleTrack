package com.android.mcameron.singletrack;

import android.app.Application;

public class Globals extends Application {
	  private String currentLevel;
	 
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
	  
	  private void setNextLevel() {
		  int currentLevel = Integer.parseInt(this.currentLevel);
		  setCurrentLevel(Integer.toString(++currentLevel));
	  }
}
