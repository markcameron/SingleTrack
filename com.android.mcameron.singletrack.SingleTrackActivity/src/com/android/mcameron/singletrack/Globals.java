package com.android.mcameron.singletrack;

import android.app.Application;

public class Globals extends Application {
	  private static String currentLevel;
	  private static String currentPack;

	  public static final byte LEVEL_UNSET = -1;
	  public static final byte LEVEL_DISABLED = 0;
	  public static final byte LEVEL_ENABLED = 1;
	  public static final byte LEVEL_SOLVED = 2;

	  public static final String PACK_SQUARES = "01";
	  public static final String PACK_RECTANGLES = "02";
	  
	  public static void setCurrentLevel(String levelID){
		  currentLevel = levelID;
	  }
	  
	  public static String getCurrentLevel(){
		  return currentLevel;
	  }
	  
	  public void setCurrentPack(String levelPack){
		  currentPack = levelPack;
	  }
	  
	  public static String getCurrentPack(){
		  return currentPack;
	  }
	  
	  public String getNextLevel() {
		  setNextLevel();
		  return getCurrentLevel();
	  }
	  
	  public static void setNextLevel() {
		  int currentLevel = Integer.parseInt(Globals.currentLevel);
		  setCurrentLevel(Integer.toString(++currentLevel));
	  }
}
