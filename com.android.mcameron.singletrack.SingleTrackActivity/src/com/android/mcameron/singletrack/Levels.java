package com.android.mcameron.singletrack;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.util.Log;

public class Levels {
	private Context mContext;
	
	private String levelPack;
	static ArrayList <float[]>levelList = new ArrayList<float[]>();
	
	public Levels() {

	}
	
	public Levels(Context context) {
		mContext = context;
	}
	
	public void setLevelPack(String levelPack) {
		this.levelPack = levelPack;
		
		if (this.levelPack.equals(Globals.PACK_SQUARES)) {
			LevelPackSquares levelPackSquares = new LevelPackSquares();
			levelList = levelPackSquares.getLevelPackSquares();
		}
		else if (this.levelPack.equals(Globals.PACK_RECTANGLES)) {
			LevelPackRectangles levelPackRectangles = new LevelPackRectangles();
			levelList = levelPackRectangles.getLevelPackRectangles();
		}
	}
	
	public float[] getLevel(int index) {
		return levelList.get(index);
	}
	
	public ArrayList<float[]> getLevels() {
		return levelList;
	}
	
	public int size() {
		return levelList.size() - 1;
	}
	
	public int completedCount() {
		AppPreferences appPrefs = new AppPreferences(mContext);
		
		int i;
		int count = 0;
		for (i = 1; i <= this.size() ; i++) {
			if (appPrefs.getLevelState(levelPack, String.format("%02d", i)) == Globals.LEVEL_SOLVED) {
				count++;
			}			
		}
		
		return count;
	}
}
