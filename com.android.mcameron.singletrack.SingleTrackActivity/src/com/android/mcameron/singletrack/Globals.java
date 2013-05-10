package com.android.mcameron.singletrack;

import android.app.Application;

public class Globals extends Application {
	  private static String currentLevel;
	  private static String currentPack;

	  public static final byte LEVEL_UNSET = -1;
	  public static final byte LEVEL_DISABLED = 0;
	  public static final byte LEVEL_ENABLED = 1;
	  public static final byte LEVEL_SOLVED = 2;
	  
	  public static final Integer SOUND_TOUCH_DRAW = 1;

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
	  
	  public static String getNextLevel() {
		  int currentLevel = Integer.parseInt(Globals.currentLevel);
		  return Integer.toString(++currentLevel);
	  }
	  
	  public static void incrementLevel() {
		  int currentLevel = Integer.parseInt(Globals.currentLevel);
		  setCurrentLevel(Integer.toString(++currentLevel));
	  }
	  
	  public static boolean hasNextLevel() {
		  String nextLevel = getNextLevel();
		  
		  
		  
		  return false;
	  }
}
