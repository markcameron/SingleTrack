package com.android.mcameron.singletrack;

import java.util.ArrayList;

public class LevelPackRectangles extends Levels {
	public ArrayList<float[]> getLevelPackRectangles() {
		ArrayList <float[]> levelList = new ArrayList<float[]>();
		
		levelList.add(null); //Level 0 - Doesn't exist - saves on silly - 1 operations
		// 1-10
		levelList.add(new float[]{2,2,0,0,0,1,1,1,0,1,1,0,0,0});
		levelList.add(new float[]{3,3,0,2,1,1,0,2,0,1,1,2,1,1,1,0,2,0,2,1,2,0});
		levelList.add(new float[]{3,3,0,2,2,0,0,2,0,1,1,0,0,0,1,1,1,0,1,1,1,2,2,0,2,1,2,2,2,1});
		
		return levelList;
	}
}
