package com.android.mcameron.singletrack;

import java.util.ArrayList;

import android.util.Log;

public class Levels {
	static ArrayList <float[]>levelList = new ArrayList<float[]>();
	
	public void setLevelPack(String levelPack) {
//		ArrayList<float[]> levelPackList = null;
		
		if (levelPack.equals(Globals.PACK_SQUARES)) {
			LevelPackSquares levelPackSquares = new LevelPackSquares();
			levelList = levelPackSquares.getLevelPackSquares();
		}
		else if (levelPack.equals(Globals.PACK_RECTANGLES)) {
			LevelPackRectangles levelPackRectangles = new LevelPackRectangles();
			levelList = levelPackRectangles.getLevelPackRectangles();
		}

//		return levelPackList;
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
}
